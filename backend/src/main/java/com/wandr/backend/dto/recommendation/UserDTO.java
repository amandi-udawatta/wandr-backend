package com.wandr.backend.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private List<String> categories;
    private List<String> activities;
}


