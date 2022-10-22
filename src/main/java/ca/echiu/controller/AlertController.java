package ca.echiu.controller;

import javafx.scene.control.Alert;

public class AlertController {

    private Alert alert;

    public AlertController(Alert.AlertType  alertType, String alertDescription) {
        this.alert = new Alert(alertType, alertDescription);
        alert.showAndWait();


    }
}
