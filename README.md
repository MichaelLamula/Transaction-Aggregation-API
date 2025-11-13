# 1. Transaction Aggregation API

This project implements a production-grade API using Spring Boot, Java 17, and PostgreSQL to aggregate and categorize customer financial transaction data from multiple mock sources.

## Prerequisites

* Java Development Kit (JDK) 17+
* Docker and Docker Compose
* Maven 3+

## 🚀 How to Build and Run the Project

The easiest way to run the entire system (Application + Database) is using Docker Compose.

### Step 1: Clone the Repository

bash
git clone <your-repo-link>
cd <your-project-directory>

### Step 2: Build and Start the Containers
docker-compose up --build -d

### Step 3: Verify the Running Services
docker-compose logs -f aggregation-api

The API will be running at http://localhost:8080.

### How to Test the Project
You can use a tool like cURL or Postman to test the main endpoints.

End points:
 1. http://localhost:8080/api/v1/transactions
 2. http://localhost:8080/api/v1/transactions/customer/{customerId}/summary
 3. http://localhost:8080/api/v1/transactions?page=0&size=10


### Build by
Michael Lamula