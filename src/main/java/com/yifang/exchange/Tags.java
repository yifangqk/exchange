package com.yifang.exchange;

import java.util.List;

public final class Tags {
    public static final String VN30_TAG = "VN30";

    public static final String FAVOURITE = "FAVOURITE";

    public static final String RSI_LOW = "RSI_LOW";

    public static final String RSI_HIGH = "RSI_HIGH";


    public static List<String> vn30() {
        return List.of("ACB", "BID", "BVH", "CTG", "FPT",
                "GAS", "GVR", "HDB", "HPG", "KDH", "MBB", "MSN", "MWG", "NVL",
                "PDR", "PLX", "PNJ", "POW", "SAB", "SSI", "STB", "TCB", "TPB",
                "VCB", "VHM", "VIC", "VJC", "VNM", "VPB", "VRE");
    }

}
