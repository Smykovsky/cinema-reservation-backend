# System Rezerwacji Biletów - Architektura Mikroserwisów

## 1. USER SERVICE

### Model danych
```sql
Users
- id: UUID (PK)
- email: VARCHAR(255) UNIQUE
- password_hash: VARCHAR(255)
- first_name: VARCHAR(100)
- last_name: VARCHAR(100)
- phone: VARCHAR(20)
- created_at: TIMESTAMP
```

### REST API
- `POST /auth/register` - rejestracja
- `POST /auth/login` - logowanie (zwraca JWT)
- `GET /users/{id}` - profil użytkownika
- `PUT /users/{id}` - aktualizacja profilu

### Kafka Events (Publisher)
- `user.registered` - nowy użytkownik

---

## 2. MOVIE SERVICE

### Model danych
```sql
Movies
- id: UUID (PK)
- title: VARCHAR(255)
- description: TEXT
- duration_minutes: INT
- release_date: DATE
- genres: VARCHAR[] (array)
- poster_url: VARCHAR(500)
- age_rating: VARCHAR(10)

Genres
- id: UUID (PK)
- name: VARCHAR(50)
```

### REST API
- `GET /movies` - lista filmów (filtrowanie, paginacja)
- `GET /movies/{id}` - szczegóły filmu
- `POST /movies` - dodaj film (admin)

### Kafka Events
Brak - read-only dla użytkowników

---

## 3. CINEMA SERVICE

### Model danych
```sql
Cinemas
- id: UUID (PK)
- name: VARCHAR(255)
- address: VARCHAR(500)
- city: VARCHAR(100)

Halls
- id: UUID (PK)
- cinema_id: UUID (FK → Cinemas)
- name: VARCHAR(100)
- total_seats: INT

Seats
- id: UUID (PK)
- hall_id: UUID (FK → Halls)
- row: VARCHAR(5)
- number: INT
- seat_type: ENUM('STANDARD', 'VIP', 'WHEELCHAIR')
```

### REST API
- `GET /cinemas` - lista kin
- `GET /cinemas/{id}/halls` - sale w kinie
- `GET /halls/{id}/seats` - miejsca w sali
- `POST /cinemas` - dodaj kino (admin)

### Kafka Events
Brak - struktura statyczna

---

## 4. SCREENING SERVICE

### Model danych
```sql
Screenings
- id: UUID (PK)
- movie_id: UUID (reference → Movie Service)
- hall_id: UUID (reference → Cinema Service)
- start_time: TIMESTAMP
- base_price: DECIMAL(10,2)

ScreeningSeats
- id: UUID (PK)
- screening_id: UUID (FK → Screenings)
- seat_id: UUID (reference → Cinema Service)
- status: ENUM('AVAILABLE', 'RESERVED', 'SOLD')
- reserved_until: TIMESTAMP (nullable)
```

### REST API
- `GET /screenings?movie_id=&cinema_id=&date=` - seanse
- `GET /screenings/{id}/seats` - dostępność miejsc
- `PUT /screenings/{id}/seats/reserve` - tymczasowa rezerwacja (internal)
- `PUT /screenings/{id}/seats/release` - zwolnienie miejsc (internal)

### Kafka Events (Consumer)
- `booking.confirmed` → zmienia status miejsc na SOLD
- `booking.cancelled` → zwalnia miejsca
- `booking.expired` → zwalnia miejsca

---

## 5. BOOKING SERVICE ⭐ (Core)

### Model danych
```sql
Bookings
- id: UUID (PK)
- user_id: UUID (reference → User Service)
- screening_id: UUID (reference → Screening Service)
- booking_number: VARCHAR(20) UNIQUE
- status: ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')
- total_amount: DECIMAL(10,2)
- expires_at: TIMESTAMP
- created_at: TIMESTAMP

BookingSeats
- id: UUID (PK)
- booking_id: UUID (FK → Bookings)
- seat_id: UUID (reference → Cinema Service)
- price: DECIMAL(10,2)
```

### REST API
- `POST /bookings` - rozpocznij rezerwację
  ```json
  {
    "screening_id": "uuid",
    "seat_ids": ["uuid1", "uuid2"]
  }
  ```
- `GET /bookings/{id}` - szczegóły rezerwacji
- `DELETE /bookings/{id}` - anuluj rezerwację

### Komunikacja REST (wywołuje synchronicznie)
- `User Service: GET /users/{id}` - walidacja użytkownika
- `Cinema Service Service: GET /screenings/{id}` - dane seansu
- `Cinema Service: PUT /screenings/{id}/seats/reserve` - blokada miejsc
- `Payment Service: POST /payments` - inicjalizacja płatności

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
- `booking.confirmed` → po udanej płatności
- `booking.cancelled` → użytkownik anulował
- `booking.expired` → upłynął timeout (15 min)

