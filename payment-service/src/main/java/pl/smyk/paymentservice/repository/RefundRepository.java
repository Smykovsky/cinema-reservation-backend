package pl.smyk.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.paymentservice.model.Refund;

import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
}
