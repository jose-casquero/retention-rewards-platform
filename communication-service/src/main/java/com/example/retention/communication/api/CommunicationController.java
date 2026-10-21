package com.example.retention.communication.api;

import com.example.retention.communication.domain.Delivery;
import com.example.retention.communication.domain.DeliveryRepository;
import com.example.retention.communication.domain.ProcessedAction;
import com.example.retention.communication.domain.ProcessedActionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/communications")
@Tag(name = "Communications")
public class CommunicationController {
    private final DeliveryRepository repository;

    public CommunicationController(DeliveryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/deliveries")
    @Operation(summary = "List communication deliveries")
    public List<Delivery> list() {
        return repository.findAll();
    }

    @GetMapping("/deliveries/{id}")
    @Operation(summary = "Get a communication delivery")
    public Delivery get(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery does not exist"));
    }
}
