package org.rp.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Prestamo_Libros extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/org/rp/view/LoginView.fxml"));
        Scene scene = new Scene(root, 500, 450);
        stage.setScene(scene);
        stage.setTitle("BiblioTech - Inicio de Sesión");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
