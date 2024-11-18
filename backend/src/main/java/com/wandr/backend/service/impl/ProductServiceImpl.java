package com.wandr.backend.service.impl;

import com.wandr.backend.dao.ProductDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.product.ProductDTO;
import com.wandr.backend.entity.Product;
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

    @Autowired
    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public ApiResponse<ProductDTO> createProduct(ProductDTO productDTO) {
        Product newProduct = new Product();
        newProduct.setName(productDTO.getName());
        newProduct.setDescription(productDTO.getDescription());
        newProduct.setPrice(productDTO.getPrice());
        newProduct.setQuantity(productDTO.getQuantity());
        newProduct.setBusiness_id(productDTO.getBusiness_id());
//        newProduct.setImage(productDTO.getImage());
        productDAO.createProduct(newProduct);

        ProductDTO product = productToProductDTO(newProduct);

        return new ApiResponse<>(true, 200, "Product created successfully", product);
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

    private ProductDTO productToProductDTO(Product product) {

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProduct_id(product.getProduct_id());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setBusiness_id(product.getBusiness_id());
        productDTO.setSales_count(product.getSales_count());
//        productDTO.setImage(product.getImage());

        return productDTO;
    }

}
