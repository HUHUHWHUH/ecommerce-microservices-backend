package com.ecom.notification;

import com.ecom.payment.Customer;
import com.ecom.payment.PaymentMethod;

import java.math.BigDecimal;

public record PaymentNotificationRequest (
    String orderReference,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    String customerFirstName,
    String customerLastName,
    String customerEmail
    ) {
}
