package it.unibo.musicality.controller;

import it.unibo.musicality.util.AlertUtil;
import it.unibo.musicality.dao.ContentDAO;
import it.unibo.musicality.dao.SubscriptionDAO;
import it.unibo.musicality.dao.UserDAO;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.util.Session;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ProfileController {
    private String currentPlan;
    // Subscription fields
    @FXML private TextField tfCurrentSubscription;
    @FXML private ChoiceBox<String> cbPlanSelection;
    @FXML private Pane pChangePlan;
    // Profile fields
    @FXML private TextField tfProfileType;
    @FXML private TextField tfEmail;
    @FXML private TextField tfName;
    @FXML private TextField tfSurname;
    @FXML private TextField tfUsername;
    @FXML private Button btnChangeProfile;

    @FXML
    public void initialize() {
        btnChangeProfile.setText((Session.getUtente().getUserType().equals("autore") ? "CAMBIA UTENTE IN ASCOLTATORE" : "CAMBIA UTENTE IN AUTORE"));
        tfProfileType.setText(Session.getUtente().getUserType());
        tfEmail.setText(Session.getUtente().getEmail());
        tfName.setText(Session.getUtente().getName());
        tfSurname.setText(Session.getUtente().getSurname());
        tfUsername.setText(Session.getUtente().getUsername());
        tfCurrentSubscription.setEditable(false);
        pChangePlan.setVisible(false);
        refreshPlanSelection();
    }

    @FXML
    private void onChangeSubscription() {
        pChangePlan.setVisible(!pChangePlan.isVisible());
    }
    @FXML
    private void onChangePlan() {
        String selectedPlan = cbPlanSelection.getValue();
        if (selectedPlan != null && !selectedPlan.isEmpty()) {
            SubscriptionDAO.changeUserPlan(selectedPlan);
            AlertUtil.show("Piano cambiato in: " + selectedPlan, Alert.AlertType.INFORMATION);
        }else{
            AlertUtil.show("Seleziona un piano valido.", Alert.AlertType.ERROR);
            return;
        }
        cbPlanSelection.getItems().clear();
        refreshPlanSelection();
        pChangePlan.setVisible(false);
    }

    @FXML
    private void onChangeProfileType() {
        if (AlertUtil.showChoice(Session.getUtente().getUserType().equals("autore") ? "Sicuro di voler cambiare il tipo di profilo ? \nTutti i contenuti inseriti verranno eliminati." : "Sicuro di voler cambiare il tipo di profilo ?")) {
            ObservableList<MultimediaContent> items = ContentDAO.getAllContentByTypeAndUser(Session.getUtente().getEmail(), "canzone");
            for (MultimediaContent multimediaContent : items) {
                ContentDAO.deleteContent(Session.getUtente().getEmail(), multimediaContent.getName());
            }
            items = ContentDAO.getAllContentByTypeAndUser(Session.getUtente().getEmail(), "podcast");
            for (MultimediaContent multimediaContent : items) {
                ContentDAO.deleteContent(Session.getUtente().getEmail(), multimediaContent.getName());
            }
            UserDAO.changeUserProfileType(Session.getUtente().getEmail());
            // Logout after changing profile type
            try {
                Session.clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unibo/musicality/view/LoginRegisterView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) tfCurrentSubscription.getScene().getWindow();
                stage.setScene(new Scene(root, 300, 330));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void onUpdateProfile() {
        String newUsername = tfUsername.getText().trim();

        if (newUsername.isEmpty()) {
            AlertUtil.show("Compila tutti i campi.", Alert.AlertType.ERROR);
            return;
        }

        boolean updated = UserDAO.updateProfile(Session.getUtente().getEmail(),newUsername);
        if (updated) {
            AlertUtil.show("Profilo aggiornato con successo.", Alert.AlertType.INFORMATION);
            // Update the session
            Session.getUtente().setUsername(newUsername);
            tfUsername.setText(newUsername);
        } else {
            AlertUtil.show("Errore durante l'aggiornamento del profilo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onLogout(){
        try {
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unibo/musicality/view/LoginRegisterView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tfCurrentSubscription.getScene().getWindow();                
            stage.setScene(new Scene(root, 300, 330));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshPlanSelection() {
        currentPlan = SubscriptionDAO.getUserPlan(Session.getUtente().getEmail());
        tfCurrentSubscription.setText(currentPlan);
        cbPlanSelection.getItems().addAll(SubscriptionDAO.getAllPlanTypes());
        cbPlanSelection.getItems().remove(currentPlan);
    }
}