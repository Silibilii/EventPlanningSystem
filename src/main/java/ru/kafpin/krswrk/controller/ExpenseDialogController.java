package ru.kafpin.krswrk.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ContractorDao;
import ru.kafpin.krswrk.dao.ExpenseDao;
import ru.kafpin.krswrk.model.*;
import ru.kafpin.krswrk.util.LocaleManager;
import ru.kafpin.krswrk.util.ValidationUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;

/**
 * Контроллер диалога добавления и редактирования расхода.
 */
public class ExpenseDialogController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseDialogController.class);

    @FXML private TextField amountField;
    @FXML private DatePicker expenseDatePicker;
    @FXML private ComboBox<ExpenseCategory> categoryCombo;
    @FXML private ComboBox<Contractor> contractorCombo;
    @FXML private TextArea descriptionArea;

    private Expense expense;
    @Setter private Event event;
    @Setter private ExpenseDao expenseDao;
    @Setter private ContractorDao contractorDao;
    @Setter private MainController mainController;
    @Setter private EventDetailsController eventDetailsController;

    @FXML
    public void initialize() {
        // Категории
        categoryCombo.setItems(FXCollections.observableArrayList(ExpenseCategory.values()));
        categoryCombo.setCellFactory(lv -> new ListCell<ExpenseCategory>() {
            @Override protected void updateItem(ExpenseCategory item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLocalized());
            }
        });
        categoryCombo.setButtonCell(categoryCombo.getCellFactory().call(null));

        // настройка отображения
        contractorCombo.setCellFactory(lv -> new ListCell<Contractor>() {
            @Override protected void updateItem(Contractor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(LocaleManager.getBundle().getString("expense.noContractor"));
                } else {
                    setText(item.getName());
                }
            }
        });
        contractorCombo.setButtonCell(contractorCombo.getCellFactory().call(null));

        // При смене категории перезагружаем подрядчиков
        categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadContractorsForCategory(newVal);
        });

        loadContractorsForCategory(null);

        expenseDatePicker.setValue(LocalDate.now());
    }


    public void init() {
    }

    private void loadContractorsForCategory(ExpenseCategory category) {
        ObservableList<Contractor> contractors = FXCollections.observableArrayList();
        contractors.add(null); // пункт "Нет подрядчика"
        if (category != null) {
            ServiceType serviceType = mapCategoryToServiceType(category);
            if (serviceType != null) {
                contractors.addAll(contractorDao.findAllByServiceType(serviceType));
            }
        }
        contractorCombo.setItems(contractors);
        contractorCombo.setValue(null);
    }

    private ServiceType mapCategoryToServiceType(ExpenseCategory category) {
        if (category == null) return null;
        switch (category) {
            case CATERING: return ServiceType.CATERING;
            case PHOTOGRAPHER: return ServiceType.PHOTOGRAPHER;
            case HOST: return ServiceType.HOST;
            case DECOR: return ServiceType.DECORATOR;
            case TRANSPORT: return ServiceType.OTHER;
            case OTHER: return ServiceType.OTHER;
            default: return null;
        }
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
        if (expense != null) {
            amountField.setText(expense.getAmount() != null ? expense.getAmount().toString() : "");
            expenseDatePicker.setValue(expense.getExpenseDate() != null ? expense.getExpenseDate() : LocalDate.now());
            categoryCombo.setValue(expense.getCategory());
            contractorCombo.setValue(expense.getContractor());
            descriptionArea.setText(expense.getDescription() != null ? expense.getDescription() : "");
        } else {
            amountField.setText("");
            expenseDatePicker.setValue(LocalDate.now());
            categoryCombo.setValue(null);
            contractorCombo.setValue(null);
            descriptionArea.clear();
        }
    }

    @FXML
    private void onSave() {
        String amountText = amountField.getText().trim();
        if (!ValidationUtils.isPositiveBigDecimal(amountText)) {
            showError(LocaleManager.getBundle().getString("error.expense.amountInvalid"));
            return;
        }
        BigDecimal amount = new BigDecimal(amountText);
        if (amount.compareTo(ValidationUtils.MAX_EXPENSE_AMOUNT) > 0) {
            String maxStr = ValidationUtils.MAX_EXPENSE_AMOUNT.toString();
            String msg = LocaleManager.getBundle().getString("error.expense.amountTooHigh");
            showError(MessageFormat.format(msg, maxStr));
            return;
        }
        if (categoryCombo.getValue() == null) {
            showError(LocaleManager.getBundle().getString("error.expense.categoryEmpty"));
            return;
        }

        if (expense == null) expense = new Expense();
        expense.setAmount(amount);
        expense.setExpenseDate(expenseDatePicker.getValue());
        expense.setDescription(descriptionArea.getText());
        expense.setEvent(event);
        expense.setCategory(categoryCombo.getValue());
        expense.setContractor(contractorCombo.getValue());

        try {
            if (expense.getExpenseId() == null || expense.getExpenseId() == 0) {
                expenseDao.save(expense);
                logger.info("Сохранён новый расход на сумму {}", expense.getAmount());
            } else {
                expenseDao.update(expense);
                logger.info("Обновлён расход с id={}", expense.getExpenseId());
            }
            closeForm();
        } catch (Exception e) {
            logger.error("Ошибка сохранения расхода", e);
            showError(LocaleManager.getBundle().getString("error.save.expense"));
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}