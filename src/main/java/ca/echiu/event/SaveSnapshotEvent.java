package ca.echiu.event;

import ca.echiu.model.ReviewFileModel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class SaveSnapshotEvent {

    @Getter
    private String reviewComment;
    @Getter
    private String timeStampString;

    public SaveSnapshotEvent(String reviewComment, String timeStampString) {
        this.reviewComment = StringUtils.left(reviewComment, 100);
        this.timeStampString = timeStampString.replace(":","");
    }
}
