package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Venue;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Venue}.
 * Предоставляет стандартные CRUD-операции.
 */
public interface VenueDao {

    /**
     * Находит площадку по её идентификатору.
     *
     * @param id идентификатор площадки
     * @return Optional с найденной площадкой, либо пустой Optional если площадка не найдена
     */
    Optional<Venue> findById(int id);

    /**
     * Возвращает список всех площадок.
     *
     * @return список площадок
     */
    List<Venue> findAll();

    /**
     * Сохраняет новую площадку в базе данных.
     *
     * @param entity площадка для сохранения
     */
    void save(Venue entity);

    /**
     * Обновляет существующую площадку в базе данных.
     *
     * @param entity площадка с обновлёнными данными
     */
    void update(Venue entity);

    /**
     * Удаляет площадку по её идентификатору.
     *
     * @param id идентификатор удаляемой площадки
     */
    void delete(int id);
}