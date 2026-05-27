package com.parkingnow.history.application;

import com.parkingnow.event.application.EventAlertService;
import com.parkingnow.event.application.dto.EventAlertResponse;
import com.parkingnow.payment.application.PaymentService;
import com.parkingnow.payment.application.dto.PaymentResponse;
import com.parkingnow.reservation.application.ReservationService;
import com.parkingnow.reservation.application.dto.ReservationResponse;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final EventAlertService eventAlertService;

    @Data @Builder
    public static class HistorySummary {
        private List<ReservationResponse> reservations;
        private List<PaymentResponse> payments;
        private List<EventAlertResponse> iotEvents;
    }

    public HistorySummary getHistory(Long lotId) {
        List<ReservationResponse> reservations = lotId != null
                ? reservationService.findByLot(lotId)
                : reservationService.findAll();

        List<PaymentResponse> payments = paymentService.findAll();
        List<EventAlertResponse> events = eventAlertService.findAll(50);

        return HistorySummary.builder()
                .reservations(reservations)
                .payments(payments)
                .iotEvents(events)
                .build();
    }
}
