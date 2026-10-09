package org.rp.controller;

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
        String pass = txtPassword.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Por favor ingresa tu correo y contraseña.");
            return;
        }

        Usuario u;
        try {
            u = usuarioDAO.autenticar(email, pass);
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "Error de conexión", "No fue posible conectarse a la base de datos. Verifica que MySQL esté activo y que bibliotech_in4cm exista.");
            return;
        }

        if (u == null) {
            showAlert(Alert.AlertType.ERROR, "Acceso denegado", "Credenciales incorrectas.");
            return;
        }

        SessionContext.getInstance().login(u.getIdUsuario(), u.getNombreCompleto(), u.getEmail(), u.getRol());

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/rp/view/LibroView.fxml"));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("BiblioTech - " + u.getRol());
            stage.setResizable(true);
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.centerOnScreen();
        } catch (Exception ex) {
            SessionContext.getInstance().logout();
            showAlert(Alert.AlertType.ERROR, "Error al abrir la aplicación", "No se pudo cargar la pantalla principal: " + ex.getMessage());
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
