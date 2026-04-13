# FaceID API

Enterprise-grade biometric authentication API built with **Java 17**, **Spring Boot 3**, and **Deep Java Library (DJL)** with a FaceNet PyTorch model.

## Architecture

- **Back-end**: Spring Boot 3.3.5, Spring Security 6 (JWT + BCrypt), Spring Data JPA (MySQL)
- **AI Engine**: DJL with PyTorch engine loading a TorchScript FaceNet model (512-dim embeddings)
- **Front-end**: Single-page HTML with Tailwind CSS, Glassmorphism dark UI, Lucide icons, webcam capture

## Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **MySQL 8+** running on `localhost:3306`
- **Python 3.8+** with `torch` and `facenet-pytorch` (for model export only)

## 1. Export the FaceNet Model

Before running the API, you need to generate the TorchScript model file.

```bash
pip install facenet-pytorch torch
python export_model.py
```

This creates `models/face_feature.pt`.

## 2. Database Setup

Create the MySQL database (the app auto-creates tables via JPA):

```sql
CREATE DATABASE IF NOT EXISTS faceid_db;
```

## 3. Configuration

Set environment variables or edit `src/main/resources/application.yml`:

| Variable       | Default | Description                     |
|----------------|---------|---------------------------------|
| `DB_PASSWORD`  | `root`  | MySQL root password             |
| `JWT_SECRET`   | (built-in) | Base64-encoded 512-bit key   |

## 4. Build & Run

```bash
cd faceid-api
mvn clean package -DskipTests
java -jar target/faceid-api-1.0.0.jar
```

Or with Maven directly:

```bash
mvn spring-boot:run
```

The app starts at **http://localhost:8080**.

## API Endpoints

| Method | Endpoint              | Auth     | Description                                      |
|--------|-----------------------|----------|--------------------------------------------------|
| POST   | `/api/auth/register`  | Public   | Register with username, email, password, faceImage |
| POST   | `/api/auth/login`     | Public   | Login with username, password, faceImage (cosine > 0.85) |
| POST   | `/api/face/verify`    | JWT      | Verify face against stored embedding             |

All endpoints accepting images use `multipart/form-data`.

## How It Works

1. **Registration**: User uploads a face photo. The DJL FaceNet model extracts a 512-dimensional embedding vector. The vector is stored as a BLOB in MySQL alongside the BCrypt-hashed password.

2. **Login**: User provides credentials + a live face photo. The API verifies the password, extracts a live embedding, and computes **Cosine Similarity** against the stored vector. Login is granted only if similarity >= 0.85.

3. **JWT**: On successful login, a signed JWT token is issued (24h expiry). Protected endpoints require `Authorization: Bearer <token>`.

## Project Structure

```
faceid-api/
  src/main/java/com/faceid/
    config/          - DJL model config, Security config
    controller/      - REST controllers (Auth, Face)
    dto/             - Java 17 Records (request/response)
    entity/          - JPA entities
    exception/       - Global exception handler
    repository/      - Spring Data repositories
    security/        - JWT service and filter
    service/         - Business logic (embedding, similarity, auth)
  src/main/resources/
    application.yml  - App configuration
    static/
      index.html     - Full front-end SPA
  models/
    face_feature.pt  - TorchScript FaceNet model (user-generated)
```

## Tech Stack

- Java 17, Spring Boot 3.3.5, Spring Security 6
- DJL 0.31.1 + PyTorch Engine
- MySQL 8 + Hibernate/JPA
- JJWT 0.12.6
- Tailwind CSS, Lucide Icons, Vanilla JS
