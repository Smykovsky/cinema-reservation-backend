package pl.smyk.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.smyk.paymentservice.service.PaymentService;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;



@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PaymentService paymentService;

    @Value("${STRIPE_WEBHOOK_SECRET}") // This needs to be configured in application.yml or config-server
    private String stripeWebhookSecret;

    @PostMapping("/stripe")
    @ResponseStatus(HttpStatus.OK)
    public String handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            // It is recommended to use the Webhook.constructEvent method for security.
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
            log.info("Received Stripe webhook event of type: {}", event.getType());
        } catch (SignatureVerificationException e) {
            log.warn("Webhook signature verification failed: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Stripe webhook signature.");
        }

        // Deserialize the event object and handle different event types
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "PaymentIntent object not found in webhook event."));
                paymentService.handlePaymentCallback(paymentIntent.getId(), paymentIntent.getStatus());
                break;
            case "payment_intent.payment_failed":
                paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "PaymentIntent object not found in webhook event."));
                paymentService.handlePaymentCallback(paymentIntent.getId(), paymentIntent.getStatus());
                break;
            case "charge.refunded": // Handle refunds via webhook
                Refund stripeRefund = (Refund) event.getDataObjectDeserializer().getObject()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refund object not found in webhook event."));
                // In a real scenario, you'd find the internal refund record and update its status.
                // For now, we'll just log and assume PaymentService.initiateRefund already handled the internal status.
                log.info("Stripe Refund event received for Refund ID: {}, PaymentIntent ID: {}", stripeRefund.getId(), stripeRefund.getPaymentIntent());
                // You might need a separate service method to handle refund status updates based on webhook
                // paymentService.handleRefundCallback(stripeRefund.getId(), stripeRefund.getStatus());
                break;
            // Handle other event types as needed
            default:
                log.warn("Unhandled event type: {}", event.getType());
                break;
        }

        return "Webhook received and processed.";
    }
}
