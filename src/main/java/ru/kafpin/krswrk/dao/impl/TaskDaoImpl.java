package ru.kafpin.krswrk.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ContractorDao;
import ru.kafpin.krswrk.dao.TaskDao;
import ru.kafpin.krswrk.model.Contractor;
import ru.kafpin.krswrk.model.Task;
import ru.kafpin.krswrk.model.TaskStatus;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.util.SqlQueries;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDaoImpl implements TaskDao {

    private static final Logger logger = LoggerFactory.getLogger(TaskDaoImpl.class);
    private final ContractorDao contractorDao;

    public TaskDaoImpl(ContractorDao contractorDao) {
        this.contractorDao = contractorDao;
    }

    @Override
    public List<Task> findByEventIdOnly(long eventId) {
        String sql = SqlQueries.get("task.findByEventIdOnly");
        return executeQuery(sql, eventId);
    }

    @Override
    public List<Task> findByEventIdAndStatus(long eventId, TaskStatus status) {
        String sql = SqlQueries.get("task.findByEventIdAndStatus");
        return executeQuery(sql, eventId, status.getDbValue());
    }

    @Override
    public List<Task> findByEventIdAndSearch(long eventId, String searchText) {
        String sql = SqlQueries.get("task.findByEventIdAndSearch");
        return executeQuery(sql, eventId, "%" + searchText.trim() + "%");
    }

    @Override
    public List<Task> findByEventIdAndStatusAndSearch(long eventId, TaskStatus status, String searchText) {
        String sql = SqlQueries.get("task.findByEventIdAndStatusAndSearch");
        return executeQuery(sql, eventId, status.getDbValue(), "%" + searchText.trim() + "%");
    }

    private List<Task> executeQuery(String sql, Object... params) {
        List<Task> list = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка выполнения запроса: " + sql, e);
        }
        return list;
    }

    @Override
    public Optional<Task> findById(int id) {
        String sql = SqlQueries.get("task.findById");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.debug("Найдена задача с id {}", id);
                return Optional.of(mapRow(rs));
            } else {
                logger.debug("Задача с id {} не найдена", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске задачи по id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Task> findAll() {
        List<Task> list = new ArrayList<>();
        String sql = SqlQueries.get("task.findAll");
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Загружено {} задач", list.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех задач", e);
        }
        return list;
    }

    @Override
    public void save(Task task) {
        String sql = SqlQueries.get("task.save");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getDescription());
            stmt.setObject(2, task.getDeadline());
            stmt.setString(3, task.getStatus().getDbValue());
            stmt.setLong(4, task.getEvent().getEventId());
            Long contractorId = null;
            if (task.getResponsibleContractor() != null) {
                contractorId = task.getResponsibleContractor().getContractorId();
            }
            if (contractorId != null && contractorId > 0) {
                stmt.setLong(5, contractorId);
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                logger.warn("Сохранение задачи не затронуло строк");
            }
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                task.setTaskId(keys.getLong(1));
                logger.info("Сохранена задача с id {}", task.getTaskId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении задачи", e);
        }
    }

    @Override
    public void update(Task task) {
        String sql = SqlQueries.get("task.update");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, task.getDescription());
            stmt.setObject(2, task.getDeadline());
            stmt.setString(3, task.getStatus().getDbValue());
            stmt.setLong(4, task.getEvent().getEventId());
            Long contractorId = null;
            if (task.getResponsibleContractor() != null) {
                contractorId = task.getResponsibleContractor().getContractorId();
            }
            if (contractorId != null && contractorId > 0) {
                stmt.setLong(5, contractorId);
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.setLong(6, task.getTaskId());
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                logger.info("Обновлена задача с id {}", task.getTaskId());
            } else {
                logger.warn("Обновление задачи с id {} не затронуло строк", task.getTaskId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении задачи id {}", task.getTaskId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = SqlQueries.get("task.delete");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                logger.info("Удалена задача с id {}", id);
            } else {
                logger.warn("Удаление задачи с id {} не затронуло строк", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении задачи id {}", id, e);
        }
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setTaskId(rs.getLong("task_id"));
        task.setDescription(rs.getString("description"));
        task.setDeadline(rs.getObject("deadline", LocalDate.class));
        task.setStatus(TaskStatus.fromDbValue(rs.getString("status")));
        Event event = new Event();
        event.setEventId(rs.getLong("event_id"));
        task.setEvent(event);

        long contractorId = rs.getLong("responsible_contractor_id");
        if (!rs.wasNull() && contractorId != 0) {
            Contractor contractor = new Contractor();
            contractor.setContractorId(contractorId);
            task.setResponsibleContractor(contractor);
        } else {
            task.setResponsibleContractor(null);
        }
        return task;
    }
}