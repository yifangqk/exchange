package com.yifang.exchange.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder(toBuilder = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SnapShot implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockCode;

    @ElementCollection
    @CollectionTable(
            name = "SnapShot_Tags",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    private BigDecimal lowestPrice;

    private BigDecimal highestPrice;

    private BigDecimal averagePrice;

    private BigDecimal currentPrice;

    private BigDecimal ssiRecommendation;


    /**
     * This field is for calculation
     * lowest price is 0 %
     * highest price is 100%
     * now calculate the current price based on lowest/highest prices
     * delta = [(currentPrice - averagePrice)/averagePrice] * 100%
     */
    private BigDecimal delta;

    private String volume;

    private Date calculationTime;

    private Date calculationDate;

    private String eps;

    private String pe;

    private int month;
}
