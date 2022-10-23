package ca.echiu.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("fxml/Review.fxml")
public class ReviewController {

    @FXML
    private TableView reviewTableView;

    @FXML
    public void initialize(){

    }
}
