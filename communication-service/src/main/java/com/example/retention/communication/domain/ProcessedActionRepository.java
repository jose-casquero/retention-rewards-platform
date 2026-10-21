package com.example.retention.communication.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface ProcessedActionRepository extends JpaRepository<ProcessedAction, UUID> {
}
