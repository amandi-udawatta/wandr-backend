package com.wandr.backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChattedTravellerDTO {
    private Long travellerId;
    private String name;
    private String email;
    private String profileImage;
}
