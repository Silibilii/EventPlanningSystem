package ru.kafpin.krswrk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Расход бюджета.
 * Хранит описание расхода, дату, сумму и тип траты, с указанием 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    private Long expenseId;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    @ToString.Exclude
    private Event event;
    @ToString.Exclude
    private Contractor contractor;
    @ToString.Exclude
    private ExpenseCategory category;
}
