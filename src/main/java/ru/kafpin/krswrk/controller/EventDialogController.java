package ru.kafpin.krswrk.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ClientDao;
import ru.kafpin.krswrk.dao.EventDao;
import ru.kafpin.krswrk.dao.VenueDao;
import ru.kafpin.krswrk.model.*;
import ru.kafpin.krswrk.util.LocaleManager;
import ru.kafpin.krswrk.util.ValidationUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Контроллер диалога создания и редактирования мероприятия.
 */
public class EventDialogController {

    private static final Logger logger = LoggerFactory.getLogger(EventDialogController.class);

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private TextField budgetField;
    @FXML private ComboBox<Client> clientCombo;
    @FXML private ComboBox<Venue> venueCombo;
    @FXML private ComboBox<EventStatus> statusCombo;

    private Event event;
    @Setter private ClientDao clientDao;
    @Setter private VenueDao venueDao;
    @Setter private EventDao eventDao;
    @Setter private MainController mainController;
    @Setter private EventDetailsController eventDetailsController;

    @FXML
    public void initialize() {
        clientCombo.setCellFactory(lv -> new ListCell<Client>() {
            @Override protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        clientCombo.setButtonCell(clientCombo.getCellFactory().call(null));

        venueCombo.setCellFactory(lv -> new ListCell<Venue>() {
            @Override protected void updateItem(Venue item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getName() + " (" + item.getCapacity() + ")"));
            }
        });
        venueCombo.setButtonCell(new ListCell<Venue>() {
            @Override protected void updateItem(Venue item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getName() + " (" + item.getCapacity() + ")");
            }
        });

        statusCombo.setCellFactory(lv -> new ListCell<EventStatus>() {
            @Override protected void updateItem(EventStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLocalized(LocaleManager.getBundle()));
            }
        });
        statusCombo.setButtonCell(statusCombo.getCellFactory().call(null));
    }

    public void init() {
        refreshCombos();
    }

    void refreshCombos() {
        clientCombo.setItems(FXCollections.observableArrayList(clientDao.findAll()));
        venueCombo.setItems(FXCollections.observableArrayList(venueDao.findAll()));
        statusCombo.setItems(FXCollections.observableArrayList(EventStatus.values()));
    }

    public void setEvent(Event event) {
        this.event = event;
        if (event != null) {
            nameField.setText(event.getName());
            datePicker.setValue(event.getEventDate());
            budgetField.setText(event.getBudget().toString());
            clientCombo.setValue(event.getClient());
            venueCombo.setValue(event.getVenue());
            statusCombo.setValue(event.getStatus());
        } else {
            statusCombo.setValue(EventStatus.PLANNING);
        }
    }

    @FXML
    private void onSave() {
        if (!validateInput()) return;

        if (event == null) event = new Event();
        event.setName(nameField.getText());
        event.setEventDate(datePicker.getValue());
        event.setBudget(new BigDecimal(budgetField.getText().trim()));
        event.setClient(clientCombo.getValue());
        event.setVenue(venueCombo.getValue());
        event.setStatus(statusCombo.getValue());
        event.setFeedback(null);

        try {
            if (event.getEventId() == null || event.getEventId() == 0) {
                eventDao.save(event);
                logger.info("Сохранено новое мероприятие: {}", event.getName());
            } else {
                eventDao.update(event);
                logger.info("Обновлено мероприятие с id={}", event.getEventId());
            }
            closeForm();
        } catch (Exception e) {
            logger.error("Ошибка сохранения мероприятия", e);
            showError(LocaleManager.getBundle().getString("error.save.event"));
        }
    }

    private boolean validateInput() {
        if (!ValidationUtils.isNotBlank(nameField.getText())) {
            showError(LocaleManager.getBundle().getString("error.event.nameEmpty"));
            return false;
        }
        if (datePicker.getValue() == null) {
            showError(LocaleManager.getBundle().getString("error.event.dateEmpty"));
            return false;
        }
        if (!ValidationUtils.isNonNegativeBigDecimal(budgetField.getText())) {
            showError(LocaleManager.getBundle().getString("error.event.budgetNegative"));
            return false;
        }
        BigDecimal budget = new BigDecimal(budgetField.getText().trim());
        if (budget.compareTo(ValidationUtils.MAX_BUDGET) > 0) {
            String maxStr = ValidationUtils.MAX_BUDGET.toString();
            String msg = LocaleManager.getBundle().getString("error.event.budgetTooHigh");
            showError(MessageFormat.format(msg, maxStr));
            return false;
        }
        if (!ValidationUtils.isNotNull(clientCombo.getValue())) {
            showError(LocaleManager.getBundle().getString("error.event.clientEmpty"));
            return false;
        }
        if (!ValidationUtils.isNotNull(venueCombo.getValue())) {
            showError(LocaleManager.getBundle().getString("error.event.venueEmpty"));
            return false;
        }
        return true;
    }

    public void setSelectedClient(Client client) {
        if (client != null && client.getClientId() != null) {
            Client found = clientCombo.getItems().stream()
                    .filter(c -> client.getClientId().equals(c.getClientId()))
                    .findFirst()
                    .orElse(null);
            clientCombo.getSelectionModel().select(found);
        }
    }

    public void setSelectedVenue(Venue venue) {
        if (venue != null && venue.getVenueId() != null) {
            Venue found = venueCombo.getItems().stream()
                    .filter(v -> venue.getVenueId().equals(v.getVenueId()))
                    .findFirst()
                    .orElse(null);
            venueCombo.getSelectionModel().select(found);
        }
    }

    private void closeForm() {
        mainController.hideRightPanel();
        if (eventDetailsController != null) {
            eventDetailsController.refreshAllData();
        }
        mainController.refreshEventList();
    }

    @FXML
    public void onCancel(ActionEvent actionEvent) {
        closeForm();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}