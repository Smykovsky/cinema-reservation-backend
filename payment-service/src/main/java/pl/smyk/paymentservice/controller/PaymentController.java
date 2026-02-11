package pl.smyk.paymentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import pl.smyk.paymentservice.dto.PaymentInitializationRequest;
import pl.smyk.paymentservice.dto.PaymentResponse;
import pl.smyk.paymentservice.dto.RefundRequest;
import pl.smyk.paymentservice.dto.RefundResponse;
import pl.smyk.paymentservice.dto.BlikConfirmRequest;
import pl.smyk.paymentservice.dto.BlikConfirmResponse;
import pl.smyk.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;



    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse initializePayment(@Valid @RequestBody PaymentInitializationRequest request) {
        return paymentService.initializePayment(request);
    }

    @PostMapping("/{id}/refund")
    @ResponseStatus(HttpStatus.OK)
    public RefundResponse refundPayment(@PathVariable("id") Long paymentId, @Valid @RequestBody RefundRequest request) {
        return paymentService.initiateRefund(paymentId, request);
    }

    @PostMapping("/{id}/confirm-blik")
    @ResponseStatus(HttpStatus.OK)
    public BlikConfirmResponse confirmBlikPayment(@PathVariable("id") Long paymentId, @Valid @RequestBody BlikConfirmRequest request) {
        return paymentService.confirmBlikPayment(paymentId, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse getPaymentDetails(@PathVariable("id") Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }
}