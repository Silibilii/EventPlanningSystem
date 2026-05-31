package ru.kafpin.krswrk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Площадка для проведения мероприятия.
 * Хранит информацию о месте проведения: название, адрес, вместимость, стоимость аренды и контактный телефон.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    private Long venueId;
    private String name;
    private String address;
    private Integer capacity;
    private BigDecimal rentalCost;
    private String contactPhone;

    @Override
    public String toString() {
        return name + " (" + capacity + ")";
    }
}