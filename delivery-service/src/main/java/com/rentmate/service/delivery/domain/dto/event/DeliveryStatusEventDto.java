package com.rentmate.service.delivery.domain.dto.event;

import lombok.Data;
import java.io.Serializable;

@Data
public class DeliveryStatusEventDto {
    private String eventType;  // delivery.delivered // delivery.inReturning // delivery.returned
    private Long rentalId;
  //  private String status;
}

