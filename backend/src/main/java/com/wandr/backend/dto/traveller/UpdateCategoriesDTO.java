package com.wandr.backend.dto.traveller;

import lombok.Data;
import java.util.List;

@Data
public class UpdateCategoriesDTO {
    private List<Long> categories;
}
