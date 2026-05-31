package ru.kafpin.krswrk.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.GuestDao;
import ru.kafpin.krswrk.model.Guest;
import ru.kafpin.krswrk.model.GuestStatus;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.util.SqlQueries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuestDaoImpl implements GuestDao {

    private static final Logger logger = LoggerFactory.getLogger(GuestDaoImpl.class);

    @Override
    public Optional<Guest> findById(int id) {
        String sql = SqlQueries.get("guest.findById");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.debug("Найден гость с id {}", id);
                return Optional.of(mapRow(rs));
            } else {
                logger.debug("Гость с id {} не найден", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске гостя по id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Guest> findByEventIdAndStatus(long eventId, GuestStatus status) {
        List<Guest> list = new ArrayList<>();
        String sql = SqlQueries.get("guest.findByEventIdAndStatus");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            stmt.setString(2, status.getDbValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Найдено {} гостей для мероприятия {} со статусом {}", list.size(), eventId, status.getDbValue());
        } catch (SQLException e) {
            logger.error("Ошибка при поиске гостей по статусу для мероприятия {}", eventId, e);
        }
        return list;
    }

    @Override
    public List<Guest> findAll() {
        List<Guest> list = new ArrayList<>();
        String sql = SqlQueries.get("guest.findAll");
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Загружено {} гостей", list.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех гостей", e);
        }
        return list;
    }

    @Override
    public List<Guest> findByEventId(long eventId) {
        List<Guest> list = new ArrayList<>();
        String sql = SqlQueries.get("guest.findByEventId");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Загружено {} гостей для мероприятия {}", list.size(), eventId);
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке гостей для мероприятия {}", eventId, e);
        }
        return list;
    }

    @Override
    public void save(Guest guest) {
        String sql = SqlQueries.get("guest.save");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, guest.getFullName());
            stmt.setString(2, guest.getContactPhone());
            stmt.setString(3, guest.getContactEmail());
            stmt.setString(4, guest.getInvitationStatus().getDbValue());
            stmt.setLong(5, guest.getEvent().getEventId());
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                logger.warn("Сохранение гостя не затронуло строк");
            }
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                guest.setGuestId(keys.getLong(1));
                logger.info("Сохранён гость с id {}", guest.getGuestId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении гостя {}", guest.getFullName(), e);
        }
    }

    @Override
    public void update(Guest guest) {
        String sql = SqlQueries.get("guest.update");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, guest.getFullName());
            stmt.setString(2, guest.getContactPhone());
            stmt.setString(3, guest.getContactEmail());
            stmt.setString(4, guest.getInvitationStatus().getDbValue());
            stmt.setLong(5, guest.getEvent().getEventId());
            stmt.setLong(6, guest.getGuestId());
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                logger.info("Обновлён гость с id {}", guest.getGuestId());
            } else {
                logger.warn("Обновление гостя с id {} не затронуло строк", guest.getGuestId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении гостя id {}", guest.getGuestId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = SqlQueries.get("guest.delete");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                logger.info("Удалён гость с id {}", id);
            } else {
                logger.warn("Удаление гостя с id {} не затронуло строк", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении гостя id {}", id, e);
        }
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setGuestId(rs.getLong("guest_id"));
        guest.setFullName(rs.getString("full_name"));
        guest.setContactPhone(rs.getString("contact_phone"));
        guest.setContactEmail(rs.getString("contact_email"));
        guest.setInvitationStatus(GuestStatus.fromDbValue(rs.getString("invitation_status")));
        Event event = new Event();
        event.setEventId(rs.getLong("event_id"));
        guest.setEvent(event);
        return guest;
    }
}