# System Rezerwacji Biletów - Architektura Mikroserwisów

## 0. API GATEWAY
Opis: Pojedynczy punkt wejścia dla wszystkich zewnętrznych żądań. Odpowiada za routing żądań do odpowiednich mikroserwisów, autentykację użytkowników na podstawie tokenów JWT (weryfikacja i przekazywanie informacji o rolach/uprawnieniach w nagłówkach), wstępne filtrowanie i podstawowe mechanizmy bezpieczeństwa.
### Technologie
- Spring Cloud Gateway
- JWT (io.jsonwebtoken) do parsowania tokenów

## 1. EUREKA SERVER
Opis: Serwer Discovery, umożliwiający mikroserwisom rejestrowanie się i odnajdywanie nawzajem.
### Technologie
- Spring Cloud Netflix Eureka Server

## 2. CONFIG SERVER
Opis: Centralny serwer konfiguracji dla wszystkich mikroserwisów. Udostępnia konfigurację z lokalnego systemu plików (profil 'native') i rejestruje się w Eureka Server, aby inne serwisy mogły go odnaleźć. Działa na porcie 8888.
### Technologie
- Spring Cloud Config Server

## 3. COMMON
Opis: Moduł zawierający wspólne definicje obiektów transferu danych (DTO) oraz obiektów zdarzeń Kafka, które są współdzielone przez wiele mikroserwisów. Zapewnia spójność typów danych w całym systemie.
### Technologie
- Java, Spring (podstawowe typy)

## 4. AUTH SERVICE
Opis: Serwis odpowiedzialny za zarządzanie użytkownikami, uwierzytelnianie (rejestracja, logowanie, JWT) oraz autoryzację opartą na rolach i uprawnieniach.

### Model danych
```sql
Users
- id: BIGINT (PK)
- email: VARCHAR(255) UNIQUE
- password_hash: VARCHAR(255)
- first_name: VARCHAR(100)
- last_name: VARCHAR(100)
- phone: VARCHAR(20)
- created_at: TIMESTAMP
- roles: VARCHAR[] (przechowywane jako ENUMy Role)
- totp_enabled: BOOLEAN
- totp_secret: VARCHAR(255)
```

### REST API
- `POST /api/auth/register` - rejestracja nowego użytkownika
- `POST /api/auth/login` - logowanie użytkownika (zwraca JWT)
- `GET /api/auth/user` - pobierz dane zalogowanego użytkownika (wymaga permisji `USER_READ`)
- `PUT /api/auth/user/{userId}` - aktualizacja danych użytkownika
- `POST /api/auth/totp/enable` - włączenie uwierzytelniania dwuskładnikowego (TOTP)
- `POST /api/auth/totp/verify` - weryfikacja kodu TOTP
- `POST /api/auth/totp/disable` - wyłączenie uwierzytelniania dwuskładnikowego (TOTP)

### Kafka Events (Publisher)
- Brak

---

## 5. MOVIE SERVICE

### Model danych
```sql
Movies
- id: BIGINT (PK)
- title: VARCHAR(255)
- description: TEXT
- duration_minutes: INT
- release_date: DATE
- genres: (relacja Many-to-Many z tabelą Genres)

Genres
- id: BIGINT (PK)
- name: VARCHAR(50)
```

### REST API
- `GET /api/movie` - lista filmów (filtrowanie, paginacja)
- `GET /api/movie/{id}` - szczegóły filmu
- `POST /api/movie` - dodaj film
- `PUT /api/movie` - aktualizuj film
- `DELETE /api/movie/{id}` - usuń film

### Kafka Events
Brak

---

## 6. CINEMA SERVICE
Opis: Zarządza danymi kin, sal projekcyjnych oraz seansów filmowych, w tym ich dostępnością miejsc.

