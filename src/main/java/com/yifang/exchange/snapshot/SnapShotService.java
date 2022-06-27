package com.yifang.exchange.snapshot;

import com.yifang.exchange.StockStatsRepository;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SnapShotService {
    private final CoPhieu68Scheduler coPhieu68Scheduler;

    private final SnapShotRepository snapShotRepository;

    private final StockStatsRepository stockStatsRepository;

    public SnapShotService(@Lazy CoPhieu68Scheduler coPhieu68Scheduler, SnapShotRepository snapShotRepository,
    StockStatsRepository stockStatsRepository) {
        this.coPhieu68Scheduler = coPhieu68Scheduler;
        this.snapShotRepository = snapShotRepository;
        this.stockStatsRepository = stockStatsRepository;
    }

    public void engage(List<SnapShot> snapShots) {
        if (CollectionUtils.isEmpty(snapShots)) {
            return;
        }

        var persistedSnapshots = this.snapShotRepository.findByMonth(snapShots.get(0).getMonth());
        this.snapShotRepository.deleteAll(persistedSnapshots);
        this.snapShotRepository.saveAll(snapShots);
    }

    // 15 mins
    @Scheduled(fixedDelay = 900000)
    void initAll() {
        List.of(1, 2, 3, 6, 12, 24).forEach(this.coPhieu68Scheduler::runWithMonth);
    }

    Snapshots getLatestSnapshots(int month) {
        var monthlySnapShots = this.snapShotRepository.findByMonth(month);

        return Snapshots.builder()
                .snapshots(monthlySnapShots)
                .numberOfMonths(month)
                .build();
    }

    MegaSnapshots mega() {
        var snap1Ms = Optional.ofNullable(this.getLatestSnapshots(1))
                .map(Snapshots::getSnapshots)
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(SnapShot::getStockCode, Function.identity()));

        var snap2Ms = Optional.ofNullable(this.getLatestSnapshots(2))
                .map(Snapshots::getSnapshots)
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(SnapShot::getStockCode, Function.identity()));

        var snap3Ms = Optional.ofNullable(this.getLatestSnapshots(3))
                .map(Snapshots::getSnapshots)
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(SnapShot::getStockCode, Function.identity()));

        var snap6Ms = Optional.ofNullable(this.getLatestSnapshots(6))
                .map(Snapshots::getSnapshots)
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(SnapShot::getStockCode, Function.identity()));

        var snap12Ms = Optional.ofNullable(this.getLatestSnapshots(12))
                .map(Snapshots::getSnapshots)
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(SnapShot::getStockCode, Function.identity()));

        var snap24Ms = Optional.ofNullable(this.getLatestSnapshots(24))
                .map(Snapshots::getSnapshots)
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(SnapShot::getStockCode, Function.identity()));

        List<MegaSnapShot> megaSnapShots = new ArrayList<>();

        for (SnapShot snapShot : snap12Ms.values()) {
            String stockCode = snapShot.getStockCode();

            var stockStats = this.stockStatsRepository.findByCode(stockCode).orElse(null);

            // 1M
            MegaSnapShot megaSnapShot = MegaSnapShot.builder()
                    .stockCode(snapShot.getStockCode())
                    .tags(snapShot.getTags())
                    .lowestPrice1M(Optional.ofNullable(snap1Ms.get(stockCode)).map(SnapShot::getLowestPrice).orElse(null))
                    .averagePrice1M(Optional.ofNullable(snap1Ms.get(stockCode)).map(SnapShot::getAveragePrice).orElse(null))
                    .highestPrice1M(Optional.ofNullable(snap1Ms.get(stockCode)).map(SnapShot::getHighestPrice).orElse(null))
                    .delta1M(Optional.ofNullable(snap1Ms.get(stockCode)).map(SnapShot::getDelta).orElse(null))
                    .build();

            // 2M
            megaSnapShot = megaSnapShot.toBuilder()
                    .stockCode(snapShot.getStockCode())
                    .tags(snapShot.getTags())
                    .lowestPrice2M(Optional.ofNullable(snap2Ms.get(stockCode)).map(SnapShot::getLowestPrice).orElse(null))
                    .averagePrice2M(Optional.ofNullable(snap2Ms.get(stockCode)).map(SnapShot::getAveragePrice).orElse(null))
                    .highestPrice2M(Optional.ofNullable(snap2Ms.get(stockCode)).map(SnapShot::getHighestPrice).orElse(null))
                    .delta2M(Optional.ofNullable(snap2Ms.get(stockCode)).map(SnapShot::getDelta).orElse(null))
                    .build();

            // 3M
            megaSnapShot = megaSnapShot.toBuilder()
                    .stockCode(snapShot.getStockCode())
                    .tags(snapShot.getTags())
                    .lowestPrice3M(Optional.ofNullable(snap3Ms.get(stockCode)).map(SnapShot::getLowestPrice).orElse(null))
                    .averagePrice3M(Optional.ofNullable(snap3Ms.get(stockCode)).map(SnapShot::getAveragePrice).orElse(null))
                    .highestPrice3M(Optional.ofNullable(snap3Ms.get(stockCode)).map(SnapShot::getHighestPrice).orElse(null))
                    .delta3M(Optional.ofNullable(snap3Ms.get(stockCode)).map(SnapShot::getDelta).orElse(null))
                    .build();

            // 6M
            megaSnapShot = megaSnapShot.toBuilder()
                    .stockCode(snapShot.getStockCode())
                    .tags(snapShot.getTags())
                    .lowestPrice6M(Optional.ofNullable(snap6Ms.get(stockCode)).map(SnapShot::getLowestPrice).orElse(null))
                    .averagePrice6M(Optional.ofNullable(snap6Ms.get(stockCode)).map(SnapShot::getAveragePrice).orElse(null))
                    .highestPrice6M(Optional.ofNullable(snap6Ms.get(stockCode)).map(SnapShot::getHighestPrice).orElse(null))
                    .delta6M(Optional.ofNullable(snap6Ms.get(stockCode)).map(SnapShot::getDelta).orElse(null))
                    .build();

            // 12M
            megaSnapShot = megaSnapShot.toBuilder()
                    .stockCode(snapShot.getStockCode())
                    .ssiRecommendation(BigDecimal.ZERO)
                    .stockStats(stockStats)
                    .tags(snapShot.getTags())
                    .currentPrice(Optional.ofNullable(snap12Ms.get(stockCode)).map(SnapShot::getCurrentPrice).orElse(null))
                    .lowestPrice12M(Optional.ofNullable(snap12Ms.get(stockCode)).map(SnapShot::getLowestPrice).orElse(null))
                    .averagePrice12M(Optional.ofNullable(snap12Ms.get(stockCode)).map(SnapShot::getAveragePrice).orElse(null))
                    .highestPrice12M(Optional.ofNullable(snap12Ms.get(stockCode)).map(SnapShot::getHighestPrice).orElse(null))
                    .delta12M(Optional.ofNullable(snap12Ms.get(stockCode)).map(SnapShot::getDelta).orElse(null))
                    .build();

            // 24M
            megaSnapShot = megaSnapShot.toBuilder()
                    .stockCode(snapShot.getStockCode())
                    .tags(snapShot.getTags())
                    .lowestPrice24M(Optional.ofNullable(snap24Ms.get(stockCode)).map(SnapShot::getLowestPrice).orElse(null))
                    .averagePrice24M(Optional.ofNullable(snap24Ms.get(stockCode)).map(SnapShot::getAveragePrice).orElse(null))
                    .highestPrice24M(Optional.ofNullable(snap24Ms.get(stockCode)).map(SnapShot::getHighestPrice).orElse(null))
                    .delta24M(Optional.ofNullable(snap24Ms.get(stockCode)).map(SnapShot::getDelta).orElse(null))
                    .build();

            megaSnapShots.add(megaSnapShot);

        }

        List<MegaSnapShot> sortedMegaSnapShots = megaSnapShots.stream()
                .sorted(Comparator.comparing(MegaSnapShot::getDelta12M))
                .collect(Collectors.toList());

        Date calculationTime = snap12Ms.values().stream().findAny().map(SnapShot::getCalculationTime).orElse(new Date());

        return MegaSnapshots.builder()
                .snapshots(sortedMegaSnapShots)
                .calculatedTime(calculationTime)
                .prettyCalculatedTime(new PrettyTime().format(calculationTime))
                .build();
    }

}
