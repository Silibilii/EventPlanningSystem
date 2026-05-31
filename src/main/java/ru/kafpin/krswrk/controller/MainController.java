package ru.kafpin.krswrk.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.*;
import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.model.EventStatus;
import ru.kafpin.krswrk.util.LocaleManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;



/**
 * Главный контроллер приложения: управление левой панелью, списком мероприятий, правой панелью.
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    @Getter
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> nameColumn;
    @FXML private TableColumn<Event, LocalDate> dateColumn;
    @FXML private BorderPane mainPane;
    @FXML private VBox leftPanel;
    @FXML private AnchorPane rightPanel;
    @FXML private VBox rightFormContainer;
    @FXML private ComboBox<String> languageCombo;
    @FXML private ComboBox<EventStatus> eventStatusFilterCombo;
    @FXML private TableColumn<Event, String> statusColumn;

    @Setter private EventDao eventDao;
    @Setter private ClientDao clientDao;
    @Setter private VenueDao venueDao;
    @Setter private ExpenseDao expenseDao;
    @Setter private TaskDao taskDao;
    @Setter private GuestDao guestDao;
    @Setter private ContractorDao contractorDao;

    private final ObservableList<Event> events = FXCollections.observableArrayList();
    @Setter private EventDetailsController currentEventDetailsController;



    /**
     * Инициализирует элементы управления, не требующие доступа к базе данных.
     * Вызывается автоматически после загрузки FXML.
     */
    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        dateColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getEventDate()));
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().getLocalized(LocaleManager.getBundle())));

        // Слушатель выбора мероприятия в таблице
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadEventDetails(newVal);
            }
        });

        // Настройка выбора языка
        languageCombo.getItems().addAll("Русский", "English", "Deutsch", "中文");

        String currentLang = LocaleManager.getCurrentLocale().getLanguage();
        if ("en".equals(currentLang)) {
            languageCombo.setValue("English");
        } else if ("de".equals(currentLang)) {
            languageCombo.setValue("Deutsch");
        } else if ("zh".equals(currentLang)) {
            languageCombo.setValue("中文");
        } else {
            languageCombo.setValue("Русский");
        }
        languageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            Locale newLocale;
            switch (newVal) {
                case "English": newLocale = new Locale("en"); break;
                case "Deutsch": newLocale = new Locale("de"); break;
                case "中文": newLocale = new Locale("zh"); break;
                default: newLocale = new Locale("ru");
            }
            LocaleManager.setLocale(newLocale);
            reloadMainWindow();
        });

        // Настройка фильтра по статусу мероприятия
        eventStatusFilterCombo.setItems(FXCollections.observableArrayList(EventStatus.values()));
        eventStatusFilterCombo.getItems().add(0, null);
        eventStatusFilterCombo.setCellFactory(lv -> new ListCell<EventStatus>() {
            @Override protected void updateItem(EventStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(LocaleManager.getBundle().getString("filter.all"));
                else setText(item.getLocalized(LocaleManager.getBundle()));
            }
        });
        eventStatusFilterCombo.setButtonCell(eventStatusFilterCombo.getCellFactory().call(null));
        eventStatusFilterCombo.setValue(null);
        // Слушатель изменения фильтра – обновляет таблицу мероприятий
        eventStatusFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshEventList());
    }

    /**
     * Инициализирует компоненты, зависящие от DAO.
     * Должен быть вызван после того, как все DAO установлены через сеттеры.
     */
    public void init() {
        refreshEventList();
        if (!events.isEmpty()) {
            eventTable.getSelectionModel().select(0);
        }
    }

    /**
     * Перезагружает главное окно при смене языка, сохраняя выбранное мероприятие и вкладку.
     */
    private void reloadMainWindow() {

        Long selectedEventId = null;
        int selectedTabIndex = -1;

        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            selectedEventId = selectedEvent.getEventId();
        }
        if (currentEventDetailsController != null) {
            selectedTabIndex = currentEventDetailsController.getSelectedTabIndex();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/mainforms/main.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent root = loader.load();
            MainController newController = loader.getController();

            // Передаём DAO новому контроллеру
            newController.setEventDao(eventDao);
            newController.setClientDao(clientDao);
            newController.setVenueDao(venueDao);
            newController.setExpenseDao(expenseDao);
            newController.setTaskDao(taskDao);
            newController.setGuestDao(guestDao);
            newController.setContractorDao(contractorDao);
            newController.init();

            newController.restoreState(selectedEventId, selectedTabIndex);

            Scene scene = languageCombo.getScene();
            scene.setRoot(root);

            if (scene.getWindow() != null) {
                ((Stage) scene.getWindow()).setTitle(LocaleManager.getBundle().getString("app.title"));
            }
        } catch (IOException e) {
            logger.error("Ошибка загрузки FXML", e);
            showError(LocaleManager.getBundle().getString("error.reload.interface"));
        }
    }

    /**
     * Восстанавливает выбранное мероприятие и активную вкладку после перезагрузки окна.
     * @param eventId идентификатор мероприятия
     * @param tabIndex индекс вкладки
     */
    public void restoreState(Long eventId, int tabIndex) {
        if (eventId != null) {
            Event found = eventTable.getItems().stream()
                    .filter(e -> e.getEventId().equals(eventId))
                    .findFirst()
                    .orElse(null);
            if (found != null) {
                eventTable.getSelectionModel().select(found);
                loadEventDetails(found);
                if (currentEventDetailsController != null && tabIndex >= 0) {
                    int finalTabIndex = tabIndex;
                    Platform.runLater(() -> currentEventDetailsController.setSelectedTabIndex(finalTabIndex));
                }
            }
        }
    }

    /**
     * Показывает форму в правой панели.
     */
    public void showFormInRightPanel(Node formNode) {
        rightFormContainer.getChildren().setAll(formNode);
        rightPanel.setVisible(true);
        rightPanel.setManaged(true);
    }

    /**
     * Скрывает правую панель.
     */
    public void hideRightPanel() {
        rightPanel.setVisible(false);
        rightPanel.setManaged(false);
        rightFormContainer.getChildren().clear();
    }


    @FXML
    private void toggleLeftPanel() {
        boolean visible = leftPanel.isVisible();
        leftPanel.setVisible(!visible);
        leftPanel.setManaged(!visible);
    }

    @FXML
    private void onNewClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/client_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            ClientDialogController controller = loader.getController();
            controller.setClientDao(clientDao);
            controller.setClient(null);
            controller.setMainController(this);
            controller.setEventDialogController(null);
            controller.init();
            showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки FXML", e);
            showError(LocaleManager.getBundle().getString("error.open.client.form"));
        }
    }

    @FXML
    private void onNewVenue() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/venue_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            VenueDialogController controller = loader.getController();
            controller.setVenueDao(venueDao);
            controller.setVenue(null);
            controller.setMainController(this);
            controller.setEventDialogController(null);
            controller.init();
            showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки FXML", e);
            showError(LocaleManager.getBundle().getString("error.open.venue.form"));
        }
    }

    @FXML
    private void exitApplication() {
        Platform.exit();
    }

    /**
     * Обновление списка мероприятий.
     */
    public void refreshEventList() {
        if (eventDao == null) return;
        EventStatus selectedStatus = eventStatusFilterCombo.getValue();
        List<Event> eventsList = (selectedStatus == null) ? eventDao.findAll() : eventDao.findByStatus(selectedStatus);
        events.setAll(eventsList);
        eventTable.setItems(events);
    }

    void loadEventDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/mainforms/event_details.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Node detailsView = loader.load();
            EventDetailsController controller = loader.getController();
            controller.setMainController(this);
            controller.setEventDao(eventDao);
            controller.setExpenseDao(expenseDao);
            controller.setTaskDao(taskDao);
            controller.setGuestDao(guestDao);
            controller.setContractorDao(contractorDao);
            setCurrentEventDetailsController(controller);
            controller.setEvent(event);
            mainPane.setCenter(detailsView);
        } catch (IOException e) {
            showError(LocaleManager.getBundle().getString("error.open.event.form"));
        }
    }

    @FXML
    private void onRefreshEvents() {
        refreshEventList();
        if (!events.isEmpty()) {
            eventTable.getSelectionModel().select(0);
        } else {
            mainPane.setCenter(null);
        }
    }

    @FXML
    private void onNewEvent() {
        openEventFormInRightPanel(null);
    }

    void openEventFormInRightPanel(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/event_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            EventDialogController controller = loader.getController();
            controller.setClientDao(clientDao);
            controller.setVenueDao(venueDao);
            controller.setEventDao(eventDao);
            controller.setMainController(this);
            controller.setEvent(event);
            controller.init();
            showFormInRightPanel(form);
        } catch (IOException e) {
            showError(LocaleManager.getBundle().getString("error.load.event.details"));
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearEventDetails() {
        mainPane.setCenter(null);
    }

    public void selectFirstEventIfAny() {
        if (!events.isEmpty()) {
            eventTable.getSelectionModel().select(0);
            loadEventDetails(events.getFirst());
        }
    }

    @FXML
    private void onNewContractor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/contractor_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            ContractorDialogController controller = loader.getController();
            controller.setContractorDao(contractorDao);
            controller.setMainController(this);
            controller.setEventDetailsController(currentEventDetailsController);
            controller.init();
            controller.setContractor(null);
            showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки FXML", e);
            showError(LocaleManager.getBundle().getString("error.load.form"));
        }
    }
}