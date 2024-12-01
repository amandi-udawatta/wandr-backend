package com.wandr.backend.entity;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long cartItemId;
    private Long travellerId;
    private Long productId;
    private Integer quantity;
}
