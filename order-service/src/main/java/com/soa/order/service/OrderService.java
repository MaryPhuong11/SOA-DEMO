package com.soa.order.service;

import com.soa.order.client.ProductServiceClient;
import com.soa.order.client.UserServiceClient;
import com.soa.order.dto.*;
import com.soa.order.model.Order;
import com.soa.order.model.OrderItem;
import com.soa.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        return toDTO(order);
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        // Kiểm tra user tồn tại
        UserDTO user = userServiceClient.getUserById(request.getUserId());
        log.info("User found: {}", user.getName());

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Xử lý từng item trong đơn hàng
        for (OrderItemRequest itemRequest : request.getItems()) {
            // Lấy thông tin sản phẩm từ Product Service
            ProductDTO product = productServiceClient.getProductById(itemRequest.getProductId());
            log.info("Product found: {} - Stock: {}", product.getName(), product.getStock());

            // Kiểm tra số lượng tồn kho
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException(
                    String.format("Sản phẩm '%s' không đủ số lượng. Hiện có: %d, yêu cầu: %d",
                            product.getName(), product.getStock(), itemRequest.getQuantity()));
            }

            // Tạo order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            
            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());

            // Cập nhật stock trong Product Service
            productServiceClient.updateStock(product.getId(), itemRequest.getQuantity());
            log.info("Updated stock for product {}: -{}", product.getName(), itemRequest.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", order.getId());

        return toDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        
        order.setStatus(status);
        order = orderRepository.save(order);
        log.info("Order {} status updated to {}", id, status);
        
        return toDTO(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Không thể hủy đơn hàng đã được giao");
        }
        
        // Hoàn lại số lượng sản phẩm vào kho (trong thực tế sẽ gọi API để tăng stock)
        // Ở đây bỏ qua để đơn giản
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order {} cancelled", id);
    }

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> items = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getUserId(),
                items,
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}




