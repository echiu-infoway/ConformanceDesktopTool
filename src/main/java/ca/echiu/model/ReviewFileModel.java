package ca.echiu.model;

import com.opencsv.bean.CsvBindByName;
import javafx.util.Duration;

public class ReviewFileModel {

    @CsvBindByName(column = "timestamp")
    private Duration timestamp;

    @CsvBindByName(column = "comments")
    private String comments;
}
