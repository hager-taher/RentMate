package com.rentmate.service.delivery.service;


import com.rentmate.service.delivery.domain.entity.DeliveryGuy;
import com.rentmate.service.delivery.domain.enumuration.DeliveryManStatus;
import com.rentmate.service.delivery.event.publisher.DeliveryEventPublisher;
import com.rentmate.service.delivery.repository.DeliveryGuyRepository;
import com.rentmate.service.delivery.repository.DeliveryRepository;
import com.rentmate.service.delivery.domain.entity.Delivery;
import com.rentmate.service.delivery.domain.enumuration.DeliveryStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryProcessService {

    private final DeliveryEventPublisher publisher;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryGuyRepository deliveryGuyRepository;


    public void startForward(Long rentalId, Long renterId, Long ownerId ,String renterAddress , String ownerAddress , LocalDateTime startDate) {
        DeliveryGuy guy = deliveryGuyRepository.findFirstByStatus(DeliveryManStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("No available delivery guy"));

        Delivery delivery = Delivery.builder()
                .rentalId(rentalId)
                .renterId(renterId)
                .ownerId(ownerId)
                .status(DeliveryStatus.SCHEDULED)
                .type("FORWARD")
                .createdDate(new Date())
                .renterAddress(renterAddress)
                .ownerAddress(ownerAddress)
                .scheduledStartTime(startDate)
                .assignedDeliveryGuy(guy) // assigned
                .build();

        guy.setStatus(DeliveryManStatus.ASSIGNED);
        guy.setLastUpdated(LocalDateTime.now());
        deliveryGuyRepository.save(guy);

        deliveryRepository.save(delivery);



        //publisher.publishStatus("delivery.status.updated", rentalId, "OUT_FOR_DELIVERY");
    }
    @Scheduled(fixedRate = 600000) // كل 10 دقايق
    public void checkAndStartDeliveries() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        // هات كل الدليفريز اللي المفروض تبدأ بعد ساعة أو أقل ولسه ما بدأتش
        List<Delivery> dueDeliveries = deliveryRepository
                .findAllByStatusAndScheduledStartTimeBetween(
                        DeliveryStatus.SCHEDULED, now, oneHourLater);

        for (Delivery delivery : dueDeliveries) {
            startDelivery(delivery);
        }
    }

    private void startDelivery(Delivery delivery) {
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        delivery.setStartDate(LocalDateTime.now());

        DeliveryGuy guy = delivery.getAssignedDeliveryGuy();
        if (guy != null) {
            guy.setStatus(DeliveryManStatus.ASSIGNED);
            guy.setLastUpdated(LocalDateTime.now());
            deliveryGuyRepository.save(guy);
        }
        deliveryRepository.save(delivery);

        // ممكن كمان تبعتي نوتيفيكيشن أو ميسج لـ RabbitMQ
        // deliveryEventPublisher.publishStartEvent(delivery);
    }
    public void completeForward(Long rentalId) {
        // update last delivery related to rental
        deliveryRepository.findByRentalId(rentalId).stream().findFirst().ifPresent(d -> {
            d.setStatus(DeliveryStatus.DELIVERED);
            d.setLastModifiedDate(new Date());
            deliveryRepository.save(d);


            DeliveryGuy guy = d.getAssignedDeliveryGuy();
            if (guy != null) {
                guy.setStatus(DeliveryManStatus.AVAILABLE);
                guy.setLastUpdated(LocalDateTime.now());
                deliveryGuyRepository.save(guy);
            }
        });
        publisher.publishStatus("delivery.delivered", rentalId);
    }

    public void startReturn(Long rentalId, Long renterId, Long ownerId , Long itemId , String renterAddress , String ownerAddress ) {
        DeliveryGuy guy = deliveryGuyRepository.findFirstByStatus(DeliveryManStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("No available delivery guy"));

        Delivery delivery = Delivery.builder()
                .rentalId(rentalId)
                .renterId(renterId)
                .ownerId(ownerId)
                .itemId(itemId)
                .status(DeliveryStatus.IN_RETURNING)
                .type("RETURN")
                .createdDate(new Date())
                .ownerAddress(ownerAddress)
                .renterAddress(renterAddress)
                .assignedDeliveryGuy(guy) //assigned
                .build();


        guy.setStatus(DeliveryManStatus.ASSIGNED);
        guy.setLastUpdated(LocalDateTime.now());
        deliveryGuyRepository.save(guy);

        deliveryRepository.save(delivery);



        publisher.publishStatus("delivery.inReturning", rentalId);
    }

    public void completeReturn(Long rentalId) {
        deliveryRepository.findByRentalId(rentalId).stream().findFirst().ifPresent(d -> {
            d.setStatus(DeliveryStatus.RETURNED);
            d.setLastModifiedDate(new Date());
            deliveryRepository.save(d);
            DeliveryGuy guy = d.getAssignedDeliveryGuy();
            if (guy != null) {
                guy.setStatus(DeliveryManStatus.AVAILABLE);
                guy.setLastUpdated(LocalDateTime.now());
                deliveryGuyRepository.save(guy);
            }
        });

        publisher.publishStatus("delivery.returned", rentalId);
    }
}
