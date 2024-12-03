package com.wandr.backend.dto.trip;

import lombok.Data;
import java.util.List;


@Data
public class AddPlaceToTripDTO {
//    private Long tripId;
//    private Long placeId;
    private Long tripId;
    private List<Long> placeIds;

}
