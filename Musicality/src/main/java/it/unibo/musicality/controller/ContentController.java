package it.unibo.musicality.controller;

import java.util.List;

import it.unibo.musicality.util.AlertUtil;

import it.unibo.musicality.dao.ContentDAO;
import it.unibo.musicality.dao.LyricsDAO;
import it.unibo.musicality.dao.TagDAO;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.model.Lyrics;
import it.unibo.musicality.util.Session;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.File;

public class ContentController {
    // New content fields
    @FXML private Button btnNewContent;
    @FXML private TextField tfNewContentName;
    @FXML private ChoiceBox<String> cbNewContentType;
    @FXML private TextArea taNewContentDescription;
    @FXML private TextArea taNewContentLyrics;
    @FXML private ListView<String> lvNewContentTags;
    @FXML private TextField tfNewContentFile;
    @FXML private TextField tfNewTag;
    private File selectedFile;

    // Modify Content Fields
    @FXML private Label lHiddenId;
    @FXML private ListView<String> lvModifyTag;
    @FXML private TextArea taModifyLyrics;
    @FXML private Label lLyricsInfo;
    
    // Pop-up
    @FXML private Pane pModifyContent;
    @FXML private Pane pPopUpContent;
    @FXML private Pane pPopUpTag;


    // List view
    @FXML private ListView<MultimediaContent> lvSongs;
    @FXML private ListView<MultimediaContent> lvPodcast;

    @FXML
    public void initialize() {
        // Initialize ChoiceBox e ListView
        pModifyContent.visibleProperty().set(false);
        pPopUpTag.visibleProperty().set(false);
        pPopUpContent.visibleProperty().set(false);
        cbNewContentType.getItems().addAll("canzone", "podcast");
        resetTags();
        // Initialize list view
        fillListView(lvSongs, "canzone");
        fillListView(lvPodcast, "podcast");
    }

    @FXML 
    private void onClosePopUp(){
        pModifyContent.setVisible(false);
        btnNewContent.setDisable(false);
    }

    @FXML
    private void onShowPopUp() {
        pPopUpTag.setVisible(false);
        pPopUpContent.visibleProperty().set(!pPopUpContent.isVisible());
    }

    @FXML
    private void onOpenTagTab() {
        pPopUpContent.visibleProperty().set(false);
        pPopUpTag.visibleProperty().set(true);
    }

    @FXML
    private void onBack() {
        pPopUpContent.visibleProperty().set(true);
        pPopUpTag.visibleProperty().set(false);
    }