### Model danych
```sql
Cinemas
- id: BIGINT (PK)
- name: VARCHAR(255)
- address: VARCHAR(500)
- city: VARCHAR(100)
- phone: VARCHAR(20)
- is_active: BOOLEAN
- created_at: TIMESTAMP
- updated_at: TIMESTAMP

Halls
- id: BIGINT (PK)
- cinema_id: BIGINT (FK → Cinemas)
- name: VARCHAR(100)
- total_seats: INT
- features: VARCHAR[] (array)
- is_active: BOOLEAN
- created_at: TIMESTAMP

Seats
- id: BIGINT (PK)
- hall_id: BIGINT (FK → Halls)
- row: INT
- number: INT
- seat_type: ENUM('STANDARD', 'VIP', 'WHEELCHAIR')

Screenings
- id: BIGINT (PK)
- movie_id: BIGINT (reference → Movie Service)
- hall_id: BIGINT (reference → Cinema Service)
- start_time: TIMESTAMP
- end_time: TIMESTAMP
- base_price: DECIMAL(10,2)
- vip_price: DECIMAL(10,2)
- wheelchair_price: DECIMAL(10,2)
- status: ENUM('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED')
- created_at: TIMESTAMP

ScreeningSeats
- screening_id: BIGINT (PK, FK → Screenings)
- seat_id: BIGINT (PK, FK → Cinema Service)
- status: ENUM('AVAILABLE', 'RESERVED', 'SOLD')
- reserved_by: VARCHAR(255) (ID rezerwacji, jeśli zarezerwowane)
- reserved_until: TIMESTAMP (nullable)
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
```

### REST API
- `GET /api/cinema` - lista kin
- `GET /api/cinema/{id}` - szczegóły kina
- `POST /api/cinema` - dodaj kino
- `PUT /api/cinema/{id}` - aktualizuj kino
- `DELETE /api/cinema/{id}` - usuń kino
- `GET /api/hall` - lista sal
- `GET /api/hall/{id}` - szczegóły sali
- `POST /api/hall` - dodaj salę
- `PUT /api/hall/{id}` - aktualizuj salę
- `DELETE /api/hall/{id}` - usuń salę
- `GET /api/screening` - lista seansów
- `GET /api/screening/{id}` - szczegóły seansu
- `POST /api/screening` - dodaj seans
- `PUT /api/screening/{id}` - aktualizuj seans
- `DELETE /api/screening/{id}` - usuń seans
- `PUT /api/screening/{id}/seats/reserve` - rezerwacja miejsc
- `GET /api/screening/{id}/seats/available` - dostępne miejsca dla seansu
- `GET /api/screening/{id}/seats` - wszystkie miejsca dla seansu

### Kafka Events (Consumer)
- `booking_created` → zmienia status miejsc na SOLD (po potwierdzeniu płatności)
- `booking_cancelled` → zwalnia miejsca
- `booking_expired` → zwalnia miejsca

---

## 7. BOOKING SERVICE

### Model danych
```sql
Bookings
- id: BIGINT (PK)
- user_id: BIGINT (reference → User Service)
- screening_id: BIGINT (reference → Cinema Service)
- booking_number: VARCHAR(20) UNIQUE
- status: ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')
- total_amount: DECIMAL(10,2)
- expires_at: DATETIME
- created_at: DATETIME

BookingSeats
- id: BIGINT (PK)
- booking_id: BIGINT (FK → Bookings)
- seat_id: BIGINT (reference → Cinema Service)
- price: DECIMAL(10,2)
```

### REST API
- `POST /api/booking` - rozpocznij rezerwację
  ```json
  {
    "screening_id": "uuid",
    "seat_ids": ["uuid1", "uuid2"]
  }
  ```
- `GET /api/booking/{id}` - szczegóły rezerwacji
- `DELETE /api/booking/{id}` - anuluj rezerwację

### Komunikacja REST (wywołuje synchronicznie)
- `Auth Service`: (Brak bezpośredniego wywołania synchronizacyjnego) `userId` jest przekazywane w żądaniu utworzenia rezerwacji, zakładając wstępną walidację przez API Gateway.
- `Cinema Service: GET /api/screening/{id}` - dane seansu
- `Cinema Service: PUT /api/screening/{id}/seats/reserve` - blokada miejsc

### Kafka Events (Publisher)
- `booking.created` → miejsca zablokowane
  ```json
  {
    "booking_id": "uuid",
    "screening_id": "uuid",
    "seats": ["seat1", "seat2"],
    "expires_at": "2024-02-15T10:45:00Z"
  }
  ```
- `booking.cancelled` → użytkownik anulował
- `booking.expired` → upłynął timeout (15 min)

### Kafka Events (Consumer)
- `payment.completed` → zmienia status na CONFIRMED
- `payment.failed` → anuluje rezerwację

---

## 8. PAYMENT SERVICE

