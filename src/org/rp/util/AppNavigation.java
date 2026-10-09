package org.rp.util;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public final class AppNavigation {
    private AppNavigation() {}

    public static void open(ActionEvent event, String view, String title) {
        try {
            Parent root = FXMLLoader.load(AppNavigation.class.getResource(view));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException | RuntimeException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo abrir la pantalla solicitada: " + ex.getMessage());
            alert.setHeaderText("Error de navegación");
            alert.showAndWait();
        }
    }

    public static void logout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SessionContext.getInstance().logout();
        open(event, "/org/rp/view/LoginView.fxml", "BiblioTech - Inicio de Sesión");
        stage.setResizable(false);
        stage.setWidth(500);
        stage.setHeight(450);
        stage.centerOnScreen();
    }
}
