package com.yifang.exchange.stockfilter;

import com.yifang.exchange.DetailedStock;
import com.yifang.exchange.StockStats;

import java.math.BigDecimal;
import java.util.Optional;

public class EPSStockPredicate implements StockPredicate {
    private static final BigDecimal COMPARING_EPS = new BigDecimal("2.5");

    @Override
    public boolean check(DetailedStock detailedStock) {
        return Optional.ofNullable(detailedStock)
                .map(DetailedStock::getStockStats)
                .map(StockStats::getEps)
                .filter(eps -> eps.compareTo(COMPARING_EPS) > 0)
                .isPresent();
    }
}
