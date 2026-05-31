package ru.kafpin.krswrk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Гость мероприятия.
 * Хранит персональные данные (ФИО, контакты) и статус приглашения.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guest {
    private Long guestId;
    private String fullName;
    private String contactPhone;
    private String contactEmail;
    private GuestStatus invitationStatus;
    @ToString.Exclude
    private Event event;

    @Override
    public String toString() {
        return fullName;
    }
}