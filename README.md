# SOA Demo Project - Service-Oriented Architecture với Spring Boot

## 📋 Tổng quan dự án

Dự án demo minh họa kiến trúc SOA (Service-Oriented Architecture) sử dụng Spring Boot và Spring Cloud. Hệ thống bao gồm:

- **Eureka Server**: Service Discovery - đăng ký và tìm kiếm các services
- **API Gateway**: Điểm vào duy nhất cho tất cả các requests từ client
- **User Service**: Quản lý thông tin người dùng
- **Product Service**: Quản lý sản phẩm
- **Order Service**: Quản lý đơn hàng (giao tiếp với User và Product services)

## 🏗️ Kiến trúc hệ thống

```
                    ┌─────────────────┐
                    │   API Gateway    │
                    │    (Port 8080)   │
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
       ┌──────▼──────┐ ┌─────▼─────┐ ┌─────▼──────┐
       │User Service │ │Product    │ │Order       │
       │(Port 8081)  │ │Service    │ │Service     │
       │             │ │(Port 8082)│ │(Port 8083) │
       └─────────────┘ └───────────┘ └────────────┘
              │              │              │
              └──────────────┼──────────────┘
                             │
                    ┌────────▼────────┐
                    │ Eureka Server   │
                    │  (Port 8761)    │
                    └─────────────────┘
```

## 🔧 Yêu cầu hệ thống

- **Java**: JDK 17 hoặc cao hơn
- **Maven**: 3.6+ (để build project)
- **IDE**: IntelliJ IDEA / Eclipse / VS Code (khuyến nghị)

## 📦 Cấu trúc dự án

```
soa-demo/
├── pom.xml                    # Parent POM
├── eureka-server/            # Service Discovery Server
├── api-gateway/              # API Gateway
├── user-service/             # User Management Service
├── product-service/          # Product Management Service
└── order-service/            # Order Management Service
```

## 🚀 Hướng dẫn chạy dự án

### Bước 1: Build toàn bộ dự án

Mở terminal/command prompt tại thư mục gốc của dự án và chạy:

```bash
mvn clean install
```

### Bước 2: Khởi động các services theo thứ tự

**⚠️ QUAN TRỌNG: Phải khởi động đúng thứ tự sau đây!**

#### 2.1. Khởi động Eureka Server (BẮT BUỘC phải chạy đầu tiên)

```bash
cd eureka-server
mvn spring-boot:run
```

Hoặc chạy class `EurekaServerApplication` từ IDE.

**Kiểm tra**: Mở trình duyệt và truy cập http://localhost:8761
- Bạn sẽ thấy Eureka Dashboard
- Ban đầu sẽ không có services nào được đăng ký

#### 2.2. Khởi động User Service

Mở terminal mới:

```bash
cd user-service
mvn spring-boot:run
```

Hoặc chạy class `UserServiceApplication` từ IDE.

**Kiểm tra**: 
- Service sẽ tự động đăng ký với Eureka
- API: http://localhost:8081/api/users
- H2 Console: http://localhost:8081/h2-console

#### 2.3. Khởi động Product Service

Mở terminal mới:

```bash
cd product-service
mvn spring-boot:run
```

Hoặc chạy class `ProductServiceApplication` từ IDE.

**Kiểm tra**: 
- API: http://localhost:8082/api/products
- H2 Console: http://localhost:8082/h2-console

#### 2.4. Khởi động Order Service

Mở terminal mới:

```bash
cd order-service
mvn spring-boot:run
```

Hoặc chạy class `OrderServiceApplication` từ IDE.

**Kiểm tra**: 
- API: http://localhost:8083/api/orders
- H2 Console: http://localhost:8083/h2-console

#### 2.5. Khởi động API Gateway

Mở terminal mới:

```bash
cd api-gateway
mvn spring-boot:run
```

Hoặc chạy class `ApiGatewayApplication` từ IDE.

**Kiểm tra**: 
- API Gateway: http://localhost:8080
- Các requests sẽ được định tuyến đến các services tương ứng

### Bước 3: Kiểm tra Eureka Dashboard

Sau khi khởi động tất cả services, truy cập lại http://localhost:8761

Bạn sẽ thấy tất cả services đã được đăng ký:
- **API-GATEWAY** (1 instance)
- **USER-SERVICE** (1 instance)
- **PRODUCT-SERVICE** (1 instance)
- **ORDER-SERVICE** (1 instance)

