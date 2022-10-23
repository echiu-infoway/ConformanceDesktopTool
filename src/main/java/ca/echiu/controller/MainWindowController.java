package ca.echiu.controller;

import ca.echiu.service.FileSystemService;
import ca.echiu.service.WeatherService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@FxmlView("/fxml/MainPage.fxml")
public class MainWindowController {
    @FXML
    private Label weatherLabel;
    @FXML
    private StackPane bottomLeftStackPane;
    @FXML
    private VBox fileOrganizerVbox;
    @FXML
    private VBox reviewVbox;
    @FXML
    private ToggleButton reviewToggleButton;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private FileSystemService fileSystemService;
    private FXMLLoader fxmlLoader = new FXMLLoader();


    public void loadWeatherForecast(ActionEvent actionEvent) {
        weatherLabel.setText(weatherService.getWeatherForecast());
    }

    @FXML
    public void initialize(){
        reviewVbox.setVisible(false);
    }

    public void toggleFileOrganizerVbox() {
        if (reviewToggleButton.isSelected()) {
            fileOrganizerVbox.setVisible(false);
            reviewVbox.setVisible(true);
        }
        if (!reviewToggleButton.isSelected()) {
            reviewVbox.setVisible(false);
            fileOrganizerVbox.setVisible(true);

        }

    }
}
