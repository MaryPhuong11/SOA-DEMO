# 🚀 Quick Start Guide

Hướng dẫn nhanh để chạy dự án SOA Demo

## Bước 1: Build Project

```bash
mvn clean install
```

## Bước 2: Khởi động Services (theo thứ tự)

### Terminal 1 - Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
Mở http://localhost:8761 để kiểm tra

### Terminal 2 - User Service
```bash
cd user-service
mvn spring-boot:run
```

### Terminal 3 - Product Service
```bash
cd product-service
mvn spring-boot:run
```

### Terminal 4 - Order Service
```bash
cd order-service
mvn spring-boot:run
```

### Terminal 5 - API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

## Bước 3: Test API

### Tạo User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Nguyễn Văn A","email":"test@example.com","address":"123 ABC","phone":"0123456789"}'
```

### Tạo Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","description":"Laptop cao cấp","price":10000000,"stock":10,"category":"Electronics"}'
```

### Tạo Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"items":[{"productId":1,"quantity":2}]}'
```

## 📍 Ports

- Eureka: 8761
- API Gateway: 8080
- User Service: 8081
- Product Service: 8082
- Order Service: 8083




