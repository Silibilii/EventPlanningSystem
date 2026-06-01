package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Contractor;
import ru.kafpin.krswrk.model.ServiceType;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Contractor}.
 * Предоставляет стандартные CRUD-операции и дополнительные методы поиска по типу услуги.
 */
public interface ContractorDao {

    /**
     * Находит подрядчика по его идентификатору.
     *
     * @param id идентификатор подрядчика
     * @return Optional с найденным подрядчиком, либо пустой Optional если подрядчик не найден
     */
    Optional<Contractor> findById(int id);

    /**
     * Возвращает список всех подрядчиков.
     *
     * @return список подрядчиков
     */
    List<Contractor> findAll();

    /**
     * Сохраняет нового подрядчика в базе данных.
     * После сохранения у сущности должен быть установлен сгенерированный идентификатор.
     *
     * @param entity подрядчик для сохранения
     */
    void save(Contractor entity);

    /**
     * Обновляет существующего подрядчика в базе данных.
     *
     * @param entity подрядчик с обновлёнными данными
     */
    void update(Contractor entity);

    /**
     * Удаляет подрядчика по его идентификатору.
     *
     * @param id идентификатор удаляемого подрядчика
     */
    void delete(int id);

    /**
     * Возвращает список подрядчиков, предоставляющих указанный тип услуги.
     *
     * @param serviceType тип услуги
     * @return список подрядчиков с указанным типом услуги
     */
    List<Contractor> findAllByServiceType(ServiceType serviceType);
}