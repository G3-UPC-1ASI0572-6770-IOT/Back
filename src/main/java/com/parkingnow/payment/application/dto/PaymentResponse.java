package com.parkingnow.payment.application.dto;

import com.parkingnow.payment.domain.Payment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder
public class PaymentResponse {
    private Long id;
    private Long reservationId;
    private BigDecimal amount;
    private String currency;
    private String method;
    private String status;
    private Instant paidAt;
    private Instant createdAt;

    public static PaymentResponse from(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .reservationId(p.getReservationId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .method(p.getMethod().name())
                .status(p.getStatus().name())
                .paidAt(p.getPaidAt())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
