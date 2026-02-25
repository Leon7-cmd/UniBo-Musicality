package it.unibo.musicality.controller;

import it.unibo.musicality.dao.StatisticsDAO;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.util.UIManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    @FXML private GridPane gpStatistics;
    private final Map<String, ListView<MultimediaContent>> listViewMap = new HashMap<>();
    private final String[][] rankingTitles = {
        {"Newest", "Top Rated", "Most Reviewed"},            
        {"Top Songs", "Most Reviewed Songs", "Top Podcasts"}, 
        {"Most Reviewed Podcasts", "Recent Reviews", "Melodia"}, 
        {"Testo", "Strumentazione", "Originalità"},          
        {"Voce", "Produzione", "Ritmo"},                
        {"Arrangiamento", "Coinvolgimento", "Atmosfera"},  
        {"Contenuto", "Chiarezza", "Informatività"},      
        {"Profondità", "Intrattenimento", "Durata"},       
        {"Struttura"}                                        
    };

    @FXML
    private void initialize() {
        // Dinamicaly creating Vbox and ListView
        for (int row = 0; row < rankingTitles.length; row++) {
            for (int col = 0; col < rankingTitles[row].length; col++) {
                String title = rankingTitles[row][col];

                VBox container = new VBox(5);

                Label label = new Label(title);
                ListView<MultimediaContent> listView = new ListView<>();
                listView.setPrefSize(180, 200);

                // Set cell
                listView.setCellFactory(lv -> createRankingCell());

                container.getChildren().addAll(label, listView);
                gpStatistics.add(container, col, row);

                // Saving the reference for later data insertion
                listViewMap.put(title, listView);
            }
        }

        // Populating dynamicaly the listView
        listViewMap.forEach((title, lv) -> {
            List<MultimediaContent> contents = StatisticsDAO.getRanking(title, 10); 
            lv.getItems().addAll(contents);
        });
    }

    private ListCell<MultimediaContent> createRankingCell() {
        return new ListCell<>() {
            private final HBox container = new HBox(10);
            private final Label lPosition = new Label();
            private final Label lName = new Label();
            private final Button btnPlay = new Button("▶");

            {
                btnPlay.getStyleClass().add("icon-button");

                lPosition.setMinWidth(15);
                lPosition.setPrefWidth(15);
                lPosition.setMaxWidth(15);

                lName.setMinWidth(75);
                lName.setPrefWidth(75);
                lName.setMaxWidth(75);
                lName.setTextOverrun(OverrunStyle.ELLIPSIS);

                container.getChildren().addAll(lPosition, lName, btnPlay);
                btnPlay.setOnAction(e -> {
                    MultimediaContent item = getItem();
                    if (item != null) {
                        UIManager.getInstance().playContent(item);
                    }
                });
            }

            @Override
            protected void updateItem(MultimediaContent item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    lPosition.setText(String.valueOf(getIndex() + 1));
                    lName.setText(item.getName());
                    setGraphic(container);
                }
            }
        };
    }

    public void populateRanking(String title, java.util.List<MultimediaContent> items) {
        ListView<MultimediaContent> listView = listViewMap.get(title);
        if (listView != null) {
            listView.getItems().setAll(items);
        }
    }

    public void refresh(){
        initialize();
    }
}