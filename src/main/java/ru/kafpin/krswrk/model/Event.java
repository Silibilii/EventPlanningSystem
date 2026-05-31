package ru.kafpin.krswrk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Мероприятие.
 * Содержит всю основную информацию: название, дату, бюджет, статус,
 * связанные сущности (клиент, площадка), обратную связь и дату создания.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long eventId;
    private String name;
    private LocalDate eventDate;
    private BigDecimal budget;
    private EventStatus status;
    private String feedback;
    private Client client;
    private Venue venue;
    private LocalDateTime createdAt;
}