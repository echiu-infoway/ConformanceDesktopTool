package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.service.WeatherService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/fxml/MainPage.fxml")
public class MainWindowController {

    @FXML
    private Label weatherLabel;
    @FXML
    private StackPane topLeftStackPane;
    @FXML
    private VBox navigatorVbox;
    @FXML
    private VBox reviewVbox;
    @FXML
    private VBox fileOrganizerVbox;
    @FXML
    private ToggleButton reviewToggleButton;
    @FXML
    private Label videoTitle;
    @Autowired
    private WeatherService weatherService;


    public void loadWeatherForecast(ActionEvent actionEvent) {
        weatherLabel.setText(weatherService.getWeatherForecast());
    }

    @FXML
    public void initialize(){
        reviewVbox.setVisible(false);
    }

    public void toggleFileOrganizerVbox() {
        if (reviewToggleButton.isSelected()) {
            navigatorVbox.setVisible(false);
            fileOrganizerVbox.setVisible(false);
            reviewVbox.setVisible(true);
        }
        if (!reviewToggleButton.isSelected()) {
            reviewVbox.setVisible(false);
            navigatorVbox.setVisible(true);
            fileOrganizerVbox.setVisible(true);

        }

    }
    @EventListener
    public void updateVideoTitle(PlayMediaEvent event){
        videoTitle.setText(FilenameUtils.removeExtension(event.getFile().getName()));
    }
}
