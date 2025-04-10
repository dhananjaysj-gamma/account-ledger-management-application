# 🧾 Account-Ledger Management System

A ledger management application where users can manage their accounts, handle internal and external transactions,. Built with Spring Boot and follows modern architectural and security best practices.

---

## 📚 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Testing](#-testing)
- [Database Schema](#-database-schema)
- [Security](#-security)
- [Future Enhancements](#-future-enhancements)
- [Contributors](#-contributors)

---

## 🚀 Features

- User registration, authentication using JWT.
- Account and ledger creation and management.
- Support for internal and external transactions.

---

## 🛠 Tech Stack

- **Backend:** Java 21, Spring Boot
- **Security:** Spring Security, JWT
- **Database:** PostgreSQL / H2 (for testing)
- **ORM:** Spring Data JPA, Hibernate
- **API Testing:** Postman, JUnit + Mockito + MockMvc
- **Documentation:** OpenAPI

---

## ⚙️ Getting Started

### 📌 Prerequisites

- Java 21
- Maven
- PostgreSQL (or H2 for development)
- IntelliJ / VS Code
- Postman (for testing APIs)

---

### 🚀 Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/dhananjaysj-gamma/account-ledger-management.git
   cd account-ledger-management

---

## 📩 API Endpoints

### 👤 Auth & User

| Method | Endpoint             | Description                    |
|--------|----------------------|--------------------------------|
| POST   | `/user/register`     | Register a new user            |
| POST   | `/user/login`        | Authenticate and receive JWT   |
| GET    | `/user/{userId}`     | Get user account info          |
| PATCH  | `/user/{userId}`     | Partially update user info     |
| DELETE | `/user/{userId}`     | Soft delete a user             |

---

### 📒 Ledger

| Method | Endpoint                                 | Description                      |
|--------|------------------------------------------|----------------------------------|         
| POST   | `/ledger/user/{userId}`                  | Create a new ledger              |
| GET    | `/ledger/{ledgerId}`                     | Get ledger details               |
| GET    | `/ledger/transaction/history?ledgerId`   | Get ledger transaction details   |

---

### 💸 Transactions

| Method | Endpoint                   | Description                        |
|--------|----------------------------|------------------------------------|
| POST   | `/transaction/transfer`   | Transfer funds between ledgers     |


---

## ✅ Testing

This project includes **unit tests** using:

- ✅ JUnit 5  
- ✅ Mockito  
- ✅ MockMvc  

### 🔍 Test Coverage

- `UserController`: register, login, CRUD
- Ledger creation & linking
- Fund transfer logic
- Error handling (e.g., Not Found, Unauthorizedn etc..)

### 🧪 Run Tests

```bash
./mvnw test
   
