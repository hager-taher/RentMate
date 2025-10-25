package com.rentmate.service.delivery.domain.dto.event;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RentalDeliveryRequestedEventDto {
    private String eventType; // "rental.delivery.requested"
    private Long rentalId;
    private Long renterId;
    private Long ownerId;

    private String renterAddress;
    private String ownerAddress;
    private LocalDateTime startDate;

}