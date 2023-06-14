package com.metanet.metabus.bus.repository;

import com.metanet.metabus.bus.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByImpUid(String impUid);
}
