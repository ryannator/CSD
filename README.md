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


### Predicted backend directory Structure 

    src/
    ├── controller/
    │   ├── UserController.java             # Handles user-related API endpoints
    │   ├── TariffController.java           # Manages tariff calculation API
    │   ├── AgreementController.java        # Manages trade agreements API
    │
    ├── service/
    │   ├── UserService.java                # Business logic for user management
    │   ├── TariffCalculationService.java              # Logic for tariff calculation and breakdown
    │   ├── AgreementService.java           # Logic for managing trade agreements
    │   ├── TariffCalculationServiceImpl.java          # Implementation of tariff service
    │   ├── UserServiceImpl.java            # Implementation of user service
    │   ├── AgreementServiceImpl.java       # Implementation of agreement service
    │
    ├── repository/
    │   ├── UserRepository.java             # Database interactions for user entities
    │   ├── ProductRepository.java          # Database interactions for product entities
    │   ├── CountryRepository.java          # Database interactions for country entities
    │   ├── AgreementRepository.java        # Database interactions for trade agreements
    │
    ├── model/
    │   ├── User.java                       # User entity class
    │   ├── Product.java                    # Product entity class
    │   ├── Country.java                    # Country entity class
    │   ├── TradeAgreement.java             # Trade agreement entity class
    │   ├── TariffCalculation.java          # Entity for storing tariff calculation details
    │
    ├── dto/
    │   ├── TariffRequestDTO.java           # Data Transfer Object for tariff requests
    │   ├── TariffCalculationResponse.java          # Data Transfer Object for tariff responses
    │   ├── AgreementDTO.java               # Data Transfer Object for trade agreements
    │
    ├── exception/
    │   ├── CustomException.java            # Custom exception for handling errors
    │   ├── TariffCalculationException.java # Specific exception for tariff calculation errors
    │
    ├── util/
    │   ├── TariffCalculator.java           # Utility class for performing tariff calculations
    │
    ├── application.properties             # Configuration file for the application (DB connection, etc.)
    ├── pom.xml                            # Maven project file for dependencies
    └── .gitignore                         # Git ignore file to exclude unnecessary files

### Frontend Directory Structure (draft)

    src/
    ├── views/
    │   ├── HomeView.vue                    # Landing page with system overview
    │   ├── TariffCalculator.vue            # Main tariff calculation interface
    │   ├── UserDashboard.vue               # User statistics and calculation history
    │   ├── AdminDashboard.vue              # Admin panel for system management
    │   ├── CountriesProducts.vue           # Browse countries and products
    │   └── AboutView.vue                   # System information and documentation
    │
    ├── components/
    │   ├── ui/                             # Reusable UI components (shadcn-vue)
    │   │   ├── sidebar/                    # Sidebar navigation components
    │   │   ├── button/                     # Button variants and styles
    │   │   ├── input/                      # Form input components
    │   │   ├── table/                      # Data table components
    │   │   └── ...                         # Other UI primitives
    │   ├── AppSidebar.vue                  # Main application sidebar navigation
    │   ├── TariffCalculationForm.vue       # Reusable tariff calculation form
    │   ├── CountrySelector.vue             # Country selection dropdown
    │   ├── ProductSelector.vue             # Product category selection
    │   ├── CalculationResultCard.vue       # Display tariff calculation results
    │   ├── StatisticsCard.vue              # Reusable statistics display card
    │   └── DataTable.vue                   # Generic data table component
    │
    ├── services/
    │   ├── api.ts                          # Base API configuration and interceptors
    │   ├── TariffCalculationService.ts                # Tariff calculation API calls
    │   ├── userService.ts                  # User management API calls
    │   ├── countryService.ts               # Country data API calls
    │   ├── productService.ts               # Product data API calls
    │   ├── agreementService.ts             # Trade agreement API calls
    │   └── authService.ts                  # Authentication API calls
    │
    ├── stores/
    │   ├── auth.ts                         # Authentication state management 
    │   ├── tariff.ts                       # Tariff calculation state
    │   ├── user.ts                         # User data and preferences
    │   ├── country.ts                      # Countries data cache
    │   ├── product.ts                      # Products data cache
    │   └── agreement.ts                    # Trade agreements state
    │
    ├── types/
    │   ├── api.ts                          # API response/request types
    │   ├── tariff.ts                       # Tariff calculation related types
    │   ├── user.ts                         # User entity types
    │   ├── country.ts                      # Country entity types
    │   ├── product.ts                      # Product entity types
    │   ├── agreement.ts                    # Trade agreement types
    │   └── common.ts                       # Shared common types
    │
    ├── composables/
    │   ├── useTariffCalculation.ts         # Composable for tariff calculation logic
    │   ├── useAuth.ts                      # Authentication composable
    │   ├── useCountries.ts                 # Countries data management
    │   ├── useProducts.ts                  # Products data management
    │   ├── useApi.ts                       # API call utilities
    │   └── useLocalStorage.ts              # Local storage utilities
    │
    ├── utils/
    │   ├── formatters.ts                   # Currency, number, date formatters
    │   ├── validators.ts                   # Form validation utilities
    │   ├── constants.ts                    # Application constants
    │   ├── helpers.ts                      # General helper functions
    │   └── tariffCalculations.ts           # Client-side tariff calculation utilities
    │
    ├── router/
    │   └── index.ts                        # Vue Router configuration and routes
    │
    ├── assets/
    │   ├── css/
    │   │   ├── tailwind.css                # Tailwind CSS imports and custom styles
    │   │   └── components.css              # Component-specific styles
    │   ├── images/                         # Static images and logos
    │   └── icons/                          # Custom SVG icons
    │
    ├── App.vue                             # Root Vue component
    ├── main.ts                             # Application entry point
    └── env.d.ts                            # TypeScript environment declarations

    root/
    ├── package.json                        # NPM dependencies and scripts
    ├── vite.config.ts                      # Vite build configuration
    ├── tailwind.config.js                  # Tailwind CSS configuration
    ├── tsconfig.json                       # TypeScript configuration
    ├── components.json                     # shadcn-vue components configuration
    ├── .gitignore                          # Git ignore file
    └── README.md                           # Project documentation

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