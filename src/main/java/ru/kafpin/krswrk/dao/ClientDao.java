package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Client;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Client}.
 * Предоставляет стандартные CRUD-операции.
 */
public interface ClientDao {

    /**
     * Находит клиента по его идентификатору.
     *
     * @param id идентификатор клиента
     * @return Optional с найденным клиентом, либо пустой Optional если клиент не найден
     */
    Optional<Client> findById(int id);

    /**
     * Возвращает список всех клиентов.
     *
     * @return список клиентов (может быть пустым)
     */
    List<Client> findAll();

    /**
     * Сохраняет нового клиента в базе данных.
     *
     * @param entity клиент для сохранения
     */
    void save(Client entity);

    /**
     * Обновляет существующего клиента в базе данных.
     *
     * @param entity клиент с обновлёнными данными
     */
    void update(Client entity);

    /**
     * Удаляет клиента по его идентификатору.
     *
     * @param id идентификатор удаляемого клиента
     */
    void delete(int id);
}