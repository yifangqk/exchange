package com.yifang.exchange.stockfilter;

import com.yifang.exchange.DetailedStock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class StockFilter {
    public static List<DetailedStock> filter(List<DetailedStock> detailedStocks) {
        List<DetailedStock> filteredDetailedStocks = new ArrayList<>();
        var stockPredicates = StockPredicates.predicates();

        return detailedStocks.stream()
                .filter(detailedStock -> check(detailedStock, stockPredicates))
                .collect(Collectors.toList());
    }

    private static boolean check(DetailedStock detailedStock, List<StockPredicate> stockPredicates) {
        return stockPredicates.stream()
                .allMatch(stockPredicate -> stockPredicate.check(detailedStock));
    }
}
