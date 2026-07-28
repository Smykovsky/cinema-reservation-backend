# cinema-service

### CinemaController

#### GET /api/cinema
- Description: Pobiera listę wszystkich kin.
- Input:
  - Brak
- Output:
  - 200 OK:
    ```json
    [
      {
        "id": 1,
        "name": "Kino Centrum",
        "address": "ul. Długa 10",
        "city": "Warszawa",
        "phone": "+48123456789",
        "isActive": true,
        "createdAt": "2026-01-01T10:00:00Z",
        "updatedAt": "2026-01-01T10:00:00Z",
        "halls": [] // Zostawiam pustą listę lub uproszczone HallDto
      }
    ]
    ```

#### GET /api/cinema/{id}
- Description: Pobiera szczegóły konkretnego kina po jego ID.
- Input:
  - id: Long (path) - ID kina.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "name": "Kino Centrum",
      "address": "ul. Długa 10",
      "city": "Warszawa",
      "phone": "+48123456789",
      "isActive": true,
      "createdAt": "2026-01-01T10:00:00Z",
      "updatedAt": "2026-01-01T10:00:00Z",
      "halls": []
    }
    ```

#### POST /api/cinema
- Description: Tworzy nowe kino.
- Input:
  - body:
    ```json
    {
      "name": "Nowe Kino",
      "address": "ul. Krótka 5",
      "city": "Kraków",
      "phone": "+48987654321",
      "isActive": true
    }
    ```
- Output:
  - 201 Created:
    ```json
    {
      "id": 2,
      "name": "Nowe Kino",
      "address": "ul. Krótka 5",
      "city": "Kraków",
      "phone": "+48987654321",
      "isActive": true,
      "createdAt": "2026-02-17T11:00:00Z",
      "updatedAt": "2026-02-17T11:00:00Z",
      "halls": []
    }
    ```

#### PUT /api/cinema/{id}
- Description: Aktualizuje istniejące kino.
- Input:
  - id: Long (path) - ID kina do zaktualizowania.
  - body:
    ```json
    {
      "id": 1,
      "name": "Zaktualizowane Kino Centrum",
      "address": "ul. Długa 10A",
      "city": "Warszawa",
      "phone": "+48111222333",
      "isActive": false
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "name": "Zaktualizowane Kino Centrum",
      "address": "ul. Długa 10A",
      "city": "Warszawa",
      "phone": "+48111222333",
      "isActive": false,
      "createdAt": "2026-01-01T10:00:00Z",
      "updatedAt": "2026-02-17T12:00:00Z",
      "halls": []
    }
    ```
  - 400 Bad Request: `{ "error": "ID w ścieżce nie zgadza się z ID w ciele żądania" }`

#### DELETE /api/cinema/{id}
- Description: Usuwa kino po jego ID.
- Input:
  - id: Long (path) - ID kina do usunięcia.
- Output:
  - 204 No Content: Brak zawartości.

### HallController

#### GET /api/hall
- Description: Pobiera listę wszystkich sal.
- Input:
  - Brak
- Output:
  - 200 OK:
    ```json
    [
      {
        "id": 201,
        "cinemaId": 1,
        "name": "Sala A",
        "totalSeats": 100,
        "features": ["Dolby Atmos"],
        "isActive": true,
        "createdAt": "2026-01-01T10:00:00Z"
      }
    ]
    ```

#### GET /api/hall/{id}
- Description: Pobiera szczegóły konkretnej sali po jej ID.
- Input:
  - id: Long (path) - ID sali.
- Output:
  - 200 OK:
    ```json
    {
      "id": 201,
      "cinemaId": 1,
      "name": "Sala A",
      "totalSeats": 100,
      "features": ["Dolby Atmos"],
      "isActive": true,
      "createdAt": "2026-01-01T10:00:00Z"
    }
    ```

#### POST /api/hall
- Description: Tworzy nową salę.
- Input:
  - body:
    ```json
    {
      "cinemaId": 1,
      "name": "Sala B",
      "totalSeats": 150,
      "features": ["IMAX"],
      "isActive": true
    }
    ```
- Output:
  - 201 Created:
    ```json
    {
      "id": 202,
      "cinemaId": 1,
      "name": "Sala B",
      "totalSeats": 150,
      "features": ["IMAX"],
      "isActive": true,
      "createdAt": "2026-02-17T13:00:00Z"
    }
    ```

#### PUT /api/hall/{id}
- Description: Aktualizuje istniejącą salę.
- Input:
  - id: Long (path) - ID sali do zaktualizowania.
  - body:
    ```json
    {
      "id": 201,
      "cinemaId": 1,
      "name": "Sala A Zaktualizowana",
      "totalSeats": 110,
      "features": ["Dolby Atmos", "3D"],
      "isActive": true
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "id": 201,
      "cinemaId": 1,
      "name": "Sala A Zaktualizowana",
      "totalSeats": 110,
      "features": ["Dolby Atmos", "3D"],
      "isActive": true,
      "createdAt": "2026-01-01T10:00:00Z"
    }
    ```
  - 400 Bad Request: `{ "error": "ID w ścieżce nie zgadza się z ID w ciele żądania" }`

#### DELETE /api/hall/{id}
- Description: Usuwa salę po jej ID.
- Input:
  - id: Long (path) - ID sali do usunięcia.
- Output:
  - 204 No Content: Brak zawartości.

### ScreeningController

#### GET /api/screening
- Description: Pobiera listę wszystkich seansów.
- Input:
  - Brak
- Output:
  - 200 OK:
    ```json
    [
      {
        "id": 1,
        "movieId": 101,
        "hallId": 201,
        "startTime": "2026-02-17T18:00:00Z",
        "endTime": "2026-02-17T20:00:00Z",
        "basePrice": 25.00,
        "vipPrice": 35.00,
        "wheelchairPrice": 20.00,
        "status": "ACTIVE",
        "createdAt": "2026-02-16T10:00:00Z"
      }
    ]
    ```

#### GET /api/screening/{id}
- Description: Pobiera szczegóły konkretnego seansu po jego ID.
- Input:
  - id: Long (path) - ID seansu.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "movieId": 101,
      "hallId": 201,
      "startTime": "2026-02-17T18:00:00Z",
      "endTime": "2026-02-17T20:00:00Z",
      "basePrice": 25.00,
      "vipPrice": 35.00,
      "wheelchairPrice": 20.00,
      "status": "ACTIVE",
      "createdAt": "2026-02-16T10:00:00Z"
    }
    ```

