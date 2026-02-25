package it.unibo.musicality.util;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class AlertUtil {
    public static void show(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(AlertUtil.class.getResource("/it/unibo/musicality/view/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }

    public static boolean showChoice(String msg){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(msg);
        alert.setHeaderText(null);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(AlertUtil.class.getResource("/it/unibo/musicality/view/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        return alert.showAndWait().get().getButtonData().isDefaultButton();
    }
}