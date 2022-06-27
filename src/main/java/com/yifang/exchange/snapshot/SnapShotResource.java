package com.yifang.exchange.snapshot;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/snapshots")
@RestController
@AllArgsConstructor
public class SnapShotResource {
    private final SnapShotService snapShotService;


    @GetMapping
    public Snapshots getAllSnapshots(@RequestParam(required = false) Integer month) {
        int param = Optional.ofNullable(month).orElse(12);
        return this.snapShotService.getLatestSnapshots(param);
    }

    @GetMapping("init")
    public String init() {
        this.snapShotService.initAll();
        return "OK";
    }

    @GetMapping("mega")
    public MegaSnapshots mega() {
        return this.snapShotService.mega();
    }
}
