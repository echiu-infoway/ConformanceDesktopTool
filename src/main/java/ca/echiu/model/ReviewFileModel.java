package ca.echiu.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class ReviewFileModel {

    @CsvBindByName(column = "timestamp")
    @Getter
    @NonNull
    private String timestamp;

    @CsvBindByName(column = "comments")
    @Getter
    @NonNull
    private String comments;

    @Override
    public String toString(){
        return this.getTimestamp()+" "+this.getComments();

    }
}
