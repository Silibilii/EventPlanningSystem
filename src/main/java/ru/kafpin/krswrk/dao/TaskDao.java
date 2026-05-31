package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Task;
import ru.kafpin.krswrk.model.TaskStatus;

import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link Task}.
 * Расширяет базовый CRUD интерфейс {@link Dao}.
 */
public interface TaskDao extends Dao<Task> {

    /**
     * Возвращает все задачи мероприятия без фильтрации.
     *
     * @param eventId идентификатор мероприятия
     * @return список задач, отсортированный по дедлайну
     */
    List<Task> findByEventIdOnly(long eventId);

    /**
     * Возвращает задачи мероприятия с указанным статусом.
     *
     * @param eventId идентификатор мероприятия
     * @param status  статус задачи
     * @return список задач, отсортированный по дедлайну
     */
    List<Task> findByEventIdAndStatus(long eventId, TaskStatus status);

    /**
     * Возвращает задачи мероприятия, содержащие указанный текст в описании (регистронезависимо).
     *
     * @param eventId    идентификатор мероприятия
     * @param searchText текст для поиска в описании
     * @return список задач, отсортированный по дедлайну
     */
    List<Task> findByEventIdAndSearch(long eventId, String searchText);

    /**
     * Возвращает задачи мероприятия по статусу и содержащие указанный текст в описании.
     *
     * @param eventId    идентификатор мероприятия
     * @param status     статус задачи
     * @param searchText текст для поиска в описании
     * @return список задач, отсортированный по дедлайну
     */
    List<Task> findByEventIdAndStatusAndSearch(long eventId, TaskStatus status, String searchText);
}