package com.rentmate.service.delivery.domain.dto.rest;


import com.rentmate.service.delivery.domain.enumuration.DeliveryStatus;

import lombok.Data;

import java.time.Instant;

@Data
public class DeliveryResponse {

    private Long id;
    private Long rentalId;
    private Long renterId;
    private Long ownerId;
    private DeliveryStatus status;
    private Double cost;
    private String type;
    private Instant createdAt;
    private Instant updatedAt;
}
