# payment-service

### PaymentController

#### POST /api/payment
- Description: Inicjuje nową płatność.
- Input:
  - body:
    ```json
    {
      "bookingId": 1
    }
    ```
- Output:
  - 201 Created:
    ```json
    {
      "id": 1,
      "bookingId": 1,
      "amount": 75.00,
      "status": "PENDING",
      "paymentMethod": null,
      "providerTransactionId": null,
      "createdAt": "2026-02-17T15:00:00Z",
      "clientSecret": "pi_xyz_secret_abc"
    }
    ```

#### POST /api/payment/{id}/refund
- Description: Inicjuje zwrot środków dla konkretnej płatności.
- Input:
  - id: Long (path) - ID płatności.
  - body:
    ```json
    {
      "amount": 25.00,
      "reason": "Anulowanie miejsca"
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "paymentId": 1,
      "amount": 25.00,
      "status": "INITIATED"
    }
    ```

#### POST /api/payment/{id}/confirm-blik
- Description: Potwierdza płatność BLIK.
- Input:
  - id: Long (path) - ID płatności.
  - body:
    ```json
    {
      "blikCode": "123456"
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "status": "success"
    }
    ```

#### GET /api/payment/{id}
- Description: Pobiera szczegóły konkretnej płatności.
- Input:
  - id: Long (path) - ID płatności.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "bookingId": 1,
      "amount": 75.00,
      "status": "COMPLETED",
      "paymentMethod": "BLIK",
      "providerTransactionId": "blik_transaction_123",
      "createdAt": "2026-02-17T15:00:00Z",
      "clientSecret": "pi_xyz_secret_abc"
    }
    ```
