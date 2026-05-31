package ru.kafpin.krswrk.model;

import lombok.Getter;

import java.util.ResourceBundle;

/**
 * Типы статусов приглашений гостей.
 * Определяет статус приглашения - подтвердил, отказался и т.д.
 */
public enum GuestStatus {
    INVITED("приглашён", "guest.status.invited"),
    CONFIRMED("подтвердил", "guest.status.confirmed"),
    PAID("оплатил", "guest.status.paid"),
    DECLINED("отказался", "guest.status.declined");

    @Getter
    private final String dbValue;
    private final String bundleKey;

    GuestStatus(String dbValue, String bundleKey) {
        this.dbValue = dbValue;
        this.bundleKey = bundleKey;
    }

    /**
     * Возвращает локализованное название статуса для отображения в интерфейсе.
     *
     * @param bundle ресурсный бандл с переводами
     * @return локализованная строка статуса
     */
    public String getLocalized(ResourceBundle bundle) { return bundle.getString(bundleKey); }

    /**
     * Преобразует строковое значение из базы данных в соответствующую константу enum.
     *
     * @param dbValue значение поля invitation_status
     * @return константа {@link GuestStatus}
     * @throws IllegalArgumentException если переданное значение не соответствует ни одному статусу
     */
    public static GuestStatus fromDbValue(String dbValue) {
        for (GuestStatus s : values()) if (s.dbValue.equals(dbValue)) return s;
        throw new IllegalArgumentException("Неизвестный статус гостя: " + dbValue);
    }
}