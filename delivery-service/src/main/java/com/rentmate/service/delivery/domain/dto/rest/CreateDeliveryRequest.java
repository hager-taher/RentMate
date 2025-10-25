package com.rentmate.service.delivery.domain.dto.rest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDeliveryRequest {

    @NotNull
    private Long rentalId;

    @NotNull
    private Long renterId;

    @NotNull
    private Long ownerId;

    @NotBlank
    private String type; // FORWARD / RETURN
}
