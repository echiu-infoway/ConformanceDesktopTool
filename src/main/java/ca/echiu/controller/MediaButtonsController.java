package ca.echiu.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/fxml/MediaControls.fxml")
public class MediaButtonsController {
    @FXML
    private HBox mediaButtonsHBox;
    final Button playButton = new Button(">");

    @FXML
    public void initialize(){
        mediaButtonsHBox.setAlignment(Pos.CENTER);
        mediaButtonsHBox.setPadding(new Insets(5, 10, 5, 10));
        mediaButtonsHBox.getChildren().add(playButton);
    }
}
