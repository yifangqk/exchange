package com.yifang.exchange.snapshot;

import com.yifang.exchange.StockStats;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class MegaSnapShot {
    private String stockCode;

    private List<String> tags;

    /**
     * 1M **
     */
    private BigDecimal lowestPrice1M;

    private BigDecimal highestPrice1M;

    private BigDecimal averagePrice1M;

    private BigDecimal delta1M;

    /**
     * 2M **
     */
    private BigDecimal lowestPrice2M;

    private BigDecimal highestPrice2M;

    private BigDecimal averagePrice2M;

    private BigDecimal delta2M;

    /**
     * 3M
     */
    private BigDecimal lowestPrice3M;

    private BigDecimal highestPrice3M;

    private BigDecimal averagePrice3M;

    private BigDecimal delta3M;

    /**
     * 6M
     */
    private BigDecimal lowestPrice6M;

    private BigDecimal highestPrice6M;

    private BigDecimal averagePrice6M;

    private BigDecimal delta6M;

    /**
     * 12M
     */
    private BigDecimal lowestPrice12M;

    private BigDecimal highestPrice12M;

    private BigDecimal averagePrice12M;

    private BigDecimal delta12M;

    /**
     * 24M
     */
    private BigDecimal lowestPrice24M;

    private BigDecimal highestPrice24M;

    private BigDecimal averagePrice24M;

    private BigDecimal delta24M;

    /**
     * General Info **
     */

    private BigDecimal currentPrice;

    private BigDecimal ssiRecommendation;

    private String volume;

    private String eps;

    private String pe;

    private StockStats stockStats;
}
