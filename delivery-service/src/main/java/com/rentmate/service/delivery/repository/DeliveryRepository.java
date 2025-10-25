package com.rentmate.service.delivery.repository;

import com.rentmate.service.delivery.domain.entity.Delivery;
import com.rentmate.service.delivery.domain.enumuration.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByRentalId(Long rentalId);
    List<Delivery> findAllByStatusAndScheduledStartTimeBetween(
            DeliveryStatus status,
            LocalDateTime from,
            LocalDateTime to
    );
    List<Delivery> findByAssignedDeliveryGuyId(Long deliveryGuyId);

}
