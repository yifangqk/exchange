package com.yifang.exchange.snapshot;

import com.yifang.exchange.Stock;
import com.yifang.exchange.StockService;
import com.yifang.exchange.Tags;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CoPhieu68Scheduler {
    private final StockService stockService;

    private final SnapShotService snapShotService;

    public void runWithMonth(int month) {
        // Get all Stocks
        var stocks = this.stockService.getStocksByTag(0, Tags.FAVOURITE);
        List<String> stockCodes = stocks
                .stream()
                .map(Stock::getCode)
                .collect(Collectors.toList());

        Map<String, Stock> mapCodeStock = stocks.stream()
                .collect(Collectors.toMap(Stock::getCode, Function.identity()));

        List<SnapShot> snapShotsV2 = stockCodes.stream()
                .map(code -> new CoPhieu68SingleSchedulerV2(code, month).run())
                .map(snap -> snap.toBuilder()
                        .month(month)
                        .tags(new ArrayList<>(mapCodeStock.get(snap.getStockCode()).getTags()))
                        .build())
                .sorted(Comparator.comparing(SnapShot::getDelta))
                .collect(Collectors.toList());

        this.snapShotService.engage(snapShotsV2);
    }

    public void innerRun() {
        List<Integer> months = List.of(1, 2, 3, 6, 12, 24);

        months.forEach(this::runWithMonth);
    }
}

