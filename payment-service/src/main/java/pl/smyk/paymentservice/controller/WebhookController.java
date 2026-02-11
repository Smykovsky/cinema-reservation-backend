package pl.smyk.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.smyk.paymentservice.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PaymentService paymentService;

    @PostMapping("/stripe")
    @ResponseStatus(HttpStatus.OK)
    public String handleStripeWebhook(@RequestParam("paymentId") Long paymentId,
                                      @RequestParam("transactionId") String transactionId,
                                      @RequestParam("status") String status) {
        log.info("Received Stripe webhook for payment ID: {}, transaction ID: {}, status: {}", paymentId, transactionId, status);
        boolean success = "succeeded".equalsIgnoreCase(status);
        paymentService.handlePaymentCallback(paymentId, transactionId, success);
        return "Webhook received and processed.";
    }
    // This is a simplified webhook for demonstration.
    // In a real application, you would typically receive a JSON payload
    // and verify the signature of the webhook to ensure its authenticity.
}
