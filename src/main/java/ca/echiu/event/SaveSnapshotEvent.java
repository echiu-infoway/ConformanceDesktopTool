package ca.echiu.event;

import ca.echiu.model.ReviewFileModel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class SaveSnapshotEvent {

    @Getter
    private String reviewComment;

    public SaveSnapshotEvent(String reviewComment) {
        this.reviewComment = StringUtils.left(reviewComment, 100);
    }
}
