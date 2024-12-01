package com.wandr.backend.dto.reservation;

import lombok.Data;

import java.util.List;


@Data
public class CreateReservationRequestDTO {
    private Long travellerId;
    private List<Long> cartItemIds;
}

