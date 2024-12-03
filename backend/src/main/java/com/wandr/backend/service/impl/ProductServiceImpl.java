package com.wandr.backend.service.impl;

import com.wandr.backend.dao.BusinessDAO;
import com.wandr.backend.dao.ProductDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.product.ProductDTO;
import com.wandr.backend.dto.product.UpdateProductDTO;
import com.wandr.backend.dto.recommendation.RecommendedPlaceDTO;
import com.wandr.backend.dto.traveller.TravellerDTO;
import com.wandr.backend.dto.traveller.UpdateProfileDTO;
import com.wandr.backend.entity.Business;
import com.wandr.backend.entity.Product;
import com.wandr.backend.entity.Traveller;
import com.wandr.backend.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;
    private final BusinessDAO businessDAO;

    @Autowired
    public ProductServiceImpl(ProductDAO productDAO, BusinessDAO businessDAO) {
        this.productDAO = productDAO;
        this.businessDAO = businessDAO;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public ApiResponse<String> createProduct(ProductDTO productDTO) {

        double reservation_percentage = 0.3;

        // Fetch the number of existing products for the business
        List<Product> existingProducts = productDAO.findAllByBusinessId(productDTO.getBusiness_id());
        int currentProductCount = existingProducts != null ? existingProducts.size() : 0;
        logger.info("Current product count: {}", currentProductCount);

        // Fetch the business plan
        Business business = businessDAO.findById(productDTO.getBusiness_id());
        if (business == null) {
            return new ApiResponse<>(false, 404, "Business not found");
        }
        int planId = business.getPlanId(); // Fetch the plan ID for the business

        //if plan id is null error msg
        if (planId == 0) {
            return new ApiResponse<>(false, 400, "You haven't subscribed to any plan yet. Please subscribe to a plan to add products.", "LIMIT_EXCEEDED");
        }

        // Determine the product limit based on the plan
        int productLimit = getProductLimitByPlan(planId);

        // Check if the product limit is reached
        if (productLimit != -1 && currentProductCount >= productLimit) {
            return new ApiResponse<>(false, 400, "Product limit reached for your current plan. Upgrade your plan to add more products.", "LIMIT_EXCEEDED");
        }

        Product newProduct = new Product();
        newProduct.setName(productDTO.getName());
        newProduct.setDescription(productDTO.getDescription());
        newProduct.setPrice(productDTO.getPrice());
        newProduct.setQuantity(productDTO.getQuantity());
        newProduct.setBusiness_id(productDTO.getBusiness_id());
        // Convert the reservation percentage and price to BigDecimal
        BigDecimal reservationPercentage = BigDecimal.valueOf(reservation_percentage);
        newProduct.setReservation_payment(productDTO.getPrice().multiply(reservationPercentage));
        newProduct.setImage(productDTO.getImage());
        productDAO.createProduct(newProduct);

        return new ApiResponse<>(true, 200, "Product created successfully", null);
    }

    private int getProductLimitByPlan(int planId) {
        switch (planId) {
            case 1: // Basic Plan
                return 5;
            case 2: // Standard Plan
                return 15;
            case 3: // Premium Plan
                return -1; // Unlimited products
            default:
                throw new IllegalArgumentException("Unknown business plan.");
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        return products.stream()
                .map(product -> {
                    ProductDTO productDTO = productToProductDTO(product);
                    return productDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(long product_id) {
        Product product = productDAO.findById(product_id);
        if (product == null) {
            logger.warn("Product not found with id: {}", product_id);
            throw new IllegalArgumentException("Product not found with id: " + product_id);
        }
        ProductDTO productDTO = productToProductDTO(product);
        return productDTO;
    }

    @Override
    public List<ProductDTO> getAllByBusinessId(long business_id) {
        List<Product> products = productDAO.findAllByBusinessId(business_id);
        return products.stream()
                .map(product -> {
                    ProductDTO productDTO = productToProductDTO(product);
                    return productDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse<ProductDTO> updateProduct(Long product_id, UpdateProductDTO request){
        Product existingProduct = productDAO.findById(product_id);

        double reservation_percentage = 0.3;

        if (existingProduct == null) {
            return new ApiResponse<>(false, 404, "Product not found");
        }
        if (request.getName() != null) {
            existingProduct.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingProduct.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            existingProduct.setPrice(request.getPrice());
            existingProduct.setReservation_payment(request.getPrice().multiply(BigDecimal.valueOf(reservation_percentage)));
        }
        if (request.getQuantity() != null) {
            existingProduct.setQuantity(request.getQuantity());
        }

        productDAO.updateProduct(existingProduct);
        ProductDTO updatedProduct = getProductById(product_id);

        //return updated traveller details
        return new ApiResponse<>(true, 200, "Product updated successfully", updatedProduct);
    }

    @Override
    public ApiResponse<ProductDTO> updateProductQuantity(Long product_id, int quantity) {
        // Find the existing product by its ID
        Product existingProduct = productDAO.findById(product_id);
        if (existingProduct == null) {
            // Return a 404 response if the product does not exist
            return new ApiResponse<>(false, 404, "Product not found");
        }

        if (quantity < 0) {
            // Return a 400 response if the quantity is invalid
            return new ApiResponse<>(false, 400, "Quantity cannot be negative");
        }

        // Update the quantity in the database
        productDAO.updateProductQuantity(product_id, quantity);

        // Retrieve the updated product details
        ProductDTO updatedProduct = getProductById(product_id);

        // Return a success response with the updated product details
        return new ApiResponse<>(true, 200, "Product quantity updated successfully", updatedProduct);
    }

    public ApiResponse<Void> deleteProduct(long product_id){
        if (productDAO.findById(product_id) == null) {
            return new ApiResponse<>(false, 404, "Product not found");
        }
        try {
            productDAO.deleteProduct(product_id);
            return new ApiResponse<>(true, 200, "Product deleted successfully");
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Error deleting product");
        }
    }




    private ProductDTO productToProductDTO(Product product) {

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProduct_id(product.getProduct_id());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setBusiness_id(product.getBusiness_id());
        productDTO.setSales_count(product.getSales_count());
        productDTO.setReservation_payment(product.getReservation_payment());
        productDTO.setImage(product.getImage());

        return productDTO;
    }

}
