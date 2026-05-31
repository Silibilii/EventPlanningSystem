package ru.kafpin.krswrk;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kafpin.krswrk.controller.MainController;
import ru.kafpin.krswrk.dao.*;
import ru.kafpin.krswrk.dao.impl.*;
import ru.kafpin.krswrk.util.DatabaseConnection;
import ru.kafpin.krswrk.util.LocaleManager;

import java.util.Objects;

/**
 * Точка входа приложения ().
 * Загружает окно аутентификации, инициализирует DAO, устанавливает соединение с БД,
 * затем запускает окно приложения.
 */
public class MainApplication extends Application {

    /**
     * Точка входа в приложение.
     * Выполняет аутентификацию, инициализацию DAO и запуск главного окна.
     *
     * @param primaryStage первичная сцена
     * @throws Exception если не удаётся загрузить ресурсы
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/login.fxml"));
        loginLoader.setResources(LocaleManager.getBundle());
        Parent loginRoot = loginLoader.load();
        Stage loginStage = new Stage();
        Scene scene = new Scene(loginRoot);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ru/kafpin/krswrk/css/styles.css")).toExternalForm());
        loginStage.setScene(scene);
        loginStage.setTitle(LocaleManager.getBundle().getString("auth.login.title"));
        loginStage.showAndWait();

        try {
            DatabaseConnection.getConnection();
        } catch (Exception e) {
            Platform.exit();
            return;
        }

        ClientDao clientDao = new ClientDaoImpl();
        VenueDao venueDao = new VenueDaoImpl();
        ContractorDao contractorDao = new ContractorDaoImpl();
        ExpenseDao expenseDao = new ExpenseDaoImpl(contractorDao);
        TaskDao taskDao = new TaskDaoImpl(contractorDao);
        GuestDao guestDao = new GuestDaoImpl();
        EventDao eventDao = new EventDaoImpl(clientDao, venueDao);

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/mainforms/main.fxml"));
        mainLoader.setResources(LocaleManager.getBundle());
        Parent mainRoot = mainLoader.load();
        MainController mainController = mainLoader.getController();

        mainController.setEventDao(eventDao);
        mainController.setClientDao(clientDao);
        mainController.setVenueDao(venueDao);
        mainController.setExpenseDao(expenseDao);
        mainController.setTaskDao(taskDao);
        mainController.setGuestDao(guestDao);
        mainController.setContractorDao(contractorDao);
        mainController.init();

        Stage mainStage = new Stage();
        scene = new Scene(mainRoot);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ru/kafpin/krswrk/css/styles.css")).toExternalForm());
        mainStage.setScene(scene);
        mainStage.setTitle(LocaleManager.getBundle().getString("app.title"));
        mainStage.show();

        mainStage.setOnCloseRequest(event -> {
            DatabaseConnection.closeConnection();
            Platform.exit();
        });
    }

    /**
     * Метод запуска приложения.
     */
    public static void main(String[] args) {
        launch(args);
    }
}