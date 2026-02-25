package it.unibo.musicality.controller;

import java.io.IOException;

import it.unibo.musicality.dao.ReviewDAO;
import it.unibo.musicality.util.AlertUtil;
import it.unibo.musicality.util.Session;
import it.unibo.musicality.util.UIManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayBarController {
 
    @FXML private Button btnPrev;
    @FXML private Button btnPlayPause;
    @FXML private Button btnNext;
    @FXML private Button btnLyrics;
    @FXML private Button btnReview;
    @FXML private Slider sProgress;
    @FXML private Label lTime;
    @FXML private Slider sVolume;

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        sVolume.setMin(0);
        sVolume.setMax(1);
        sVolume.setValue(0.5);

        btnPrev.setOnAction(e -> prevPlay());
        btnNext.setOnAction(e -> nextPlay());
        btnPlayPause.setOnAction(e -> togglePlayPause());
        btnLyrics.setOnAction(e -> UIManager.getInstance().toggleLyrics());
        btnReview.setOnAction(e -> openReviewPopup());

        sProgress.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                double percent = sProgress.getValue() / 100.0;
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(percent));
            }
        });

        sVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) mediaPlayer.setVolume(newVal.doubleValue());
        });

        Text dummy = new Text("00:00 / 99:59");
        dummy.setFont(lTime.getFont());
        lTime.setMinWidth(dummy.getLayoutBounds().getWidth());
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!sProgress.isValueChanging() && mediaPlayer.getTotalDuration() != null) {
                double progress = newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds();
                sProgress.setValue(progress * 100);
            }
            updateTimeLabel(newTime, mediaPlayer.getTotalDuration());
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    private void prevPlay(){
        UIManager.getInstance().previous();
        setMediaPlayer(UIManager.getInstance().getCurrentMediaPlayer());
    }

    private void nextPlay() {
        UIManager.getInstance().next();
        setMediaPlayer(UIManager.getInstance().getCurrentMediaPlayer());
    }

    private void updateTimeLabel(Duration current, Duration total) {
        String currentStr = formatTime(current);
        String totalStr = formatTime(total != null ? total : Duration.UNKNOWN);
        lTime.setText(currentStr + " / " + totalStr);
    }

    private String formatTime(Duration d) {
        if (d == null || d.isUnknown()) return "--:--";
        int minutes = (int) d.toMinutes();
        int seconds = (int) (d.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void openReviewPopup() {
        if (!ReviewDAO.hasUserReviewedContent(Session.getUtente().getEmail(), UIManager.getInstance().getCurrentContent().getId())) {
            try {
                // Loading the window view form FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unibo/musicality/view/ReviewPopup.fxml"));
                Pane root = loader.load();

                // Pause the current content
                MediaPlayer player = UIManager.getInstance().getCurrentMediaPlayer();
                if (player != null && player.getStatus() == MediaPlayer.Status.PLAYING) {
                    player.pause();
                }

                // Create a new stage for the window
                Stage popupStage = new Stage();
                popupStage.setTitle("Valutazione contenuto");
                popupStage.setScene(new Scene(root));
                popupStage.initOwner(btnReview.getScene().getWindow());
                popupStage.setResizable(false);
                popupStage.initModality(Modality.APPLICATION_MODAL);

                // Listener used to play the content back if the window get closed
                popupStage.setOnCloseRequest(event -> {
                    if (player != null) player.play();
                });
                popupStage.showAndWait();

            } catch (IOException ex) {
                ex.printStackTrace();
            }   
        } else {
            AlertUtil.show("Il contenuto è già stato valutato", AlertType.INFORMATION);
        }
    }
}