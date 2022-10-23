package ca.echiu.controller;

import ca.echiu.service.FileSystemService;
import ca.echiu.service.WeatherService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/fxml/MainPage.fxml")
public class MainWindowController {
    @FXML
    private Label weatherLabel;
    @FXML
    private VBox fileOrganizerVBox;
    @FXML
    private ToggleButton reviewToggleButton;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private FileSystemService fileSystemService;


    public void loadWeatherForecast(ActionEvent actionEvent) {
        weatherLabel.setText(weatherService.getWeatherForecast());
    }

    public void toggleFileOrganizerVbox(){
        if(reviewToggleButton.isSelected())
        {
            fileOrganizerVBox.setVisible(false);
        }
        if(!reviewToggleButton.isSelected()){
            fileOrganizerVBox.setVisible(true);
        }

    }
}
