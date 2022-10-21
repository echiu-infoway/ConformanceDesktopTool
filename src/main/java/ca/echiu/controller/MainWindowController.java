package ca.echiu.controller;

import ca.echiu.service.WeatherService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/fxml/MainPage.fxml")
public class MainWindowController {
    @FXML
    private Label weatherLabel;
    private WeatherService weatherService;
    @Autowired
    public MainWindowController(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    public void loadWeatherForecast(ActionEvent actionEvent){
        weatherLabel.setText(weatherService.getWeatherForecast());
    }
}
