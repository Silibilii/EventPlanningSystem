package ru.kafpin.krswrk.model;

import java.util.ResourceBundle;
import ru.kafpin.krswrk.util.LocaleManager;

/**
 * Типы подрядчиков.
 * Определяет тип подрядчика - ведущий, декоратор, кейтеринг и т.д.
 */
public enum ServiceType {
    HOST(1, "ведущий"),
    DECORATOR(2, "декоратор"),
    CATERING(3, "кейтеринг"),
    PHOTOGRAPHER(4, "фотограф"),
    SOUND_ENGINEER(5, "звукорежиссёр"),
    OTHER(6, "прочее");

    private final int id;
    private final String dbValue;

    ServiceType(int id, String dbValue) {
        this.id = id;
        this.dbValue = dbValue;
    }

    public int getId() { return id; }
    public String getDbValue() { return dbValue; }

    /**
     * Возвращает локализованное название типа услуги для отображения в интерфейсе.
     *
     * @return локализованная строка
     */
    public String getLocalized() {
        ResourceBundle bundle = LocaleManager.getBundle();
        return bundle.getString("service.type." + name().toLowerCase());
    }

    /**
     * Преобразует строковое значение из базы данных в соответствующую константу enum.
     *
     * @param dbValue значение поля service_type
     * @return константа {@link ServiceType}
     * @throws IllegalArgumentException если значение не распознано
     */
    public static ServiceType fromDbValue(String dbValue) {
        for (ServiceType st : values())
            if (st.dbValue.equals(dbValue)) return st;
        throw new IllegalArgumentException("Unknown service type: " + dbValue);
    }

    /**
     * Преобразует числовой идентификатор в соответствующую константу enum.
     *
     * @param id идентификатор
     * @return константа {@link ServiceType}
     * @throws IllegalArgumentException если идентификатор не соответствует ни одному типу
     */
    public static ServiceType fromId(int id) {
        for (ServiceType st : values())
            if (st.id == id) return st;
        throw new IllegalArgumentException("Unknown service type id: " + id);
    }
}