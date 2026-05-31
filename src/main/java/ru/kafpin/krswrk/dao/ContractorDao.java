package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Contractor;
import ru.kafpin.krswrk.model.ServiceType;

import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link Contractor}.
 * Расширяет базовый CRUD интерфейс {@link Dao}.
 */
public interface ContractorDao extends Dao<Contractor> {

    /**
     * Возвращает список подрядчиков, предоставляющих указанный тип услуги.
     *
     * @param serviceType тип услуги {@link ServiceType}
     * @return список подрядчиков.
     */
    List<Contractor> findAllByServiceType(ServiceType serviceType);
}