package com.yifang.exchange;

import com.yifang.exchange.stockfilter.StockFilterResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("stocks")
@AllArgsConstructor
public class StockResource {
    private final StockService stockService;

    @GetMapping
    public List<Stock> get(@RequestParam(required = false) Integer page, @RequestParam(required = false) String tag) {
        int pageNumber = Optional.ofNullable(page).orElse(0);

        if (tag == null || "ALL".equalsIgnoreCase(tag)) {
            return this.stockService.getAllStocks(pageNumber);
        }

        return this.stockService.getStocksByTag(pageNumber, tag);
    }

    @PostMapping
    public List<Stock> init() {
        return this.stockService.init();
    }

    @GetMapping("/{code}")
    public DetailedStock getStock(@PathVariable String code) {
        return this.stockService.getStock(code);
    }

    @GetMapping("/filtered")
    public List<StockFilterResult> getFilteredStock() {
        return this.stockService.filterByStockPredicates();
    }

    @PutMapping
    public Stock update(@RequestBody Stock stock) {
        return this.stockService.update(stock);
    }

    @GetMapping("tags")
    public List<String> getAllTags() {
        return this.stockService.getAllTags();
    }

    @PostMapping("{code}")
    public DetailedStock addTag(@PathVariable String code, @RequestBody Stock stock) {
        return this.stockService.updateTag(code, stock);
    }

    @PostMapping("rsi")
    public void initRSI() {
        this.stockService.updateRSI();
    }
}
