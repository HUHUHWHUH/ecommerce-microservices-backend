package com.ecom.kafka.payment;

import java.math.BigDecimal;

public record PaymentConfirmation(
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String id,
        String customerFirstname,
        String customerLastname,
        String customerEmail
) {
}
