package com.parkingnow.payment.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull
    private Long reservationId;

    @Positive
    private BigDecimal amount;

    private String method;  // DEMO_CARD | YAPE_DEMO | CASH_DEMO
}
