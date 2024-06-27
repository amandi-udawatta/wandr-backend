package com.wandr.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateCategoriesDTO {
    private List<Integer> categories;
}
