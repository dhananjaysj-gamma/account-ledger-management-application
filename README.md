# 🧾 Account-Ledger Management System

## 📖 Overview

A ledger management application where users can manage their accounts, handle internal and external transactions,. Built with Spring Boot and follows modern architectural and security best practices. It follows a **technology service provider model**, where:

- **Tenants (banks)** are onboarded into the system from the backend.
- **Users** are created under each tenant and can hold multiple **ledgers** (accounts).
- **Users** can create account using the Id recieved by the tenant(banks).
- **Ledgers** are created and associated with the user.
- **Transactions** are recorded between ledgers, allowing fund transfers between internal and external ledgers.
- The system ensures proper **authentication via JWT**, and **soft deletes** for audit safety.

This project includes clean modular layers (Controller → Service → Repository), and follows best practices in exception handling, DTO usage, and security configuration.

---

## 📚 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Testing](#-testing)
- [Database Schema](#-database-schema)
- [Security](#-security)
- [Complete Documentation](#-complete-documentation)

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
   git clone https://github.com/dhananjaysj-gamma/account-ledger-management-application.git
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

## 🧩 Database Schema

### ⚙️ Entity Relationships

- **Tenant ↔ Users**: One Tenant has many Users  
- **User ↔ Ledger**: One User has many Ledgers  
- **Ledger ↔ Transactions**: One Ledger has many Transactions

---

### 🔐 Security

- ✅ **JWT-based Authentication**  
- ✅ **Role-based Authorization**  
- ✅ **Soft deletes for Users**  
- ✅ **Exception Handling and Validation**

### 📄 Complete Documentation

For detailed technical documentation, API contracts, flow diagram, architecture diagram, class diagram, and database schema:

👉 [View Full Documentation](https://your-domain.com/docs)  
