//changed package name to match orderNotification package in order-service for mapping
package com.drebo.microservices.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderNotification {
    private String orderNumber;
    private String email;
}
