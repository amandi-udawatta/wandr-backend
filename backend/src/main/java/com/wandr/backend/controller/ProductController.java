package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.product.ProductDTO;
import com.wandr.backend.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(TouristPlanController.class);

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            return ResponseEntity.ok(productService.createProduct(productDTO));
        } catch (Exception e) {
            logger.error("Error creating the product", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error creating the product", null));
        }
    }

    @GetMapping
    public ApiResponse<List<ProductDTO>> getAllProducts() {
        try {
            List<ProductDTO> products = productService.getAllProducts();
            logger.info("Successfully retrieved all products");
            return new ApiResponse<>(true, HttpStatus.OK.value(), "All products retrieved successfully", products);
        } catch (Exception e) {
            logger.error("Error retrieving all products: {}", e.getMessage(), e);
            return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving all products", null);
        }
    }


    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long productId) {
        try {
            ProductDTO product = productService.getProductById(productId);
            if (product == null) {
                logger.error("Product with id {} not found", productId);
                return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.NOT_FOUND.value(), "No product exists with the id", null));
            }
            logger.info("Successfully retrieved product with id: {}", productId);
            return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Successfully retrieved the product", product));

        } catch (Exception e) {
            logger.error("Error retrieving product with id {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error finding the product", null));

        }
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByBusinessId(@PathVariable Long businessId) {
        try {
            List<ProductDTO> products = productService.getAllByBusinessId(businessId);
            if (products.isEmpty()) {
                logger.info("No products found for business id: {}", businessId);
                return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "No products found for the business", null));
            }
            logger.info("Successfully retrieved products for business id: {}", businessId);
            return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Successfully retrieved the products", products));

        } catch (Exception e) {
            logger.error("Error retrieving products for business id {}: {}", businessId, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error finding the products", null));

        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Product Controller is working");
    }

}

