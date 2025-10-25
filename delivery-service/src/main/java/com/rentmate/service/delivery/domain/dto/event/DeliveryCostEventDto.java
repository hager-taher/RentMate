package com.rentmate.service.delivery.domain.dto.event;

import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

public class DeliveryCostEventDto{
    private String eventType; // "delivery.deliveryCost"
    private Long rentalId;
   // private Long renterId;
    private BigDecimal deliveryCost;
}
