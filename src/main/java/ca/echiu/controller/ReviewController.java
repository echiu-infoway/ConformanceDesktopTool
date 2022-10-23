package ca.echiu.controller;

import ca.echiu.model.ReviewFileModel;
import ca.echiu.service.FileSystemService;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;

@Component
@FxmlView("fxml/Review.fxml")
public class ReviewController {

    @FXML
    private TableView reviewTableView;


    @Autowired
    private FileSystemService fileSystemService;

    private List<ReviewFileModel> reviewFileModelList;

    @FXML
    public void initialize() throws FileNotFoundException {
        TableColumn<ReviewFileModel, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        TableColumn<ReviewFileModel, String> commentsColumn = new TableColumn<>("Comments");
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
        reviewTableView.getColumns().add(timestampColumn);
        reviewTableView.getColumns().add(commentsColumn);
        reviewFileModelList = fileSystemService.getReviewFile("C:\\Users\\email\\Downloads\\NewFolder\\music video.csv");
        reviewFileModelList.forEach(e -> reviewTableView.getItems().add(e));

    }
}
