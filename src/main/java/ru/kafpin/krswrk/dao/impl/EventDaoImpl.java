package ru.kafpin.krswrk.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ClientDao;
import ru.kafpin.krswrk.dao.EventDao;
import ru.kafpin.krswrk.dao.VenueDao;
import ru.kafpin.krswrk.model.Client;
import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.model.EventStatus;
import ru.kafpin.krswrk.model.Venue;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.util.SqlQueries;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventDaoImpl implements EventDao {

    private static final Logger logger = LoggerFactory.getLogger(EventDaoImpl.class);
    private final ClientDao clientDao;
    private final VenueDao venueDao;

    public EventDaoImpl(ClientDao clientDao, VenueDao venueDao) {
        this.clientDao = clientDao;
        this.venueDao = venueDao;
    }

    @Override
    public Optional<Event> findById(int id) {
        String sql = SqlQueries.get("event.findById");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Event event = mapRow(rs);
                event.setClient(clientDao.findById(event.getClient().getClientId().intValue()).orElse(null));
                event.setVenue(venueDao.findById(event.getVenue().getVenueId().intValue()).orElse(null));
                return Optional.of(event);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске мероприятия по id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Event> findByStatus(EventStatus status) {
        List<Event> list = new ArrayList<>();
        String sql = SqlQueries.get("event.findByStatus");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status.getDbValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Event event = mapRow(rs);
                event.getClient().setName(rs.getString("client_name"));
                event.getVenue().setName(rs.getString("venue_name"));
                event.getVenue().setAddress(rs.getString("venue_address"));
                list.add(event);
            }
            logger.debug("Загружено {} мероприятий со статусом {}", list.size(), status.getDbValue());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке мероприятий по статусу {}", status.getDbValue(), e);
        }
        return list;
    }

    @Override
    public List<Event> findAll() {
        List<Event> list = new ArrayList<>();
        String sql = SqlQueries.get("event.findAll");
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Event event = mapRow(rs);
                event.getClient().setName(rs.getString("client_name"));
                event.getVenue().setName(rs.getString("venue_name"));
                event.getVenue().setAddress(rs.getString("venue_address"));
                list.add(event);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех мероприятий", e);
        }
        return list;
    }

    @Override
    public void save(Event event) {
        String sql = SqlQueries.get("event.save");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setObject(2, event.getEventDate());
            stmt.setBigDecimal(3, event.getBudget());
            stmt.setString(4, event.getStatus().getDbValue());
            stmt.setString(5, event.getFeedback());
            stmt.setLong(6, event.getClient().getClientId());
            stmt.setLong(7, event.getVenue().getVenueId());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                event.setEventId(keys.getLong(1));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении мероприятия {}", event.getName(), e);
        }
    }

    @Override
    public void update(Event event) {
        String sql = SqlQueries.get("event.update");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setObject(2, event.getEventDate());
            stmt.setBigDecimal(3, event.getBudget());
            stmt.setString(4, event.getStatus().getDbValue());
            stmt.setString(5, event.getFeedback());
            stmt.setLong(6, event.getClient().getClientId());
            stmt.setLong(7, event.getVenue().getVenueId());
            stmt.setLong(8, event.getEventId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении мероприятия id {}", event.getEventId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = SqlQueries.get("event.delete");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка при удалении мероприятия id {}", id, e);
        }
    }

    @Override
    public BigDecimal getRemainingBudget(long eventId) {
        String sql = SqlQueries.get("event.getRemainingBudget");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при вычислении остатка бюджета для мероприятия {}", eventId, e);
        }
        return BigDecimal.ZERO;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getLong("event_id"));
        event.setName(rs.getString("event_name"));
        event.setEventDate(rs.getObject("event_date", LocalDate.class));
        event.setBudget(rs.getBigDecimal("budget"));
        event.setStatus(EventStatus.fromDbValue(rs.getString("status")));
        event.setFeedback(rs.getString("feedback"));
        if (rs.getTimestamp("created_at") != null) {
            event.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        Client client = new Client();
        client.setClientId(rs.getLong("client_id"));
        event.setClient(client);
        Venue venue = new Venue();
        venue.setVenueId(rs.getLong("venue_id"));
        try {
            venue.setName(rs.getString("venue_name"));
            venue.setAddress(rs.getString("venue_address"));
            venue.setCapacity(rs.getInt("venue_capacity"));
        } catch (SQLException ignored) {}
        event.setVenue(venue);
        return event;
    }
}