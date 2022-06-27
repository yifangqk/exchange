package com.yifang.exchange.stockfilter;

import com.yifang.exchange.DetailedStock;

public interface StockPredicate {
    boolean check(DetailedStock detailedStock);
}
