package com.rentmate.service.delivery.event.publisher;

import com.rentmate.service.delivery.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange = "rental.exchange";
    private final String DELIVERY_COST_ROUTING_KEY = "DeliveryService.deliveryCost";

    /**
     * Publishes the calculated delivery cost to the rental service.
     */
    public void publishDeliveryCost(Long rentalId, BigDecimal cost) {
        Map<String, Object> msg = Map.of(
                "eventType", "delivery.deliveryCost",
                "rentalId", rentalId,
                "deliveryCost", cost
        );

        try {
            rabbitTemplate.convertAndSend(exchange, DELIVERY_COST_ROUTING_KEY, msg);
            log.info("✅ Published delivery cost event → Exchange='{}', RoutingKey='{}', rentalId={}, cost={}",
                    exchange, DELIVERY_COST_ROUTING_KEY, rentalId, cost);
        } catch (Exception e) {
            log.error("❌ Failed to publish delivery cost event for rentalId={} due to {}", rentalId, e.getMessage(), e);
        }
    }

    /**
     * Publishes a delivery status update (e.g., delivered, returned, etc.).
     */
    public void publishStatus(String eventType, Long rentalId) {
        Map<String, Object> msg = Map.of(
                "eventType", eventType,
                "rentalId", rentalId
        );

        try {
            rabbitTemplate.convertAndSend(exchange, "delivery.return.status", msg);
            log.info("📦 Published delivery status event → Exchange='{}', RoutingKey='delivery.status', eventType='{}', rentalId={}",
                    exchange, eventType, rentalId);
        } catch (Exception e) {
            log.error("❌ Failed to publish delivery status for rentalId={} due to {}", rentalId, e.getMessage(), e);
        }
    }
}
