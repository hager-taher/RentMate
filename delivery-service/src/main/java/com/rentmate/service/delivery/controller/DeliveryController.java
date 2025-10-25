package com.rentmate.service.delivery.controller;

import com.rentmate.service.delivery.domain.entity.Delivery;
import com.rentmate.service.delivery.repository.DeliveryRepository;
import com.rentmate.service.delivery.service.DeliveryProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryRepository repository;
    private final DeliveryProcessService deliveryProcessService;


    @GetMapping
    public ResponseEntity<List<Delivery>> all() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<Delivery>> byRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(repository.findByRentalId(rentalId));
    }

    @PostMapping("/{rentalId}/complete")
    public ResponseEntity<Void> completeDelivery(
            @PathVariable Long rentalId,
            @RequestParam String type // FORWARD or RETURN
    ) {
        if ("FORWARD".equalsIgnoreCase(type)) {
            deliveryProcessService.completeForward(rentalId);
        } else if ("RETURN".equalsIgnoreCase(type)) {
            deliveryProcessService.completeReturn(rentalId);
        }
        return ResponseEntity.ok().build();
    }


}

