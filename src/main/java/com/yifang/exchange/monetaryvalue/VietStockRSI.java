package com.yifang.exchange.monetaryvalue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yifang.exchange.DetailedStock;
import com.yifang.exchange.StockService;
import com.yifang.exchange.StockStatsRepository;
import com.yifang.exchange.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class VietStockRSI {
    private final StockStatsRepository stockStatsRepository;

    private final StockService stockService;

    public static final String VIET_STOCK_URL = "https://api.vietstock.vn/finance/stockfilterover?transNo=14&indicator=RSI&toBuy=50&toSell=50&minVol=10000&page={0}&pageSize=20&orderBy=1&desc=1&catID=0&sectorID=0";

    private static final BigDecimal RSI_30 = new BigDecimal(30);

    private static final BigDecimal RSI_70 = new BigDecimal(70);


    @Scheduled(fixedDelay = 3600000) // every 1 hour
    public void updateRSI() {
        this.stockService.deleteRSITags();

        for (int i = 1; i < 1000; i++) {
            try {
                var document = Jsoup.connect(MessageFormat.format(VIET_STOCK_URL, i))
                        .ignoreContentType(true)
                        .timeout(100 * 1000)
                        .validateTLSCertificates(false)
                        .get();

                Type listType = new TypeToken<ArrayList<RawVietStockRSI>>() {
                }.getType();
                List<RawVietStockRSI> rawVietStockRSIS = new Gson().fromJson(document.body().text(), listType);
                rawVietStockRSIS.forEach(this::updateStockRSI);
                if (rawVietStockRSIS.isEmpty()) {
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStockRSI(RawVietStockRSI rawVietStockRSI) {
        var detailedStock = this.stockService.getStock(rawVietStockRSI.getStockCode());

        if (detailedStock == DetailedStock.EMPTY_DETAILED_STOCK) return;

        var stockStatsOpt = this.stockStatsRepository.findByCode(rawVietStockRSI.getStockCode());

        if (stockStatsOpt.isEmpty()) return;

        var stockStats = stockStatsOpt.get()
                .toBuilder()
                .rsi(new BigDecimal(rawVietStockRSI.getRSI()))
                .build();

        this.stockStatsRepository.save(stockStats);

        var rsiTag = "";

        if (stockStats.getRsi().compareTo(RSI_30) < 0) {
            rsiTag = Tags.RSI_LOW;
        } else if (stockStats.getRsi().compareTo(RSI_70) > 0) {
            rsiTag = Tags.RSI_HIGH;
        }

        if (StringUtils.hasText(rsiTag)) {
            this.stockService.addTag(rawVietStockRSI.getStockCode(), rsiTag);
        }

        System.out.println("Finished updating RSI for " + rawVietStockRSI.getStockCode());
    }

    @Data
    public static final class RawVietStockRSI {
        private String StockCode;

        private String Exchange;

        private String TradingDate;

        private String MFI;

        private String RSI;

        private String ClosePrice;

        private String Change;

        private String PerChange;

        private String Vol;

        private String ColorID;

        private String StatusID;

        private String AvgVol;

        private String AvgFBVal;

        private String AvgFSVal;

        private String Note;

        private String Row;

        private String Rows;
    }
}
