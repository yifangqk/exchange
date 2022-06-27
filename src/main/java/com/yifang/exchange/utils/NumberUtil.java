package com.yifang.exchange.utils;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
public final class NumberUtil {
    public static final BigDecimal NUMBER_FORMAT_ERROR = new BigDecimal(-1_111_111_111);

    public static BigDecimal parseBigDecimal(String str) {
        try {
            return new BigDecimal(str).setScale(2, RoundingMode.UP);
        } catch (Exception e) {
            System.out.println("Cannot parse to BigDecimal: " + str);
            e.printStackTrace();
            return NUMBER_FORMAT_ERROR;
        }
    }
}
