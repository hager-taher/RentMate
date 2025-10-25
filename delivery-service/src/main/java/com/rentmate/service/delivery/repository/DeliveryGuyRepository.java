package com.rentmate.service.delivery.repository;

import com.rentmate.service.delivery.domain.entity.DeliveryGuy;
import com.rentmate.service.delivery.domain.enumuration.DeliveryManStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryGuyRepository extends JpaRepository<DeliveryGuy, Long> {

    Optional<DeliveryGuy> findFirstByStatus(DeliveryManStatus status);
}