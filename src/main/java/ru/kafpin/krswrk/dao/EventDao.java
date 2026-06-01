package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.model.EventStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Event}.
 * Предоставляет стандартные CRUD-операции и дополнительные методы для работы с бюджетом и статусами.
 */
public interface EventDao {

    /**
     * Находит мероприятие по его идентификатору.
     *
     * @param id идентификатор мероприятия
     * @return Optional с найденным мероприятием, либо пустой Optional если мероприятие не найдено
     */
    Optional<Event> findById(int id);

    /**
     * Возвращает список всех мероприятий.
     *
     * @return список мероприятий (может быть пустым)
     */
    List<Event> findAll();

    /**
     * Сохраняет новое мероприятие в базе данных.
     *
     * @param entity мероприятие для сохранения
     */
    void save(Event entity);

    /**
     * Обновляет существующее мероприятие в базе данных.
     *
     * @param entity мероприятие с обновлёнными данными
     */
    void update(Event entity);

    /**
     * Удаляет мероприятие по его идентификатору.
     *
     * @param id идентификатор удаляемого мероприятия
     */
    void delete(int id);

    /**
     * Возвращает остаток бюджета мероприятия (бюджет минус сумма всех расходов).
     * Вычисляется с помощью хранимой функции БД.
     *
     * @param eventId идентификатор мероприятия
     * @return остаток бюджета
     */
    BigDecimal getRemainingBudget(long eventId);

    /**
     * Возвращает список мероприятий с указанным статусом.
     *
     * @param status статус мероприятия
     * @return список мероприятий;
     * если мероприятий с таким статусом нет, возвращается пустой список
     */
    List<Event> findByStatus(EventStatus status);
}