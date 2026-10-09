package org.rp.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.rp.dao.UsuarioDAO;
import org.rp.daoimpl.UsuarioDAOImpl;
import org.rp.model.Usuario;
import org.rp.util.SessionContext;

public class LoginController implements Initializable {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String pass = txtPassword.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Por favor ingresa tu correo y contraseña.");
            return;
        }

        Usuario u = usuarioDAO.autenticar(email, pass);

        if (u == null) {
            showAlert(Alert.AlertType.ERROR, "Acceso denegado", "Credenciales incorrectas.");
            return;
        }

        SessionContext.getInstance().login(u.getIdUsuario(), u.getNombreCompleto(), u.getEmail(), u.getRol());

        try {
            String fxml;
            switch (u.getRol()) {
                case "ADMIN":
                    fxml = "/org/rp/view/AdminView.fxml";
                    break;
                case "BIBLIOTECARIO":
                    fxml = "/org/rp/view/BibliotecarioView.fxml";
                    break;
                default:
                    fxml = "/org/rp/view/LibroView.fxml";
                    break;
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("BiblioTech - " + u.getRol());
        } catch (IOException e) {
            // Si no existe la vista específica, carga la de libros
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/org/rp/view/LibroView.fxml"));
                Stage stage = (Stage) txtEmail.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("BiblioTech - " + u.getNombreCompleto());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
