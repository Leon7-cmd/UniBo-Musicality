package it.unibo.musicality.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

import it.unibo.musicality.model.User;
import it.unibo.musicality.util.Session;
import it.unibo.musicality.util.UIManager;

public class MainController {
    @FXML private TabPane tbMain;
    @FXML private Tab ContentView;
    @FXML private Tab AdminView;
    @FXML private Tab ProfileView;
    @FXML private ScrollPane spLyrics;
    @FXML private Pane pLyricsOverlay;
    @FXML private TextFlow tfLyrics;

    @FXML
    public void initialize() {
        User user = Session.getUtente();
        if (user != null) {
            if (!"autore".equals(user.getUserType())) {
                tbMain.getTabs().remove(ContentView);
            }
            if (user.getAdminCode() == null || user.getAdminCode().isEmpty()) {
                tbMain.getTabs().remove(AdminView);
            }
        }
        UIManager.getInstance().setLyricsOverlay(pLyricsOverlay, spLyrics, tfLyrics);

        // Added a listener to all tabs so the data is always correct
        tbMain.getTabs().forEach(tab -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unibo/musicality/view/" + tab.getId() + ".fxml"));
                tab.setContent(loader.load());
                tab.setUserData(loader.getController());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tbMain.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && (newTab != AdminView && newTab != ProfileView)) {
                Object controller = newTab.getUserData();
                if (controller != null) {
                    try {
                        controller.getClass().getMethod("refresh").invoke(controller);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @FXML private HBox playBar; 
    @FXML private PlayBarController playBarController;

    public void initUIManager(Stage stage) {
        UIManager.getInstance().init(stage, playBar, playBarController);
    }

    @FXML
    private void reloadUI() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unibo/musicality/view/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tbMain.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}