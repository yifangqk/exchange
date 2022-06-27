package com.yifang.exchange.snapshot;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
class CoPhieu68SingleSchedulerV2 {
    private static final String STOCK_CODE = "td:eq(1)";

    private static final String LOWEST_PRICE = "td:eq(2)";

    private static final String HIGHEST_PRICE = "td:eq(8)";

    private static final String CURRENT_PRICE = "td:eq(4)";

    private static final String VOLUME = "td:eq(7)";

    private static final String EPS = "td:eq(11)";

    private static final String PE = "td:eq(12)";

    private final String stockCode;

    int month;

    @SneakyThrows
    SnapShot run() {
        try {
            Objects.requireNonNull(this.stockCode, "StockCode should never be null or empty!");
            if (this.month <= 0) {
                throw new IllegalArgumentException("Month is less than zero!");
            }

            String URL = "https://www.cophieu68.vn/atbottom.php?keyword={0}&d={1}&search=T%C3%ACm+Ki%E1%BA%BFm";

            URL = MessageFormat.format(URL, this.stockCode, this.month);

            Document doc = Jsoup.connect(URL)
                    .timeout(10 * 1000)
                    .validateTLSCertificates(false)
                    .get();

            return fromDoc(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SnapShot.builder()
                .stockCode(this.stockCode)
                .month(this.month)
                .delta(new BigDecimal(1234567890))
                .build();

    }

    private SnapShot fromDoc(Document document) {
        Element rowForStock = document.select("#fred tr").stream()
                .filter(tr -> tr.select(STOCK_CODE).text().equals(this.stockCode))
                .findAny()
                .orElseThrow(IllegalStateException::new);

        BigDecimal highestPrice = new BigDecimal(rowForStock.select(HIGHEST_PRICE).text());
        BigDecimal lowestPrice = new BigDecimal(rowForStock.select(LOWEST_PRICE).text());
        BigDecimal currentPrice = new BigDecimal(rowForStock.select(CURRENT_PRICE).text());
        String volume = rowForStock.select(VOLUME).text();
        String eps = rowForStock.select(EPS).text();
        String pe = rowForStock.select(PE).text();


        BigDecimal averagePrice = highestPrice
                .add(lowestPrice)
                .divide(new BigDecimal(2), 2, RoundingMode.CEILING);


        BigDecimal delta = currentPrice
                .subtract(averagePrice)
                .divide(averagePrice, 2, RoundingMode.CEILING)
                .multiply(new BigDecimal(100));

        return SnapShot.builder()
                .stockCode(rowForStock.select(STOCK_CODE).text())
                .lowestPrice(lowestPrice)
                .averagePrice(averagePrice)
                .highestPrice(highestPrice)
                .delta(delta)
                .currentPrice(currentPrice)
                .volume(volume)
                .eps(eps)
                .pe(pe)
                .calculationTime(new Date())
                .build();
    }
}
