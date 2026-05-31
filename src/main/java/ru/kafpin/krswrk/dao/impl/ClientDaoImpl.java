package ru.kafpin.krswrk.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ClientDao;
import ru.kafpin.krswrk.model.Client;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.util.SqlQueries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDaoImpl implements ClientDao {

    private static final Logger logger = LoggerFactory.getLogger(ClientDaoImpl.class);

    @Override
    public Optional<Client> findById(int id) {
        String sql = SqlQueries.get("client.findById");
        logger.debug("Выполнение запроса: {}", sql);
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Client client = mapRow(rs);
                logger.debug("Клиент с id={} найден", id);
                return Optional.of(client);
            }
            logger.debug("Клиент с id={} не найден", id);
        } catch (SQLException e) {
            logger.error("Ошибка при поиске клиента по id={}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        String sql = SqlQueries.get("client.findAll");
        logger.debug("Выполнение запроса: {}", sql);
        List<Client> list = new ArrayList<>();
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Загружено {} клиентов", list.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех клиентов", e);
        }
        return list;
    }

    @Override
    public void save(Client client) {
        String sql = SqlQueries.get("client.save");
        logger.debug("Выполнение запроса: {}", sql);
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getContactPerson());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getEmail());
            stmt.setBoolean(5, client.getIsLegal());
            stmt.setString(6, client.getOrganizationName());
            stmt.setString(7, client.getNotes());
            int affected = stmt.executeUpdate();
            logger.debug("Сохранение клиента, затронуто строк: {}", affected);
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                client.setClientId(keys.getLong(1));
                logger.debug("Сгенерирован client_id={}", client.getClientId());
            } else {
                logger.warn("Не удалось получить сгенерированный ключ для клиента");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении клиента {}", client.getName(), e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = SqlQueries.get("client.update");
        logger.debug("Выполнение запроса: {}", sql);
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getContactPerson());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getEmail());
            stmt.setBoolean(5, client.getIsLegal());
            stmt.setString(6, client.getOrganizationName());
            stmt.setString(7, client.getNotes());
            stmt.setLong(8, client.getClientId());
            int affected = stmt.executeUpdate();
            logger.debug("Обновление клиента id={}, затронуто строк: {}", client.getClientId(), affected);
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении клиента id={}", client.getClientId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = SqlQueries.get("client.delete");
        logger.debug("Выполнение запроса: {}", sql);
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            logger.debug("Удаление клиента id={}, затронуто строк: {}", id, affected);
        } catch (SQLException e) {
            logger.error("Ошибка при удалении клиента id={}", id, e);
        }
    }

    private Client mapRow(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setClientId(rs.getLong("client_id"));
        client.setName(rs.getString("client_name"));
        client.setContactPerson(rs.getString("contact_person"));
        client.setPhone(rs.getString("phone"));
        client.setEmail(rs.getString("email"));
        client.setIsLegal(rs.getBoolean("is_legal"));
        client.setOrganizationName(rs.getString("organization_name"));
        client.setNotes(rs.getString("notes"));
        return client;
    }
}