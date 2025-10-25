package com.rentmate.service.delivery.service;
import com.rentmate.service.delivery.event.publisher.DeliveryEventPublisher;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;


import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryCostService {

    private final DeliveryEventPublisher publisher;
    private static final BigDecimal COST_PER_10KM = BigDecimal.valueOf(20.0);
  @NotNull
    public void handleCostRequest(com.rentmate.service.delivery.domain.dto.event.RentalCostRequestedEventDto dto) {
       log.info("start handle cost function");
        String renterAddress = dto.getRenterAddress();
        String ownerAddress = dto.getOwnerAddress();

        if (renterAddress == null || ownerAddress == null) {
            throw new IllegalArgumentException("Addresses must be provided for cost calculation");
        }

        double[] renterCoords = geocodeAddress(renterAddress);
        double[] ownerCoords = geocodeAddress(ownerAddress);

        // Forward distance: Owner -> Renter
        double forwardKm = haversine(ownerCoords[0], ownerCoords[1],
                renterCoords[0], renterCoords[1]);

        // Return distance: Renter -> Owner
        double returnKm = haversine(renterCoords[0], renterCoords[1],
                ownerCoords[0], ownerCoords[1]);

        double totalKm = forwardKm + returnKm;

        // BigDecimal cost
        BigDecimal distanceBD = BigDecimal.valueOf(totalKm);
        BigDecimal multiplier = distanceBD.divide(BigDecimal.TEN, 0, RoundingMode.CEILING);
        BigDecimal totalCost = multiplier.multiply(COST_PER_10KM);
        if(totalCost.equals(BigDecimal.ZERO)){
            totalCost = totalCost.add(BigDecimal.valueOf(20));

        }

        log.debug("Forward={} km, Return={} km, Total={} km, Cost={} for rentalId={}",
                forwardKm, returnKm, totalKm, totalCost, dto.getRentalId());

        // نشر الحدث
        publisher.publishDeliveryCost(dto.getRentalId(),totalCost);
    }

    private double[] geocodeAddress(String address) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" +
                    URLEncoder.encode(address, StandardCharsets.UTF_8);
            WebClient client = WebClient.create();
            NominatimResponse[] response = client.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(NominatimResponse[].class)
                    .block();

            if (response != null && response.length > 0) {
                double lat = Double.parseDouble(response[0].lat);
                double lon = Double.parseDouble(response[0].lon);
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            log.error("Failed geocoding address: {}", address, e);
        }
        return new double[]{0.0, 0.0};
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private static class NominatimResponse {
        public String lat;
        public String lon;
    }
}
