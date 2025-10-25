package com.rentmate.service.delivery.event.listener;

import com.rentmate.service.delivery.domain.dto.event.UserRegisteredEventDto;
import com.rentmate.service.delivery.domain.entity.DeliveryGuy;
import com.rentmate.service.delivery.domain.enumuration.DeliveryManStatus;
import com.rentmate.service.delivery.repository.DeliveryGuyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final DeliveryGuyRepository deliveryGuyRepository;

    @RabbitListener(queues = "user.delivery.queue")
    public void handleUserRegistered(UserRegisteredEventDto event) {

        if (!"DELIVERY_GUY".equalsIgnoreCase(event.role())) {
            return;
        }

        // لو موجود خلاص نخرج
        if (deliveryGuyRepository.existsById(event.userId())) {
            return;
        }

        // نحفظ الدليفري مان الجديد
        DeliveryGuy deliveryGuy = DeliveryGuy.builder()
                .id(event.userId())
                .name(event.username())
                .phone(event.phone())
                .status(DeliveryManStatus.AVAILABLE)
                .activeDeliveries(0)
                .lastUpdated(LocalDateTime.now())
                .build();

        deliveryGuyRepository.save(deliveryGuy);
        System.out.println("✅ Added new delivery guy: " + event.username());
    }
}
