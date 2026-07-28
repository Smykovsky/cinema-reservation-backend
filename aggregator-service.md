# aggregator-service

### ScreeningAggregatorController

#### GET /api/aggregator/test
- Description: Prosty punkt końcowy testowy.
- Input:
  - Brak
- Output:
  - 200 OK: "Test"

#### GET /api/aggregator/screenings
- Description: Pobiera listę seansów ze szczegółowymi informacjami o filmach dla danego kina i daty.
- Input:
  - cinemaId: Long (query)
  - date: LocalDate (query, format: RRRR-MM-DD)
- Output:
  - 200 OK:
    ```json
    [
      {
        "screening": {
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
        },
        "movie": {
          "id": 101,
          "title": "Tytuł Filmu",
          "description": "Opis filmu.",
          "duration": 120,
          "genres": ["Akcja", "Sci-Fi"],
          "releaseDate": "2025-01-01",
          "imageUrl": "http://example.com/movie101.jpg"
        }
      }
    ]
    ```

#### GET /api/aggregator/screening-details/{id}
- Description: Pobiera szczegółowe informacje o konkretnym seansie, w tym szczegóły filmu.
- Input:
  - id: Long (path) - ID seansu.
- Output:
  - 200 OK:
    ```json
    {
      "screening": {
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
      },
      "movie": {
        "id": 101,
        "title": "Tytuł Filmu",
        "description": "Opis filmu.",
        "duration": 120,
        "genres": ["Akcja", "Sci-Fi"],
        "releaseDate": "2025-01-01",
        "imageUrl": "http://example.com/movie101.jpg"
      }
    }
    ```