    @FXML
    private void onChooseFile() {
        // Open file chooser to select a media file
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleziona un file multimediale");
        // Set extension filters
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav")
        );
        // Show file chooser dialog
        selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            tfNewContentFile.setText(selectedFile.getAbsolutePath());
        }
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    @FXML
    private void onUpload() {
        String email = Session.getUtente().getEmail();
        String name = tfNewContentName.getText().trim();
        String type = cbNewContentType.getValue();
        String description = taNewContentDescription.getText().trim();
        String lyrics = taNewContentLyrics.getText().trim();
        String tags = String.join(",", lvNewContentTags.getSelectionModel().getSelectedItems());
        String file = tfNewContentFile.getText().trim();

        if (name.isEmpty() || type == null || description.isEmpty() || lyrics.isEmpty() || tags == null || file.isEmpty()) {
            AlertUtil.show("Compila tutti i campi.", Alert.AlertType.ERROR);
            return;
        }

        // Insert the new content in the DB
        MultimediaContent cm = new MultimediaContent("0", name, type, description, email, file);
        if(!ContentDAO.insertContent(cm)){
            AlertUtil.show("Errore nell'inserimento del contenuto.", Alert.AlertType.ERROR);
            return;
        }

        // Find content ID
        int idContent = ContentDAO.getContentId(cm);
        if (idContent != -1) {
            // Insert lyrics in the DB
            Lyrics l = new Lyrics(lyrics, idContent);
            LyricsDAO.insertLyrics(l);
            // Link tags to the content
            for (String tag : lvNewContentTags.getSelectionModel().getSelectedItems()) {
                TagDAO.linkTagToContent(idContent, tag);
            }
        }

        // Reset fields
        pPopUpContent.visibleProperty().set(false);
        tfNewContentName.clear();
        cbNewContentType.setValue(null);
        taNewContentDescription.clear();
        taNewContentLyrics.clear();
        lvNewContentTags.getSelectionModel().clearSelection();
        tfNewContentFile.clear();
        selectedFile = null;
        fillListView(lvSongs, "canzone");
        fillListView(lvPodcast, "podcast");
    }

    @FXML
    private void onCreateTag() {
        String tag = tfNewTag.getText().trim();
        if (tag != null && !tag.trim().isEmpty()) {
            if (TagDAO.createTag(tag)) {
                AlertUtil.show("Tag creato con successo.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtil.show("Errore nella creazione del tag.", Alert.AlertType.ERROR);
            }
        } else {
            AlertUtil.show("Inserisci un tag valido.", Alert.AlertType.ERROR);
        }

        // Reset fields
        resetTags();
    }
    @FXML 
    private void resetTags(){
        tfNewTag.clear();
        List<String> tags = TagDAO.getAllTags();
        lvNewContentTags.getItems().clear();
        lvNewContentTags.getItems().addAll(tags);
        lvNewContentTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvModifyTag.getItems().clear();
        lvModifyTag.getItems().addAll(tags);
        lvModifyTag.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void fillListView(ListView<MultimediaContent> listView, String type) {
        // Populate the ListView with content from the database
        ObservableList<MultimediaContent> items = ContentDAO.getAllContentByTypeAndUser(Session.getUtente().getEmail(), type);
        listView.setItems(items);
        // Cell Factory
        listView.setCellFactory(lv -> new ListCell<>() {
            private final Label lName = new Label();
            private final Label lDesc = new Label();
            private final Label lFile = new Label();
            private final Button btnModify = new Button("#");
            private final Button btnDelete = new Button("❌");
            private final HBox content = new HBox(10, lName, lDesc, lFile, btnModify, btnDelete);
            {
                btnModify.getStyleClass().add("icon-button");
                btnDelete.getStyleClass().add("icon-button");

                lName.setMinWidth(100);
                lName.setMaxWidth(100);
                lFile.setMinWidth(100);
                lFile.setMaxWidth(100);
                lDesc.setMinWidth(200);
                lDesc.setMaxWidth(200);

                lName.setEllipsisString("…");
                lFile.setEllipsisString("…");
                lDesc.setEllipsisString("…");

                lName.setStyle("-fx-text-overrun: ellipsis; -fx-alignment: CENTER_LEFT;");
                lFile.setStyle("-fx-text-overrun: ellipsis; -fx-alignment: CENTER;");
                lDesc.setStyle("-fx-text-overrun: ellipsis; -fx-alignment: CENTER_LEFT;");

                // Action to delete content
                btnDelete.setOnAction(e -> {
                    pModifyContent.setVisible(false);
                    MultimediaContent item = getItem();
                    if (AlertUtil.showChoice("Sicuro di voler eliminare il contenuto?")) {
                        ContentDAO.deleteContent(Session.getUtente().getEmail(), item.getName());
                    }
                    if(item.getType().equals("canzone")){fillListView(lvSongs, "canzone");}
                    else{fillListView(lvPodcast, "podcast");}
                });

                // Action to modify content
                btnModify.setOnAction(e -> {
                    MultimediaContent item = getItem();

                    lHiddenId.setText(item.getId());
                    lHiddenId.setVisible(false);
                    btnNewContent.setDisable(true);
                    pModifyContent.setVisible(true);
                    pPopUpContent.setVisible(false);
                    pPopUpTag.setVisible(false);
                    
                    taModifyLyrics.setText(LyricsDAO.getLyricsByContentId(item.getId()));
                    if (!LyricsDAO.isLyricsApproved(item.getId())) {
                        taModifyLyrics.setDisable(true);
                        lLyricsInfo.setText("Il testo è stato approvato");
                    } else{
                        taModifyLyrics.setDisable(false);
                        lLyricsInfo.setText("Il testo non è stato approvato");
                    }

                    List<String> list = TagDAO.getTagsOfContent(item.getId());
                    for (String tag : list) {
                        lvModifyTag.getSelectionModel().select(tag);
                    }
                });
            }

            @Override
            protected void updateItem(MultimediaContent item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lName.setText(item.getName());
                    lFile.setText(item.getFile());
                    lDesc.setText(item.getDescription());
                    setGraphic(content);
                }
            }
        });
    }

    @FXML 
    public void onUpdateContent(){
        if (AlertUtil.showChoice("Sicuro di voler modificare il contenuto?")) {
            // Update the Lyrics
            if (!taModifyLyrics.isDisabled()) {
                LyricsDAO.updateLyrics(taModifyLyrics.getText(), lHiddenId.getText());
            }
            // Update tags
            TagDAO.deleteTagFromContent(lHiddenId.getText());
            for (String tag : lvModifyTag.getSelectionModel().getSelectedItems()) {
                TagDAO.linkTagToContent(Integer.parseInt(lHiddenId.getText()), tag);
            }   
        }
        onClosePopUp();
    }

    public void refresh(){
        pModifyContent.visibleProperty().set(false);
        pPopUpTag.visibleProperty().set(false);
        pPopUpContent.visibleProperty().set(false);
        // Initialize list view
        fillListView(lvSongs, "canzone");
        fillListView(lvPodcast, "podcast");
    }
}