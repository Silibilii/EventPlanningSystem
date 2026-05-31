package ru.kafpin.krswrk.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Управляет подключением к базе данных.
 * Реализует соединение с автоматическим переподключением при разрыве.
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static Connection connection;
    private static String lastUser;
    private static String lastPassword;

    /**
     * Инициализирует соединение с БД с указанными учётными данными.
     * Если уже существовало открытое соединение, оно будет закрыто.
     *
     * @param user имя пользователя БД
     * @param password пароль пользователя
     * @throws SQLException если не удалось установить соединение
     */
    public static void initConnection(String user, String password) throws SQLException {
        lastUser = user;
        lastPassword = password;
        if (connection != null && !connection.isClosed()) {
            closeConnection();
        }
        String url = "jdbc:postgresql://localhost:5432/events";
        logger.info("Подключение к БД пользователем {}", user);
        connection = DriverManager.getConnection(url, user, password);
        logger.info("Соединение установлено");
    }

    /**
     * Возвращает активное соединение с БД.
     * Если соединение отсутствует или закрыто, выполняет автоматическое переподключение.
     *
     * @return активное соединение
     * @throws SQLException если соединение не было инициализировано
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (lastUser == null) {
                throw new SQLException("Соединение не инициализировано.");
            }
            logger.warn("Соединение потеряно, переподключение пользователем {}", lastUser);
            initConnection(lastUser, lastPassword);
        }
        return connection;
    }

    /**
     * Закрывает текущее соединение с БД, если оно открыто.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Соединение закрыто");
            } catch (SQLException e) {
                logger.error("Ошибка закрытия соединения", e);
            }
        }
    }
}