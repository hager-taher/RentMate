package com.rentmate.service.delivery.domain.dto.rest;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class DeliveryDetailsResponse {
    private Long id;
    private String type;
    private String status;

    private String pickupName;
    private String pickupAddress;
    private String pickupPhone;

    private String dropoffName;
    private String dropoffAddress;
    private String dropoffPhone;

    private String deliveryGuyName;
    private BigDecimal deliveryCost;

    private Date createdDate;
    private Date lastModifiedDate;
}
