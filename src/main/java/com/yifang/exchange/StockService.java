package com.yifang.exchange;

import com.google.gson.Gson;
import com.yifang.exchange.monetaryvalue.VietStockRSI;
import com.yifang.exchange.stockfilter.StockFilter;
import com.yifang.exchange.stockfilter.StockFilterResult;
import com.yifang.exchange.stockfilter.StockFilterResultRepository;
import com.yifang.exchange.utils.ResourceUtil;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockService {
    private static final int DEFAULT_PAGING_SIZE = 30;

    public static final String ALLOW_INIT = "application.masterdata.allow_init";

    private final StockRepository stockRepository;

    private final StockFilterResultRepository stockFilterResultRepository;

    private final StockStatsRepository stockStatsRepository;

    private final VietStockRSI vietStockRSI;

    private final Environment environment;


    public StockService(StockRepository stockRepository, StockFilterResultRepository stockFilterResultRepository, StockStatsRepository stockStatsRepository,@Lazy VietStockRSI vietStockRSI, Environment environment) {
        this.stockRepository = stockRepository;
        this.stockFilterResultRepository = stockFilterResultRepository;
        this.stockStatsRepository = stockStatsRepository;
        this.vietStockRSI = vietStockRSI;
        this.environment = environment;
    }

    public List<Stock> getAllStocks() {
        return this.stockRepository.findAll();
    }

    public List<Stock> getAllStocks(int page) {
        var stocksPage = this.stockRepository.findAll(PageRequest.of(page, DEFAULT_PAGING_SIZE));

        return stocksPage.getContent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteRSITags() {
        this.stockRepository.deleteStockTags(Tags.RSI_HIGH);
        this.stockRepository.deleteStockTags(Tags.RSI_LOW);
        this.stockRepository.deleteSnapShotTags(Tags.RSI_HIGH);
        this.stockRepository.deleteSnapShotTags(Tags.RSI_LOW);
        this.stockStatsRepository.eraseStatRSI();
    }

    public List<Stock> getStocksByTag(int page, String tag) {
        return this.stockRepository.findAllByTags(tag);
    }

    public List<String> getAllTags() {
        return this.stockRepository.findAll().stream()
                .map(Stock::getTags)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<StockFilterResult> filterByStockPredicates() {
        return this.stockFilterResultRepository.findAll();
    }

    // 15 mins
    @Scheduled(fixedDelay = 900000)
    void buildFilteredStocks() {
        var vn30Stocks = this.stockRepository.findAllByTags(Tags.FAVOURITE);
        var gson = new Gson();
        var detailedVN30Stocks = vn30Stocks.stream()
                .map(stock -> getStock(stock.getCode()))
                .collect(Collectors.toList());

        var stockFilterResults = StockFilter.filter(detailedVN30Stocks)
                .stream()
                .map(detailedStock -> StockFilterResult.builder()
                        .code(detailedStock.getStock().getCode())
                        .name(detailedStock.getStock().getName())
                        .tags(new ArrayList<>(detailedStock.getStock().getTags()))
                        .data(gson.toJson(detailedStock))
                        .build())
                .collect(Collectors.toList());
        this.stockFilterResultRepository.deleteAll();
        this.stockFilterResultRepository.saveAll(stockFilterResults);

    }

    @Async
    public void updateStockStats(String code) {
        var stats = SingleStockCrawler.fetch(code);

        var persistedStats = this.stockStatsRepository.findByCode(code);

        if (persistedStats.isEmpty()) {
            this.stockStatsRepository.save(stats);
        } else {
            var toUpdateStats = stats.toBuilder()
                    .rsi(persistedStats.get().getRsi())
                    .id(persistedStats.get().getId())
                    .build();
            this.stockStatsRepository.save(toUpdateStats);
        }
    }

    @Scheduled(fixedDelay = 660000) // every 11 mins
    public void buildAllStockStats() {
        this.getAllStocks()
                .stream()
                .map(Stock::getCode)
                .forEach(this::updateStockStats);
    }

    public DetailedStock getStock(String code) {
        var stock = this.stockRepository.findByCode(code).orElse(Stock.EMPTY_STOCK);

        if (stock == Stock.EMPTY_STOCK) {
            return DetailedStock.EMPTY_DETAILED_STOCK;
        }

        var stockStats = this.stockStatsRepository.findByCode(code)
                .or(() -> {
                    this.updateStockStats(code);
                    return Optional.of(SingleStockCrawler.fetch(code));
                }).orElseThrow();

        var timeout = new Date().getTime() - Optional.ofNullable(stockStats)
                .map(StockStats::getUpdatedTime)
                .orElse(new Date())
                .getTime() > 15000L;

        if (timeout) {
            this.updateStockStats(code);
        }

        return DetailedStock.builder()
                .stock(stock)
                .stockStats(stockStats)
                .build();
    }

    public Stock update(Stock stock) {
        return this.stockRepository.save(stock);
    }

    public DetailedStock updateTag(String code, Stock stock) {
        var persistedStock = this.stockRepository.findByCode(code).orElseThrow();
        this.stockRepository.save(persistedStock.toBuilder()
                .tags(new ArrayList<>(new LinkedHashSet<>(stock.getTags())))
                .build());
        return this.getStock(code);
    }

    public void addTag(String code, String tag) {
        var persistedStock = this.stockRepository.findByCode(code).orElseThrow();
        var newTags = new ArrayList<>(persistedStock.getTags());
        newTags.add(tag);

        var newStock = persistedStock.toBuilder()
                .tags(new ArrayList<>(new LinkedHashSet<>(newTags)))
                .build();

        this.stockRepository.save(newStock);
    }

    @PostConstruct
    public List<Stock> init() {
        var shouldInit = Optional.ofNullable(this.environment.getProperty(ALLOW_INIT))
                .map(Boolean::parseBoolean)
                .orElse(false);

        if(!shouldInit) return List.of();

        System.out.println("Start initializing all stocks");

        var toPersistStocks = this.rawStocks().stream()
                .map(rawStock -> rawStock.toBuilder().build())
                .map(Stock::from)
                .collect(Collectors.toList());
        var stocks = this.stockRepository.saveAll(toPersistStocks);
        this.addDefaultTags();

        System.out.println("Finished initializing all stocks");
        return stocks;
    }

    private void addDefaultTags() {
        Tags.vn30().stream()
                .map(this.stockRepository::findByCode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .map(stock -> stock.toBuilder()
                        .tags(List.of(Tags.VN30_TAG, Tags.FAVOURITE))
                        .build())
                .forEach(this.stockRepository::save);
    }

    @SneakyThrows
    private List<RawStock> rawStocks() {
        var allStocksInputStream = ResourceUtil.getResourceAsStream("defaultAllStocks.json").orElseThrow();
        String text = new String(allStocksInputStream.readAllBytes(), StandardCharsets.UTF_8);

        var rawStockWrapper = new Gson().fromJson(text, RawStocksWrapper.class);
        return rawStockWrapper.getData();
    }

    public void updateRSI() {
        this.vietStockRSI.updateRSI();
    }
}
