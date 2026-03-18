# AI-Powered Bill Payment Microservices System

An enterprise-grade automated bill payment system built with Java Spring Boot microservices, React.js, PostgreSQL, and Groq LLM AI.

## Architecture

5 independent microservices orchestrated through an API Gateway:

| Service | Port | Responsibility |
|---|---|---|
| API Gateway | 8080 | Routing & Orchestration |
| File Service | 8081 | Bill upload & storage |
| AI Service | 8082 | Groq LLM bill extraction |
| Verification Service | 8083 | Vendor & amount validation |
| Payment Service | 8084 | Payment processing |
| Notification Service | 8085 | Email notifications |

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.5, Spring Cloud Gateway
- **Frontend**: React.js, Vite, Axios
- **Database**: PostgreSQL
- **AI**: Groq LLM API (llama-3.3-70b) + PDFBox
- **DevOps**: Docker, Docker Compose

## Features

- AI-powered PDF bill reading and data extraction
- Vendor registration and validation system
- Duplicate bill prevention
- Expired bill detection
- Real-time processing pipeline
- Containerized with Docker

## How to Run

1. Clone the repository
2. Add your credentials to `docker-compose.yml`:
   - `YOUR_GROQ_API_KEY_HERE` → your Groq API key
   - `YOUR_DB_PASSWORD_HERE` → your PostgreSQL password
3. Run: `docker-compose up --build`
4. Open: `http://localhost:3000`

## Flow

Upload Bill → AI Extraction → Vendor Verification → Payment Processing → Notification
