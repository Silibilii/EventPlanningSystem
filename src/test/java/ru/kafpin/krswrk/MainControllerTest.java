package ru.kafpin.krswrk;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import ru.kafpin.krswrk.controller.MainController;
import ru.kafpin.krswrk.dao.*;
import ru.kafpin.krswrk.model.Event;
import ru.kafpin.krswrk.model.EventStatus;
import ru.kafpin.krswrk.util.LocaleManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

@ExtendWith(MockitoExtension.class)
public class MainControllerTest extends ApplicationTest {

    @Mock private EventDao eventDao;
    @Mock private ClientDao clientDao;
    @Mock private VenueDao venueDao;
    @Mock private ExpenseDao expenseDao;
    @Mock private TaskDao taskDao;
    @Mock private GuestDao guestDao;
    @Mock private ContractorDao contractorDao;

    private MainController controller;

    @Override
    public void start(Stage stage) throws Exception {
        LocaleManager.setLocale(new java.util.Locale("ru"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin/krswrk/mainforms/main.fxml"));
        loader.setResources(LocaleManager.getBundle());
        Parent root = loader.load();
        controller = loader.getController();

        controller.setEventDao(eventDao);
        controller.setClientDao(clientDao);
        controller.setVenueDao(venueDao);
        controller.setExpenseDao(expenseDao);
        controller.setTaskDao(taskDao);
        controller.setGuestDao(guestDao);
        controller.setContractorDao(contractorDao);

        controller.init();

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    @DisplayName("загрузка списка мероприятий")
    void testRefreshEventListWithMock() {
        Event mockEvent = new Event();
        mockEvent.setEventId(1L);
        mockEvent.setName("MockEvent");
        mockEvent.setStatus(EventStatus.PLANNING);
        when(eventDao.findAll()).thenReturn(List.of(mockEvent));

        controller.refreshEventList();

        assertThat(controller.getEventTable().getItems()).hasSize(1);
        assertThat(controller.getEventTable().getItems().get(0).getName()).isEqualTo("MockEvent");
    }

    @Test
    @DisplayName("комбобокс фильтра статусов содержит значения")
    void testEventStatusFilterComboHasItems() {
        ComboBox<EventStatus> filterCombo = lookup("#eventStatusFilterCombo").query();
        assertThat(filterCombo.getItems()).isNotEmpty();
        assertThat(filterCombo.getItems().size()).isGreaterThan(1);
    }

    @Test
    @DisplayName("создание клиента с пустым именем")
    void testAddClientEmptyName() throws TimeoutException {
        clickOn("#menuAdd");
        clickOn("#menuAddClient");
        waitFor(3, TimeUnit.SECONDS, () -> lookup("#nameField").tryQuery().isPresent());
        clickOn("Сохранить");
        waitFor(2, TimeUnit.SECONDS, () -> lookup(".dialog-pane").tryQuery().isPresent());
        DialogPane dialogPane = lookup(".dialog-pane").query();
        String content = dialogPane.getContentText();
        assertThat(content).contains("Введите название клиента");
        clickOn("OK");
        assertThat(lookup("#nameField").tryQuery().isPresent()).isTrue();
        clickOn("Отмена");
    }

    @Test
    @DisplayName("ввод букв в поле телефона при создании клиента")
    void testAddClientInvalidPhone() throws TimeoutException {
        clickOn("#menuAdd");
        clickOn("#menuAddClient");
        waitFor(3, TimeUnit.SECONDS, () -> lookup("#nameField").tryQuery().isPresent());
        clickOn("#nameField").write("Тестовый клиент");
        clickOn("#phoneField").write("абвгдеж");
        clickOn("Сохранить");
        waitFor(2, TimeUnit.SECONDS, () -> lookup(".dialog-pane").tryQuery().isPresent());
        DialogPane dialogPane = lookup(".dialog-pane").query();
        String content = dialogPane.getContentText();
        assertThat(content).contains("Некорректный номер телефона");
        clickOn("OK");
        assertThat(lookup("#nameField").tryQuery().isPresent()).isTrue();
        clickOn("Отмена");
    }
}