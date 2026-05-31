package ru.kafpin.krswrk.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ContractorDao;
import ru.kafpin.krswrk.model.Contractor;
import ru.kafpin.krswrk.model.ServiceType;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.util.SqlQueries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContractorDaoImpl implements ContractorDao {

    private static final Logger logger = LoggerFactory.getLogger(ContractorDaoImpl.class);

    @Override
    public Optional<Contractor> findById(int id) {
        String sql = SqlQueries.get("contractor.findById");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске подрядчика по id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Contractor> findAllByServiceType(ServiceType serviceType) {
        List<Contractor> list = new ArrayList<>();
        String sql = SqlQueries.get("contractor.findAllByServiceType");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, serviceType.getDbValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Найдено {} подрядчиков с типом услуги {}", list.size(), serviceType.getDbValue());
        } catch (SQLException e) {
            logger.error("Ошибка при поиске подрядчиков по типу услуги {}", serviceType.getDbValue(), e);
        }
        return list;
    }

    @Override
    public List<Contractor> findAll() {
        List<Contractor> list = new ArrayList<>();
        String sql = SqlQueries.get("contractor.findAll");
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Загружено {} подрядчиков", list.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех подрядчиков", e);
        }
        return list;
    }

    @Override
    public void save(Contractor contractor) {
        String sql = SqlQueries.get("contractor.save");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, contractor.getName());
            stmt.setString(2, contractor.getContactPerson());
            stmt.setString(3, contractor.getPhone());
            stmt.setString(4, contractor.getEmail());
            stmt.setString(5, contractor.getServiceType().getDbValue());
            stmt.setString(6, contractor.getPriceList());
            stmt.setString(7, contractor.getNotes());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                contractor.setContractorId(keys.getLong(1));
                logger.info("Сохранён подрядчик с id {}", contractor.getContractorId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении подрядчика", e);
        }
    }

    @Override
    public void update(Contractor contractor) {
        String sql = SqlQueries.get("contractor.update");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, contractor.getName());
            stmt.setString(2, contractor.getContactPerson());
            stmt.setString(3, contractor.getPhone());
            stmt.setString(4, contractor.getEmail());
            stmt.setString(5, contractor.getServiceType().getDbValue());
            stmt.setString(6, contractor.getPriceList());
            stmt.setString(7, contractor.getNotes());
            stmt.setLong(8, contractor.getContractorId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении подрядчика id {}", contractor.getContractorId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = SqlQueries.get("contractor.delete");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                logger.info("Удалён подрядчик с id {}", id);
            } else {
                logger.warn("Удаление подрядчика с id {} не затронуло строк", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении подрядчика id {}", id, e);
            throw new RuntimeException("Ошибка БД при удалении подрядчика", e);
        }
    }

    private Contractor mapRow(ResultSet rs) throws SQLException {
        Contractor contractor = new Contractor();
        contractor.setContractorId(rs.getLong("contractor_id"));
        contractor.setName(rs.getString("contractor_name"));
        contractor.setContactPerson(rs.getString("contact_person"));
        contractor.setPhone(rs.getString("phone"));
        contractor.setEmail(rs.getString("email"));
        contractor.setPriceList(rs.getString("price_list"));
        contractor.setNotes(rs.getString("notes"));
        String serviceTypeStr = rs.getString("service_type");
        if (!rs.wasNull()) {
            contractor.setServiceType(ServiceType.fromDbValue(serviceTypeStr));
        }
        return contractor;
    }
}