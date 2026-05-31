package ru.kafpin.krswrk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.VenueDao;
import ru.kafpin.krswrk.model.Venue;
import ru.kafpin.krswrk.util.LocaleManager;
import ru.kafpin.krswrk.util.ValidationUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Контроллер диалога добавления и редактирования площадки.
 */
public class VenueDialogController {

    private static final Logger logger = LoggerFactory.getLogger(VenueDialogController.class);

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField capacityField;
    @FXML private TextField rentalCostField;
    @FXML private TextField contactPhoneField;

    @Setter private Venue venue;
    @Setter private VenueDao venueDao;
    @Setter private MainController mainController;
    @Setter private EventDialogController eventDialogController;

    @FXML
    public void initialize() {
    }

    public void init() {
        if (venue != null) {
            nameField.setText(venue.getName());
            addressField.setText(venue.getAddress());
            capacityField.setText(String.valueOf(venue.getCapacity()));
            rentalCostField.setText(venue.getRentalCost().toString());
            contactPhoneField.setText(venue.getContactPhone());
        } else {
            nameField.clear();
            addressField.clear();
            capacityField.clear();
            rentalCostField.clear();
            contactPhoneField.clear();
        }
    }

    @FXML
    private void onSave() {
        if (nameField.getText().trim().isEmpty()) {
            showError(LocaleManager.getBundle().getString("error.venue.nameEmpty"));
            return;
        }

        if (!ValidationUtils.isPositiveInt(capacityField.getText())) {
            showError(LocaleManager.getBundle().getString("error.venue.capacityInvalid"));
            return;
        }
        int capacity = Integer.parseInt(capacityField.getText().trim());
        if (capacity > ValidationUtils.MAX_CAPACITY) {
            String maxStr = String.valueOf(ValidationUtils.MAX_CAPACITY);
            String msg = LocaleManager.getBundle().getString("error.venue.capacityTooHigh");
            showError(MessageFormat.format(msg, maxStr));
            return;
        }


        if (!ValidationUtils.isRentalCostValid(rentalCostField.getText())) {
            String maxStr = ValidationUtils.MAX_RENTAL_COST.toString();
            String msg = LocaleManager.getBundle().getString("error.venue.rentalCostTooHigh");
            showError(MessageFormat.format(msg, maxStr));
            return;
        }
        BigDecimal rentalCost = new BigDecimal(rentalCostField.getText().trim());

        String phone = contactPhoneField.getText().trim();
        if (!ValidationUtils.isValidPhone(phone)) {
            showError(LocaleManager.getBundle().getString("error.phoneInvalid"));
            return;
        }

        if (venue == null) venue = new Venue();
        venue.setName(nameField.getText());
        venue.setAddress(addressField.getText());
        venue.setCapacity(capacity);
        venue.setRentalCost(rentalCost);
        venue.setContactPhone(phone);

        try {
            if (venue.getVenueId() == null || venue.getVenueId() == 0) {
                venueDao.save(venue);
                logger.info("Сохранена новая площадка: {}", venue.getName());
            } else {
                venueDao.update(venue);
                logger.info("Обновлена площадка с id={}", venue.getVenueId());
            }
        } catch (Exception e) {
            logger.error("Ошибка сохранения площадки", e);
            showError(LocaleManager.getBundle().getString("error.save.venue"));
            return;
        }

        if (eventDialogController != null) {
            eventDialogController.refreshCombos();
            if (venue.getVenueId() != null) {
                eventDialogController.setSelectedVenue(venue);
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