### Kafka Events (Consumer)
- `payment.completed` → zmienia status na CONFIRMED
- `payment.failed` → anuluje rezerwację

---

## 6. PAYMENT SERVICE

### Model danych
```sql
Payments
- id: UUID (PK)
- booking_id: UUID (reference → Booking Service)
- amount: DECIMAL(10,2)
- status: ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')
- payment_method: VARCHAR(50)
- provider_transaction_id: VARCHAR(255)
- created_at: TIMESTAMP

Refunds
- id: UUID (PK)
- payment_id: UUID (FK → Payments)
- amount: DECIMAL(10,2)
- status: ENUM('PENDING', 'COMPLETED', 'FAILED')
```

### REST API
- `POST /payments` - inicjalizacja płatności
  ```json
  {
    "booking_id": "uuid",
    "amount": 50.00,
    "payment_method": "CARD"
  }
  ```
- `POST /payments/{id}/refund` - zwrot pieniędzy
- `POST /webhooks/stripe` - callback od providera płatności

### Kafka Events (Publisher)
- `payment.completed` → płatność udana
- `payment.failed` → płatność nieudana
- `payment.refunded` → zwrot wykonany

---

## 7. NOTIFICATION SERVICE

### Model danych
```sql
Notifications
- id: UUID (PK)
- user_id: UUID (reference → User Service)
- type: ENUM('EMAIL', 'SMS')
- template: VARCHAR(100)
- status: ENUM('PENDING', 'SENT', 'FAILED')
- sent_at: TIMESTAMP
```

### REST API
Brak (tylko wewnętrzne)

### Kafka Events (Consumer)
- `booking.confirmed` → wyślij email z potwierdzeniem + QR code
- `booking.cancelled` → wyślij email o anulowaniu
- `payment.failed` → wyślij powiadomienie o błędzie

---

## PRZEPŁYW DANYCH - Rezerwacja biletu

### 1. Użytkownik wybiera seans
```
[Frontend] 
  → REST: GET /screenings?movie_id=X&date=Y
  → [Screening Service]
```

### 2. Użytkownik wybiera miejsca i tworzy rezerwację
```
[Frontend]
  → REST: POST /bookings {screening_id, seat_ids}
  → [Booking Service]
      → REST: GET /users/{id} → [User Service]
      → REST: PUT /screenings/{id}/seats/reserve → [Screening Service]
      → Tworzy booking (status: PENDING)
      → KAFKA: publikuje booking.created
```

### 3. Płatność
```
[Frontend]
  → REST: POST /payments {booking_id, amount}
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

[Screening Service] (konsumuje booking.confirmed)
  → Zmienia status miejsc: RESERVED → SOLD

[Notification Service] (konsumuje booking.confirmed)
  → REST: GET /users/{id} → [User Service]
  → REST: GET /screenings/{id} → [Screening Service]
  → Wysyła email z QR code
```

---

## WZORCE KOMUNIKACJI

### REST (Synchroniczna) - używamy gdy:
- Potrzebujemy natychmiastowej odpowiedzi
- Walidacja danych (czy user istnieje?)
- Pobieranie danych (lista filmów, szczegóły seansu)
- Operacje transakcyjne (rezerwacja miejsc)

### Kafka (Asynchroniczna) - używamy gdy:
- Zdarzenie, na które reaguje wiele serwisów
- Nie potrzebujemy natychmiastowej odpowiedzi
- Event-driven architecture
- Rozluźnione powiązanie między serwisami

---

## SAGA PATTERN - Obsługa błędów

### Scenariusz: Płatność nie powiodła się
```
1. Booking Service tworzy rezerwację (PENDING)
2. Payment Service: płatność FAILED
3. KAFKA: payment.failed
4. Booking Service konsumuje → anuluje rezerwację
5. KAFKA: booking.cancelled
6. Screening Service konsumuje → zwalnia miejsca
7. Notification Service konsumuje → wysyła email o błędzie
```

### Scenariusz: Timeout rezerwacji (brak płatności)
```
1. Scheduled Job w Booking Service (co 1 min)
2. Znajduje rezerwacje gdzie expires_at < now()
3. Zmienia status: PENDING → EXPIRED
4. KAFKA: booking.expired
5. Screening Service konsumuje → zwalnia miejsca
```

---

## TECHNOLOGIE

**Backend:** Spring Boot (Java)
**Database:** PostgreSQL
**Message Broker:** Apache Kafka
**API Gateway:** Spring Cloud Gateway / Kong
**Cache:** Redis (dla Screening Service - dostępność miejsc)
**Container:** Docker + Kubernetes

---

## PORZĄDEK IMPLEMENTACJI

1. User Service (autentykacja JWT)
2. Movie Service (prosty CRUD)
3. Cinema Service (CRUD + relacje)
4. Screening Service (agregacja danych, cache)
5. Booking Service (Saga pattern, Kafka)
6. Payment Service (Stripe integration)
7. Notification Service (email templates)