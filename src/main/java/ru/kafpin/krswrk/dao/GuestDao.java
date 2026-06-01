package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Guest;
import ru.kafpin.krswrk.model.GuestStatus;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Guest}.
 * Предоставляет стандартные CRUD-операции и методы для поиска по мероприятию и статусу.
 */
public interface GuestDao {

    /**
     * Находит гостя по его идентификатору.
     *
     * @param id идентификатор гостя
     * @return Optional с найденным гостем, либо пустой Optional если гость не найден
     */
    Optional<Guest> findById(int id);

    /**
     * Возвращает список всех гостей.
     *
     * @return список гостей
     */
    List<Guest> findAll();

    /**
     * Сохраняет нового гостя в базе данных.
     *
     * @param entity гость для сохранения
     */
    void save(Guest entity);

    /**
     * Обновляет существующего гостя в базе данных.
     *
     * @param entity гость с обновлёнными данными
     */
    void update(Guest entity);

    /**
     * Удаляет гостя по его идентификатору.
     *
     * @param id идентификатор удаляемого гостя
     */
    void delete(int id);

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
     * @param status  статус приглашения  {@link GuestStatus}
     * @return список гостей, удовлетворяющих условию
     */
    List<Guest> findByEventIdAndStatus(long eventId, GuestStatus status);
}