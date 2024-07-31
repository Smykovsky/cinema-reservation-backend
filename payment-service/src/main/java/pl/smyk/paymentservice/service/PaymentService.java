package pl.smyk.paymentservice.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Coupon;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentConfirmParams.PaymentMethodOptions.Blik;
import org.springframework.stereotype.Service;
import pl.smyk.paymentservice.dto.response.PaymentResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.stripe.param.PaymentIntentConfirmParams.PaymentMethodData.Type.BLIK;

@Service
public class PaymentService {
    public PaymentIntent findPaymentIntentById(String id) throws StripeException {
        PaymentIntent retrieve = PaymentIntent.retrieve(id);
        if (retrieve == null) {
            return null;
        }
        return retrieve;

    }

    public PaymentIntent createPaymentIntent(String customerEmail, String reservationId, Double amount) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int) (amount * 100));
        params.put("currency", "PLN");
        params.put("payment_method_types", Collections.singletonList("blik"));
        params.put("confirmation_method", "manual");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("reservation_id", reservationId);
        metadata.put("customer_email", customerEmail);
        params.put("metadata", metadata);


        return PaymentIntent.create(params);
    }

    public String confirmBlikPayment(String paymentId, String code) {
        try {
            PaymentIntent paymentIntent = findPaymentIntentById(paymentId);

            PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                    .setPaymentMethodData(PaymentIntentConfirmParams.PaymentMethodData.builder().setType(BLIK).build())
                    .setPaymentMethodOptions(PaymentIntentConfirmParams.PaymentMethodOptions.builder()
                            .setBlik(Blik.builder().setCode(code).build())
                            .build())
                    .build();

            paymentIntent.confirm(confirmParams);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted during sleep", e);
            }

            return pollPaymentStatus(paymentId);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private String pollPaymentStatus(String paymentId) {
        try {
            PaymentIntent paymentIntent = findPaymentIntentById(paymentId);

            String status = paymentIntent.getStatus();
            return switch (status) {
                case "succeeded" ->
                        "success";
                case "requires_action" ->
                        "in_process";
                case "payment_failed" ->
                        "failed";
                default -> "undefined_error";
            };
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyPayment(String paymentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
        return "succeeded".equals(paymentIntent.getStatus());
    }

    public Coupon createGiftCard(Double amount, String customerEmail) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount_off", (int) (amount * 100));
        params.put("currency", "PLN");
        params.put("duration", "forever");
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("assigned_to", customerEmail);
        params.put("meta_data", metaData);
        Coupon coupon = Coupon.create(params);
        System.out.println(coupon);
        return coupon;
    }
}
