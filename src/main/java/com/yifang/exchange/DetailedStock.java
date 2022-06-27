package com.yifang.exchange;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailedStock {
    public static final DetailedStock EMPTY_DETAILED_STOCK = DetailedStock.builder()
            .stock(Stock.EMPTY_STOCK)
            .build();

    private Stock stock;

    private StockStats stockStats;
}
