package com.soa.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @Valid
    @NotNull(message = "Danh sách sản phẩm không được để trống")
    private List<OrderItemRequest> items;
}



