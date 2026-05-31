package ru.kafpin.krswrk.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.*;
import ru.kafpin.krswrk.model.*;
import ru.kafpin.krswrk.util.LocaleManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Контроллер вкладок с деталями мероприятия.
 */
public class EventDetailsController {

    @FXML private TextField nameField;
    @FXML private Label dateLabel;
    @FXML private TextField budgetField;
    @FXML private TextField statusField;
    @FXML private TextField clientField;
    @FXML private TextField venueField;
    @FXML private TextArea feedbackArea;

    @FXML private TabPane detailsTabPane;
    @FXML private Label budgetLabel;
    @FXML private Label expensesSumLabel;
    @FXML private Label remainingLabel;
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, LocalDate> expenseDateCol;
    @FXML private TableColumn<Expense, BigDecimal> amountCol;
    @FXML private TableColumn<Expense, String> categoryCol;
    @FXML private TableColumn<Expense, String> contractorCol;
    @FXML private TableColumn<Expense, String> descCol;

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> taskDescCol;
    @FXML private TableColumn<Task, LocalDate> deadlineCol;
    @FXML private TableColumn<Task, String> taskStatusCol;
    @FXML private TableColumn<Task, String> responsibleCol;

    @FXML private TableView<Guest> guestTable;
    @FXML private TableColumn<Guest, String> guestNameCol;
    @FXML private TableColumn<Guest, String> guestPhoneCol;
    @FXML private TableColumn<Guest, String> guestEmailCol;
    @FXML private TableColumn<Guest, String> guestStatusCol;

    @FXML private TextArea reportArea;
    @FXML private TextField clientFeedbackField;
    @FXML private ComboBox<TaskStatus> statusFilterCombo;
    @FXML private TextField taskSearchField;

    @FXML private TableView<Contractor> contractorTable;
    @FXML private TableColumn<Contractor, String> contractorNameCol;
    @FXML private TableColumn<Contractor, String> contractorServiceCol;
    @FXML private Label detailName;
    @FXML private Label detailContactPerson;
    @FXML private Label detailPhone;
    @FXML private Label detailEmail;
    @FXML private Label detailServiceType;
    @FXML private TextArea detailPriceList;
    @FXML private TextArea detailNotes;

    @FXML private ComboBox<ServiceType> contractorTypeFilterCombo;


    @FXML private ComboBox<ExpenseCategory> expenseCategoryFilterCombo;
    @FXML private TextField expenseSearchField;

    @FXML private ComboBox<GuestStatus> guestStatusFilterCombo;


    @Setter
    private ExpenseDao expenseDao;
    @Setter
    private TaskDao taskDao;
    @Setter
    private GuestDao guestDao;
    @Setter
    private EventDao eventDao;

    @Setter
    private ContractorDao contractorDao;

    private Event currentEvent;
    @Setter
    private MainController mainController;
    private static final Logger logger = LoggerFactory.getLogger(EventDetailsController.class);

