# auth-service

### AuthController

#### POST /api/auth/register
- Description: Rejestruje nowego użytkownika w systemie.
- Input:
  - body:
    ```json
    {
      "firstName": "Jan",
      "lastName": "Kowalski",
      "email": "jan.kowalski@example.com",
      "password": "StrongPassword123",
      "passwordConfirmed": "StrongPassword123"
    }
    ```
- Output:
  - 201 Created:
    ```json
    {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "message": "Użytkownik zarejestrowany pomyślnie"
    }
    ```

#### POST /api/auth/login
- Description: Uwierzytelnia użytkownika i zwraca tokeny dostępu.
- Input:
  - body:
    ```json
    {
      "email": "jan.kowalski@example.com",
      "password": "StrongPassword123",
      "code": 123456 // Opcjonalny kod TOTP, jeśli 2FA jest włączone
    }
    ```
- Output:
  - 200 OK:
    ```json
    {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "message": "Logowanie pomyślne"
    }
    ```

#### GET /api/auth/user
- Description: Pobiera dane uwierzytelnionego użytkownika. Wymaga autoryzacji `USER_READ`.
- Input:
  - X-User-Email: string (header) - Adres e-mail użytkownika.
- Output:
  - 200 OK:
    ```json
    {
      "id": 1,
      "email": "jan.kowalski@example.com",
      "firstName": "Jan",
      "lastName": "Kowalski",
      "roles": ["USER"],
      "permissions": ["USER_READ"],
      "phoneNumber": "+48123456789",
      "totpEnabled": false
    }
    ```

#### PUT /api/auth/user/{userId}
- Description: Aktualizuje informacje o użytkowniku.
- Input:
  - userId: Long (path) - ID użytkownika do aktualizacji.
  - body:
    ```json
    {
      "email": "nowy.email@example.com",
      "firstName": "Adam",
      "lastName": "Nowak"
    }
    ```
- Output:
  - 204 No Content: Brak zawartości.

#### POST /api/auth/totp/enable
- Description: Włącza uwierzytelnianie dwuskładnikowe (TOTP) dla użytkownika.
- Input:
  - X-User-Email: string (header) - Adres e-mail użytkownika.
- Output:
  - 200 OK:
    ```json
    {
      "secret": "JBSWY3DPEHPK3PXP",
      "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAA..." // QR Code jako obraz base64
    }
    ```

#### POST /api/auth/totp/verify
- Description: Weryfikuje kod TOTP i aktywuje uwierzytelnianie dwuskładnikowe dla użytkownika.
- Input:
  - X-User-Email: string (header) - Adres e-mail użytkownika.
  - body:
    ```json
    {
      "code": 123456
    }
    ```
- Output:
  - 204 No Content: Brak zawartości.

#### POST /api/auth/totp/disable
- Description: Wyłącza uwierzytelnianie dwuskładnikowe (TOTP) dla użytkownika.
- Input:
  - X-User-Email: string (header) - Adres e-mail użytkownika.
- Output:
  - 204 No Content: Brak zawartości.