## 🧪 Test các API

### 1. Tạo User (qua API Gateway)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "address": "123 Đường ABC, Hà Nội",
    "phone": "0123456789"
  }'
```

### 2. Lấy danh sách Users

```bash
curl http://localhost:8080/api/users
```

### 3. Tạo Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 15",
    "description": "Laptop cao cấp với màn hình 15 inch",
    "price": 25000000,
    "stock": 10,
    "category": "Electronics"
  }'
```

### 4. Tạo Product thứ 2

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Điện thoại thông minh cao cấp",
    "price": 30000000,
    "stock": 5,
    "category": "Electronics"
  }'
```

### 5. Lấy danh sách Products

```bash
curl http://localhost:8080/api/products
```

### 6. Tạo Order (quan trọng - minh họa service-to-service communication)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }'
```

**Giải thích**: Order Service sẽ:
1. Gọi User Service để kiểm tra user có tồn tại không
2. Gọi Product Service để lấy thông tin sản phẩm và kiểm tra stock
3. Tạo đơn hàng và cập nhật số lượng tồn kho

### 7. Lấy đơn hàng theo User ID

```bash
curl http://localhost:8080/api/orders/user/1
```

### 8. Cập nhật trạng thái đơn hàng

```bash
curl -X PUT "http://localhost:8080/api/orders/1/status?status=CONFIRMED"
```

## 📊 Ports của các services

| Service | Port | URL |
|---------|------|-----|
| Eureka Server | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| User Service | 8081 | http://localhost:8081 |
| Product Service | 8082 | http://localhost:8082 |
| Order Service | 8083 | http://localhost:8083 |

## 🔍 Các tính năng SOA được minh họa

### 1. Service Discovery (Eureka)
- Tự động đăng ký và phát hiện services
- Load balancing tự động
- Health checks

### 2. API Gateway Pattern
- Single entry point
- Route requests đến các services
- Load balancing

### 3. Service-to-Service Communication
- Order Service gọi User Service và Product Service
- Sử dụng RestTemplate với @LoadBalanced
- Service discovery qua Eureka (sử dụng tên service thay vì IP)

### 4. Loosely Coupled Services
- Mỗi service có database riêng (H2 in-memory)
- Services độc lập và có thể deploy riêng biệt

### 5. RESTful API
- Tất cả services expose REST APIs
- Standard HTTP methods (GET, POST, PUT, DELETE)

## 🗄️ Database

Tất cả services sử dụng **H2 In-Memory Database** cho mục đích demo:
- Tự động tạo schema khi khởi động
- Dữ liệu sẽ mất khi restart service
- Có thể truy cập H2 Console qua: `http://localhost:PORT/h2-console`
  - JDBC URL: `jdbc:h2:mem:servicedb` (thay servicedb bằng userdb, productdb, orderdb)
  - Username: `sa`
  - Password: (để trống)

## 🐛 Xử lý lỗi thường gặp

### 1. Port đã được sử dụng

**Lỗi**: `Port 8080 is already in use`

**Giải pháp**: 
- Tắt ứng dụng đang sử dụng port đó
- Hoặc thay đổi port trong `application.yml`

### 2. Services không đăng ký được với Eureka

**Lỗi**: Services không xuất hiện trong Eureka Dashboard

**Giải pháp**:
- Đảm bảo Eureka Server đã chạy trước
- Kiểm tra `defaultZone` trong `application.yml` của từng service
- Đợi vài giây để services đăng ký (thường mất 30-60 giây)

### 3. Order Service không thể gọi được User/Product Service

**Lỗi**: `Connection refused` hoặc `Unknown host: user-service`

**Giải pháp**:
- Kiểm tra tất cả services đã đăng ký với Eureka chưa
- Đảm bảo có `@LoadBalanced` cho RestTemplate
- Kiểm tra service name phải đúng với tên trong Eureka

### 4. Lỗi compile Lombok

**Lỗi**: `Cannot find symbol` cho các getter/setter

**Giải pháp**:
- Cài đặt Lombok plugin trong IDE
- Enable annotation processing trong IDE settings

## 📚 Tài liệu tham khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Netflix Eureka](https://github.com/Netflix/eureka)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

## 👨‍💻 Tác giả

Dự án demo SOA Architecture với Spring Boot

## 📝 License

Dự án demo - Sử dụng cho mục đích học tập

