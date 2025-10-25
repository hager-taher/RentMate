package com.rentmate.service.delivery.domain.dto.event;


import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalCostRequestedEventDto {
    private String eventType; // "rental.cost.requested"
    private Long rentalId;
    private Long renterId;
    private Long ownerId;
    private Long itemId ;

    private String renterAddress;
    private String ownerAddress;


}
