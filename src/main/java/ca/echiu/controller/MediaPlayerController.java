package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
    private static MediaPlayer mediaPlayer;

    public MediaPlayerController() {
        this.mediaView = new MediaView();
    }

    @FXML
    public void initialize(){

    }

    @EventListener
    public void playVideo(PlayMediaEvent playMediaEvent) throws MalformedURLException {

        File videoFile = playMediaEvent.getFile();
        Media media = new Media(videoFile.toURI().toURL().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> mediaView.setMediaPlayer(mediaPlayer));
        mediaPlayer.setAutoPlay(true);
        mediaView.fitWidthProperty().bind(mediaPane.widthProperty());
    }


}
