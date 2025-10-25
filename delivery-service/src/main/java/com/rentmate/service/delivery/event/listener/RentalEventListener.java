package com.rentmate.service.delivery.event.listener;

import com.rentmate.service.delivery.config.RabbitMQConfig;
import com.rentmate.service.delivery.domain.dto.event.RentalCostRequestedEventDto;
import com.rentmate.service.delivery.service.DeliveryProcessService;

import com.rentmate.service.delivery.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RentalEventListener {

        private final DeliveryCostService costService ;
        private final DeliveryProcessService processService;

        @RabbitListener(queues = RabbitMQConfig.RENTAL_TO_DELIVERY_QUEUE)

        public void handleRentalEvents(Map<String, Object> payload) {
            String eventType = (String) payload.get("eventType");

            switch (eventType) {
                case "rental.cost.requested":

                    RentalCostRequestedEventDto dto = new RentalCostRequestedEventDto();
                    dto.setRentalId(Long.valueOf(payload.get("rentalId").toString()));
                    dto.setItemId(Long.valueOf(payload.get("itemId").toString()));
                    dto.setRenterId(Long.valueOf(payload.get("renterId").toString()));
                    dto.setOwnerId(Long.valueOf(payload.get("ownerId").toString()));
                    dto.setRenterAddress(payload.get("renterAddress").toString());
                    dto.setOwnerAddress(payload.get("ownerAddress").toString());
                    costService.handleCostRequest(dto);

                break;

                case "rental.delivery.requested":
                    processService.startForward(
                            Long.valueOf(payload.get("rentalId").toString()),
                            Long.valueOf(payload.getOrDefault("renterId", "0").toString()),
                            Long.valueOf(payload.getOrDefault("ownerId", "0").toString()),
                            String.valueOf(payload.getOrDefault("renterAddress", "")),
                            String.valueOf(payload.getOrDefault("ownerAddress", "")) ,
                            LocalDateTime.parse(String.valueOf(payload.get("startDate")))



                    );
                    break;

                case "rental.return.requested":
                case "rental.return.late":
                    processService.startReturn(
                            Long.valueOf(payload.get("rentalId").toString()),
                            Long.valueOf(payload.getOrDefault("renterId", "0").toString()),
                            Long.valueOf(payload.getOrDefault("ownerId", "0").toString()) ,
                            Long.valueOf(payload.getOrDefault("itemId", "0").toString()) ,
                            String.valueOf(payload.getOrDefault("renterAddress", "")),
                            String.valueOf(payload.getOrDefault("ownerAddress", ""))
                    );
                    break;

                default:
                    log.info("no event type selected ");
                    break;
            }
        }
    }


