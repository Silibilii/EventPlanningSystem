package ru.kafpin.krswrk.dao;

import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс для {@link Dao}, предоставляющий стандартные CRUD-операции.
 *
 * @param <T> тип сущности, с которой работает DAO
 */
public interface Dao<T> {
    /**
     * Возвращает сущность по её идентификатору.
     *
     * @param id идентификатор
     * @return Optional с найденной сущностью
     */
    Optional<T> findById(int id);

    /**
     * Возвращает список всех сущностей.
     *
     * @return список (может быть пустым)
     */
    List<T> findAll();

    /**
     * Сохраняет новую сущность в БД.
     *
     * @param entity сущность для сохранения
     */
    void save(T entity);

    /**
     * Обновляет существующую сущность в БД.
     *
     * @param entity сущность с обновлёнными данными
     */
    void update(T entity);

    /**
     * Удаляет сущность по идентификатору.
     *
     * @param id идентификатор удаляемой сущности
     */
    void delete(int id);
}