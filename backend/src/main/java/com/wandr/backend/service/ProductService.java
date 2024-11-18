package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.product.ProductDTO;

import java.util.List;
import java.math.BigDecimal;


public interface ProductService {

    ApiResponse<ProductDTO> createProduct(ProductDTO productDTO);
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(long product_id);
    List<ProductDTO> getAllByBusinessId(long business_id);

}
