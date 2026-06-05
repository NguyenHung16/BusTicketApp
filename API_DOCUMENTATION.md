# Bus App API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

### JWT Token
- Header: `Authorization: Bearer <token>`
- Token có hiệu lực trong **24 giờ**
- Token được trả về khi đăng nhập thành công

### Roles (Vai trò)

| Role | Mô tả | Quyền hạn |
|------|--------|-----------|
| `USER` | Người dùng thông thường | Đặt vé, xem vé, hủy vé, đánh giá |
| `OPERATOR` | Nhà xe | Quản lý chuyến xe, xác nhận vé của mình |
| `ADMIN` | Quản trị viên | Toàn quyền hệ thống |

---

## Public Endpoints (Không cần đăng nhập)

### Authentication

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| POST | `/auth/register` | Đăng ký tài khoản mới |
| POST | `/auth/login` | Đăng nhập |

### Provinces (Tỉnh/Thành phố)

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/provinces` | Lấy danh sách tất cả tỉnh/thành |

### Routes (Tuyến đường)

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/routes` | Lấy danh sách tất cả tuyến đường |
| GET | `/routes/{id}` | Lấy thông tin tuyến đường theo ID |
| GET | `/routes/popular` | Lấy danh sách tuyến phổ biến |
| GET | `/routes/search?departure=X&destination=Y` | Tìm tuyến đường theo tỉnh |
| GET | `/routes/{routeId}/pickup-dropoff-points` | Lấy điểm đón/trả khách |

### Operators (Nhà xe)

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/operators` | Lấy danh sách nhà xe (phân trang) |
| GET | `/operators/top-rated` | Lấy nhà xe đánh giá cao |
| GET | `/operators/{id}` | Lấy thông tin nhà xe |
| GET | `/operators/{id}/reviews` | Lấy đánh giá của nhà xe |

### Trips (Chuyến xe)

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/trips/search` | Tìm kiếm chuyến xe |
| GET | `/trips/{id}` | Lấy chi tiết chuyến xe |
| GET | `/trips/{id}/pickup-points` | Lấy điểm đón |
| GET | `/trips/{id}/dropoff-points` | Lấy điểm trả |
| GET | `/trips/{id}/seats` | Lấy sơ đồ ghế |

### Seats (Ghế)

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/seats/trip/{tripId}` | Lấy sơ đồ ghế của chuyến |
| GET | `/seats/trip/{tripId}/available` | Lấy danh sách ghế trống |

### Bookings (Đặt vé)

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/bookings/code/{bookingCode}` | Xem thông tin vé theo mã |

### Reviews (Đánh giá)

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/reviews/trip/{tripId}` | Lấy đánh giá theo chuyến xe |
| GET | `/reviews/operator/{operatorId}` | Lấy đánh giá theo nhà xe |

---

## Protected Endpoints (Cần đăng nhập)

### Authentication

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|--------|
| GET | `/auth/me` | USER, OPERATOR, ADMIN | Lấy thông tin người dùng hiện tại |

### Bookings (Đặt vé)

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|--------|
| POST | `/bookings` | USER+ | Tạo đặt vé mới |
| GET | `/bookings/my` | USER+ | Lấy danh sách vé của tôi |
| POST | `/bookings/{bookingCode}/cancel` | USER+ | Hủy đặt vé (chỉ chủ vé) |
| POST | `/bookings/{bookingCode}/confirm` | OPERATOR, ADMIN | Xác nhận đặt vé |
| PATCH | `/bookings/{bookingCode}/payment` | OPERATOR, ADMIN | Cập nhật thanh toán |

### Seats (Ghế)

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|--------|
| POST | `/seats/lock/{tripId}/{seatCode}` | USER+ | Khóa ghế (giữ chỗ) |
| DELETE | `/seats/lock/{tripId}/{seatCode}` | USER+ | Mở khóa ghế |

### Reviews (Đánh giá)

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|--------|
| POST | `/reviews` | USER+ | Tạo đánh giá |
| DELETE | `/reviews/{reviewId}` | USER+ | Xóa đánh giá (chỉ người tạo) |

---

## Admin Only Endpoints

### Operators (Nhà xe)

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|--------|
| POST | `/operators` | OPERATOR, ADMIN | Tạo nhà xe mới |
| PUT | `/operators/{id}` | OPERATOR, ADMIN | Cập nhật nhà xe |
| DELETE | `/operators/{id}` | ADMIN | Xóa nhà xe |

### Routes (Tuyến đường)

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|--------|
| POST | `/routes` | OPERATOR, ADMIN | Tạo tuyến đường mới |
| PUT | `/routes/{id}` | OPERATOR, ADMIN | Cập nhật tuyến đường |

### Trips (Chuyến xe)

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|--------|
| POST | `/trips` | OPERATOR, ADMIN | Tạo chuyến xe mới |
| PUT | `/trips/{id}` | OPERATOR, ADMIN | Cập nhật chuyến xe |
| GET | `/trips/operator/{operatorId}` | OPERATOR, ADMIN | Lấy chuyến xe của nhà xe |

---

## API Request/Response Examples

### 1. Register (Đăng ký)

**Request:**
```http
POST /api/auth/register
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "123456",
    "fullName": "Nguyen Van A",
    "phone": "0912345678"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Đăng ký thành công",
    "data": {
        "token": "eyJhbGciOiJIUzI1...",
        "userId": 1,
        "email": "user@example.com",
        "fullName": "Nguyen Van A",
        "role": "USER"
    }
}
```

### 2. Login (Đăng nhập)

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "123456"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Đăng nhập thành công",
    "data": {
        "token": "eyJhbGciOiJIUzI1...",
        "userId": 1,
        "email": "user@example.com",
        "fullName": "Nguyen Van A",
        "role": "USER"
    }
}
```

