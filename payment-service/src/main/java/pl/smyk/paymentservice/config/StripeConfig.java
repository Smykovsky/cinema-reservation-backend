package pl.smyk.paymentservice.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Value("${STRIPE_SECRET_KEY}")
    private String secretApiKey;
    @Value("${STRIPE_PUBLIC_KEY}")
    private String publicApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretApiKey;
    }
}
