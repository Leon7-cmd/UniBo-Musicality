package it.unibo.musicality.controller;

import it.unibo.musicality.dao.ContentDAO;
import it.unibo.musicality.dao.PlaylistDAO;
import it.unibo.musicality.dao.SubscriptionDAO;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.util.AlertUtil;
import it.unibo.musicality.util.Session;
import it.unibo.musicality.util.UIManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class PlaylistController {
    // New playlist fields
    @FXML private TextField tfName;
    @FXML private TextArea taDescription;
    @FXML private CheckBox cbVisibility;
    @FXML private Pane pCreatePlaylist;

    // Playlist fields
    @FXML private Pane pPlaylistSelected;
    @FXML private ListView<String> lvPlaylistList;
    @FXML private ChoiceBox<String> cbPlaylistSelection;
    @FXML private Label lPlaylistName;
    @FXML private Label lPlaylistDescription;

    // Add content fields
    @FXML private Pane pAddContent;
    @FXML private TextField tfSearchString;
    @FXML private ListView<String> lvSearchContent;

    @FXML
    public void initialize() {
        pPlaylistSelected.setVisible(false);
        pAddContent.setVisible(false);
        cbPlaylistSelection.setItems(PlaylistDAO.getUserPlaylists(Session.getUtente().getEmail()));
        pCreatePlaylist.setVisible(false);
    }

    @FXML
    private void onShowCreatePlaylist() {
        pAddContent.setVisible(false);
        cbPlaylistSelection.getSelectionModel().clearSelection();
        pPlaylistSelected.setVisible(false);
        pCreatePlaylist.setVisible(!pCreatePlaylist.isVisible());
    }
    @FXML
    private void onCreatePlaylist() {
        if (PlaylistDAO.numberOfPlaylists(Session.getUtente().getEmail()) < 3 || !SubscriptionDAO.getUserPlan(Session.getUtente().getEmail()).equals("1")) {
            String name = tfName.getText().trim();
            String description = taDescription.getText().trim();
            String visibility = cbVisibility.isSelected() ? "pubblica" : "privata";
            if (name.isEmpty() || description.isEmpty()) {
                AlertUtil.show("Compila tutti i campi.", Alert.AlertType.ERROR);
                return;
            }
            if (PlaylistDAO.createPlaylist(name, description, visibility, Session.getUtente().getEmail())) {
                AlertUtil.show("Playlist creata con successo.", Alert.AlertType.INFORMATION);
                tfName.clear();
                taDescription.clear();
                cbVisibility.setSelected(false);
                pCreatePlaylist.setVisible(false);
            } else {
                AlertUtil.show("Errore nella creazione della playlist.", Alert.AlertType.ERROR);
            }
            cbPlaylistSelection.setItems(PlaylistDAO.getUserPlaylists(Session.getUtente().getEmail()));
            cbPlaylistSelection.getSelectionModel().clearSelection();
            pPlaylistSelected.setVisible(false);
        } else {
            AlertUtil.show("Per aggiungere altre playlist modifica il tuo abbonamento", AlertType.WARNING);
        }
    }
    @FXML
    private void onDeletePlaylist() {
        // Logic to delete the playlist
        String selectedPlaylist = cbPlaylistSelection.getValue();
        if (selectedPlaylist == null || selectedPlaylist.isEmpty()) {
            AlertUtil.show("Seleziona una playlist.", Alert.AlertType.ERROR);
            return;
        }
        if (AlertUtil.showChoice("Sicuro di voler eliminare la playlist?")) {
            if (PlaylistDAO.deletePlaylist(selectedPlaylist, Session.getUtente().getEmail())) {
                cbPlaylistSelection.setItems(PlaylistDAO.getUserPlaylists(Session.getUtente().getEmail()));
                cbPlaylistSelection.getSelectionModel().clearSelection();
                pPlaylistSelected.setVisible(false);
            } else {
                AlertUtil.show("Errore nell'eliminazione della playlist.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void onShowAddContent() {
        // Logic to link content to the selected playlist
        String selectedPlaylist = cbPlaylistSelection.getValue();
        if (selectedPlaylist == null || selectedPlaylist.isEmpty()) {
            AlertUtil.show("Seleziona una playlist.", Alert.AlertType.ERROR);
            return;
        }
        pAddContent.setVisible(!pAddContent.isVisible());
        pCreatePlaylist.setVisible(false);
        onSearchContent(); // Refresh the search content list
    }
    @FXML
    private void onSearchContent() {
        String query = tfSearchString.getText().trim().toLowerCase();
        // Get all existing content excluding those already in the selected playlist
        ObservableList<MultimediaContent> allContent = ContentDAO.getAllContent();
        ObservableList<MultimediaContent> playlistContent = PlaylistDAO.getPlaylistContent(cbPlaylistSelection.getValue(), Session.getUtente().getEmail());
        for (MultimediaContent pc : playlistContent) {
            allContent.removeIf(ac -> ac.getId().equals(pc.getId()));
        }
        // Filter content based on search query
        ObservableList<MultimediaContent> filteredContent = allContent.filtered(item -> item.getName().toLowerCase().contains(query));

        // Cell factory used to display content with add button
        lvSearchContent.setItems(FXCollections.observableArrayList());
        for (MultimediaContent content : filteredContent) {
            lvSearchContent.getItems().add(content.getName());
        }
        lvSearchContent.setCellFactory(listView -> new ListCell<>() {
            private final Label lContentName = new Label();
            private final Button btnAdd = new Button("+");
            private final HBox container = new HBox(10, lContentName, btnAdd);

            {
                btnAdd.getStyleClass().add("icon-button");
                lContentName.setMinWidth(145); 
                lContentName.setPrefWidth(145); 
                lContentName.setMaxWidth(145);  
                lContentName.setTextOverrun(OverrunStyle.ELLIPSIS);

                // Action to add content
                btnAdd.setOnAction(e -> {
                    String item = getItem();
                    if (item != null) {
                        String authorEmail = null;
                        for (MultimediaContent mc : filteredContent) {
                            if (mc.getName().equals(item)) {
                                authorEmail = mc.getEmail();
                                break;
                            }
                        }
                        PlaylistDAO.addContentToPlaylist(cbPlaylistSelection.getValue(), item, Session.getUtente().getEmail(), authorEmail);
                        getListView().getItems().remove(item); // Remove the item from the search list
                        onShowPlaylist(); // Refresh the playlist view
                        pAddContent.setVisible(true);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lContentName.setText(item);
                    setGraphic(container);
                }
            }
        });
    }
    
    @FXML
    private void onShowPlaylist(){
        lPlaylistName.setText(cbPlaylistSelection.getValue());
        lPlaylistDescription.setText(PlaylistDAO.getPlaylistDescription(cbPlaylistSelection.getValue(), Session.getUtente().getEmail()));
        pCreatePlaylist.setVisible(false);
        pAddContent.setVisible(false);
        pPlaylistSelected.setVisible(true);
        // Show contents of the selected playlist
        ObservableList<MultimediaContent> contenuti = PlaylistDAO.getPlaylistContent(cbPlaylistSelection.getValue(), Session.getUtente().getEmail());
        lvPlaylistList.setItems(FXCollections.observableArrayList());
        for (MultimediaContent content : contenuti) {
            lvPlaylistList.getItems().add(content.getId());
        }

        // Cell factory to display content with play and remove buttons
        lvPlaylistList.setCellFactory(listView -> new ListCell<>() {
        private final Label lId = new Label();   // contiene l'ID
        private final Label lContentName = new Label();  // mostrata all'utente
        private final Button btnPlay = new Button("▶");
        private final Button btnRemove = new Button("❌");
        private final HBox container = new HBox(10, lContentName, btnPlay, btnRemove);

        {
            btnPlay.getStyleClass().add("icon-button");
            btnRemove.getStyleClass().add("icon-button");

            // Fixed label dimension
            lContentName.setMinWidth(185);
            lContentName.setPrefWidth(185);
            lContentName.setMaxWidth(185);
            lContentName.setTextOverrun(OverrunStyle.ELLIPSIS);

            // Hide the label
            lId.setVisible(false);
            lId.setManaged(false);

            btnPlay.setOnAction(e -> {
                MultimediaContent content = ContentDAO.getContentById(lId.getText());
                if (content != null) {
                    UIManager.getInstance().togglePlayBar();
                    UIManager.getInstance().playPlaylist(PlaylistDAO.getPlaylistContent(lPlaylistName.getText(), Session.getUtente().getEmail()), content);
                }
            });

            btnRemove.setOnAction(e -> {
                String id = lId.getText();
                MultimediaContent content = ContentDAO.getContentById(id);
                if (content != null) {
                    PlaylistDAO.removeContentFromPlaylist(cbPlaylistSelection.getValue(), content.getName(), Session.getUtente().getEmail(), content.getEmail());
                    getListView().getItems().remove(id);
                }
                onSearchContent();
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
                    lContentName.setText(content != null ? content.getName() : "Contenuto sconosciuto");
                    setGraphic(container);
                }
            }
        });
    }

    @FXML
    private void onStartPlaylist(){
        UIManager.getInstance().playPlaylist(PlaylistDAO.getPlaylistContent(lPlaylistName.getText(), Session.getUtente().getEmail()));
    }

    public void refresh(){
        cbPlaylistSelection.getSelectionModel().clearSelection();
        pPlaylistSelected.setVisible(false);
        pAddContent.setVisible(false);
        pCreatePlaylist.setVisible(false);
    }
}