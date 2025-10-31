package com.rentmate.service.delivery.controller;

import com.rentmate.service.delivery.client.UserClient;
import com.rentmate.service.delivery.domain.dto.rest.DeliveryDetailsResponse;
import com.rentmate.service.delivery.domain.dto.rest.UserResponseDto;
import com.rentmate.service.delivery.domain.entity.Delivery;
import com.rentmate.service.delivery.domain.entity.DeliveryGuy;
import com.rentmate.service.delivery.repository.DeliveryRepository;
import com.rentmate.service.delivery.service.DeliveryProcessService;
import com.rentmate.service.delivery.shared.utility.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
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


    private final JwtUtils jwtService;
    private final DeliveryRepository deliveryRepository ;
    private final UserClient userClient;


    @GetMapping("/my")
    public ResponseEntity<List<Delivery>> getMyDeliveries(HttpServletRequest request) {
        Long userId = jwtService.getExtractedId(request);
        List<Delivery> deliveries = repository.findByAssignedDeliveryGuy_Id(userId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDetailsResponse> getDeliveryDetails(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        UserResponseDto owner = userClient.getUserById(delivery.getOwnerId(), token);
        UserResponseDto renter = userClient.getUserById(delivery.getRenterId(), token);

        DeliveryDetailsResponse response = new DeliveryDetailsResponse();
        response.setId(delivery.getId());
        response.setType(delivery.getType());
        response.setStatus(delivery.getStatus().name());
        response.setDeliveryCost(delivery.getDeliveryCost());
        response.setDeliveryGuyName(delivery.getAssignedDeliveryGuy() != null
                ? delivery.getAssignedDeliveryGuy().getName()
                : "Not Assigned");

        response.setCreatedDate(delivery.getCreatedDate());
        response.setLastModifiedDate(delivery.getLastModifiedDate());

        if ("FORWARD".equalsIgnoreCase(delivery.getType())) {
            // من الـ Owner إلى الـ Renter
            response.setPickupName(owner.getName());
            response.setPickupAddress(delivery.getOwnerAddress());
            response.setPickupPhone(owner.getPhone());

            response.setDropoffName(renter.getName());
            response.setDropoffAddress(delivery.getRenterAddress());
            response.setDropoffPhone(renter.getPhone());
        } else {
            // من الـ Renter إلى الـ Owner
            response.setPickupName(renter.getName());
            response.setPickupAddress(delivery.getRenterAddress());
            response.setPickupPhone(renter.getPhone());

            response.setDropoffName(owner.getName());
            response.setDropoffAddress(delivery.getOwnerAddress());
            response.setDropoffPhone(owner.getPhone());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<Delivery>> byRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(repository.findByRentalId(rentalId));
    }

    @PostMapping("/{deliveryId}/complete")
    public ResponseEntity<Void> completeDelivery(
            @PathVariable Long deliveryId) {
           deliveryProcessService.handleDeliveryAction(deliveryId);
        return ResponseEntity.ok().build();
    }


}

