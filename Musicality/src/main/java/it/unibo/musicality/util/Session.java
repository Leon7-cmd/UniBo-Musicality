package it.unibo.musicality.util;

import it.unibo.musicality.model.User;
import javafx.stage.Stage;

public class Session {

    private static User utenteCorrente;
    private static Stage primaryStage;

    // Set and Fetch the current user
    public static void setUtente(User utente) {utenteCorrente = utente;}
    public static User getUtente() {return utenteCorrente;}
    // Set and Fetch the primary stage
    public static void setStage(Stage stage) {primaryStage = stage;};
    public static Stage getStage() {return primaryStage;}

    // Clear the session (logout)
    public static void clear() {
        utenteCorrente = null;
        primaryStage = null;
    }
}