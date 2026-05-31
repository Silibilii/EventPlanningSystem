package ru.kafpin.krswrk.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.VenueDao;
import ru.kafpin.krswrk.model.Venue;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.util.SqlQueries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VenueDaoImpl implements VenueDao {

    private static final Logger logger = LoggerFactory.getLogger(VenueDaoImpl.class);

    @Override
    public Optional<Venue> findById(int id) {
        String sql = SqlQueries.get("venue.findById");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.debug("Найдена площадка с id {}", id);
                return Optional.of(mapRow(rs));
            } else {
                logger.debug("Площадка с id {} не найдена", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске площадки по id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Venue> findAll() {
        List<Venue> list = new ArrayList<>();
        String sql = SqlQueries.get("venue.findAll");
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Загружено {} площадок", list.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех площадок", e);
        }
        return list;
    }

    @Override
    public void save(Venue venue) {
        String sql = SqlQueries.get("venue.save");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, venue.getName());
            stmt.setString(2, venue.getAddress());
            stmt.setInt(3, venue.getCapacity());
            stmt.setBigDecimal(4, venue.getRentalCost());
            stmt.setString(5, venue.getContactPhone());
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                logger.warn("Сохранение площадки не затронуло строк");
            }
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                venue.setVenueId(keys.getLong(1));
                logger.info("Сохранена площадка с id {}", venue.getVenueId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении площадки {}", venue.getName(), e);
        }
    }

    @Override
    public void update(Venue venue) {
        String sql = SqlQueries.get("venue.update");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, venue.getName());
            stmt.setString(2, venue.getAddress());
            stmt.setInt(3, venue.getCapacity());
            stmt.setBigDecimal(4, venue.getRentalCost());
            stmt.setString(5, venue.getContactPhone());
            stmt.setLong(6, venue.getVenueId());
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                logger.info("Обновлена площадка с id {}", venue.getVenueId());
            } else {
                logger.warn("Обновление площадки с id {} не затронуло строк", venue.getVenueId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении площадки id {}", venue.getVenueId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = SqlQueries.get("venue.delete");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                logger.info("Удалена площадка с id {}", id);
            } else {
                logger.warn("Удаление площадки с id {} не затронуло строк", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении площадки id {}", id, e);
        }
    }

    private Venue mapRow(ResultSet rs) throws SQLException {
        Venue venue = new Venue();
        venue.setVenueId(rs.getLong("venue_id"));
        venue.setName(rs.getString("venue_name"));
        venue.setAddress(rs.getString("address"));
        venue.setCapacity(rs.getInt("capacity"));
        venue.setRentalCost(rs.getBigDecimal("rental_cost"));
        venue.setContactPhone(rs.getString("contact_phone"));
        return venue;
    }
}