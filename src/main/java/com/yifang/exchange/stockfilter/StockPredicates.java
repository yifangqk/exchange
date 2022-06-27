package com.yifang.exchange.stockfilter;

import java.util.List;

public final class StockPredicates {
    static List<StockPredicate> predicates() {
        return List.of(
                new EPSStockPredicate(),
                new VolumeStockPredicate()
        );
    }
}
