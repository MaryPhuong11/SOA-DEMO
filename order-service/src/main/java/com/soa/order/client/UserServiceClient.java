package com.soa.order.client;

import com.soa.order.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserServiceClient {
    private final RestTemplate restTemplate;

    public UserDTO getUserById(Long userId) {
        try {
            String url = "http://user-service/api/users/" + userId;
            return restTemplate.getForObject(url, UserDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy thông tin user: " + e.getMessage());
        }
    }
}




