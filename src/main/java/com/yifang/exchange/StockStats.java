package com.yifang.exchange;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class StockStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private BigDecimal referencePrice;

    private BigDecimal currentPrice;

    private BigDecimal openingPrice;

    private BigDecimal closingPrice;

    private BigDecimal lowestPrice;

    private BigDecimal highestPrice;

    private BigDecimal volume;

    private BigDecimal eps;

    private BigDecimal pe;

    private BigDecimal capitalMarket;

    private BigDecimal listingVolume;

    private BigDecimal roe;

    private BigDecimal beta;

    private BigDecimal bookValue;

    private BigDecimal eps4QuarterAgo;

    private BigDecimal highest52Weeks;

    private BigDecimal lowest52Weeks;

    private BigDecimal averageVolume13Weeks;

    private BigDecimal averageVolume10Days;

    private BigDecimal rsi;

    private Date updatedTime;

}
