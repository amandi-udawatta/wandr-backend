package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.product.ProductDTO;
import com.wandr.backend.dto.product.UpdateProductDTO;
import com.wandr.backend.dto.traveller.TravellerDTO;
import com.wandr.backend.dto.traveller.UpdateProfileDTO;

import java.util.List;
import java.math.BigDecimal;


public interface ProductService {

    ApiResponse<ProductDTO> createProduct(ProductDTO productDTO);
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(long product_id);
    List<ProductDTO> getAllByBusinessId(long business_id);
    ApiResponse<ProductDTO> updateProduct(Long product_id, UpdateProductDTO request);
    ApiResponse<ProductDTO> updateProductQuantity(Long product_id, int quantity);
    ApiResponse<Void> deleteProduct(long product_id);
}
