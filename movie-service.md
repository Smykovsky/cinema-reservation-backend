# movie-service

### MovieController

#### GET /api/movie
- Description: Pobiera paginowaną listę wszystkich filmów, z opcjonalnym filtrowaniem.
- Input:
  - title: string (query, optional) - Tytuł filmu.
  - genre: string (query, optional) - Gatunek filmu.
  - minDuration: integer (query, optional) - Minimalny czas trwania w minutach.
  - maxDuration: integer (query, optional) - Maksymalny czas trwania w minutach.
  - releaseDateFrom: LocalDate (query, optional, format: RRRR-MM-DD) - Data wydania od.
  - releaseDateTo: LocalDate (query, optional, format: RRRR-MM-DD) - Data wydania do.
  - page: integer (query, optional, default: 0) - Numer strony.
  - size: integer (query, optional, default: 20) - Liczba elementów na stronę.
  - sort: string (query, optional, default: "releaseDate,desc") - Kryteria sortowania (np. "releaseDate,asc", "title,desc").
- Output:
  - 200 OK:
    ```json
    {
      "content": [
        {
          "id": 1,
          "title": "Tytuł Filmu 1",
          "description": "Opis filmu 1.",
          "duration": 120,
          "genres": ["Akcja", "Sci-Fi"],
          "releaseDate": "2025-01-01",
          "imageUrl": "http://example.com/movie1.jpg"
        }
      ],
      "pageNumber": 0,
      "pageSize": 20,
      "totalElements": 1,
      "totalPages": 1
    }
    ```

#### GET /api/movie/{id}
- Description: Pobiera szczegóły konkretnego filmu po jego ID.
- Input:
  - id: Long (path) - ID filmu.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "title": "Tytuł Filmu 1",
      "description": "Opis filmu 1.",
      "duration": 120,
      "genres": ["Akcja", "Sci-Fi"],
      "releaseDate": "2025-01-01",
      "imageUrl": "http://example.com/movie1.jpg"
    }
    ```

#### POST /api/movie/batch
- Description: Pobiera listę filmów na podstawie listy identyfikatorów.
- Input:
  - body:
    ```json
    {
      "ids": [1, 2, 3]
    }
    ```
- Output:
  - 200 OK:
    ```json
    [
      {
        "id": 1,
        "title": "Tytuł Filmu 1",
        "description": "Opis filmu 1.",
        "duration": 120,
        "genres": ["Akcja", "Sci-Fi"],
        "releaseDate": "2025-01-01",
        "imageUrl": "http://example.com/movie1.jpg"
      }
    ]
    ```

#### POST /api/movie
- Description: Tworzy nowy film, opcjonalnie z obrazem.
- Input:
  - movie: MultipartFile (form-data) - Dane filmu w formacie JSON.
    ```json
    {
      "title": "Nowy Film",
      "description": "Opis nowego filmu.",
      "duration": 130,
      "genreIds": [1, 2],
      "releaseDate": "2026-03-15"
    }
    ```
  - image: MultipartFile (form-data, optional) - Plik obrazu.
- Output:
  - 201 Created:
    ```json
    {
      "id": 2,
      "title": "Nowy Film",
      "description": "Opis nowego filmu.",
      "duration": 130,
      "genres": ["Dramat", "Komedia"],
      "releaseDate": "2026-03-15",
      "imageUrl": "http://example.com/movie2.jpg"
    }
    ```

#### PUT /api/movie
- Description: Aktualizuje istniejący film, opcjonalnie z nowym obrazem.
- Input:
  - movie: MultipartFile (form-data) - Zaktualizowane dane filmu w formacie JSON.
    ```json
    {
      "id": 1,
      "title": "Zaktualizowany Tytuł Filmu",
      "description": "Zaktualizowany opis filmu.",
      "duration": 125,
      "genreIds": [1],
      "releaseDate": "2025-01-01"
    }
    ```
  - image: MultipartFile (form-data, optional) - Nowy plik obrazu.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "title": "Zaktualizowany Tytuł Filmu",
      "description": "Zaktualizowany opis filmu.",
      "duration": 125,
      "genres": ["Akcja"],
      "releaseDate": "2025-01-01",
      "imageUrl": "http://example.com/movie1_updated.jpg"
    }
    ```

#### DELETE /api/movie/{id}
- Description: Usuwa film po jego ID.
- Input:
  - id: Long (path) - ID filmu do usunięcia.
- Output:
  - 204 No Content: Brak zawartości.

### GenreController

#### GET /api/genre
- Description: Pobiera listę wszystkich gatunków filmowych.
- Input:
  - Brak
- Output:
  - 200 OK:
    ```json
    [
      {
        "id": 1,
        "name": "Akcja"
      },
      {
        "id": 2,
        "name": "Sci-Fi"
      }
    ]
    ```

#### GET /api/genre/{id}
- Description: Pobiera szczegóły konkretnego gatunku filmowego po jego ID.
- Input:
  - id: Long (path) - ID gatunku.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "name": "Akcja"
    }
    ```
  - 404 Not Found: Brak gatunku o podanym ID.

#### POST /api/genre
- Description: Tworzy nowy gatunek filmowy.
- Input:
  - body:
    ```json
    {
      "name": "Horror"
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "id": 3,
      "name": "Horror"
    }
    ```

#### PUT /api/genre/{id}
- Description: Aktualizuje istniejący gatunek filmowy.
- Input:
  - id: Long (path) - ID gatunku do zaktualizowania.
  - body:
    ```json
    {
      "name": "Akcja i Przygoda"
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "name": "Akcja i Przygoda"
    }
    ```
  - 404 Not Found: Brak gatunku o podanym ID.

#### DELETE /api/genre/{id}
- Description: Usuwa gatunek filmowy po jego ID.
- Input:
  - id: Long (path) - ID gatunku do usunięcia.
- Output:
  - 204 No Content: Brak zawartości.
