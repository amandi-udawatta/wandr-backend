package com.wandr.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateActivitiesDTO {
    private List<Integer> activities;
}
