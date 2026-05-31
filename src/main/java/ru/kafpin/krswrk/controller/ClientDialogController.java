package ru.kafpin.krswrk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ClientDao;
import ru.kafpin.krswrk.model.Client;
import ru.kafpin.krswrk.util.LocaleManager;
import ru.kafpin.krswrk.util.ValidationUtils;

/**
 * Контроллер формы добавления/редактирования клиента.
 */
public class ClientDialogController {

    private static final Logger logger = LoggerFactory.getLogger(ClientDialogController.class);

    @FXML private TextField nameField;
    @FXML private TextField contactPersonField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private CheckBox legalCheckBox;
    @FXML private TextField organizationField;
    @FXML private TextArea notesArea;

    @Setter private Client client;
    @Setter private ClientDao clientDao;
    @Setter private MainController mainController;
    @Setter private EventDialogController eventDialogController;

    @FXML
    public void initialize() {
    }

    /**
     * Заполняет поля формы при редактировании или очищает при создании.
     */
    public void init() {
        if (client != null) {
            nameField.setText(client.getName());
            contactPersonField.setText(client.getContactPerson());
            phoneField.setText(client.getPhone());
            emailField.setText(client.getEmail());
            legalCheckBox.setSelected(client.getIsLegal());
            organizationField.setText(client.getOrganizationName());
            notesArea.setText(client.getNotes());
        } else {
            nameField.clear();
            contactPersonField.clear();
            phoneField.clear();
            emailField.clear();
            legalCheckBox.setSelected(false);
            organizationField.clear();
            notesArea.clear();
        }
    }

    /**
     * Сохраняет клиента в БД.
     */
    @FXML
    private void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError(LocaleManager.getBundle().getString("error.client.nameEmpty"));
            return;
        }
        if (!ValidationUtils.isValidName(name)) {
            showError(LocaleManager.getBundle().getString("error.nameInvalid"));
            return;
        }

        String contactPerson = contactPersonField.getText().trim();
        if (!contactPerson.isEmpty() && !ValidationUtils.isValidName(contactPerson)) {
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

        if (client == null) client = new Client();
        client.setName(name);
        client.setContactPerson(contactPerson);
        client.setPhone(phone);
        client.setEmail(email);
        client.setIsLegal(legalCheckBox.isSelected());
        client.setOrganizationName(organizationField.getText().trim());
        client.setNotes(notesArea.getText());

        try {
            if (client.getClientId() == null || client.getClientId() == 0) {
                clientDao.save(client);
                logger.info("Сохранён новый клиент: {}", client.getName());
            } else {
                clientDao.update(client);
                logger.info("Обновлён клиент с id={}", client.getClientId());
            }
        } catch (Exception e) {
            logger.error("Ошибка сохранения клиента", e);
            showError(LocaleManager.getBundle().getString("error.save.client"));
            return;
        }

        if (eventDialogController != null) {
            eventDialogController.refreshCombos();
            if (client.getClientId() != null) {
                eventDialogController.setSelectedClient(client);
            }
        }
        closeForm();
    }

    @FXML
    private void onCancel() {
        closeForm();
    }

    private void closeForm() {
        mainController.hideRightPanel();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}