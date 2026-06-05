# 🚌 Bus App

## 📌 Introduction

Bus App is a mobile application that helps users search for bus routes and book tickets.
The system is built using a client-server architecture, including a mobile app and a backend API.

---

## 🛠 Technologies

* **Frontend:** Android (Kotlin)
* **Backend:** Spring Boot (Java)
* **Database:** MySQL
* **API:** RESTful

---

## 📂 Project Structure

```
Bus-app/
├── backend/
│   └── bus-api/           # Spring Boot Backend API
│       └── src/
│           ├── main/
│           │   ├── java/com/busapp/buss_api/
│           │   │   ├── controller/    # REST Controllers
│           │   │   ├── service/        # Business Logic
│           │   │   ├── repository/     # Data Access
│           │   │   ├── entity/         # JPA Entities
│           │   │   ├── dto/           # Data Transfer Objects
│           │   │   └── security/      # JWT & Security
│           │   └── resources/
│           │       ├── application.yaml
│           │       ├── schema.sql      # Database Schema
│           │       └── data.sql        # Sample Data
│           └── pom.xml
│
├── BusApp_API.postman_collection.json   # Postman Test Collection
├── API_DOCUMENTATION.md                 # Full API Documentation
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Setup Database
```bash
# Create database
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS busapp_db;"
```

### Run Backend
```bash
cd backend/bus-api
./mvnw spring-boot:run
```

Backend will start at: **http://localhost:8080**

---

## 📚 API Documentation

### Swagger UI
Interactive API documentation available at:
```
http://localhost:8080/swagger-ui.html
```

### Quick Links
- **API Documentation:** [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
- **Postman Collection:** [BusApp_API.postman_collection.json](./BusApp_API.postman_collection.json)

### Authentication
- JWT-based authentication
- Token expires in 24 hours
- Include token in header: `Authorization: Bearer <token>`

### API Overview

| Module | Endpoints | Description |
|--------|-----------|-------------|
| Auth | 3 | Register, Login, Get Profile |
| Trips | 8 | Search, Details, Seats |
| Bookings | 6 | Create, Cancel, Confirm |
| Reviews | 4 | CRUD for reviews |
| Operators | 6 | Bus company management |
| Routes | 6 | Route management |
| Seats | 4 | Seat map & locking |

### Public APIs (No Login Required)
- Search trips
- View trip details
- View seat maps
- View operators
- View routes

### Protected APIs (Login Required)
- Book tickets
- Cancel bookings
- Create reviews
- Lock/unlock seats

---

## 🔐 Authorization

| Role | Description |
|------|-------------|
| `USER` | Regular users - book tickets, review trips |
| `OPERATOR` | Bus companies - manage trips, confirm bookings |
| `ADMIN` | System administrators - full access |

---

## 🗄️ Database Schema

The database schema is automatically created based on JPA entities.
For manual setup, see `backend/bus-api/src/main/resources/schema.sql`.

---

## 📱 Mobile App

The Android mobile app is located in the parent directory for bus ticket booking functionality.
