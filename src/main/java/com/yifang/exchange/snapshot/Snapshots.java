package com.yifang.exchange.snapshot;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Snapshots {
    private int numberOfMonths;

    private Date calculatedTime;

    private List<SnapShot> snapshots;
}
