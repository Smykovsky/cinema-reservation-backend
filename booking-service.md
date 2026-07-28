# booking-service

### BookingController

#### POST /api/booking
- Description: Tworzy nową rezerwację.
- Input:
  - body:
    ```json
    {
      "screeningId": 101,
      "userId": 1,
      "seatIds": [1, 2, 3]
    }
    ```
- Output:
  - 201 Created:
    ```json
    {
      "id": 1,
      "userId": 1,
      "screeningId": 101,
      "bookingNumber": "UUID-example",
      "status": "PENDING",
      "totalAmount": 75.00,
      "expiresAt": "2026-02-17T18:30:00",
      "createdAt": "2026-02-17T18:00:00",
      "seats": [
        {
          "id": 1,
          "seatId": 1,
          "price": 25.00
        },
        {
          "id": 2,
          "seatId": 2,
          "price": 25.00
        },
        {
          "id": 3,
          "seatId": 3,
          "price": 25.00
        }
      ]
    }
    ```

#### GET /api/booking/{id}
- Description: Pobiera szczegóły konkretnej rezerwacji.
- Input:
  - id: Long (path) - ID rezerwacji.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "userId": 1,
      "screeningId": 101,
      "bookingNumber": "UUID-example",
      "status": "PENDING",
      "totalAmount": 75.00,
      "expiresAt": "2026-02-17T18:30:00",
      "createdAt": "2026-02-17T18:00:00",
      "seats": [
        {
          "id": 1,
          "seatId": 1,
          "price": 25.00
        },
        {
          "id": 2,
          "seatId": 2,
          "price": 25.00
        },
        {
          "id": 3,
          "seatId": 3,
          "price": 25.00
        }
      ]
    }
    ```

#### DELETE /api/booking/{id}
- Description: Anuluje konkretną rezerwację.
- Input:
  - id: Long (path) - ID rezerwacji do anulowania.
  - body: string (reason) - Powód anulowania rezerwacji.
- Output:
  - 204 No Content: Brak zawartości.
