package ca.echiu.model;

import com.opencsv.bean.CsvBindByName;
import lombok.*;


@NoArgsConstructor
public class ReviewFileModel {

    @CsvBindByName(column = "TIMESTAMP")
    @Getter
    private String timestamp;

    @CsvBindByName(column = "COMMENTS")
    @Getter
    private String comments;

    public ReviewFileModel(String timestamp, String comments) {
        this.timestamp = timestamp;
        this.comments = comments;
    }

    @Override
    public String toString(){
        return this.getTimestamp()+" "+this.getComments();

    }
}
