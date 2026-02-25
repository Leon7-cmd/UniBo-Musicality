package it.unibo.musicality.controller;

import it.unibo.musicality.util.Session;
import it.unibo.musicality.util.Validator;
import it.unibo.musicality.util.AlertUtil;
import it.unibo.musicality.dao.SubscriptionDAO;
import it.unibo.musicality.dao.UserDAO;
import it.unibo.musicality.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AuthController {
    // Login fields
    @FXML private TextField tfLoginEmail;
    @FXML private PasswordField pfLoginPassword;

    // Registration fields
    @FXML private TextField tfRegUsername;
    @FXML private TextField tfRegEmail;
    @FXML private TextField tfRegNome;
    @FXML private TextField tfRegCognome;
    @FXML private PasswordField pfRegPassword;
    @FXML private PasswordField pfRegConfermaPassword;
    @FXML private CheckBox cbRegAutore;

    @FXML
    private void onLogin() {
        String email = tfLoginEmail.getText().trim();
        String pwd = pfLoginPassword.getText();
        if (UserDAO.userIsBlocked(email)) {
            AlertUtil.show("Utente bloccato. Contatta l'amministratore.", Alert.AlertType.ERROR);
            return;
        }
        if (UserDAO.checkPassword(email, pwd)) {
            try {
                // Fetch the user from the db and start the session
                User u = UserDAO.getByEmail(email);
                Session.setUtente(u);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unibo/musicality/view/MainView.fxml"));
                Parent root = loader.load();
                MainController mainController = loader.getController();

                // Setting the main stage in the session
                Stage stage = Session.getStage();
                mainController.initUIManager(stage);

                Stage mainScene = (Stage) tfLoginEmail.getScene().getWindow();
                mainScene.setScene(new Scene(root, 600, 430));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertUtil.show("Email o password errati.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void onRegister() {
        String username = tfRegUsername.getText().trim();
        String email = tfRegEmail.getText().trim();
        String name = tfRegNome.getText().trim();
        String surname = tfRegCognome.getText().trim();
        String pwd = pfRegPassword.getText();
        String pwd2 = pfRegConfermaPassword.getText();
        boolean isAuthor = cbRegAutore.isSelected();

        if (username.isEmpty() || email.isEmpty() || name.isEmpty() || surname.isEmpty() || pwd.isEmpty() || pwd2.isEmpty()) {
            AlertUtil.show("Compila tutti i campi.", Alert.AlertType.ERROR);
            return;
        }
        if (!pwd.equals(pwd2)) {
            AlertUtil.show("Le password non coincidono.", Alert.AlertType.ERROR);
            return;
        }
        if (!Validator.isValidEmail(email)) {
            AlertUtil.show("Email non valida.", Alert.AlertType.ERROR);
            return;
        } else if(UserDAO.existsByEmail(email)) {
            AlertUtil.show("Esiste già un account con questa email.", Alert.AlertType.ERROR);
            return;
        }

        User u = new User(email, username, pwd, name, surname, null, isAuthor ? "autore" : "ascoltatore");
        boolean created = UserDAO.createUser(u);
        if (created) {
            AlertUtil.show("Registrazione avvenuta con successo. Effettua il login.", Alert.AlertType.INFORMATION);
            // Set standard subscription for new user
            SubscriptionDAO.setDefaultPlan(email);
            // Optionally clear fields
            tfRegUsername.clear(); tfRegEmail.clear(); tfRegNome.clear(); tfRegCognome.clear(); pfRegPassword.clear(); pfRegConfermaPassword.clear(); cbRegAutore.setSelected(false);
        } else {
            AlertUtil.show("Errore durante la registrazione.", Alert.AlertType.ERROR);
        }
    }
}