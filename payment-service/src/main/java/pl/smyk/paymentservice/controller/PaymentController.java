package pl.smyk.paymentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.paymentservice.dto.PaymentInitializationRequest;
import pl.smyk.paymentservice.dto.PaymentResponse;
import pl.smyk.paymentservice.dto.RefundRequest;
import pl.smyk.paymentservice.dto.RefundResponse;
import pl.smyk.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("")
    public ResponseEntity<PaymentResponse> initializePayment(@Valid @RequestBody PaymentInitializationRequest request) {
        PaymentResponse paymentResponse = paymentService.initializePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<RefundResponse> refundPayment(@PathVariable("id") Long paymentId, @Valid @RequestBody RefundRequest request) {
        RefundResponse refundResponse = paymentService.initiateRefund(paymentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(refundResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(@PathVariable("id") Long paymentId) {
        PaymentResponse paymentResponse = paymentService.getPaymentById(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);
    }
}