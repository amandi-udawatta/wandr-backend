package com.wandr.backend.dto.traveller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ChattedBusinessDTO {
    private Long businessId;
    private String name;
    private String email;
    private String profileImage;
}
