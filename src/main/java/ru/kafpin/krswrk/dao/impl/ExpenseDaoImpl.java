package ru.kafpin.krswrk.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ContractorDao;
import ru.kafpin.krswrk.dao.ExpenseDao;
import ru.kafpin.krswrk.model.Contractor;
import ru.kafpin.krswrk.model.Expense;
import ru.kafpin.krswrk.model.ExpenseCategory;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.util.SqlQueries;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ExpenseDaoImpl implements ExpenseDao {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseDaoImpl.class);
    private final ContractorDao contractorDao;

    public ExpenseDaoImpl(ContractorDao contractorDao) {
        this.contractorDao = contractorDao;
    }

    @Override
    @Deprecated
    public Optional<Expense> findById(int id) {
        String sql = SqlQueries.get("expense.findById");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске расхода по id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public BigDecimal getTotalExpensesByEventId(long eventId) {
        String sql = SqlQueries.get("expense.getTotalByEventId");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении общей суммы расходов для мероприятия {}", eventId, e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Map<String, BigDecimal> getExpenseSummaryByEventId(long eventId) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        String sql = SqlQueries.get("expense.getSummaryByEvent");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("category_name"), rs.getBigDecimal("total_amount"));
            }
        } catch (SQLException e) {
            logger.error("Ошибка получения сводки расходов для мероприятия {}", eventId, e);
        }
        return result;
    }

    @Override
    public List<Expense> findByEventIdOnly(long eventId) {
        String sql = SqlQueries.get("expense.findByEventIdOnly");
        return executeQuery(sql, eventId);
    }

    @Override
    public List<Expense> findByEventIdAndCategory(long eventId, String category) {
        String sql = SqlQueries.get("expense.findByEventIdAndCategory");
        return executeQuery(sql, eventId, category);
    }

    @Override
    public List<Expense> findByEventIdAndSearch(long eventId, String searchText) {
        String sql = SqlQueries.get("expense.findByEventIdAndSearch");
        return executeQuery(sql, eventId, "%" + searchText.trim() + "%");
    }

    @Override
    public List<Expense> findByEventIdAndCategoryAndSearch(long eventId, String category, String searchText) {
        String sql = SqlQueries.get("expense.findByEventIdAndCategoryAndSearch");
        return executeQuery(sql, eventId, category, "%" + searchText.trim() + "%");
    }

    private List<Expense> executeQuery(String sql, Object... params) {
        List<Expense> list = new ArrayList<>();
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
    public List<Expense> findAll() {
        List<Expense> list = new ArrayList<>();
        String sql = SqlQueries.get("expense.findAll");
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Загружено {} расходов", list.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех расходов", e);
        }
        return list;
    }

    @Override
    public void save(Expense expense) {
        String sql = SqlQueries.get("expense.save");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBigDecimal(1, expense.getAmount());
            stmt.setObject(2, expense.getExpenseDate());
            stmt.setString(3, expense.getDescription());
            stmt.setLong(4, expense.getEvent().getEventId());
            if (expense.getContractor() != null) {
                stmt.setLong(5, expense.getContractor().getContractorId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.setString(6, expense.getCategory().getDbValue());
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                logger.warn("Сохранение расхода не затронуло строк");
            }
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                expense.setExpenseId(keys.getLong(1));
                logger.info("Сохранён расход с id {} для мероприятия {}", expense.getExpenseId(), expense.getEvent().getEventId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении расхода для мероприятия {}", expense.getEvent().getEventId(), e);
        }
    }

    @Override
    public void update(Expense expense) {
        String sql = SqlQueries.get("expense.update");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setBigDecimal(1, expense.getAmount());
            stmt.setObject(2, expense.getExpenseDate());
            stmt.setString(3, expense.getDescription());
            stmt.setLong(4, expense.getEvent().getEventId());
            if (expense.getContractor() != null) {
                stmt.setLong(5, expense.getContractor().getContractorId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.setString(6, expense.getCategory().getDbValue());
            stmt.setLong(7, expense.getExpenseId());
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                logger.info("Обновлён расход с id {}", expense.getExpenseId());
            } else {
                logger.warn("Обновление расхода с id {} не затронуло строк", expense.getExpenseId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении расхода id {}", expense.getExpenseId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = SqlQueries.get("expense.delete");
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                logger.info("Удалён расход с id {}", id);
            } else {
                logger.warn("Удаление расхода с id {} не затронуло строк", id);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении расхода id {}", id, e);
        }
    }

    private Expense mapRow(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setExpenseId(rs.getLong("expense_id"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setExpenseDate(rs.getObject("expense_date", LocalDate.class));
        expense.setDescription(rs.getString("description"));

        Event event = new Event();
        event.setEventId(rs.getLong("event_id"));
        expense.setEvent(event);

        long contractorId = rs.getLong("contractor_id");
        if (!rs.wasNull()) {
            Contractor contractor = new Contractor();
            contractor.setContractorId(contractorId);
            expense.setContractor(contractor);
        } else {
            expense.setContractor(null);
        }

        expense.setCategory(ExpenseCategory.fromDbValue(rs.getString("category")));

        return expense;
    }
}