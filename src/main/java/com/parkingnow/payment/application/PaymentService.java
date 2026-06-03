package com.parkingnow.payment.application;

import com.parkingnow.payment.application.dto.PaymentRequest;
import com.parkingnow.payment.application.dto.PaymentResponse;
import com.parkingnow.payment.domain.Payment;
import com.parkingnow.payment.infrastructure.PaymentRepository;
import com.parkingnow.parkinglot.domain.ParkingLot;
import com.parkingnow.parkinglot.infrastructure.ParkingLotRepository;
import com.parkingnow.reservation.domain.Reservation;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final ReservationRepository reservationRepo;
    private final ParkingLotRepository lotRepo;

    @Transactional
    public PaymentResponse create(PaymentRequest req, Long driverId) {
        Reservation reservation = reservationRepo.findById(req.getReservationId())
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + req.getReservationId()));

        if (paymentRepo.findByReservationId(req.getReservationId()).isPresent()) {
            throw new ConflictException("Reservation already paid");
        }

        BigDecimal amount = req.getAmount() != null
                ? req.getAmount()
                : calculateAmount(reservation);

        Payment payment = Payment.builder()
                .reservationId(req.getReservationId())
                .driverId(driverId)
                .amount(amount)
                .method(Payment.PaymentMethod.DEMO_CARD)
                .status(Payment.PaymentStatus.PAID)
                .paidAt(Instant.now())
                .build();
        return PaymentResponse.from(paymentRepo.save(payment));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByDriver(Long driverId) {
        return paymentRepo.findByDriverIdOrderByCreatedAtDesc(driverId)
                .stream().map(PaymentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByLot(Long lotId) {
        return paymentRepo.findByLotId(lotId)
                .stream().map(PaymentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal sumPaidToday() {
        Instant startOfDay = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        return paymentRepo.sumPaidSince(startOfDay);
    }

    @Transactional(readOnly = true)
    public int countPaidToday() {
        Instant startOfDay = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        return paymentRepo.countPaidSince(startOfDay);
    }

    @Transactional(readOnly = true)
    public List<Object[]> last7DaysRevenue() {
        Instant since = Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS)
                .truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        return paymentRepo.sumByDay(since);
    }

    private BigDecimal calculateAmount(Reservation reservation) {
        Long lotId = reservation.getLotId();
        if (lotId == null) return BigDecimal.valueOf(3.00);
        ParkingLot lot = lotRepo.findById(lotId).orElse(null);
        if (lot == null) return BigDecimal.valueOf(3.00);

        long minutes = 60;
        if (reservation.getCreatedAt() != null && reservation.getExpiresAt() != null) {
            minutes = Math.max(1, Duration.between(reservation.getCreatedAt(), reservation.getExpiresAt()).toMinutes());
        }

        return lot.getHourlyRate()
                .multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
