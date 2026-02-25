package it.unibo.musicality;

import it.unibo.musicality.util.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unibo/musicality/view/LoginRegisterView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 300, 330);
        scene.getStylesheets().add(getClass().getResource("/it/unibo/musicality/view/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Musicality");
        stage.show();
        Session.setStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}