package com.yifang.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RawStock {
    private String clientName;

    private String clientNameEn;

    private String code;

    private String description;

    private String exchange;

    private String full_name;

    private String securityName;

    private String stockNo;

    private String type;
}
