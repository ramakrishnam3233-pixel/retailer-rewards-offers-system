# API Name
retailer-rewards-offers-system

## Endpoints Details
1) /api/rewards
2) /api/transactions

# Used Technologies
Java 21
Spring Boot 3
REST API
Maven 3.9+.

## Reward rules

- 2 points for each whole dollar **above $100** in a transaction.
- 1 point for each whole dollar **above $50 through $100**.
- Amounts at or below $50 earn 0 points.
- Cents are ignored within each tier (e.g. $120.99 earns 90 points). This rounding policy is explicit and can be changed if business rules require another interpretation.
- Calculate points per transaction first, then aggregate by customer and calendar month.

Examples: $120 -> (120 - 100) * 2 + 50 = 90 points; $75 -> 25 points; $50 -> 0 points.

# /api/rewards?startDate=2026-01-10&endDate=2026-01-10

```JSON Response
{
    "startDate": "2026-01-10",
    "endDate": "2026-01-10",
    "customers": [
        {
            "customerId": "CUST00",
            "monthlyRewards": [
                {
                    "month": "2026-01",
                    "points": 90
                }
            ],
            "totalPoints": 90
        }
    ]
}
```
# POST /api/transactions

```JSON REQUEST:
{
  "customerId": "CUST01",
  "date": "2026-01-10",
  "amount": 120.00
}
```
# Project structure
```Java packages and classes structure
src/main/java/com/retailer/offer/rewards/
RetailerOfferRewardsApplication.java
controller/OfferRewardController.java
exception/ApiExceptionHandler.java
exception/InvalidDateRangeException.java
model/Transaction.java
model/MonthlyReward.java
model/CustomerReward.java
model/RewardSummary.java
repository/TransactionRepository.java
repository/InMemoryTransactionRepository.java
service/RewardService.java
src/test/java/com/retailer/offer/rewards/
RewardControllerIntegrationTest.java
service/RewardServiceTest.java
```