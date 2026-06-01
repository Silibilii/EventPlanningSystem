package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Task;
import ru.kafpin.krswrk.model.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Task}.
 * Предоставляет стандартные CRUD-операции и методы для фильтрации задач по мероприятию.
 */
public interface TaskDao {

    /**
     * Находит задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return Optional с найденной задачей, либо пустой Optional если задача не найдена
     */
    Optional<Task> findById(int id);

    /**
     * Возвращает список всех задач.
     *
     * @return список задач
     */
    List<Task> findAll();

    /**
     * Сохраняет новую задачу в базе данных.
     *
     * @param entity задача для сохранения
     */
    void save(Task entity);

    /**
     * Обновляет существующую задачу в базе данных.
     *
     * @param entity задача с обновлёнными данными
     */
    void update(Task entity);

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор удаляемой задачи
     */
    void delete(int id);

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
     * @param status  статус задачи (например, {@link TaskStatus})
     * @return список задач, отсортированный по дедлайну
     */
    List<Task> findByEventIdAndStatus(long eventId, TaskStatus status);

    /**
     * Возвращает задачи мероприятия, содержащие указанный текст в описании.
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