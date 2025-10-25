package com.rentmate.service.delivery.domain.dto.event;

import java.time.LocalDateTime;

public record UserRegisteredEventDto(
        Long userId,
        String username,
        String email,
        String role,
        LocalDateTime registeredAt,
        String phone
) {}