    /**
     * Настраивает фильтры и слушатели при загрузке FXML.
     */
    @FXML
    public void initialize() {
        // Фильтр задач
        statusFilterCombo.setItems(FXCollections.observableArrayList(TaskStatus.values()));
        statusFilterCombo.getItems().add(0, null);
        statusFilterCombo.setCellFactory(lv -> new ListCell<TaskStatus>() {
            @Override protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(LocaleManager.getBundle().getString("filter.all"));
                else setText(item.getLocalized(LocaleManager.getBundle()));
            }
        });
        statusFilterCombo.setButtonCell(statusFilterCombo.getCellFactory().call(null));
        statusFilterCombo.setValue(null);
        statusFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> loadTasks());
        taskSearchField.textProperty().addListener((obs, oldVal, newVal) -> loadTasks());

        // Фильтр гостей
        guestStatusFilterCombo.setItems(FXCollections.observableArrayList(GuestStatus.values()));
        guestStatusFilterCombo.getItems().add(0, null);
        guestStatusFilterCombo.setCellFactory(lv -> new ListCell<GuestStatus>() {
            @Override protected void updateItem(GuestStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(LocaleManager.getBundle().getString("filter.all"));
                else setText(item.getLocalized(LocaleManager.getBundle()));
            }
        });
        guestStatusFilterCombo.setButtonCell(guestStatusFilterCombo.getCellFactory().call(null));
        guestStatusFilterCombo.setValue(null);
        guestStatusFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> loadGuests());

        // Фильтр расходов
        expenseCategoryFilterCombo.setItems(FXCollections.observableArrayList(ExpenseCategory.values()));
        expenseCategoryFilterCombo.getItems().add(0, null);
        expenseCategoryFilterCombo.setCellFactory(lv -> new ListCell<ExpenseCategory>() {
            @Override protected void updateItem(ExpenseCategory item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? LocaleManager.getBundle().getString("filter.all") : item.getLocalized());
            }
        });
        expenseCategoryFilterCombo.setButtonCell(expenseCategoryFilterCombo.getCellFactory().call(null));
        expenseCategoryFilterCombo.setValue(null);
        expenseCategoryFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> loadExpenses());
        expenseSearchField.textProperty().addListener((obs, oldVal, newVal) -> loadExpenses());

        contractorTypeFilterCombo.setItems(FXCollections.observableArrayList(ServiceType.values()));
        contractorTypeFilterCombo.getItems().add(0, null);
        contractorTypeFilterCombo.setCellFactory(lv -> new ListCell<ServiceType>() {
            @Override protected void updateItem(ServiceType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(LocaleManager.getBundle().getString("filter.all"));
                } else {
                    setText(item.getLocalized());
                }
            }
        });
        contractorTypeFilterCombo.setButtonCell(contractorTypeFilterCombo.getCellFactory().call(null));
        contractorTypeFilterCombo.setValue(null);
        contractorTypeFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> loadContractors());



        // Настройка таблицы расходов
        expenseDateCol.setCellValueFactory(new PropertyValueFactory<>("expenseDate"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().getLocalized()));
        contractorCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContractor() != null ?
                        cellData.getValue().getContractor().getName() : ""));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) onEditExpense();
            });
            return row;
        });

        // Настройка таблицы задач
        taskDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        taskStatusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().getLocalized(LocaleManager.getBundle())));
        responsibleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getResponsibleContractor() != null ?
                        cellData.getValue().getResponsibleContractor().getName() : ""));
        taskTable.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) onEditTask();
            });
            return row;
        });

        // Настройка таблицы гостей
        guestNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        guestPhoneCol.setCellValueFactory(new PropertyValueFactory<>("contactPhone"));
        guestEmailCol.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        guestStatusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getInvitationStatus().getLocalized(LocaleManager.getBundle())));
        guestTable.setRowFactory(tv -> {
            TableRow<Guest> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) onEditGuest();
            });
            return row;
        });

        // Настройка таблицы подрядчиков
        contractorNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        contractorServiceCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServiceType().getLocalized()));
        contractorTable.setRowFactory(tv -> {
            TableRow<Contractor> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditContractorDialog(row.getItem());
                }
            });
            return row;
        });
        contractorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) showContractorDetails(newVal);
            else clearContractorDetails();
        });
    }


    /**
     * Загружает расходы по текущему мероприятию с учётом выбранной категории и поиска, так же вычисляет остаток по бюджету.
     */
    private void loadExpenses() {
        if (currentEvent == null) return;

        BigDecimal totalExpenses = expenseDao.getTotalExpensesByEventId(currentEvent.getEventId());
        expensesSumLabel.setText(totalExpenses.toString());
        budgetLabel.setText(currentEvent.getBudget().toString());
        remainingLabel.setText(eventDao.getRemainingBudget(currentEvent.getEventId()).toString());

        ExpenseCategory selectedCat = expenseCategoryFilterCombo.getValue();
        String category = selectedCat == null ? null : selectedCat.getDbValue();
        String searchText = expenseSearchField.getText();
        boolean hasCategory = category != null;
        boolean hasSearch = searchText != null && !searchText.trim().isEmpty();

        List<Expense> expenses;
        if (hasCategory && hasSearch) {
            expenses = expenseDao.findByEventIdAndCategoryAndSearch(currentEvent.getEventId(), category, searchText);
        } else if (hasCategory) {
            expenses = expenseDao.findByEventIdAndCategory(currentEvent.getEventId(), category);
        } else if (hasSearch) {
            expenses = expenseDao.findByEventIdAndSearch(currentEvent.getEventId(), searchText);
        } else {
            expenses = expenseDao.findByEventIdOnly(currentEvent.getEventId());
        }

        for (Expense e : expenses) {
            if (e.getContractor() != null && e.getContractor().getName() == null) {
                contractorDao.findById(e.getContractor().getContractorId().intValue()).ifPresent(e::setContractor);
            }
        }

        expenseTable.setItems(FXCollections.observableArrayList(expenses));
    }

    /**
     * Возвращает индекс выбранной вкладки (0-основное, 1-бюджет, 2-задачи, 3-подрядчики, 4-гости, 5-отчёт).
     */
    public int getSelectedTabIndex() {
        return detailsTabPane.getSelectionModel().getSelectedIndex();
    }

    /**
     * Устанавливает активную вкладку по её индексу.
     *
     * @param index индекс вкладки (должен быть в пределах от 0 до количества вкладок - 1)
     * @throws IndexOutOfBoundsException если индекс выходит за допустимый диапазон
     */
    public void setSelectedTabIndex(int index) {
        if (index >= 0 && index < detailsTabPane.getTabs().size()) {
            detailsTabPane.getSelectionModel().select(index);
        }
    }

    /**
     * Загружает задачи по текущему мероприятию с учётом статуса и поиска.
     */
    private void loadTasks() {
        if (currentEvent == null) return;

        TaskStatus selectedStatus = statusFilterCombo.getValue();
        String searchText = taskSearchField.getText();
        boolean hasStatus = selectedStatus != null;
        boolean hasSearch = searchText != null && !searchText.trim().isEmpty();

        List<Task> tasks;
        if (hasStatus && hasSearch) {
            tasks = taskDao.findByEventIdAndStatusAndSearch(currentEvent.getEventId(), selectedStatus, searchText);
        } else if (hasStatus) {
            tasks = taskDao.findByEventIdAndStatus(currentEvent.getEventId(), selectedStatus);
        } else if (hasSearch) {
            tasks = taskDao.findByEventIdAndSearch(currentEvent.getEventId(), searchText);
        } else {
            tasks = taskDao.findByEventIdOnly(currentEvent.getEventId());
        }

        for (Task t : tasks) {
            if (t.getResponsibleContractor() != null && t.getResponsibleContractor().getContractorId() != null && t.getResponsibleContractor().getName() == null) {
                contractorDao.findById(t.getResponsibleContractor().getContractorId().intValue())
                        .ifPresent(t::setResponsibleContractor);
            }
        }

        taskTable.setItems(FXCollections.observableArrayList(tasks));
    }

    /**
     * Загружает гостей текущего мероприятия с учётом выбранного статуса.
     */
    private void loadGuests() {
        if (currentEvent == null) return;
        GuestStatus selectedStatus = guestStatusFilterCombo.getValue();
        List<Guest> guests;
        if (selectedStatus == null) {
            guests = guestDao.findByEventId(currentEvent.getEventId());
        } else {
            guests = guestDao.findByEventIdAndStatus(currentEvent.getEventId(), selectedStatus);
        }
        guestTable.setItems(FXCollections.observableArrayList(guests));
    }

    /**
     * Загружает подрядчиков с фильтрацией по типу услуги.
     */
    private void loadContractors() {
        if (currentEvent == null) return;
        ServiceType selectedType = contractorTypeFilterCombo.getValue();
        List<Contractor> contractors;
        if (selectedType == null) {
            contractors = contractorDao.findAll();
        } else {
            contractors = contractorDao.findAllByServiceType(selectedType);
        }
        contractorTable.setItems(FXCollections.observableArrayList(contractors));
        if (!contractors.isEmpty()) {
            contractorTable.getSelectionModel().select(0);
        } else {
            clearContractorDetails();
        }
    }



    /**
     * Устанавливает текущее мероприятие и обновляет все вкладки.
     * @param event мероприятие для отображения
     */
    public void setEvent(Event event) {
        this.currentEvent = event;
        refreshAllData();
    }

    /**
     * Перезагружает все данные текущего мероприятия.
     */
    void refreshAllData() {
        if (currentEvent == null) return;

        if (expenseDao == null || taskDao == null || guestDao == null || eventDao == null ||
                contractorDao == null) {
            showError(LocaleManager.getBundle().getString("eventdetails.error.init.dao"));
            return;
        }

        // Основная информация
        nameField.setText(currentEvent.getName());
        dateLabel.setText(currentEvent.getEventDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        budgetField.setText(currentEvent.getBudget().toString());
        statusField.setText(currentEvent.getStatus().getLocalized(LocaleManager.getBundle()));
        clientField.setText(currentEvent.getClient().getName());
        Venue v = currentEvent.getVenue();
        venueField.setText(v.getName() + " (" + v.getAddress() + ")");
        feedbackArea.setText(currentEvent.getFeedback());

        // Расходы
        loadExpenses();

        // Задачи
        loadTasks();

        // Гости
        loadGuests();

        // Контракторы
        loadContractors();
    }

    @FXML
    private void onEditContractor() {
        Contractor selected = contractorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(LocaleManager.getBundle().getString("eventdetails.error.select.upd.contractor"));
            return;
        }
        openEditContractorDialog(selected);
    }

    private void openEditContractorDialog(Contractor contractor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/contractor_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            ContractorDialogController controller = loader.getController();
            controller.setContractorDao(contractorDao);
            controller.setMainController(mainController);
            controller.setEventDetailsController(this);
            controller.init();
            controller.setContractor(contractor);
            mainController.showFormInRightPanel(form);
        } catch (IOException e) {
            e.printStackTrace();
            showError(LocaleManager.getBundle().getString("error.load.form"));
        }
    }


    // ======================== Методы удаления ========================
    @FXML
    private void onDeleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(LocaleManager.getBundle().getString("eventdetails.error.select.expense"));
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(LocaleManager.getBundle().getString("eventdetails.confirm.title"));
        confirm.setHeaderText(MessageFormat.format(LocaleManager.getBundle().getString("eventdetails.confirm.delete.expense.header"), selected.getAmount()));
        confirm.setContentText(LocaleManager.getBundle().getString("eventdetails.confirm.delete.expense.content"));
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            expenseDao.delete(selected.getExpenseId().intValue());
            logger.info("Удалён расход id={} для мероприятия {}", selected.getExpenseId(), currentEvent.getName());
            refreshAllData();
        }
    }

    @FXML
    private void onDeleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(LocaleManager.getBundle().getString("eventdetails.error.select.task"));
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(LocaleManager.getBundle().getString("eventdetails.confirm.title"));
        confirm.setHeaderText(MessageFormat.format(LocaleManager.getBundle().getString("eventdetails.confirm.delete.task.header"), selected.getDescription()));
        confirm.setContentText(LocaleManager.getBundle().getString("eventdetails.confirm.delete.task.content"));
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            taskDao.delete(selected.getTaskId().intValue());
            refreshAllData();
        }
    }

    @FXML
    private void onDeleteGuest() {
        Guest selected = guestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(LocaleManager.getBundle().getString("eventdetails.error.select.guest"));
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(LocaleManager.getBundle().getString("eventdetails.confirm.title"));
        confirm.setHeaderText(MessageFormat.format(LocaleManager.getBundle().getString("eventdetails.confirm.delete.guest.header"), selected.getFullName()));
        confirm.setContentText(LocaleManager.getBundle().getString("eventdetails.confirm.delete.guest.content"));
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guestDao.delete(selected.getGuestId().intValue());
            refreshAllData();
        }
    }

    @FXML
    private void onDeleteContractor() {
        Contractor selected = contractorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(LocaleManager.getBundle().getString("eventdetails.error.select.contractor"));
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(LocaleManager.getBundle().getString("eventdetails.confirm.title"));
        confirm.setHeaderText(MessageFormat.format(LocaleManager.getBundle().getString("eventdetails.confirm.delete.contractor.header"), selected.getName()));
        confirm.setContentText(LocaleManager.getBundle().getString("eventdetails.confirm.delete.contractor.content"));
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                contractorDao.delete(selected.getContractorId().intValue());
                refreshAllData();
            } catch (Exception e) {
                showError(LocaleManager.getBundle().getString("eventdetails.error.delete.contractor"));
            }
        }
    }

    // ======================== Редактирование события ========================
    @FXML
    private void onEditEvent() {
        if (mainController != null) {
            mainController.openEventFormInRightPanel(currentEvent);
        } else {
            showError(LocaleManager.getBundle().getString("eventdetails.error.edit.event"));
        }
    }

    // ======================== Добавление и редактирование ========================
    @FXML private void onAddExpense() { openExpenseDialog(null); }
    @FXML private void onAddTask() { openTaskDialog(null); }
    @FXML private void onAddGuest() { openGuestDialog(null); }

    @FXML private void onEditGuest() {
        Guest selected = guestTable.getSelectionModel().getSelectedItem();
        if (selected == null) showError(LocaleManager.getBundle().getString("eventdetails.error.select.upd.guest"));
        else openGuestDialog(selected);
    }

    @FXML private void onEditTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) showError(LocaleManager.getBundle().getString("eventdetails.error.select.upd.task"));
        else openTaskDialog(selected);
    }

    @FXML private void onEditExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) showError(LocaleManager.getBundle().getString("eventdetails.error.select.upd.expense"));
        else openExpenseDialog(selected);
    }

    // ======================== Отчёт и обратная связь ========================

    private String getLocalizedCategory(String dbCategory) {
        for (ExpenseCategory cat : ExpenseCategory.values()) {
            if (cat.getDbValue().equals(dbCategory)) {
                return cat.getLocalized();
            }
        }
        return dbCategory;
    }

    @FXML
    private void onGenerateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(LocaleManager.getBundle().getString("eventdetails.report.header"))
                .append(currentEvent.getName()).append("\n");
        sb.append(LocaleManager.getBundle().getString("eventdetails.report.budget"))
                .append(currentEvent.getBudget()).append("\n");
        sb.append(LocaleManager.getBundle().getString("eventdetails.report.expenses"))
                .append(expensesSumLabel.getText()).append("\n");
        sb.append(LocaleManager.getBundle().getString("eventdetails.report.remaining"))
                .append(remainingLabel.getText()).append("\n\n");

        sb.append("--- ").append(LocaleManager.getBundle().getString("report.byCategory"))
                .append(" ---\n");
        Map<String, BigDecimal> summary = expenseDao.getExpenseSummaryByEventId(currentEvent.getEventId());
        if (summary.isEmpty()) {
            sb.append(LocaleManager.getBundle().getString("report.noExpenses")).append("\n");
        } else {
            for (Map.Entry<String, BigDecimal> entry : summary.entrySet()) {
                sb.append(getLocalizedCategory(entry.getKey()))
                        .append(": ").append(entry.getValue()).append("\n");
            }
        }
        reportArea.setText(sb.toString());
    }

    @FXML
    private void onSaveFeedback() {
        String newFeedback = clientFeedbackField.getText();
        if (newFeedback != null && !newFeedback.trim().isEmpty()) {
            currentEvent.setFeedback(newFeedback);
            eventDao.update(currentEvent);
            feedbackArea.setText(newFeedback);
            clientFeedbackField.clear();
            logger.info("Сохранена обратная связь для мероприятия {}", currentEvent.getName());
            showInfo(LocaleManager.getBundle().getString("eventdetails.info.feedback.saved"));
        }
    }

    // ======================== Детали подрядчика ========================
    /**
     * Обновляет детали выбранного подрядчика в панели.
     */
    private void showContractorDetails(Contractor c) {
        detailName.setText(LocaleManager.getBundle().getString("eventdetails.contractor.name") + " " + c.getName());
        detailContactPerson.setText(LocaleManager.getBundle().getString("eventdetails.contractor.contactPerson") + " " + (c.getContactPerson() != null ? c.getContactPerson() : ""));
        detailPhone.setText(LocaleManager.getBundle().getString("eventdetails.contractor.phone") + " " + (c.getPhone() != null ? c.getPhone() : ""));
        detailEmail.setText(LocaleManager.getBundle().getString("eventdetails.contractor.email") + " " + (c.getEmail() != null ? c.getEmail() : ""));
        detailServiceType.setText(LocaleManager.getBundle().getString("eventdetails.contractor.serviceType") + " " +
                (c.getServiceType() != null ? c.getServiceType().getLocalized() : ""));
        detailPriceList.setText(c.getPriceList() != null ? c.getPriceList() : "");
        detailNotes.setText(c.getNotes() != null ? c.getNotes() : "");
    }

    /**
     * Очищает поля деталей подрядчика.
     */
    private void clearContractorDetails() {
        detailName.setText("");
        detailContactPerson.setText("");
        detailPhone.setText("");
        detailEmail.setText("");
        detailServiceType.setText("");
        detailPriceList.clear();
        detailNotes.clear();
    }


    // ======================== Открытие диалогов (правой панели) ========================

    @FXML
    private void onAddContractor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/contractor_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            ContractorDialogController controller = loader.getController();
            controller.setContractorDao(contractorDao);
            controller.setMainController(mainController);
            controller.setEventDetailsController(this);
            controller.init();
            mainController.showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки формы", e);
            showError(LocaleManager.getBundle().getString("error.load.form"));
        }
    }

    private void openExpenseDialog(Expense expense) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/expense_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            ExpenseDialogController controller = loader.getController();
            controller.setExpenseDao(expenseDao);
            controller.setContractorDao(contractorDao);
            controller.setEvent(currentEvent);
            controller.setExpense(expense);
            controller.setMainController(mainController);
            controller.setEventDetailsController(this);
            controller.init();
            mainController.showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки формы", e);
            showError(LocaleManager.getBundle().getString("eventdetails.error.load.form"));
        }
    }

    private void openTaskDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/task_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            TaskDialogController controller = loader.getController();
            controller.setTaskDao(taskDao);
            controller.setContractorDao(contractorDao);
            controller.setEvent(currentEvent);
            controller.init();
            controller.setTask(task);
            controller.setMainController(mainController);
            controller.setEventDetailsController(this);
            mainController.showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки формы", e);
            showError(LocaleManager.getBundle().getString("eventdetails.error.load.form"));
        }
    }

    private void openGuestDialog(Guest guest) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/guest_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            GuestDialogController controller = loader.getController();
            controller.setGuestDao(guestDao);
            controller.setEvent(currentEvent);
            controller.init();
            controller.setGuest(guest);
            controller.setMainController(mainController);
            controller.setEventDetailsController(this);
            mainController.showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки формы", e);
            showError(LocaleManager.getBundle().getString("eventdetails.error.load.form"));
        }
    }

    private void openExpenseDialogWithContractor(Contractor contractor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/expense_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            ExpenseDialogController controller = loader.getController();
            controller.setExpenseDao(expenseDao);
            controller.setContractorDao(contractorDao);
            controller.setEvent(currentEvent);
            controller.init();
            Expense expense = new Expense();
            expense.setContractor(contractor);
            controller.setExpense(expense);
            controller.setMainController(mainController);
            controller.setEventDetailsController(this);
            mainController.showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки формы", e);
            showError(LocaleManager.getBundle().getString("eventdetails.error.load.form"));
        }
    }

    private void openTaskDialogWithContractor(Contractor contractor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/dialogs/task_dialog.fxml"));
            loader.setResources(LocaleManager.getBundle());
            Parent form = loader.load();
            TaskDialogController controller = loader.getController();
            controller.setTaskDao(taskDao);
            controller.setContractorDao(contractorDao);
            controller.setEvent(currentEvent);
            controller.init();
            Task task = new Task();
            task.setResponsibleContractor(contractor);
            controller.setTask(task);
            controller.setMainController(mainController);
            controller.setEventDetailsController(this);
            mainController.showFormInRightPanel(form);
        } catch (IOException e) {
            logger.error("Ошибка загрузки формы", e);
            showError(LocaleManager.getBundle().getString("eventdetails.error.load.form"));
        }
    }


    @FXML
    private void onDeleteEvent() {
        if (currentEvent == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(LocaleManager.getBundle().getString("eventdetails.confirm.title"));
        confirm.setHeaderText(MessageFormat.format(LocaleManager.getBundle().getString("eventdetails.confirm.delete.event.header"), currentEvent.getName()));
        confirm.setContentText(LocaleManager.getBundle().getString("eventdetails.confirm.delete.event.content"));
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eventDao.delete(currentEvent.getEventId().intValue());
            mainController.refreshEventList();
            mainController.hideRightPanel();
            mainController.clearEventDetails();
            mainController.selectFirstEventIfAny();
        }
    }

    // ======================== Кнопки в табе подрядчиков ========================
    @FXML
    private void onCreateExpenseForContractor() {
        Contractor selected = contractorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(LocaleManager.getBundle().getString("eventdetails.error.select.upd.contractor"));
            return;
        }
        openExpenseDialogWithContractor(selected);
    }

    @FXML
    private void onCreateTaskForContractor() {
        Contractor selected = contractorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(LocaleManager.getBundle().getString("eventdetails.error.select.upd.contractor"));
            return;
        }
        openTaskDialogWithContractor(selected);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LocaleManager.getBundle().getString("eventdetails.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleManager.getBundle().getString("eventdetails.info.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    @FXML
    private void onExportReportToFile() {
        String content = reportArea.getText();
        if (content == null || content.trim().isEmpty()) {
            showError(LocaleManager.getBundle().getString("report.empty"));
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LocaleManager.getBundle().getString("report.save.title"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        String safeName = currentEvent.getName().replaceAll("[^a-zA-Zа-яА-Я0-9]", "_");
        fileChooser.setInitialFileName("report_" + safeName + ".txt");
        File file = fileChooser.showSaveDialog(reportArea.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.print(content);
                showInfo(LocaleManager.getBundle().getString("report.saved") + " " + file.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Ошибка сохранения отчёта", e);
                showError(LocaleManager.getBundle().getString("report.save.error"));
            }
        }
    }

}