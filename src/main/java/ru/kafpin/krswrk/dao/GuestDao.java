package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Guest;
import ru.kafpin.krswrk.model.GuestStatus;

import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link Guest}.
 * Расширяет базовый CRUD интерфейс {@link Dao}.
 */
public interface GuestDao extends Dao<Guest> {

    /**
     * Возвращает всех гостей указанного мероприятия.
     *
     * @param eventId идентификатор мероприятия
     * @return список гостей, отсортированный по полному имени
     */
    List<Guest> findByEventId(long eventId);

    /**
     * Возвращает гостей мероприятия с определённым статусом приглашения.
     *
     * @param eventId идентификатор мероприятия
     * @param status  статус приглашения
     * @return список гостей, удовлетворяющих условию.
     */
    List<Guest> findByEventIdAndStatus(long eventId, GuestStatus status);
}