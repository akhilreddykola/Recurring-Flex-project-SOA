# RecurringFlex - Subscription Lifecycle Management Engine (PS009)

Microservices project for: **Recurring Revenue & Subscription Lifecycle Management Engine**

## Modules
1. eureka-server        (port 8761) - Service Discovery
2. api-gateway           (port 8080) - API Gateway + JWT validation at the edge
3. user-service          (port 8081) - Register/Login, issues JWT, DB: userdb
4. subscription-service  (port 8082) - Subscription plans, activation, renewal, expiry tracking, DB: subscriptiondb
5. payment-service       (port 8083) - Mock payment authorization, DB: paymentdb

## Prerequisites
- Java 17 (JDK)
- Maven (STS has it bundled) or use the m2e integration
- MySQL Server running on localhost:3306, user "root", password "root"
  (databases userdb / subscriptiondb / paymentdb are auto-created by
   `createDatabaseIfNotExist=true` in each application.yml, so you do NOT
   need to manually create them - just make sure MySQL server is running.)

## Import into STS (Spring Tool Suite)
1. Unzip this project.
2. In STS: File -> Import -> Maven -> Existing Maven Projects.
3. Browse to the unzipped `recurringflex-project` folder.
4. STS will detect all 5 pom.xml files (eureka-server, api-gateway,
   user-service, subscription-service, payment-service) as 5 separate
   Maven projects. Select all 5 and click Finish.
5. Wait for Maven to download dependencies (needs internet access once).

## Run order (IMPORTANT - always start in this order)
1. eureka-server        -> wait until http://localhost:8761 shows the Eureka dashboard
2. payment-service
3. user-service
4. subscription-service
5. api-gateway

Run each as: Right-click project -> Run As -> Spring Boot App
(or run the *Application.java main class directly)

## Testing the flow (use Postman / curl)

### 1. Register a user
POST http://localhost:8080/api/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "pass123"
}

### 2. Login (get JWT)
POST http://localhost:8080/api/auth/login
{
  "username": "john",
  "password": "pass123"
}
-> returns { "token": "...", "userId": 1, "username": "john" }

### 3. Activate a subscription (use the token from step 2)
POST http://localhost:8080/api/subscriptions
Header: Authorization: Bearer <token>
{
  "userId": 1,
  "planName": "PRO_MONTHLY",
  "amount": 499.00
}

### 4. Check subscriptions for a user
GET http://localhost:8080/api/subscriptions/user/1
Header: Authorization: Bearer <token>

### 5. Renew a subscription
PUT http://localhost:8080/api/subscriptions/1/renew
Header: Authorization: Bearer <token>

### 6. Cancel a subscription
PUT http://localhost:8080/api/subscriptions/1/cancel
Header: Authorization: Bearer <token>

## Notes
- All inter-service calls (subscription-service -> payment-service) go
  through Eureka service discovery using a @LoadBalanced RestTemplate,
  not hardcoded URLs.
- The JWT secret string is duplicated (identical) in JwtUtil of
  api-gateway, user-service and subscription-service - this is required
  so every service can validate tokens issued by user-service.
- Subscription statuses: ACTIVE, EXPIRED, CANCELLED. A scheduled job in
  subscription-service runs every hour and flips ACTIVE subscriptions
  whose endDate has passed to EXPIRED.
- payment-service is a MOCK: it always returns success=true and
  generates a random transaction ID, but still persists a Payment row
  to paymentdb, matching the "Payment Service (mock implementation)"
  requirement in the problem statement.
