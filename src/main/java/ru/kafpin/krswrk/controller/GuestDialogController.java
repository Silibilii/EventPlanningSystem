package ru.kafpin.krswrk.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.GuestDao;
import ru.kafpin.krswrk.model.Guest;
import ru.kafpin.krswrk.model.GuestStatus;
import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.util.LocaleManager;
import ru.kafpin.krswrk.util.ValidationUtils;

/**
 * Контроллер диалога добавления и редактирования гостя.
 */
public class GuestDialogController {

    private static final Logger logger = LoggerFactory.getLogger(GuestDialogController.class);

    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<GuestStatus> statusCombo;

    private Guest guest;
    @Setter private Event event;
    @Setter private GuestDao guestDao;
    @Setter private MainController mainController;
    @Setter private EventDetailsController eventDetailsController;

    public void setGuest(Guest guest) {
        this.guest = guest;
        if (guest != null) {
            fullNameField.setText(guest.getFullName());
            phoneField.setText(guest.getContactPhone());
            emailField.setText(guest.getContactEmail());
            statusCombo.setValue(guest.getInvitationStatus());
        } else {
            fullNameField.clear();
            phoneField.clear();
            emailField.clear();
            statusCombo.setValue(GuestStatus.INVITED);
        }
    }

    @FXML
    public void initialize() {
        statusCombo.setCellFactory(lv -> new ListCell<GuestStatus>() {
            @Override protected void updateItem(GuestStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLocalized(LocaleManager.getBundle()));
            }
        });
        statusCombo.setButtonCell(statusCombo.getCellFactory().call(null));
    }

    public void init() {
        statusCombo.setItems(FXCollections.observableArrayList(GuestStatus.values()));
    }

    @FXML
    private void onSave() {
        String fullName = fullNameField.getText().trim();
        if (fullName.isEmpty()) {
            showError(LocaleManager.getBundle().getString("error.guest.nameEmpty"));
            return;
        }
        if (!ValidationUtils.isValidName(fullName)) {
            showError(LocaleManager.getBundle().getString("error.nameInvalid"));
            return;
        }

        String phone = phoneField.getText().trim();
        if (!ValidationUtils.isValidPhone(phone)) {
            showError(LocaleManager.getBundle().getString("error.phoneInvalid"));
            return;
        }

        String email = emailField.getText().trim();
        if (!ValidationUtils.isValidEmail(email)) {
            showError(LocaleManager.getBundle().getString("error.emailInvalid"));
            return;
        }

        if (guest == null) guest = new Guest();
        guest.setFullName(fullName);
        guest.setContactPhone(phone);
        guest.setContactEmail(email);
        guest.setInvitationStatus(statusCombo.getValue());
        guest.setEvent(event);

        try {
            if (guest.getGuestId() == null || guest.getGuestId() == 0) {
                guestDao.save(guest);
                logger.info("Сохранён новый гость: {}", guest.getFullName());
            } else {
                guestDao.update(guest);
                logger.info("Обновлён гость с id={}", guest.getGuestId());
            }
            closeForm();
        } catch (Exception e) {
            logger.error("Ошибка сохранения гостя", e);
            showError(LocaleManager.getBundle().getString("error.save.guest"));
        }
    }

    @FXML
    private void onCancel() {
        closeForm();
    }

    private void closeForm() {
        mainController.hideRightPanel();
        if (eventDetailsController != null) {
            eventDetailsController.refreshAllData();
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("error.title"));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}