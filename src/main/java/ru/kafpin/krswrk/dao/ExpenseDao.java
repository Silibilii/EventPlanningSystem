package ru.kafpin.krswrk.dao;

import ru.kafpin.krswrk.model.Expense;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Expense}.
 * Предоставляет стандартные CRUD-операции и методы для фильтрации и получения сводок.
 */
public interface ExpenseDao {

    /**
     * Находит расход по его идентификатору.
     *
     * @param id идентификатор расхода
     * @return Optional с найденным расходом, либо пустой Optional если расход не найден
     */
    Optional<Expense> findById(int id);

    /**
     * Возвращает список всех расходов.
     *
     * @return список расходов
     */
    List<Expense> findAll();

    /**
     * Сохраняет новый расход в базе данных.
     *
     * @param entity расход для сохранения
     */
    void save(Expense entity);

    /**
     * Обновляет существующий расход в базе данных.
     *
     * @param entity расход с обновлёнными данными
     */
    void update(Expense entity);

    /**
     * Удаляет расход по его идентификатору.
     *
     * @param id идентификатор удаляемого расхода
     */
    void delete(int id);

    /**
     * Возвращает все расходы мероприятия без фильтрации.
     *
     * @param eventId идентификатор мероприятия
     * @return список расходов, отсортированный по дате расхода (от новых к старым)
     */
    List<Expense> findByEventIdOnly(long eventId);

    /**
     * Возвращает расходы мероприятия по указанной категории.
     *
     * @param eventId  идентификатор мероприятия
     * @param category категория расхода (например, "аренда", "кейтеринг")
     * @return список расходов, отсортированный по дате расхода
     */
    List<Expense> findByEventIdAndCategory(long eventId, String category);

    /**
     * Возвращает расходы мероприятия, содержащие указанный текст в описании.
     *
     * @param eventId    идентификатор мероприятия
     * @param searchText текст для поиска в описании
     * @return список расходов, отсортированный по дате расхода
     */
    List<Expense> findByEventIdAndSearch(long eventId, String searchText);

    /**
     * Возвращает расходы мероприятия по категории и содержащие указанный текст в описании.
     *
     * @param eventId    идентификатор мероприятия
     * @param category   категория расхода
     * @param searchText текст для поиска в описании
     * @return список расходов, отсортированный по дате расхода
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
     * Данные получаются вызовом хранимой функции.
     *
     * @param eventId идентификатор мероприятия
     * @return карта, ключ — название категории, значение — общая сумма по данной категории
     */
    Map<String, BigDecimal> getExpenseSummaryByEventId(long eventId);
}