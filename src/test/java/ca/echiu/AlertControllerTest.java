package ca.echiu;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(ApplicationExtension.class)
public class AlertControllerTest {

    private final String ALERT_DESCRIPTION = "alert description text";
    private Alert alert;

    @Start
    private void start(Stage stage){
        alert = new Alert(Alert.AlertType.ERROR, ALERT_DESCRIPTION);
        stage.setScene(new Scene(new Pane(), 100, 100));
        alert.show();
    }

    @Test
    void alertShouldBeErrorTypeAndContainTextDescription(){
        assertThat(alert.getAlertType()).isEqualTo(Alert.AlertType.ERROR);
        assertThat(alert.getContentText()).isEqualTo(ALERT_DESCRIPTION);
    }


}
