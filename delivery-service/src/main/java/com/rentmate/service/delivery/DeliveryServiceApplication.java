package com.rentmate.service.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "com.rentmate.service.delivery.domain.entity")
@EnableScheduling
@EnableFeignClients(basePackages = "com.rentmate.service.delivery.client")
public class DeliveryServiceApplication {

	public static void main(String[] args) {

        SpringApplication.run(DeliveryServiceApplication.class, args);
        System.out.println(System.getProperty("spring.datasource.password"));

    }

}
