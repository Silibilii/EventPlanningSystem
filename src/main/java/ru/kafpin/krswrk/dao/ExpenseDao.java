package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.model.Expense;
import ru.kafpin.krswrk.model.ExpenseCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс доступа к данным для сущности {@link Expense}.
 * Расширяет базовый CRUD интерфейс {@link Dao}.
 */
public interface ExpenseDao extends Dao<Expense> {

    /**
     * Возвращает все расходы мероприятия без фильтрации.
     *
     * @param eventId идентификатор мероприятия
     * @return список расходов
     */
    List<Expense> findByEventIdOnly(long eventId);

    /**
     * Возвращает расходы мероприятия по указанной категории.
     *
     * @param eventId  идентификатор мероприятия
     * @param category категория расхода
     * @return список расходов
     */
    List<Expense> findByEventIdAndCategory(long eventId, String category);

    /**
     * Возвращает расходы мероприятия, содержащие указанный текст в описании.
     *
     * @param eventId    идентификатор мероприятия
     * @param searchText текст для поиска в описании
     * @return список расходов
     */
    List<Expense> findByEventIdAndSearch(long eventId, String searchText);

    /**
     * Возвращает расходы мероприятия по категории и содержащие указанный текст в описании.
     *
     * @param eventId    идентификатор мероприятия
     * @param category   категория расхода
     * @param searchText текст для поиска в описании
     * @return список расходов
     */
    List<Expense> findByEventIdAndCategoryAndSearch(long eventId, String category, String searchText);

    /**
     * Возвращает общую сумму всех расходов по мероприятию.
     *
     * @param eventId идентификатор мероприятия
     * @return сумма расходов (0, если расходов нет)
     */
    BigDecimal getTotalExpensesByEventId(long eventId);

    /**
     * Возвращает сводку расходов по категориям для указанного мероприятия.
     * Вызов хранимой функции.
     *
     * @param eventId идентификатор мероприятия
     * @return карта, ключ — название категории, значение — общая сумма по категории
     */
    Map<String, BigDecimal> getExpenseSummaryByEventId(long eventId);
}