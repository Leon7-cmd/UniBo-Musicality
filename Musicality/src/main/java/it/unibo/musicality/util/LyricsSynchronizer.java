package it.unibo.musicality.util;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class LyricsSynchronizer {

    private static class LyricLine {
        int timeSeconds;
        LyricLine(int timeSeconds) {
            this.timeSeconds = timeSeconds;
        }
    }

    private List<LyricLine> lyricsLines = new ArrayList<>();
    private TextFlow tfLyrics;
    private ScrollPane spLyrics;
    private MediaPlayer mediaPlayer;
    private Timeline lyricsTimer;
    private int currentLine = -1;

    public LyricsSynchronizer(TextFlow tfLyrics, ScrollPane spLyrics) {
        this.tfLyrics = tfLyrics;
        this.spLyrics = spLyrics;
    }

    /**
     * Collega il MediaPlayer e il testo delle lyrics.
     * @param mediaPlayer il player corrente
     * @param lyrics testo del brano, con eventuali timestamp [mm:ss] Testo
     */
    public void bind(MediaPlayer mediaPlayer, String lyrics) {
        this.mediaPlayer = mediaPlayer;
        lyricsLines.clear();
        tfLyrics.getChildren().clear();
        tfLyrics.setPadding(new Insets(40, 70, 10, 70));
        tfLyrics.prefHeightProperty().bind(spLyrics.heightProperty());
        
        String[] lines = lyrics.split("\n");

        for (String line : lines) {
            String trimmed = line.trim();
            String textOnly = trimmed;
            int time = -1;

            if (trimmed.matches("\\[\\d{1,2}:\\d{2}]\\s*.*")) {
                int min = Integer.parseInt(trimmed.substring(1, trimmed.indexOf(":")));
                int sec = Integer.parseInt(trimmed.substring(trimmed.indexOf(":") + 1, trimmed.indexOf("]")));
                time = min * 60 + sec;
                textOnly = trimmed.substring(trimmed.indexOf("]") + 1).trim();
            }

            Text t = new Text(textOnly + "\n");
            t.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            t.setFill(Color.GRAY);
            t.wrappingWidthProperty().bind(tfLyrics.widthProperty().subtract(40));

            // If the line is empty, add extra spacing
            if (textOnly.isEmpty()) {
                t.setStyle("-fx-line-spacing: 10px;");
            }

            tfLyrics.getChildren().add(t);
            lyricsLines.add(new LyricLine(time));
        }


        currentLine = -1;
    }

    public void start() {
        if (mediaPlayer == null || lyricsLines.isEmpty()) return;

        stop(); // Reset if already running

        lyricsTimer = new Timeline(new KeyFrame(Duration.millis(200), e -> updateLyrics()));
        lyricsTimer.setCycleCount(Animation.INDEFINITE);
        lyricsTimer.play();
    }

    public void stop() {
        if (lyricsTimer != null) {
            lyricsTimer.stop();
            lyricsTimer = null;
        }
        currentLine = -1;
    }

    private void updateLyrics() {
        if (mediaPlayer == null || lyricsLines.isEmpty()) return;

        int currentSeconds = (int) mediaPlayer.getCurrentTime().toSeconds();
        int newLine = currentLine;

        // Find the line that should be highlighted
        for (int i = 0; i < lyricsLines.size(); i++) {
            if (lyricsLines.get(i).timeSeconds < 0) continue; // Skips lines without a timestamp

            int start = lyricsLines.get(i).timeSeconds;
            int end = Integer.MAX_VALUE;
            // Find the start time of the next line with a timestamp
            for (int j = i + 1; j < lyricsLines.size(); j++) {
                if (lyricsLines.get(j).timeSeconds >= 0) {
                    end = lyricsLines.get(j).timeSeconds;
                    break;
                }
            }

            if (currentSeconds >= start && currentSeconds < end) {
                newLine = i;
                break;
            }
        }

        if (newLine != currentLine) {
            currentLine = newLine;

            for (int i = 0; i < tfLyrics.getChildren().size(); i++) {
                Text t = (Text) tfLyrics.getChildren().get(i);
                if (i == currentLine) {
                    t.setFill(Color.WHITE);
                    t.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                } else {
                    t.setFill(Color.GRAY);
                    t.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                }
            }

            // Automatic scroll to keep current line visible
            double targetV = (double) currentLine / Math.max(1, lyricsLines.size() - 1);
            Timeline scrollAnim = new Timeline(
                new KeyFrame(Duration.millis(300),
                    new KeyValue(spLyrics.vvalueProperty(), targetV, Interpolator.EASE_BOTH)
                )
            );
            scrollAnim.play();
        }
    }

    public ScrollPane getSpLyrics() {
        return spLyrics;
    }

    public TextFlow getTfLyrics() {
        return tfLyrics;
    }
}