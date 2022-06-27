package com.yifang.exchange;

import com.yifang.exchange.utils.NumberUtil;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;

import java.util.Date;

public class SingleStockCrawler {
    private static final String REFERENCE_PRICE = "#snapshot_trading tr:eq(0) td:eq(1)";
    private static final String CURRENT_PRICE = "#stockname_close";
    private static final String OPENING_PRICE = "#snapshot_trading tr:eq(1) td:eq(1)";
    private static final String LOWEST_PRICE = "#snapshot_trading #stockname_price_lowest";
    private static final String HIGHEST_PRICE = "#snapshot_trading #stockname_price_highest";
    private static final String VOLUME = "#snapshot_trading tr:eq(3) td:eq(1)";
    private static final String GATHER_INFORMATION = "#snapshot_trading table tr";

    @SneakyThrows
    public static StockStats fetch(String code) {
        try {
            return innerFetch(code);
        } catch (Exception e) {
            System.out.println("Fetching stats failed for " + code);
            e.printStackTrace();
            return StockStats.builder()
                    .build();
        }
    }

    @SneakyThrows
    private static StockStats innerFetch(String code) {
        var url = "https://www.cophieu68.vn/snapshot.php?id=" + code;

        var document = Jsoup.connect(url)
                .validateTLSCertificates(false)
                .ignoreContentType(true)
                .timeout(10000)
                .get();

        var referencePrice = document.select(REFERENCE_PRICE).text().split(" ")[0];
        var currentPrice = document.select(CURRENT_PRICE).text().split(" ")[0];
        var openingPrice = document.select(OPENING_PRICE).text().split(" ")[0];
        var lowestPrice = document.select(LOWEST_PRICE).text().split(" ")[0];
        var highestPrice = document.select(HIGHEST_PRICE).text().split(" ")[0];
        var volume = document.select(VOLUME).text().split(" ")[0].replace(",", "");
        var eps = document.select(GATHER_INFORMATION).get(8).select("td").get(1).text().split(" ")[0].replace(",", "");
        var pe = document.select(GATHER_INFORMATION).get(9).select("td").get(1).text().split(" ")[0].replace(",", "");
        var capitalMarket = document.select(GATHER_INFORMATION).get(10).select("td").get(1).text().split(" ")[0].replace(",", "");
        var listingVolume = document.select(GATHER_INFORMATION).get(11).select("td").get(1).text().split(" ")[0].replace(",", "");
        var roe = document.select(GATHER_INFORMATION).get(13).select("td").get(1).text().replace("%", "");
        var beta = document.select(GATHER_INFORMATION).get(14).select("td").get(1).text();
        var bookValue = document.select(GATHER_INFORMATION).get(12).select("td").get(1).text().split(" ")[0];
        var eps4QuarterAgo = document.select(GATHER_INFORMATION).get(15).select("td").get(1).text().replace(",", "");
        var highest52Weeks = document.select(GATHER_INFORMATION).get(6).select("td").get(1).text();
        var lowest52Weeks = document.select(GATHER_INFORMATION).get(7).select("td").get(1).text();
        var averageVolume13Weeks = document.select(GATHER_INFORMATION).get(4).select("td").get(1).text().split(" ")[0].replace(",", "");
        var averageVolume10Days = document.select(GATHER_INFORMATION).get(5).select("td").get(1).text().split(" ")[0].replace(",", "");

        return StockStats.builder()
                .code(code)
                .lowestPrice(NumberUtil.parseBigDecimal(lowestPrice))
                .highestPrice(NumberUtil.parseBigDecimal(highestPrice))
                .currentPrice(NumberUtil.parseBigDecimal(currentPrice))
                .openingPrice(NumberUtil.parseBigDecimal(openingPrice))
                .referencePrice(NumberUtil.parseBigDecimal(referencePrice))
                .volume(NumberUtil.parseBigDecimal(volume))
                .eps(NumberUtil.parseBigDecimal(eps))
                .pe(NumberUtil.parseBigDecimal(pe))
                .capitalMarket(NumberUtil.parseBigDecimal(capitalMarket))
                .listingVolume(NumberUtil.parseBigDecimal(listingVolume))
                .roe(NumberUtil.parseBigDecimal(roe))
                .beta(NumberUtil.parseBigDecimal(beta))
                .bookValue(NumberUtil.parseBigDecimal(bookValue))
                .eps4QuarterAgo(NumberUtil.parseBigDecimal(eps4QuarterAgo))
                .highest52Weeks(NumberUtil.parseBigDecimal(highest52Weeks))
                .lowest52Weeks(NumberUtil.parseBigDecimal(lowest52Weeks))
                .averageVolume13Weeks(NumberUtil.parseBigDecimal(averageVolume13Weeks))
                .averageVolume10Days(NumberUtil.parseBigDecimal(averageVolume10Days))
                .updatedTime(new Date())
                .build();
    }
}