### Model danych
```sql
Payments
- id: BIGINT (PK)
- booking_id: BIGINT (reference → Booking Service)
- user_id: BIGINT (reference → User Service)
- booking_number: VARCHAR(20)
- amount: DECIMAL(10,2)
- currency: VARCHAR(3)
- status: ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')
- payment_method: VARCHAR(50)
- provider_transaction_id: VARCHAR(255)
- created_at: DATETIME

Refunds
- id: BIGINT (PK)
- payment_id: BIGINT (FK → Payments)
- user_id: BIGINT (reference → User Service)
- amount: DECIMAL(10,2)
- status: ENUM('PENDING', 'COMPLETED', 'FAILED')
- provider_refund_id: VARCHAR(255)
```

### REST API
- `POST /api/payment` - inicjalizacja płatności
  ```json
  {
    "booking_id": "uuid",
    "amount": 50.00,
    "payment_method": "CARD"
  }
  ```
- `GET /api/payment/{id}` - szczegóły płatności
- `POST /api/payment/{id}/refund` - zwrot pieniędzy
- `POST /api/payment/{id}/confirm-blik` - potwierdzenie płatności BLIK

### Kafka Events (Publisher)
- `payment.completed` → płatność udana
- `payment.failed` → płatność nieudana
- `payment.refunded` → zwrot wykonany

---

## 9. NOTIFICATION SERVICE



### REST API
Brak (tylko wewnętrzne)

### Kafka Events (Consumer)
- `payment_completed` → wyślij potwierdzenie płatności / bilety
- `payment_failed` → wyślij powiadomienie o błędzie płatności
- `payment_refunded` → wyślij powiadomienie o zwrocie

---

## PRZEPŁYW DANYCH - Rezerwacja biletu

### 1. Użytkownik wybiera seans
```
[Frontend]
  → REST: GET /screenings?movie_id=X&date=Y
  → [API Gateway]
  → [Cinema Service]
```

### 2. Użytkownik wybiera miejsca i tworzy rezerwację
```
[Frontend]
  → REST: POST /bookings {screening_id, seat_ids}
  → [API Gateway]
  → [Booking Service]
      → (userId przekazane z nagłówka, walidacja przez API Gateway)
      → REST: PUT /api/screening/{id}/seats/reserve → [Cinema Service]
      → Tworzy booking (status: PENDING)
      → KAFKA: publikuje booking.created
```

### 3. Płatność
```
[Frontend]
  → REST: POST /payments {booking_id, amount}
  → [API Gateway]
  → [Payment Service]
      → Stripe API: tworzy payment intent
      → Czeka na webhook
```

### 4. Callback od Stripe
```
[Stripe]
  → REST: POST /webhooks/stripe
  → [Payment Service]
      → Aktualizuje payment (status: COMPLETED)
      → KAFKA: publikuje payment.completed
```

### 5. Potwierdzenie rezerwacji
```
[Booking Service] (konsumuje payment.completed)
  → Aktualizuje booking (status: CONFIRMED)
  → KAFKA: publikuje booking.confirmed

[Cinema Service] (konsumuje booking.created)
  → Zmienia status miejsc: RESERVED → SOLD

[Notification Service] (konsumuje payment.completed)
  → REST: GET /api/auth/user (lub inny endpoint) → [Auth Service] - pobranie danych użytkownika
  → REST: GET /api/screening/{id} → [Cinema Service] - pobranie danych seansu
  → Wysyła email z potwierdzeniem + QR code
```

--

## SAGA PATTERN - Obsługa błędów

### Scenariusz: Płatność nie powiodła się
```
1. Booking Service tworzy rezerwację (PENDING)
2. Payment Service: płatność FAILED
3. KAFKA: payment.failed
4. Booking Service konsumuje → anuluje rezerwację
5. KAFKA: booking.cancelled
6. Cinema Service konsumuje → zwalnia miejsca
7. Notification Service konsumuje → wysyła powiadomienie o błędzie płatności (konsumuje payment.failed)
```

### Scenariusz: Timeout rezerwacji (brak płatności)
```
1. Scheduled Job w Booking Service (co 1 min)
2. Znajduje rezerwacje gdzie expires_at < now()
3. Zmienia status: PENDING → EXPIRED
4. KAFKA: booking.expired
5. Cinema Service konsumuje → zwalnia miejsca
```