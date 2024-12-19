package com.drebo.microservices.notification.service;

import com.drebo.microservices.order.event.OrderNotification;
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
    public void listen(OrderNotification orderNotification){

        final String emailSubject = String.format("""
                Order number: %s was placed successfully
                """, orderNotification.getOrderNumber());

        final String emailText = String.format( """
            Hi,

            Your order corresponding with the order number: %s was placed successfully.

            Thank you for your purchase.
            """, orderNotification.getOrderNumber());

        //send email to customer
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("test@email.com");
            messageHelper.setTo(orderNotification.getEmail());
            messageHelper.setSubject(emailSubject);
            messageHelper.setText(emailText);
        };

        try{
            javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            throw new RuntimeException("Exception occurred when sending notification email.", e);
        }
    }
}
