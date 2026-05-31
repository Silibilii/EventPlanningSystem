package ru.kafpin.krswrk.model;

import lombok.Getter;

import java.util.ResourceBundle;

/**
 * Статус выполнения задачи.
 * Определяет текущее состояние задачи в процессе подготовки мероприятия.
 *
 * @see Task
 */
public enum TaskStatus {
    PENDING("ожидает", "task.status.pending"),
    IN_PROGRESS("в работе", "task.status.in_progress"),
    DONE("выполнено", "task.status.done"),
    OVERDUE("просрочено", "task.status.overdue");

    @Getter
    private final String dbValue;
    private final String bundleKey;

    TaskStatus(String dbValue, String bundleKey) {
        this.dbValue = dbValue;
        this.bundleKey = bundleKey;
    }

    /**
     * Возвращает локализованное название статуса для отображения в интерфейсе.
     *
     * @param bundle ресурсный бандл с переводами
     * @return локализованная строка
     */
    public String getLocalized(ResourceBundle bundle) {
        return bundle.getString(bundleKey);
    }

    /**
     * Преобразует строковое значение из базы данных в константу enum.
     *
     * @param dbValue значение поля status
     * @return соответствующая константа {@link TaskStatus}
     * @throws IllegalArgumentException если значение не распознано
     */
    public static TaskStatus fromDbValue(String dbValue) {
        for (TaskStatus s : values()) {
            if (s.dbValue.equals(dbValue)) return s;
        }
        throw new IllegalArgumentException("Неизвестный статус задачи: " + dbValue);
    }
}