#### GET /api/screening/by-cinema-and-date
- Description: Pobiera listę seansów dla określonego kina i daty.
- Input:
  - cinemaId: Long (query) - ID kina.
  - date: LocalDate (query, format: RRRR-MM-DD) - Data seansów.
- Output:
  - 200 OK:
    ```json
    [
      {
        "id": 1,
        "movieId": 101,
        "hallId": 201,
        "startTime": "2026-02-17T18:00:00Z",
        "endTime": "2026-02-17T20:00:00Z",
        "basePrice": 25.00,
        "vipPrice": 35.00,
        "wheelchairPrice": 20.00,
        "status": "ACTIVE",
        "createdAt": "2026-02-16T10:00:00Z"
      }
    ]
    ```

#### POST /api/screening
- Description: Tworzy nowy seans.
- Input:
  - body:
    ```json
    {
      "movieId": 101,
      "hallId": 201,
      "startTime": "2026-02-18T10:00:00Z",
      "endTime": "2026-02-18T12:00:00Z",
      "basePrice": 25.00,
      "vipPrice": 35.00,
      "wheelchairPrice": 20.00
    }
    ```
- Output:
  - 201 Created:
    ```json
    {
      "id": 2,
      "movieId": 101,
      "hallId": 201,
      "startTime": "2026-02-18T10:00:00Z",
      "endTime": "2026-02-18T12:00:00Z",
      "basePrice": 25.00,
      "vipPrice": 35.00,
      "wheelchairPrice": 20.00,
      "status": "ACTIVE",
      "createdAt": "2026-02-17T14:00:00Z"
    }
    ```

