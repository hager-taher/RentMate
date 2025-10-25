package com.rentmate.service.delivery.domain.entity;

import com.rentmate.service.delivery.domain.enumuration.DeliveryManStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "delivery_guy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryGuy {

    @Id
    private Long id;

    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private DeliveryManStatus status;

    private int activeDeliveries;

    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "assignedDeliveryGuy", cascade = CascadeType.ALL)
    private List<Delivery> deliveries;

}
