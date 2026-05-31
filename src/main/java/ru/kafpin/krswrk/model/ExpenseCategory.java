package ru.kafpin.krswrk.model;

import ru.kafpin.krswrk.util.LocaleManager;

/**
 * Категория расхода мероприятия.
 * Определяет тип финансовой операции - аренда, кейтеринг, фотограф и т.д.
 */
public enum ExpenseCategory {
    RENT("аренда"),
    CATERING("кейтеринг"),
    PHOTOGRAPHER("фотограф"),
    HOST("ведущий"),
    DECOR("декор"),
    TRANSPORT("транспорт"),
    OTHER("прочее");

    private final String dbValue; // русское значение для БД

    ExpenseCategory(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Возвращает строковое значение категории для хранения в базе данных.
     *
     * @return строковое представление
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Возвращает локализованное название категории для отображения в интерфейсе.
     *
     * @return локализованная строка
     */
    public String getLocalized() {
        return LocaleManager.getBundle().getString("expense.category." + name().toLowerCase());
    }

    /**
     * Преобразует строковое значение из базы данных в соответствующую константу enum.
     *
     * @param dbValue строковое значение
     * @return константа {@link ExpenseCategory}
     * @throws IllegalArgumentException если значение не соответствует ни одной категории
     */
    public static ExpenseCategory fromDbValue(String dbValue) {
        for (ExpenseCategory ec : values()) {
            if (ec.dbValue.equals(dbValue)) return ec;
        }
        throw new IllegalArgumentException("Unknown expense category: " + dbValue);
    }
}