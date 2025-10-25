package com.rentmate.service.delivery.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;


@Configuration
public class RabbitMQConfig {

        public static final String DELIVERY_EXCHANGE = "delivery.exchange";
        public static final String USER_EXCHANGE = "users.exchange";



    public static final String RENTAL_TO_DELIVERY_QUEUE = "rental.delivery.queue";
    public static final String USER_TO_DELIVERY_QUEUE = "user.delivery.queue";


    public static final String  RENTAL_ROUTING_KEY = "rental.*" ; //rental.cost.requested
    public static final String  DELIVERY_ROUTING_KEY = "delivery.*" ;
    public static final String USER_ROUTING_KEY = "user.registered";

    @Bean
    public TopicExchange deliveryExchange() {
        System.out.println("Creating exchange: " + DELIVERY_EXCHANGE);
        return new TopicExchange(DELIVERY_EXCHANGE);
    }


    @Bean
    public TopicExchange userExchange() {
        System.out.println("Creating exchange: " +USER_EXCHANGE);
        return new TopicExchange(USER_EXCHANGE);
    }



       @Bean
        public Queue rentalToDeliveryQueue() {
            return new Queue(RENTAL_TO_DELIVERY_QUEUE, true);
        }

    @Bean
    public Queue userToDeliveryQueue() {
        return new Queue(USER_TO_DELIVERY_QUEUE, true);
    }


    // @Bean
        //public Queue deliveryToRentalQueue() {
           // return new Queue(DELIVERY_TO_RENTAL_QUEUE, true);
        //}

    @Bean
    public Binding bindingDeliveryEventQueue(
            @Qualifier("rentalToDeliveryQueue") Queue queue,
            @Qualifier("deliveryExchange")
            TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(DELIVERY_ROUTING_KEY);
    }

    @Bean
    public Binding bindingUserEventQueue(
            @Qualifier("userToDeliveryQueue") Queue queue,
            @Qualifier("userExchange")
            TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(USER_ROUTING_KEY);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    }


