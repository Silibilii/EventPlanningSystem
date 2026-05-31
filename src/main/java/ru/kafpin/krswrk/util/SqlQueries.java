package ru.kafpin.krswrk.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Properties;


/**
 * Утилитный класс для загрузки и хранения SQL-запросов из файла свойств.
 * Все запросы хранятся в файле.
 */
public class SqlQueries {
    private static final Logger logger = LoggerFactory.getLogger(SqlQueries.class);
    private static final Properties props = new Properties();

    static {
        try (InputStream is = SqlQueries.class.getResourceAsStream("/ru/kafpin/krswrk/sql.properties")) {
            if (is == null) {
                throw new RuntimeException("Файл sql.properties не найден в classpath");
            }
            props.load(is);
            logger.info("SQL-запросы загружены, всего {}", props.size());
        } catch (Exception e) {
            logger.error("Ошибка загрузки sql.properties", e);
            throw new RuntimeException("Не удалось загрузить SQL-запросы", e);
        }
    }

    /**
     * Возвращает SQL-запрос по ключу из файла свойств.
     *
     * @param key ключ запроса
     * @return строка SQL-запроса
     * @throws RuntimeException если ключ отсутствует в загруженных свойствах
     */
    public static String get(String key) {
        String sql = props.getProperty(key);
        if (sql == null) {
            throw new RuntimeException("SQL запрос не найден для ключа: " + key);
        }
        return sql;
    }
}