#### PUT /api/screening/{id}
- Description: Aktualizuje istniejący seans.
- Input:
  - id: Long (path) - ID seansu do zaktualizowania.
  - body:
    ```json
    {
      "id": 1,
      "movieId": 101,
      "hallId": 201,
      "startTime": "2026-02-17T18:30:00Z",
      "endTime": "2026-02-17T20:30:00Z",
      "basePrice": 27.00,
      "vipPrice": 37.00,
      "wheelchairPrice": 22.00,
      "status": "CANCELED"
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "movieId": 101,
      "hallId": 201,
      "startTime": "2026-02-17T18:30:00Z",
      "endTime": "2026-02-17T20:30:00Z",
      "basePrice": 27.00,
      "vipPrice": 37.00,
      "wheelchairPrice": 22.00,
      "status": "CANCELED",
      "createdAt": "2026-02-16T10:00:00Z"
    }
    ```
  - 400 Bad Request: `{ "error": "ID w ścieżce nie zgadza się z ID w ciele żądania" }`

#### DELETE /api/screening/{id}
- Description: Usuwa seans po jego ID.
- Input:
  - id: Long (path) - ID seansu do usunięcia.
- Output:
  - 204 No Content: Brak zawartości.

#### PUT /api/screening/{id}/seats/reserve
- Description: Rezerwuje miejsca na konkretny seans.
- Input:
  - id: Long (path) - ID seansu.
  - body:
    ```json
    {
      "screeningId": 1,
      "seatIds": [10, 11]
    }
    ```
- Output:
  - 204 No Content: Brak zawartości.

#### GET /api/screening/{id}/seats/available
- Description: Pobiera listę dostępnych miejsc na konkretny seans.
- Input:
  - id: Long (path) - ID seansu.
- Output:
  - 200 OK:
    ```json
    [
      {
        "seatId": 10,
        "row": 1,
        "number": 10,
        "seatType": "STANDARD",
        "status": "AVAILABLE"
      }
    ]
    ```

#### GET /api/screening/{id}/seats
- Description: Pobiera listę wszystkich miejsc na konkretny seans (w tym dostępne i zarezerwowane).
- Input:
  - id: Long (path) - ID seansu.
- Output:
  - 200 OK:
    ```json
    [
      {
        "seatId": 10,
        "row": 1,
        "number": 10,
        "seatType": "STANDARD",
        "status": "AVAILABLE"
      },
      {
        "seatId": 11,
        "row": 1,
        "number": 11,
        "seatType": "STANDARD",
        "status": "RESERVED"
      }
    ]
    ```

### SeatController

#### GET /api/seat
- Description: Pobiera listę wszystkich miejsc.
- Input:
  - Brak
- Output:
  - 200 OK:
    ```json
    [
      {
        "id": 1,
        "hallId": 201,
        "row": 1,
        "number": 1,
        "seatType": "STANDARD"
      }
    ]
    ```

#### GET /api/seat/{id}
- Description: Pobiera szczegóły konkretnego miejsca po jego ID.
- Input:
  - id: Long (path) - ID miejsca.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "hallId": 201,
      "row": 1,
      "number": 1,
      "seatType": "STANDARD"
    }
    ```

#### POST /api/seat
- Description: Tworzy nowe miejsce.
- Input:
  - body:
    ```json
    {
      "hallId": 201,
      "row": 1,
      "number": 5,
      "seatType": "STANDARD"
    }
    ```
- Output:
  - 201 Created:
    ```json
    {
      "id": 2,
      "hallId": 201,
      "row": 1,
      "number": 5,
      "seatType": "STANDARD"
    }
    ```

#### PUT /api/seat/{id}
- Description: Aktualizuje istniejące miejsce.
- Input:
  - id: Long (path) - ID miejsca do zaktualizowania.
  - body:
    ```json
    {
      "id": 1,
      "hallId": 201,
      "row": 1,
      "number": 1,
      "seatType": "VIP"
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "hallId": 201,
      "row": 1,
      "number": 1,
      "seatType": "VIP"
    }
    ```
  - 400 Bad Request: `{ "error": "ID w ścieżce nie zgadza się z ID w ciele żądania" }`

#### DELETE /api/seat/{id}
- Description: Usuwa miejsce po jego ID.
- Input:
  - id: Long (path) - ID miejsca do usunięcia.
- Output:
  - 204 No Content: Brak zawartości.
