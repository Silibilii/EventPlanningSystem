package ru.kafpin.krswrk.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Утилитный класс для валидации данных, вводимых пользователем.
 * Содержит статические методы для проверки телефонов, email, чисел, дат, а также ограничений
 * в соответствии с типами полей в базе данных.
 */
public final class ValidationUtils {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[\\d\\s\\-\\(\\)]{5,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public static final BigDecimal MAX_EXPENSE_AMOUNT = new BigDecimal("99999999.99");
    public static final BigDecimal MAX_BUDGET = new BigDecimal("9999999999.99");
    public static final BigDecimal MAX_RENTAL_COST = new BigDecimal("99999999.99");
    public static final int MAX_CAPACITY = 1_000_000;

    private ValidationUtils() {
    }

    /**
     * Проверяет, что строка не null и не состоит только из пробелов.
     *
     * @param s строка для проверки
     * @return true, если строка содержит хотя бы один непробельный символ
     */
    public static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
     * Проверяет, что строка является целым положительным числом.
     *
     * @param s строка для проверки
     * @return true, если число положительное
     */
    public static boolean isPositiveInt(String s) {
        if (!isNotBlank(s)) return false;
        try {
            int value = Integer.parseInt(s.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Проверяет, что строка является положительным числом с плавающей точкой.
     *
     * @param s строка для проверки
     * @return true, если строка представляет положительное число
     */
    public static boolean isPositiveBigDecimal(String s) {
        if (!isNotBlank(s)) return false;
        try {
            BigDecimal value = new BigDecimal(s.trim());
            return value.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Проверяет, что строка является неотрицательным числом.
     *
     * @param s строка для проверки
     * @return true, если строка представляет неотрицательное число
     */
    public static boolean isNonNegativeBigDecimal(String s) {
        if (!isNotBlank(s)) return false;
        try {
            BigDecimal value = new BigDecimal(s.trim());
            return value.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Проверяет корректность номера телефона.
     * Пустое поле считается валидным.
     *
     * @param phone телефонная строка
     * @return true, если телефон корректен или пуст
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return true;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Проверяет корректность email-адреса.
     * Пустое поле считается валидным.
     *
     * @param email строка email
     * @return true, если email корректен или пуст
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Проверяет, что объект не равен null.
     *
     * @param obj проверяемый объект
     * @return true, если obj != null
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * Проверяет корректность суммы расхода: положительное число и не превышает максимального значения 99 999 999.99.
     *
     * @param s строка суммы расхода
     * @return true, если сумма корректна
     */
    public static boolean isExpenseAmountValid(String s) {
        if (!isPositiveBigDecimal(s)) return false;
        BigDecimal value = new BigDecimal(s.trim());
        return value.compareTo(MAX_EXPENSE_AMOUNT) <= 0;
    }

    /**
     * Проверяет корректность бюджета мероприятия: неотрицательное число и не превышает максимального значения 9 999 999 999.99.
     *
     * @param s строка бюджета
     * @return true, если бюджет корректен
     */
    public static boolean isBudgetValid(String s) {
        if (!isNonNegativeBigDecimal(s)) return false;
        BigDecimal value = new BigDecimal(s.trim());
        return value.compareTo(MAX_BUDGET) <= 0;
    }

    /**
     * Проверяет корректность стоимости аренды площадки: неотрицательное число и не превышает максимального значения 99 999 999.99.
     *
     * @param s строка стоимости аренды
     * @return true, если стоимость аренды корректна
     */
    public static boolean isRentalCostValid(String s) {
        if (!isNonNegativeBigDecimal(s)) return false;
        BigDecimal value = new BigDecimal(s.trim());
        return value.compareTo(MAX_RENTAL_COST) <= 0;
    }

    /**
     * Проверяет, что строка не пуста и не содержит цифр.
     *
     * @param s строка для проверки
     * @return true, если строка не пуста и не содержит цифр
     */
    public static boolean isValidName(String s) {
        if (!isNotBlank(s)) return false;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) return false;
        }
        return true;
    }


    /**
     * Проверяет корректность вместимости площадки: положительное целое число и не превышает максимального значения 1 000 000.
     *
     * @param s строка вместимости
     * @return true, если вместимость корректна
     */
    public static boolean isCapacityValid(String s) {
        if (!isPositiveInt(s)) return false;
        int value = Integer.parseInt(s.trim());
        return value <= MAX_CAPACITY;
    }
}