### 3. Search Trips (Tìm chuyến xe)

**Request:**
```http
GET /api/trips/search?departure=ho-chi-minh&destination=ha-noi&departureDate=2026-04-10
```

**Response:**
```json
{
    "success": true,
    "data": {
        "content": [
            {
                "id": 1,
                "operatorName": "Phong Ha Express",
                "departureProvince": "Ho Chi Minh",
                "destinationProvince": "Ha Noi",
                "departureDate": "2026-04-10",
                "departureTime": "08:00:00",
                "arrivalTime": "20:00:00",
                "price": 350000,
                "availableSeats": 20,
                "totalSeats": 40,
                "vehicleTypeName": "Giường nằm 40 chỗ",
                "amenities": "WiFi,Điều hòa,WiFi,Nước uống"
            }
        ],
        "page": 0,
        "size": 10,
        "totalElements": 5,
        "totalPages": 1,
        "first": true,
        "last": true
    }
}
```

### 4. Create Booking (Tạo đặt vé)

**Request:**
```http
POST /api/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
    "tripId": 1,
    "seatId": 5,
    "pickupPointId": 1,
    "dropoffPointId": 3,
    "passengerName": "Nguyen Van A",
    "passengerPhone": "0912345678",
    "passengerEmail": "user@example.com",
    "paymentMethod": "cod",
    "ticketType": "one_way"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Đặt vé thành công. Mã vé: BUS12345678",
    "data": {
        "id": 1,
        "bookingCode": "BUS12345678",
        "bookingStatus": "pending",
        "paymentStatus": "pending",
        "ticketPrice": 350000
    }
}
```

### 5. Lock Seat (Khóa ghế)

**Request:**
```http
POST /api/seats/lock/1/A1
Authorization: Bearer <token>
```

**Response:**
```json
{
    "success": true,
    "message": "Khóa ghế thành công",
    "data": {
        "id": 1,
        "tripId": 1,
        "seatCode": "A1",
        "status": "locked",
        "lockedBy": 1
    }
}
```

### 6. Unlock Seat (Mở khóa ghế)

**Request:**
```http
DELETE /api/seats/lock/1/A1
Authorization: Bearer <token>
```

**Response:**
```json
{
    "success": true,
    "message": "Mở khóa ghế thành công",
    "data": {
        "id": 1,
        "tripId": 1,
        "seatCode": "A1",
        "status": "available",
        "lockedBy": null
    }
}
```

---

## Error Response Format

```json
{
    "success": false,
    "message": "Mô tả lỗi",
    "errors": [
        {
            "field": "email",
            "message": "Email không hợp lệ"
        }
    ],
    "timestamp": "2026-04-06T12:00:00",
    "path": "/api/auth/register"
}
```

### Common Error Codes

| HTTP Code | Mô tả |
|-----------|--------|
| 400 | Bad Request - Dữ liệu không hợp lệ |
| 401 | Unauthorized - Chưa đăng nhập |
| 403 | Forbidden - Không có quyền truy cập |
| 404 | Not Found - Không tìm thấy tài nguyên |
| 409 | Conflict - Xung đột dữ liệu |
| 500 | Internal Server Error - Lỗi hệ thống |

---

## Business Rules

### Booking (Đặt vé)
- Thời hạn hủy vé: **2 giờ** trước giờ khởi hành
- Ghế phải ở trạng thái `available` hoặc `locked` mới có thể đặt
- Mỗi người dùng chỉ có thể đánh giá 1 chuyến xe 1 lần

### Seat Locking (Khóa ghế)
- Thời gian khóa ghế: **5 phút** (cần cải thiện thêm TTL)
- Chỉ người khóa ghế mới có thể mở khóa
- Ghế đã khóa không thể đặt bởi người khác

### Operator Authorization (Phân quyền nhà xe)
- OPERATOR chỉ có thể quản lý chuyến xe và xác nhận vé của mình
- ADMIN có quyền quản lý tất cả

---

## Postman Collection

Import file `BusApp_API.postman_collection.json` vào Postman để test API.

### Test Flow:
1. Register → Login (lấy token)
2. Search Trips → Get Seat Map
3. Lock Seat → Create Booking
4. Confirm Booking (với token OPERATOR)
5. Cancel Booking (với token USER)

---

## Development

### Run Backend
```bash
cd backend/bus-api
./mvnw spring-boot:run
```

### API Documentation (Swagger)
```
http://localhost:8080/swagger-ui.html
```

### Database (MySQL)
- Database: `busapp_db`
- Username: `root`
- Password: `root`
- Port: `3306`
