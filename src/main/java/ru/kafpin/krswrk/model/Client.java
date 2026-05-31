package ru.kafpin.krswrk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Заказчик мероприятия.
 * Хранит контактные данные и информацию о юридическом/физическом лице.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long clientId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private Boolean isLegal;
    private String organizationName;
    private String notes;
    @Override
    public String toString() {
        return name;
    }
}