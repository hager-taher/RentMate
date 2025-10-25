package com.rentmate.service.delivery.domain.dto.event;


import lombok.Data;
import java.io.Serializable;

@Data
public class RentalReturnRequestedEventDto  {
    private String eventType; // "rental.return.requested" / "rental.return.late"
    private Long rentalId;
    private Long renterId;
    private Long ownerId;
    private Long itemId ;

    private String renterAddress;
    private String ownerAddress;

}

