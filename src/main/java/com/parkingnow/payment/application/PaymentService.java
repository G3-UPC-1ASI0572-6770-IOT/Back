package com.parkingnow.payment.application;

import com.parkingnow.payment.application.dto.PaymentRequest;
import com.parkingnow.payment.application.dto.PaymentResponse;
import com.parkingnow.payment.domain.Payment;
import com.parkingnow.payment.infrastructure.PaymentRepository;
import com.parkingnow.reservation.domain.Reservation;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final ReservationRepository reservationRepo;

    @Transactional
    public PaymentResponse demo(PaymentRequest req) {
        Reservation reservation = reservationRepo.findById(req.getReservationId())
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + req.getReservationId()));

        Payment payment = Payment.builder()
                .reservationId(req.getReservationId())
                .amount(req.getAmount() != null ? req.getAmount() : BigDecimal.valueOf(8.50))
                .method(parseMethod(req.getMethod()))
                .status(Payment.PaymentStatus.PAID)
                .paidAt(Instant.now())
                .build();
        payment = paymentRepo.save(payment);

        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservationRepo.save(reservation);

        return PaymentResponse.from(payment);
    }

    public PaymentResponse findByReservation(Long reservationId) {
        return paymentRepo.findByReservationId(reservationId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new NotFoundException("Payment not found for reservation: " + reservationId));
    }

    public List<PaymentResponse> findAll() {
        return paymentRepo.findAllByOrderByCreatedAtDesc().stream()
                .map(PaymentResponse::from)
                .toList();
    }

    private Payment.PaymentMethod parseMethod(String method) {
        if (method == null) return Payment.PaymentMethod.DEMO_CARD;
        try {
            return Payment.PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Payment.PaymentMethod.DEMO_CARD;
        }
    }
}
