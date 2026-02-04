package pl.smyk.authservice.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.smyk.authservice.dto.TotpEnableResponse;
import pl.smyk.authservice.exception.InvalidTotpCodeException;
import pl.smyk.authservice.exception.TotpAlreadyEnabledException;
import pl.smyk.authservice.exception.TotpNotConfiguredException;
import pl.smyk.authservice.model.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class TotpService {
    private final UserService userService;

    /**
     * Włącza TOTP dla użytkownika - generuje secret i QR kod
     */
    public TotpEnableResponse enableTotp(String email) {
        User user = userService.findByEmail(email);

        if (user.isTotpEnabled()) {
            throw new TotpAlreadyEnabledException("TOTP jest już włączony dla tego użytkownika");
        }

        String secret = generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(false);
        userService.save(user);

        String qrCode = generateQrCode(secret, email);
        log.info("TOTP enabled for user: {}", email);

        return new TotpEnableResponse(secret, qrCode);
    }

    /**
     * Weryfikuje kod TOTP i finalnie włącza 2FA
     */
    public void verifyAndActivateTotp(String email, int code) {
        User user = userService.findByEmail(email);

        if (user.getTotpSecret() == null) {
            throw new TotpNotConfiguredException("TOTP nie został skonfigurowany. Użyj najpierw /totp/enable");
        }

        if (!verifyCode(user.getTotpSecret(), code)) {
            throw new InvalidTotpCodeException("Wprowadzono niepoprawny kod TOTP");
        }

        user.setTotpEnabled(true);
        userService.save(user);
        log.info("TOTP verified and activated for user: {}", email);
    }

    /**
     * Wyłącza TOTP dla użytkownika
     */
    public void disableTotp(String email) {
        User user = userService.findByEmail(email);

        if (!user.isTotpEnabled() && user.getTotpSecret() == null) {
            throw new TotpNotConfiguredException("TOTP nie jest włączony");
        }

        user.setTotpEnabled(false);
        user.setTotpSecret(null);
        userService.save(user);
        log.info("TOTP disabled for user: {}", email);
    }

    /**
     * Weryfikuje kod TOTP podczas logowania
     */
    public void validateTotpForLogin(User user, int code) {
        if (code == 0) {
            throw new InvalidTotpCodeException("Kod TOTP jest wymagany");
        }

        if (!verifyCode(user.getTotpSecret(), code)) {
            throw new InvalidTotpCodeException("Wprowadzono niepoprawny kod TOTP");
        }
    }

    // ===== Metody pomocnicze (private) =====

    private String generateSecret() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    private String generateQrCode(String secret, String email) {
        final GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secret).build();
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("Cinema-Reservation", email, key);
    }

    private boolean verifyCode(String secret, int code) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(secret, code);
    }
}