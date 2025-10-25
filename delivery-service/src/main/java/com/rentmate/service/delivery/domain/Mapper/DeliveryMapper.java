package com.rentmate.service.delivery.domain.Mapper;

import com.rentmate.service.delivery.domain.dto.rest.CreateDeliveryRequest;
import com.rentmate.service.delivery.domain.dto.rest.DeliveryResponse;
import com.rentmate.service.delivery.domain.entity.Delivery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    Delivery toEntity(CreateDeliveryRequest dto);

    DeliveryResponse toDto(Delivery entity);
}