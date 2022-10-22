package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;

@Component
@FxmlView("fxml/MediaPlayer.fxml")
public class MediaPlayerController {
    @FXML
    private MediaView mediaView;
    @FXML
    private Pane mediaPane;
    @FXML
    private HBox mediaButtonsHBox;
    private static MediaPlayer mediaPlayer;
    final Button playButton = new Button(">");
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;

    public MediaPlayerController() {
        this.mediaView = new MediaView();
    }

    @FXML
    public void initialize(){
        mediaButtonsHBox.setAlignment(Pos.CENTER);
        mediaButtonsHBox.setPadding(new Insets(5, 10, 5, 10));
        setPlayButtonFunctions();
        mediaButtonsHBox.getChildren().add(playButton);
        // Add spacer
        Label spacer = new Label("   ");
        mediaButtonsHBox.getChildren().add(spacer);

// Add Time label
        Label timeLabel = new Label("Time: ");
        mediaButtonsHBox.getChildren().add(timeLabel);

// Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        mediaButtonsHBox.getChildren().add(timeSlider);

// Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaButtonsHBox.getChildren().add(playTime);

// Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaButtonsHBox.getChildren().add(volumeLabel);

// Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);

        mediaButtonsHBox.getChildren().add(volumeSlider);
    }

    @EventListener
    public void playVideo(PlayMediaEvent playMediaEvent) throws MalformedURLException {
        mediaView.setMediaPlayer(null);
        File videoFile = playMediaEvent.getFile();
        Media media = new Media(videoFile.toURI().toURL().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> mediaView.setMediaPlayer(mediaPlayer));
        mediaPlayer.setAutoPlay(true);
        mediaView.fitWidthProperty().bind(mediaPane.widthProperty());
    }

    public void setPlayButtonFunctions(){
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                MediaPlayer.Status status = mediaPlayer.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN  || status == MediaPlayer.Status.HALTED)
                {
                    // don't do anything in these states
                    return;
                }

                if ( status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED)
                {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }
                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                }
            }
        });
    }


}
