# CS203
Collaborative Software Development Project – TARIFF System (CS203)

## Project Overview 
Project consists of the following components: 
- **Frontend** – VueJS  
- **Backend** – Spring Boot
- **Database** – PostgreSQL 

## First time setup instructions - Instructions based on macOS
### 1. Database (PostgreSQL) 

Install PostgreSQL (v17 recommended):  

    brew install postgresql@17  
    brew services start postgresql@17
    psql -U postgres
    CREATE DATABASE tariff;
    CREATE USER postgres WITH PASSWORD 'postgres';
    GRANT ALL PRIVILEGES ON DATABASE tariff TO postgres;

### 2. Backend Environment Variables
This project uses an external config file called dev.properties for environment variables.

1. In the /backend folder, there is a file called 'dev.propeties.template'
    ```   
    cd ./backend
2. Create a copy and rename it
   ```
    cp dev.properties.template dev.properties
3. Fill in your local values inside dev.properties
4. Fill in a blank version in the template file 
5. The backend will automatically import this file through the application.properties

### 3. Backend (Spring Boot)
    
Option 1: Run the backend with Maven:
    
    cd backend
    ./mvnw spring-boot:run
    
Option 2: Use the Spring Boot Dashboard:
  - In vscode, install extension spring boot dashboard. 
  - Under -APPS, click on the play button for backend. 

### 4. Frontend (VueJS)

    cd frontend
    npm install

## To run the application 
### 1. Backend (Spring Boot)
    
Option 1: Run the backend with Maven:
    
    cd backend
    ./mvnw spring-boot:run
    
Option 2: Use the Spring Boot Dashboard:
  - In vscode, install extension spring boot dashboard. 
  - Under -APPS, click on the play button for backend. 

APIs to make sure project is running well 
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health check endpoint: http://localhost:8080/actuator/health

### 2. Frontend (VueJS)

    cd frontend
    npm run dev

APIs to make sure project is running well 
- Application: http://localhost:5173

## Frontend Demo Credentials

#### Admin:
- admin@tariff.com / admin123   
#### User:
- user@tariff.com / user123   

### Key Frontend Patterns:

**Views** - Page-level components corresponding to routes.   
**Components** - Reusable UI building blocks following atomic design.   
**Services** - API communication layer with backend.   
**Stores** - State management using Pinia for reactive data.   
**Types** - TypeScript interfaces matching backend DTOs.   
**Composables** - Reusable reactive logic following Vue 3 patterns.   
**Utils** - Pure utility functions for formatting, validation, etc.

### Frontend-Backend Integration:
- Services mirror backend controllers (TariffCalculationService ↔ TariffController)
- Types match backend DTOs for seamless data exchange
- State stores cache backend data for optimal user experience
- Composables encapsulate complex business logic from backend services

## Testing & Code Coverage

### Backend Testing Commands

**Run All Tests:**
```bash
cd backend
mvn test
```

**Run Tests with Coverage:**
```bash
cd backend
mvn clean test jacoco:report
```

**Generate JaCoCo Coverage Report:**
```bash
cd backend
mvn jacoco:report
```

**View Coverage Report:**
```bash
open target/site/jacoco/index.html
```
