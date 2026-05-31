package ru.kafpin.krswrk.model;

import lombok.Getter;

import java.util.ResourceBundle;

/**
 * Статус мероприятия.
 * Определяет текущее состояние жизненного цикла события.
 *
 * @see Event
 */
public enum EventStatus {
    PLANNING("планирование", "event.status.planning"),
    IN_PROGRESS("в процессе", "event.status.in_progress"),
    COMPLETED("завершено", "event.status.completed"),
    CANCELLED("отменено", "event.status.cancelled");

    @Getter
    private final String dbValue;
    private final String bundleKey;

    EventStatus(String dbValue, String bundleKey) {
        this.dbValue = dbValue;
        this.bundleKey = bundleKey;
    }

    /**
     * Возвращает локализованное название статуса для отображения в интерфейсе.
     *
     * @param bundle ресурсный бандл с переводами
     * @return локализованная строка статуса
     */
    public String getLocalized(ResourceBundle bundle) {
        return bundle.getString(bundleKey);
    }

    /**
     * Преобразует строковое значение из базы данных в соответствующую константу enum.
     *
     * @param dbValue строковое значение из поля БД
     * @return константа {@link EventStatus}
     * @throws IllegalArgumentException если переданное значение не соответствует ни одному статусу
     */
    public static EventStatus fromDbValue(String dbValue) {
        for (EventStatus s : values()) {
            if (s.dbValue.equals(dbValue)) return s;
        }
        throw new IllegalArgumentException("Неизвестный статус мероприятия: " + dbValue);
    }
}