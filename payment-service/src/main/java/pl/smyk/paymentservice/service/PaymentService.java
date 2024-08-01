package pl.smyk.paymentservice.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Coupon;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentConfirmParams.PaymentMethodOptions.Blik;
import com.stripe.param.PaymentIntentListParams;
import org.springframework.stereotype.Service;
import pl.smyk.paymentservice.dto.response.PaymentResponse;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.stripe.param.PaymentIntentConfirmParams.PaymentMethodData.Type.BLIK;

@Service
public class PaymentService {
  private int RETRY_COUNT = 0;
  private final int MAX_RETRY_COUNT = 12;
  private final int DELAY_MILLIS = 2000;

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

            if (!code.equals("123456")) {
                return "blik_code_error";
            }
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
      PaymentIntent paymentIntent;
      String status;

      do {
        paymentIntent = findPaymentIntentById(paymentId);
        status = paymentIntent.getStatus();

        if (status.equals("succeeded")) {
          return "success";
        } else if (status.equals("in_process")){
        return "in_process";
        } else if (status.equals("payment_failed")) {
          return "failed";
        }

        RETRY_COUNT++;
        if (RETRY_COUNT >= MAX_RETRY_COUNT) {
          return "undefined_error";
        }

        Thread.sleep(DELAY_MILLIS);
      } while (!"succeeded".equals(status) || !"payment_failed".equals(status));

      return "undefined_error";
    } catch (StripeException | InterruptedException e) {
      throw new RuntimeException(e);
    }
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

  public List<PaymentIntent> getUserPaymentsByEmail(String email) throws StripeException {
    List<PaymentIntent> userPayments = new ArrayList<>();
    String startingAfter = null;

    do {
      PaymentIntentListParams params = PaymentIntentListParams.builder()
        .setLimit(100L)
        .setStartingAfter(startingAfter)
        .build();

      PaymentIntentCollection paymentIntents = PaymentIntent.list(params);

      for (PaymentIntent paymentIntent : paymentIntents.getData()) {
        String metadataEmail = paymentIntent.getMetadata().get("customer_email");
        if (metadataEmail != null && metadataEmail.trim().equalsIgnoreCase(email.trim())) {
          userPayments.add(paymentIntent);
        }
      }

      startingAfter = paymentIntents.getData().isEmpty() ? null : paymentIntents.getData().get(paymentIntents.getData().size() - 1).getId();
    } while (startingAfter != null);

    return userPayments;
  }
}
