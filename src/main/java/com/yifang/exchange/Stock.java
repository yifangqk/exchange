package com.yifang.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Stock {
    public static final Stock EMPTY_STOCK = Stock.builder()
            .id(-1L)
            .code("EMPTY")
            .build();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String enName;

    @Column(nullable = false)
    private String exchange;

    @Column(nullable = false)
    private String stockNumber;

    @Column(nullable = false)
    private String type;

    @ElementCollection
    @CollectionTable(
            name = "Stock_Tags",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    public static Stock from(RawStock rawStock) {
        return Stock.builder()
                .code(rawStock.getCode())
                .name(rawStock.getClientName())
                .enName(rawStock.getClientNameEn())
                .exchange(rawStock.getExchange())
                .stockNumber(rawStock.getStockNo())
                .type(rawStock.getType())
                .build();
    }
}
