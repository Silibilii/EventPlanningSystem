package ru.kafpin.krswrk.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ContractorDao;
import ru.kafpin.krswrk.model.Contractor;
import ru.kafpin.krswrk.model.ServiceType;
import ru.kafpin.krswrk.util.LocaleManager;
import ru.kafpin.krswrk.util.ValidationUtils;

/**
 * Контроллер формы добавления/редактирования подрядчика.
 */
public class ContractorDialogController {

    private static final Logger logger = LoggerFactory.getLogger(ContractorDialogController.class);

    @FXML private TextField nameField;
    @FXML private TextField contactPersonField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<ServiceType> serviceTypeCombo;
    @FXML private TextArea priceListArea;
    @FXML private TextArea notesArea;

    private Contractor contractor;
    @Setter private ContractorDao contractorDao;
    @Setter private MainController mainController;
    @Setter private EventDetailsController eventDetailsController;

    /**
     * Передаёт данные редактируемого подрядчика в поля формы.
     * @param contractor подрядчик для редактирования, null для нового.
     */
    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
        if (contractor != null) {
            nameField.setText(contractor.getName());
            contactPersonField.setText(contractor.getContactPerson());
            phoneField.setText(contractor.getPhone());
            emailField.setText(contractor.getEmail());
            serviceTypeCombo.setValue(contractor.getServiceType());
            priceListArea.setText(contractor.getPriceList());
            notesArea.setText(contractor.getNotes());
        }
    }

    public void init() {
        serviceTypeCombo.setItems(FXCollections.observableArrayList(ServiceType.values()));
        serviceTypeCombo.setCellFactory(lv -> new ListCell<ServiceType>() {
            @Override protected void updateItem(ServiceType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLocalized());
            }
        });
        serviceTypeCombo.setButtonCell(serviceTypeCombo.getCellFactory().call(null));
        serviceTypeCombo.setValue(null);
    }

    @FXML
    private void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError(LocaleManager.getBundle().getString("error.contractor.nameEmpty"));
            return;
        }
        if (!ValidationUtils.isValidName(name)) {
            showError(LocaleManager.getBundle().getString("error.nameInvalid"));
            return;
        }

        String contact = contactPersonField.getText().trim();
        if (!contact.isEmpty() && !ValidationUtils.isValidName(contact)) {
            showError(LocaleManager.getBundle().getString("error.nameInvalid"));
            return;
        }

        ServiceType selectedType = serviceTypeCombo.getValue();
        if (selectedType == null) {
            showError(LocaleManager.getBundle().getString("error.contractor.serviceTypeEmpty"));
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

        if (contractor == null) contractor = new Contractor();
        contractor.setName(name);
        contractor.setContactPerson(contact);
        contractor.setPhone(phone);
        contractor.setEmail(email);
        contractor.setServiceType(selectedType);
        contractor.setPriceList(priceListArea.getText());
        contractor.setNotes(notesArea.getText());

        try {
            if (contractor.getContractorId() == null || contractor.getContractorId() == 0) {
                contractorDao.save(contractor);
                logger.info("Сохранён новый подрядчик: {}", contractor.getName());
            } else {
                contractorDao.update(contractor);
                logger.info("Обновлён подрядчик с id={}", contractor.getContractorId());
            }
        } catch (Exception e) {
            logger.error("Ошибка сохранения подрядчика", e);
            showError(LocaleManager.getBundle().getString("error.save.contractor"));
            return;
        }

        if (eventDetailsController != null) {
            eventDetailsController.refreshAllData();
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

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("error.title"));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}