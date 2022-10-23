package ca.echiu.model;

import com.opencsv.bean.CsvBindByName;
import javafx.util.Duration;
import lombok.Getter;

public class ReviewFileModel {

    @CsvBindByName(column = "timestamp")
    @Getter
    private String timestamp;

    @CsvBindByName(column = "comments")
    @Getter
    private String comments;

    @Override
    public String toString(){
        return this.getTimestamp()+" "+this.getComments();

    }
}
