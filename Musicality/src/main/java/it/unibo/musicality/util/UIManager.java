package it.unibo.musicality.util;

import it.unibo.musicality.controller.PlayBarController;
import it.unibo.musicality.dao.LyricsDAO;
import it.unibo.musicality.model.MultimediaContent;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class UIManager {
    private static UIManager instance;

    private Stage stage;
    private HBox playBar;
    private PlayBarController playBarController;
    private boolean playBarVisible = false;

    private MediaPlayer mediaPlayer;
    private ObservableList<MultimediaContent> playlist = FXCollections.observableArrayList();
    private int currentIndex = 0;
    private MultimediaContent currentContent; 

    private boolean lyricsVisible = false;
    private Pane pLyricsOverlay;
    private LyricsSynchronizer lyricsSync;

    private UIManager() {}

    public static UIManager getInstance() {
        if (instance == null) instance = new UIManager();
        return instance;
    }

    public void init(Stage stage, HBox playBar, PlayBarController playBarController) {
        this.stage = stage;
        this.playBar = playBar;
        this.playBarController = playBarController;

        playBar.setVisible(false);
        playBar.setManaged(false);
    }

    // ================= Playlist =================
    public void playPlaylist(ObservableList<MultimediaContent> contents) {
        if (contents == null || contents.isEmpty()) return;

        playlist.setAll(contents);
        currentIndex = 0;
        playCurrent();
    }

    public void playPlaylist(ObservableList<MultimediaContent> contents, MultimediaContent startItem) {
        if (contents == null || contents.isEmpty()) return;

        playlist.setAll(contents);
        currentIndex = playlist.indexOf(startItem);
        if (currentIndex < 0) currentIndex = 0; // If not found, start from beginning
        playCurrent();
    }

    private void playCurrent() {
        if (playlist.isEmpty() || currentIndex < 0 || currentIndex >= playlist.size()) return;
        playContent(playlist.get(currentIndex));
    }

    // ================= Single Content =================
    public void playContent(MultimediaContent content) {
        if (content == null || content.getFile() == null) return;
        if (playlist.isEmpty() || !(playlist.get(currentIndex) == content)) clearQueue();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        currentContent = content;

        try {
            Media media = new Media(new File(content.getFile()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            if (playBarController != null) playBarController.setMediaPlayer(mediaPlayer);

            // Autoplay next track if in playlist
            mediaPlayer.setOnEndOfMedia(this::next);
            mediaPlayer.play();

            if (!playBarVisible) togglePlayBar();

            // =================== Lyrics ===================
            if (lyricsSync != null) {
                String lyrics = LyricsDAO.isLyricsApproved(content.getId()) 
                                ? LyricsDAO.getLyricsByContentId(content.getId()) 
                                : "Lyrics not available";
                lyricsSync.bind(mediaPlayer, lyrics);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearQueue() {
        playlist.clear();
        currentIndex = 0;
    }


    // ================= Navigation =================
    public void next() {
        if (playlist.isEmpty()) {
            if (lyricsVisible) toggleLyrics();
            return;
        }
        currentIndex = currentIndex == playlist.size()-1 ? 0 : Math.min(currentIndex + 1, playlist.size() - 1);
        playCurrent();
    }

    public void previous() {
        if (playlist.isEmpty()) return;
        currentIndex = currentIndex == 0 ? playlist.size()-1 : Math.max(currentIndex - 1, 0);
        playCurrent();
    }

    // ================= Getters =================
    public MediaPlayer getCurrentMediaPlayer() {
        return mediaPlayer;
    }

    public MultimediaContent getCurrentContent() {
        return currentContent;
    }

    public ObservableList<MultimediaContent> getPlaylist() {
        return playlist;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    // ================= PlayBar =================
    public void togglePlayBar() {
        if (stage == null || playBar == null) return;

        stage.setMaximized(false);
        stage.setFullScreen(false);

        if (!playBarVisible) {
            playBar.setManaged(true);
            playBar.setVisible(true);
            adjustStageHeight(44); 
        } else {
            playBar.setManaged(false);
            playBar.setVisible(false);
            adjustStageHeight(-44); 
        }

        playBarVisible = !playBarVisible;
    }

    private void adjustStageHeight(double delta) {
        stage.setResizable(true);
        stage.setHeight(stage.getHeight() + delta);
        stage.setResizable(false);
    }

    // ================= Lyrics Overlay =================
    public void setLyricsOverlay(Pane pLyricsOverlay, ScrollPane scrollPane, TextFlow textFlow) {
        this.pLyricsOverlay = pLyricsOverlay;
        this.lyricsSync = new LyricsSynchronizer(textFlow, scrollPane);

        this.pLyricsOverlay.setVisible(false);
        this.pLyricsOverlay.setManaged(false);
    }

    public void toggleLyrics() {
        if (lyricsSync == null || currentContent == null || pLyricsOverlay == null) return;

        // Get lyrics from DB
        String lyrics = LyricsDAO.isLyricsApproved(currentContent.getId()) ?
                        LyricsDAO.getLyricsByContentId(currentContent.getId()) : "Non disponibile";

        // Bind lyrics to synchronizer
        lyricsSync.bind(mediaPlayer, lyrics);

        // Overlay placement and sizing
        double playBarHeight = (playBarVisible && playBar != null) ? playBar.getHeight() : 0;
        pLyricsOverlay.prefWidthProperty().bind(stage.widthProperty());
        pLyricsOverlay.prefHeightProperty().bind(stage.heightProperty().subtract(playBarHeight));
        pLyricsOverlay.setLayoutY(0);

        ScrollPane spLyrics = lyricsSync.getSpLyrics();
        TextFlow tfLyrics = lyricsSync.getTfLyrics();

        // Remove the padding of the ScrollPane to fit the overlay
        spLyrics.setPadding(javafx.geometry.Insets.EMPTY);
        spLyrics.setFitToWidth(true);
        spLyrics.setFitToHeight(true);
        spLyrics.prefWidthProperty().bind(pLyricsOverlay.widthProperty());
        spLyrics.prefHeightProperty().bind(pLyricsOverlay.heightProperty());

        // TextFlow fills the ScrollPane
        tfLyrics.prefWidthProperty().bind(spLyrics.widthProperty().subtract(20));
        tfLyrics.prefHeightProperty().bind(spLyrics.heightProperty());
        tfLyrics.setLayoutY(0);
        tfLyrics.setTextAlignment(TextAlignment.CENTER);

        // Open/Close animation
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), pLyricsOverlay);

        if (!lyricsVisible) {
            pLyricsOverlay.setVisible(true);
            pLyricsOverlay.setManaged(true);
            pLyricsOverlay.setTranslateY(pLyricsOverlay.getHeight());
            transition.setFromY(pLyricsOverlay.getHeight());
            transition.setToY(0);

            lyricsSync.start();
        } else {
            transition.setFromY(0);
            transition.setToY(pLyricsOverlay.getHeight());
            transition.setOnFinished(e -> {
                pLyricsOverlay.setVisible(false);
                pLyricsOverlay.setManaged(false);
                lyricsSync.stop();
            });
        }

        transition.play();
        lyricsVisible = !lyricsVisible;
    }

    public boolean isLyricsVisible() {
        return lyricsVisible;
    }
}