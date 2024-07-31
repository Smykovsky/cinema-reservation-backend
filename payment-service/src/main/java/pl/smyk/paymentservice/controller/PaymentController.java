package pl.smyk.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.paymentservice.FeignClient.AuthServiceClient;
import pl.smyk.paymentservice.FeignClient.ReservationServiceClient;
import pl.smyk.paymentservice.dto.request.PaymentAcceptRequest;
import pl.smyk.paymentservice.dto.request.PaymentRequest;
import pl.smyk.paymentservice.dto.response.PaymentResponse;
import pl.smyk.paymentservice.mapper.ResponseEntityMapper;
import pl.smyk.paymentservice.service.PaymentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final ReservationServiceClient reservationServiceClient;
    private final AuthServiceClient authServiceClient;
    private final ResponseEntityMapper responseMapper;


    @PostMapping("/create-payment/{reservationId}")
    public ResponseEntity<?> createPaymentIntent(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String reservationId) {
        ResponseEntity<?> customerResponse = authServiceClient.getCustomer(authorizationHeader);
        HashMap<String, Object> customerDto = responseMapper.convertResponseToMap(customerResponse);
        if (customerDto == null) {
            return ResponseEntity.status(404).body("Customer not found!");
        }

        ResponseEntity<?> reservationResponse = reservationServiceClient.getLoggedUserReservationById(authorizationHeader, reservationId);

        HashMap<String, Object> reservationDto = responseMapper.convertResponseToMap(reservationResponse);

        if (reservationDto == null) {
            return ResponseEntity.status(404).body("Reservation not found!");
        }

        String customerEmail = String.valueOf(reservationDto.get("customerEmail"));
        String id = String.valueOf(reservationDto.get("id"));
        double totalPrice = Double.parseDouble(reservationDto.get("totalPrice").toString());

        PaymentIntent paymentIntent = null;
        try {
            paymentIntent = paymentService.createPaymentIntent(customerEmail, id, totalPrice);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(paymentIntent.getId());

    }

    @PostMapping("/accept-payment")
    public PaymentResponse confirmPayment(@RequestHeader("Authorization") String authorizationHeader, @RequestBody PaymentAcceptRequest request) throws StripeException {
        ResponseEntity<?> customerResponse = authServiceClient.getCustomer(authorizationHeader);
        HashMap<String, Object> customerDto = responseMapper.convertResponseToMap(customerResponse);
        if (customerDto == null) {
            return PaymentResponse.builder().message("User not found!").build();
        }

        PaymentIntent paymentIntentById = paymentService.findPaymentIntentById(request.getPaymentId());
        String reservationIdFromPayment = paymentIntentById.getMetadata().get("reservation_id");

        String paymentResponse = paymentService.confirmBlikPayment(request.getPaymentId(), request.getBlikCode());

        if (paymentResponse.equals("success")){
            reservationServiceClient.updatePaymentStatusAsPaid(authorizationHeader, reservationIdFromPayment);
            return PaymentResponse.builder().paymentId(request.getPaymentId()).message("Successfuly paid for reservation!").build();
        } else if (paymentResponse.equals("in_process")) {
            return PaymentResponse.builder().paymentId(request.getPaymentId()).message("Payment is in process!").build();
        } else if (paymentResponse.equals("failed")) {
            return PaymentResponse.builder().paymentId(request.getPaymentId()).message("Payment process failed!").build();
        } else return PaymentResponse.builder().paymentId(request.getPaymentId()).message("Unknow payment process error!").build();
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getPaymentList(@RequestHeader("Authorization") String authorizationHeader) throws StripeException {
        ResponseEntity<?> customerResponse = authServiceClient.getCustomer(authorizationHeader);
        HashMap<String, Object> customerDto = responseMapper.convertResponseToMap(customerResponse);
        if (customerDto == null)  {
          return ResponseEntity.status(404).body("Customer not found!");
        }

        List<PaymentIntent> userPaymentsByEmail = paymentService.getUserPaymentsByEmail(customerDto.get("email").toString());
        return ResponseEntity.ok(userPaymentsByEmail);
    }
}
