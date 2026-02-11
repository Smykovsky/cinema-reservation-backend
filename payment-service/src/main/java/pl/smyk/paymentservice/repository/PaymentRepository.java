package pl.smyk.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.paymentservice.model.Payment;



@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
