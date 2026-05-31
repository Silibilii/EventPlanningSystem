package ru.kafpin.krswrk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;

/**
 * Задача, связанная с мероприятием.
 * Определяет действие, которое необходимо выполнить в рамках подготовки или проведения мероприятия.
 * Содержит описание, срок выполнения, статус, ответственного подрядчика и ссылку на мероприятие.
 *
 * @see Event
 * @see Contractor
 * @see TaskStatus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Long taskId;
    private String description;
    private LocalDate deadline;
    private TaskStatus status;
    @ToString.Exclude
    private Event event;
    @ToString.Exclude
    private Contractor responsibleContractor;

    @Override
    public String toString() {
        return description;
    }
}
