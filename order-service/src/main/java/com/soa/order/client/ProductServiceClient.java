package com.soa.order.client;

import com.soa.order.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ProductServiceClient {
    private final RestTemplate restTemplate;

    public ProductDTO getProductById(Long productId) {
        try {
            String url = "http://product-service/api/products/" + productId;
            return restTemplate.getForObject(url, ProductDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy thông tin sản phẩm: " + e.getMessage());
        }
    }

    public void updateStock(Long productId, Integer quantity) {
        try {
            String url = "http://product-service/api/products/" + productId + "/stock?quantity=" + quantity;
            restTemplate.put(url, null);
        } catch (Exception e) {
            throw new RuntimeException("Không thể cập nhật số lượng sản phẩm: " + e.getMessage());
        }
    }
}

