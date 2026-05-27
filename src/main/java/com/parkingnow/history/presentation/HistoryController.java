package com.parkingnow.history.presentation;

import com.parkingnow.history.application.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService service;

    @GetMapping
    public HistoryService.HistorySummary getHistory(
            @RequestParam(required = false) Long lotId) {
        return service.getHistory(lotId);
    }
}
