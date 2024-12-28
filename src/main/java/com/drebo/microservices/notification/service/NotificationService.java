package com.drebo.microservices.notification.service;

import com.drebo.microservices.order.event.OrderNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @KafkaListener(topics = "order-notification")
    public void listen(OrderNotificationEvent orderNotificationEvent){

        final String emailSubject = String.format("""
                Order number: %s was placed successfully
                """, orderNotificationEvent.getOrderNumber());

        final String emailText = String.format( """
            Hi,
            Your order corresponding with the order number: %s was placed successfully.

            Thank you for your purchase.
            """, orderNotificationEvent.getOrderNumber());

        //send email to customer
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("testadmin@email.com");
            messageHelper.setTo(orderNotificationEvent.getEmail().toString());
            messageHelper.setSubject(emailSubject);
            messageHelper.setText(emailText);
        };

        try{
            log.info("Sending order notification email");
            javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            log.error("Failed to send order notification email");
            throw new RuntimeException("Exception occurred when sending notification email.", e);
        }
    }
}
