package it.unibo.musicality.controller;

import it.unibo.musicality.dao.ContentDAO;
import it.unibo.musicality.dao.PlaylistDAO;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.model.Playlist;
import it.unibo.musicality.util.UIManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class SearchController {
    @FXML private ListView<String> lvSearchResult;
    @FXML private ChoiceBox<String> cbSearchType;
    @FXML private TextField tfSearchString;
    // Playlist fields
    @FXML private Pane pPlaylistSelected;
    @FXML private ListView<String> lvPlaylistList;
    @FXML private Label lPlaylistName;
    @FXML private Label lHiddenId;
    @FXML private Label lPlaylistDescription;

    @FXML
    public void initialize(){
        pPlaylistSelected.setVisible(false);
        cbSearchType.getItems().addAll("Canzoni/Podcast", "Playlist");
        cbSearchType.getSelectionModel().select(0);
        onSearchContent();
    }

    @FXML
    private void onSearchContent() {
        pPlaylistSelected.setVisible(false);
        String query = tfSearchString.getText().trim().toLowerCase();

        if (cbSearchType.getSelectionModel().isSelected(0)) {
            // Get all existing content
            ObservableList<MultimediaContent> allContent = ContentDAO.getAllContent();
            // Filter content based on search query
            ObservableList<MultimediaContent> filteredContent = allContent.filtered(item -> item.getName().toLowerCase().contains(query));
            // Cell factory used to display content with add button
            lvSearchResult.setItems(FXCollections.observableArrayList());
            for (MultimediaContent content : filteredContent) {
                lvSearchResult.getItems().add(content.getId());
            }    
        }else{
            // Get all existing public playlists
            ObservableList<Playlist> allPublicPlaylists = PlaylistDAO.getAllPublicPlaylists();
            // Filter content based on search query
            ObservableList<Playlist> filteredContent = allPublicPlaylists.filtered(item -> item.getName().toLowerCase().contains(query));
            // Cell factory used to display content with add button
            lvSearchResult.setItems(FXCollections.observableArrayList());
            for (Playlist playlist : filteredContent) {
                lvSearchResult.getItems().add(String.valueOf(playlist.getId()));
            }  
        }

        lvSearchResult.setCellFactory(listView -> new ListCell<>() {
            private final Label lId = new Label();
            private final Label lName = new Label();
            private final Button btnPlay = new Button("▶");
            private final HBox container = new HBox(10, lId, lName, btnPlay);

            {
                btnPlay.getStyleClass().add("icon-button");
                lName.setMinWidth(168);   
                lName.setPrefWidth(168);   
                lName.setMaxWidth(168);   
                lName.setTextOverrun(OverrunStyle.ELLIPSIS); 
                
                lId.setVisible(false);
                lId.setManaged(false);

                // Action to add content
                btnPlay.setOnAction(e -> {
                    String item = getItem();
                    if (item != null) {
                        // Play the content or show the selected playlist
                        if (cbSearchType.getSelectionModel().getSelectedIndex() == 0) {
                            UIManager.getInstance().togglePlayBar();
                            UIManager.getInstance().playContent(ContentDAO.getContentById(item));
                        }else{
                            showPlaylist(lName.getText(), lId.getText());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lId.setText(item);
                    lName.setText(cbSearchType.getSelectionModel().getSelectedIndex() == 0 ? ContentDAO.getContentNameById(Integer.parseInt(item)) : PlaylistDAO.getPlaylistNameById(item));
                    setGraphic(container);
                }
            }
        });
    }

    private void showPlaylist(String playlistName, String playlistId){
        lPlaylistName.setText(playlistName);
        lHiddenId.setVisible(false);
        lHiddenId.setText(playlistId);
        lPlaylistDescription.setText(PlaylistDAO.getPlaylistDescription(playlistName, PlaylistDAO.getPlaylistAuthorById(playlistId)));
        pPlaylistSelected.setVisible(true);
        // Show contents of the selected playlist
        ObservableList<MultimediaContent> contenuti = PlaylistDAO.getPlaylistContent(playlistName, PlaylistDAO.getPlaylistAuthorById(playlistId));
        lvPlaylistList.setItems(FXCollections.observableArrayList());
        for (MultimediaContent content : contenuti) {
            lvPlaylistList.getItems().add(content.getId());
        }

        // Cell factory to display content with play and remove buttons
        lvPlaylistList.setCellFactory(listView -> new ListCell<>() {
        private final Label lId = new Label();  
        private final Label lName = new Label();  
        private final Button btnPlay = new Button("▶");
        private final HBox container = new HBox(10, lName, lId, btnPlay);
        {
            btnPlay.getStyleClass().add("icon-button");

            lName.setMinWidth(240);
            lName.setPrefWidth(240);
            lName.setMaxWidth(240);
            lName.setTextOverrun(OverrunStyle.ELLIPSIS);

            lId.setVisible(false);
            lId.setManaged(false);

            btnPlay.setOnAction(e -> {
                MultimediaContent content = ContentDAO.getContentById(lId.getText());
                if (content != null) {
                    ObservableList<MultimediaContent> allPlaylistContent = PlaylistDAO.getPlaylistContent(lPlaylistName.getText(), PlaylistDAO.getPlaylistAuthorById(lHiddenId.getText()));
                    UIManager.getInstance().togglePlayBar();
                    UIManager.getInstance().playPlaylist(allPlaylistContent, content);
                }
            });
        }

            @Override
            protected void updateItem(String itemId, boolean empty) {
                super.updateItem(itemId, empty);
                if (empty || itemId == null) {
                    setGraphic(null);
                } else {
                    lId.setText(itemId);
                    MultimediaContent content = ContentDAO.getContentById(itemId);
                    lName.setText(content != null ? content.getName() : "Contenuto sconosciuto");
                    setGraphic(container);
                }
            }
        });
    }

    @FXML
    private void onStartPlaylist(){
        UIManager.getInstance().playPlaylist(PlaylistDAO.getPlaylistContent(lPlaylistName.getText(), PlaylistDAO.getPlaylistAuthorById(lHiddenId.getText())));
    }

    public void refresh(){
        pPlaylistSelected.setVisible(false);
        onSearchContent();
    }
}