package com.yifang.exchange.stockfilter;

import com.yifang.exchange.DetailedStock;
import com.yifang.exchange.StockStats;

import java.math.BigDecimal;
import java.util.Optional;

public class VolumeStockPredicate implements StockPredicate {
    private static final BigDecimal COMPARING_VOLUME = new BigDecimal("100000");

    @Override
    public boolean check(DetailedStock detailedStock) {
        return Optional.ofNullable(detailedStock)
                .map(DetailedStock::getStockStats)
                .map(StockStats::getVolume)
                .filter(volume -> volume.compareTo(COMPARING_VOLUME) > 0)
                .isPresent();
    }
}
