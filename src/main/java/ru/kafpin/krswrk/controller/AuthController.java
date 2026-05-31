package ru.kafpin.krswrk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.util.LocaleManager;

import java.sql.SQLException;

/**
 * Контроллер окна аутентификации.
 * Отвечает за отправку введённого логина и пароля с формы в класс DatabaseConnection.
 * В случае успешного подключения закрывает окно и открывает основное приложение.
 */
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    /**
     * Обработчик нажатия кнопки входа.
     * Пытается установить подключение к базе по введённому логину и паролю.
     */
    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        //username="manager";
        //password="manager";
        try {
            DatabaseConnection.initConnection(username, password);
            logger.info("Успешный вход пользователя {}", username);
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (SQLException e) {
            logger.warn("Неудачная попытка входа для пользователя {}", username);
            showError(LocaleManager.getBundle().getString("auth.error.connection"));
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}