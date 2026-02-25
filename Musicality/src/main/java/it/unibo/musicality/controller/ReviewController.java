package it.unibo.musicality.controller;

import it.unibo.musicality.dao.ReviewDAO;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.util.AlertUtil;
import it.unibo.musicality.util.UIManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ReviewController {

    @FXML private Button btnSubmit;
    @FXML private Button btnCancel;
    @FXML private ListView<String> lvReview;

    private MultimediaContent content;
    private final Map<String, Integer> ratings = new HashMap<>();

    @FXML
    private void initialize() {
        content = UIManager.getInstance().getCurrentContent();
        btnSubmit.setOnAction(e -> submitReview());
        btnCancel.setOnAction(e -> closePopup());

        lvReview.setCellFactory(list -> createRatingCell());
        lvReview.getItems().addAll(ReviewDAO.getReviewCategoryByType(content.getType()));
    }

    private void submitReview() {
        if (AlertUtil.showChoice("Sicuro di voler inviare la valutazione?")) {
            if(!ReviewDAO.insertReview(ratings, content)){
                AlertUtil.show("C'è stato un errore nella registrazione della valutazione", AlertType.ERROR);
            }
            closePopup();
        }
    }

    private void closePopup() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();

        MediaPlayer player = UIManager.getInstance().getCurrentMediaPlayer();
        if (player != null) {
            player.play();
        }
    }

    private ListCell<String> createRatingCell() {
        return new ListCell<>() {
            private final HBox container = new HBox(10);
            private final Label lReviewNamLabel = new Label();
            private final HBox starsBox = new HBox(5);

            {
                lReviewNamLabel.setMinWidth(160);
                lReviewNamLabel.setPrefWidth(160);
                lReviewNamLabel.setMaxWidth(160);

                starsBox.setAlignment(Pos.CENTER_LEFT);
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().addAll(lReviewNamLabel, starsBox);
            }

            private void updateStars(String category, int rating) {
                starsBox.getChildren().clear();
                for (int i = 1; i <= 5; i++) {
                    Label star = new Label(i <= rating ? "★" : "☆");
                    star.setStyle("-fx-font-size: 18px; -fx-text-fill: gold;");
                    int finalRating = i;
                    star.setOnMouseClicked(e -> {
                        ratings.put(category, finalRating);
                        updateStars(category, finalRating);
                    });
                    starsBox.getChildren().add(star);
                }
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    lReviewNamLabel.setText(item);
                    int rating = ratings.getOrDefault(item, 0);
                    updateStars(item, rating);
                    setGraphic(container);
                }
            }
        };
    }
}