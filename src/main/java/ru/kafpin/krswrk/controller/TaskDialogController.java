package ru.kafpin.krswrk.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin.krswrk.dao.ContractorDao;
import ru.kafpin.krswrk.dao.TaskDao;
import ru.kafpin.krswrk.model.Contractor;
import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.model.Task;
import ru.kafpin.krswrk.model.TaskStatus;
import ru.kafpin.krswrk.util.LocaleManager;

import java.time.LocalDate;

/**
 * Контроллер диалога добавления и редактирования задачи.
 */
public class TaskDialogController {

    private static final Logger logger = LoggerFactory.getLogger(TaskDialogController.class);

    @FXML private TextArea descriptionArea;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<Contractor> contractorCombo;
    @FXML private ComboBox<TaskStatus> statusCombo;

    private Task task;
    @Setter private Event event;
    @Setter private TaskDao taskDao;
    @Setter private ContractorDao contractorDao;
    @Setter private MainController mainController;
    @Setter private EventDetailsController eventDetailsController;

    @FXML
    public void initialize() {
        contractorCombo.setCellFactory(lv -> new ListCell<Contractor>() {
            @Override protected void updateItem(Contractor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        contractorCombo.setButtonCell(contractorCombo.getCellFactory().call(null));

        statusCombo.setCellFactory(lv -> new ListCell<TaskStatus>() {
            @Override protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLocalized(LocaleManager.getBundle()));
            }
        });
        statusCombo.setButtonCell(statusCombo.getCellFactory().call(null));
    }

    public void init() {
        ObservableList<Contractor> contractors = FXCollections.observableArrayList(contractorDao.findAll());
        contractors.add(0, null);
        contractorCombo.setItems(contractors);
        statusCombo.setItems(FXCollections.observableArrayList(TaskStatus.values()));
    }

    /**
     * Устанавливает редактируемую задачу и заполняет поля.
     * @param task задача для редактирования (null для новой)
     */
    public void setTask(Task task) {
        this.task = task;
        if (task != null) {
            descriptionArea.setText(task.getDescription());
            deadlinePicker.setValue(task.getDeadline());
            contractorCombo.setValue(task.getResponsibleContractor());
            statusCombo.setValue(task.getStatus());
        } else {
            descriptionArea.clear();
            deadlinePicker.setValue(LocalDate.now().plusWeeks(1));
            contractorCombo.setValue(null);
            statusCombo.setValue(TaskStatus.PENDING);
        }
    }

    @FXML
    private void onSave() {
        System.out.println("Saving task with contractor: " + contractorCombo.getValue());


        if (descriptionArea.getText().trim().isEmpty()) {
            showError(LocaleManager.getBundle().getString("error.task.descriptionEmpty"));
            return;
        }
        if (deadlinePicker.getValue() == null) {
            showError(LocaleManager.getBundle().getString("error.task.deadlineEmpty"));
            return;
        }

        if (task == null) task = new Task();
        task.setDescription(descriptionArea.getText());
        task.setDeadline(deadlinePicker.getValue());


        Contractor selected = contractorCombo.getValue();
        System.out.println("Selected contractor: " + selected + " id=" + (selected != null ? selected.getContractorId() : "null"));


        task.setResponsibleContractor(contractorCombo.getValue());
        task.setStatus(statusCombo.getValue());
        task.setEvent(event);

        try {
            if (task.getTaskId() == null || task.getTaskId() == 0) {
                taskDao.save(task);
                logger.info("Сохранена новая задача: {}", task.getDescription());
            } else {
                taskDao.update(task);
                logger.info("Обновлена задача с id={}", task.getTaskId());
            }
            closeForm();
        } catch (Exception e) {
            logger.error("Ошибка сохранения задачи", e);
            showError(LocaleManager.getBundle().getString("error.save.task"));
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