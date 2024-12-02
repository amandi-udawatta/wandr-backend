package com.wandr.backend.dto.product;

import com.wandr.backend.entity.Product;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private Long businessId;
    private String image;

}


