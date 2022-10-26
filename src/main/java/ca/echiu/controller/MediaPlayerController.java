package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.event.SaveSnapshotEvent;
import ca.echiu.service.FileSystemService;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    @Autowired
    private FileSystemService fileSystemService;

    public MediaPlayerController() {
        this.mediaView = new MediaView();
    }

    @FXML
    public void initialize() {


    }
//todo: there is a bug where first play a video, then choose another folder and play a video from there, the first video is still playing
    @EventListener
    public void playVideo(PlayMediaEvent playMediaEvent) {
        if(mediaPlayer != null){mediaPlayer.pause();}
        mediaView.setMediaPlayer(null);
        File videoFile = playMediaEvent.getFile();
        try{
            Media media = new Media(videoFile.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);
        } catch (MalformedURLException e) {
            new AlertController(Alert.AlertType.ERROR, e.getMessage()+" is a bad file path");
        }
        mediaButtonsHBox.getChildren().clear();
        setMediaControls();
        setPlayButtonFunctions();
        setMediaPlayerListener();
        mediaView.setFitHeight(mediaPane.getScene().getHeight()*0.9);

    }

    public static String getCurrentPlayTime(){
        return formatDurationToTimeString(mediaPlayer.getCurrentTime());
    }

    @EventListener
    public void saveMediaViewSnapshot(SaveSnapshotEvent saveSnapshotEvent) {
        WritableImage snapshot = mediaView.snapshot(new SnapshotParameters(), null);
        fileSystemService.writeImageFile(snapshot, FileSystemService.getReviewDirectoryPath(), saveSnapshotEvent.getReviewComment());
    }

    private String getCurrentPlayTimeForSnapshotFile(){
        String currentPlayTime = getCurrentPlayTime();
        String[] splitCurrentPlayTime = currentPlayTime.split(":");
        return splitCurrentPlayTime[0] + "_" + splitCurrentPlayTime[1];
    }

    public static void seekToPlayTime(Duration seekTime){
        mediaPlayer.seek(seekTime);
        mediaPlayer.pause();
    }

    private void setMediaControls() {
        mediaButtonsHBox.setAlignment(Pos.CENTER);
        mediaButtonsHBox.setPadding(new Insets(5, 10, 5, 10));

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
        setTimeSliderListener();
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
        setVolumeControlListener();
        mediaButtonsHBox.getChildren().add(volumeSlider);
    }

    private void setPlayButtonFunctions() {
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                MediaPlayer.Status status = mediaPlayer.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
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


    private void setMediaPlayerListener() {
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mediaPlayer.pause();
                    stopRequested = false;
                } else {
                    playButton.setText("||");
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                playButton.setText(">");
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            public void run() {
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.setAutoPlay(true);
                mediaView.fitWidthProperty().bind(mediaPane.widthProperty());
                duration = mediaPlayer.getMedia().getDuration();
                updateValues();
            }
        });

        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    playButton.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });
    }

    private void setTimeSliderListener() {
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });

    }

    private void setVolumeControlListener() {
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });

    }

    private void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    private static String formatDurationToTimeString(Duration elapsed) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (elapsedHours > 0) {
            return String.format("%02d:%02d:00", elapsedHours,
                    elapsedMinutes, elapsedSeconds);
        }
        if (elapsedHours < 0) {
            return String.format("%02d:%02d", elapsedMinutes,
                    elapsedSeconds);
        }
        return String.format("%02d:%02d", elapsedMinutes,
                elapsedSeconds);
    }


}
