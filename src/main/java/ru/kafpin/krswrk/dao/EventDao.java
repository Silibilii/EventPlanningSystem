package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.model.EventStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link Event}.
 * Расширяет базовый CRUD интерфейс {@link Dao} специфичными для мероприятий методами.
 */
public interface EventDao extends Dao<Event> {

    /**
     * Возвращает остаток бюджета мероприятия.
     *
     * @param eventId идентификатор мероприятия
     * @return остаток бюджета !!! может быть отрицательным, если расходы превысили бюджет !!!
     */
    BigDecimal getRemainingBudget(long eventId);

    /**
     * Возвращает список мероприятий с указанным статусом.
     *
     * @param status статус мероприятия
     * @return список мероприятий, отсортированных по дате проведения. Если мероприятий с таким статусом нет, возвращается пустой список.
     */
    List<Event> findByStatus(EventStatus status);
}