package ru.kafpin.krswrk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Нанимаемый контрактор.
 * Хранит информацию о контракторе - контактные данные, прайс-лист, заметки.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contractor {
    private Long contractorId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private ServiceType serviceType;
    private String priceList;
    private String notes;

    @Override
    public String toString() {
        return name;
    }
}