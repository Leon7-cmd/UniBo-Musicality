package it.unibo.musicality.controller;

import java.util.List;

import javafx.scene.control.Alert;
import it.unibo.musicality.util.AlertUtil;
import javafx.scene.control.Button;
import it.unibo.musicality.dao.SubscriptionDAO;
import it.unibo.musicality.dao.LyricsDAO;
import it.unibo.musicality.dao.ContentDAO;
import it.unibo.musicality.dao.TagDAO;
import it.unibo.musicality.dao.UserDAO;
import it.unibo.musicality.model.Lyrics;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.util.Session;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class AdminController {
    // PopUp Panes
    @FXML private Pane pAdminLock;
    @FXML private Pane pNewAdmin;
    @FXML private Pane pRemoveTags;
    @FXML private Pane pRewiewLyrics;
    @FXML private Pane pUserBlock;
    @FXML private Pane pUpdatePrice;  
    
    @FXML private TextField tfAdminCode;

    // New Admin Fields
    @FXML private ChoiceBox<String> cbNewAdminEmail;
    @FXML private TextField tfNewAdminCode;
    // Tag Removal Fields
    @FXML private ListView<String> lvAllTags;
    // Lyrics Review Fields
    @FXML private ChoiceBox<String> cbSelectLyrics;
    @FXML private TextArea taShowLyrics;
    @FXML private ChoiceBox<String> cbLyricsAuthor;
    // User Blocking Fields
    @FXML private TextField tfUserSearch;
    @FXML private ChoiceBox<String> cbUserSelect;
    @FXML private Button btnBlock;
    @FXML private Button btnUnlock;
    // Price Update Fields
    @FXML private TextField tfPlanPrice;
    @FXML private TextField tfDiscountPrice;
    @FXML private ChoiceBox<String> cbPlanType;



    @FXML
    public void initialize() {
        refreshLyrics();
        btnBlock.setDisable(true);
        btnUnlock.setDisable(true);
        cbPlanType.getItems().addAll(SubscriptionDAO.getAllPlanTypes());
        pAdminLock.visibleProperty().set(true);
        refreshTags();
        cbNewAdminEmail.getItems().addAll(UserDAO.getNotAdminUsers(Session.getUtente().getEmail()));
    }

    @FXML
    private void onOpenAdminSettings() {
        String code = tfAdminCode.getText().trim();
        String email = Session.getUtente().getEmail();

        if (UserDAO.isUserAdmin(email, code)) { 
            pAdminLock.visibleProperty().set(false);
        }
    }
    
    @FXML
    private void showPanel(Pane panelToShow) {
        pNewAdmin.visibleProperty().set(false);
        pRemoveTags.visibleProperty().set(false);
        pRewiewLyrics.visibleProperty().set(false);
        pUserBlock.visibleProperty().set(false);
        pUpdatePrice.visibleProperty().set(false);
        panelToShow.visibleProperty().set(true);
    }

    // ================= New Admin =================
    @FXML
    private void onShowNewAdmin() {
        showPanel(pNewAdmin);
    }
    @FXML
    private void onCreateNewAdmin() {
        String email = cbNewAdminEmail.getValue();
        String code = tfNewAdminCode.getText().trim();

        if (email != null && !code.isEmpty()) {
            // Call the method to create a new admin
            if (UserDAO.createAdmin(email, code)) {
                AlertUtil.show("Nuovo amministratore creato con successo.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtil.show("Errore nella creazione del nuovo amministratore.", Alert.AlertType.ERROR);
            }
        }
    }

    // ================= Tag Removal =================
    @FXML
    private void onShowRemoveTags() {
        refreshTags();
        showPanel(pRemoveTags);
    }
    @FXML
    private void onDeleteTags() {
        List<String> tags = lvAllTags.getSelectionModel().getSelectedItems();
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                TagDAO.removeTagFromAllContents(tag);
            }
        }
        lvAllTags.getItems().clear();
        refreshTags();
    }
    @FXML
    private void refreshTags() {
        List<String> tags = TagDAO.getAllTags();
        lvAllTags.getItems().clear();
        lvAllTags.getItems().addAll(tags);
        lvAllTags.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    // ================= Lyrics Review =================
    @FXML
    private void onShowRewiewLyrics() {
        refreshLyrics();
        showPanel(pRewiewLyrics);
    }
    @FXML
    private void onLyricsSelected() {
        String contentName = cbSelectLyrics.getSelectionModel().getSelectedItem();
        cbLyricsAuthor.getSelectionModel().select(cbSelectLyrics.getSelectionModel().getSelectedIndex());
        String authorEmail = cbLyricsAuthor.getSelectionModel().getSelectedItem();
        if (contentName != null && !contentName.isEmpty()) {
            MultimediaContent content = new MultimediaContent();
            content.setName(contentName);
            content.setEmail(authorEmail);
            int id = ContentDAO.getContentId(content);
            String lyrics = LyricsDAO.getLyricsByContentId(String.valueOf(id));
            taShowLyrics.setText(lyrics != null ? lyrics : "Testo non trovato.");
        }
    }
    @FXML
    private void onApproveLyrics() {
        String contentName = cbSelectLyrics.getSelectionModel().getSelectedItem();
        String authorEmail = cbLyricsAuthor.getSelectionModel().getSelectedItem();
        if (contentName != null && !contentName.isEmpty()) {
            MultimediaContent content = new MultimediaContent();
            content.setName(contentName);
            content.setEmail(authorEmail);
            int id = ContentDAO.getContentId(content);
            if (LyricsDAO.approveLyrics(id, Session.getUtente().getAdminCode())) {
                AlertUtil.show("Testo approvato con successo.", Alert.AlertType.INFORMATION);
                refreshLyrics();
            } else {
                AlertUtil.show("Errore nell'approvazione del testo.", Alert.AlertType.ERROR);
            }
        }
        
    }
    @FXML
    private void refreshLyrics(){
        cbLyricsAuthor.getSelectionModel().clearSelection();
        cbSelectLyrics.getSelectionModel().clearSelection();
        cbLyricsAuthor.getItems().clear();
        cbSelectLyrics.getItems().clear();
        ObservableList<Lyrics> lyricsList = LyricsDAO.searchNotReviewedLyrics();
        for (Lyrics lyrics : lyricsList) {
            cbLyricsAuthor.getItems().add(ContentDAO.getAuthorEmailById(lyrics.getIdContent()));
            cbSelectLyrics.getItems().add(ContentDAO.getContentNameById(lyrics.getIdContent()));
        }
        taShowLyrics.clear();
    }

    // ================= User =================
    @FXML
    private void onShowUserBlock() {
        showPanel(pUserBlock);
    }
    @FXML
    private void onSearching() {
        cbUserSelect.getItems().clear();
        List<String> users = UserDAO.getNotAdminUsers(Session.getUtente().getEmail());
        String searchText = tfUserSearch.getText().trim().toLowerCase();
        for (String user : users) {
            if (user.toLowerCase().contains(searchText) && !searchText.isEmpty()) {
                cbUserSelect.getItems().add(user);
            }
        }
    }
    @FXML
    private void onSelectUser() {
        String selectedUser = cbUserSelect.getValue();
        if (UserDAO.userIsBlocked(selectedUser)) {
            btnBlock.setDisable(true);
            btnUnlock.setDisable(false);
        } else {
            btnBlock.setDisable(false);
            btnUnlock.setDisable(true);
        }
    }
    @FXML
    private void onBlockUser() {
        String selectedUser = cbUserSelect.getValue();
        if (selectedUser != null && !selectedUser.isEmpty()) {
            if (UserDAO.blockUser(selectedUser, Session.getUtente().getAdminCode())) {
                AlertUtil.show("Utente bloccato con successo.", Alert.AlertType.INFORMATION);
                btnBlock.setDisable(true);
                btnUnlock.setDisable(true);
            } else {
                AlertUtil.show("Errore nel bloccare l'utente.", Alert.AlertType.ERROR);
            }
        }
        cbUserSelect.getSelectionModel().clearSelection();
    }
    @FXML
    private void onUnlockUser() {
        String selectedUser = cbUserSelect.getValue();
        if (selectedUser != null && !selectedUser.isEmpty()) {
            if (UserDAO.unlockUser(selectedUser)) {
                AlertUtil.show("Utente sbloccato con successo.", Alert.AlertType.INFORMATION);
                btnBlock.setDisable(true);
                btnUnlock.setDisable(true);
            } else {
                AlertUtil.show("Errore nello sbloccare l'utente.", Alert.AlertType.ERROR);
            }
        }
        cbUserSelect.getSelectionModel().clearSelection();
    }

    // ================= Price Update =================
    @FXML
    private void onShowUpdatePrice() {
        showPanel(pUpdatePrice);
    }
    @FXML
    private void onPlanSelected(){
        String planType = cbPlanType.getValue();
        if (planType != null && !planType.isEmpty()) {
            double[] prices = SubscriptionDAO.getPlanPrices(planType);
            if (prices != null) {
                tfPlanPrice.setText(String.valueOf(prices[0]));
                tfDiscountPrice.setText(String.valueOf(prices[1]));
            } else {
                tfPlanPrice.clear();
                tfDiscountPrice.clear();
                AlertUtil.show("Errore nel recupero dei prezzi del piano.", Alert.AlertType.ERROR);
            }
        }
    }
    @FXML
    private void onUpdatePrice() {
        String planType = cbPlanType.getValue();
        String planPriceStr = tfPlanPrice.getText().trim();
        String discountPriceStr = tfDiscountPrice.getText().trim();

        if (planType != null && !planType.isEmpty() && !planPriceStr.isEmpty() && !discountPriceStr.isEmpty()) {
            try {
                double planPrice = Double.parseDouble(planPriceStr);
                double discountPrice = Double.parseDouble(discountPriceStr);

                if (SubscriptionDAO.updatePlanPrices(planType, planPrice, discountPrice)) {
                    AlertUtil.show("Prezzi aggiornati con successo.", Alert.AlertType.INFORMATION);
                } else {
                    AlertUtil.show("Errore nell'aggiornamento dei prezzi.", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                AlertUtil.show("Inserisci valori numerici validi per i prezzi.", Alert.AlertType.ERROR);
            }
        } else {
            AlertUtil.show("Compila tutti i campi.", Alert.AlertType.ERROR);
        }

        tfPlanPrice.clear();
        tfDiscountPrice.clear();
        cbPlanType.getSelectionModel().clearSelection();
    }
}