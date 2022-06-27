package com.yifang.exchange.snapshot;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class MegaSnapshots {
    private Date calculatedTime;

    private String prettyCalculatedTime;

    private List<MegaSnapShot> snapshots;
}
