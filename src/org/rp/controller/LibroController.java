package org.rp.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rp.dao.LibroDAO;
import org.rp.daoimpl.LibroDAOImpl;
import org.rp.model.Libro;
import org.rp.util.AppNavigation;
import org.rp.util.SessionContext;

public class LibroController implements Initializable {

    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, Integer> colStock;
    @FXML private TableColumn<Libro, Integer> colAnio;

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtStock;
    @FXML private TextField txtAnio;
    @FXML private Button btnInsertar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnPrestamos;

    private LibroDAO libroDAO = new LibroDAOImpl();
    private ObservableList<Libro> listaLibros = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SessionContext session = SessionContext.getInstance();
        if (session.getIdUsuario() == 0) {
            txtTitulo.setDisable(true);
            txtAutor.setDisable(true);
            txtIsbn.setDisable(true);
            txtStock.setDisable(true);
            txtAnio.setDisable(true);
            tblLibros.setDisable(true);
            btnInsertar.setDisable(true);
            btnActualizar.setDisable(true);
            btnEliminar.setDisable(true);
            btnPrestamos.setVisible(false);
            btnPrestamos.setManaged(false);
            showAlert(Alert.AlertType.WARNING, "Sesión requerida", "Inicia sesión para acceder al catálogo.");
            return;
        }
        boolean puedeGestionar = "ADMIN".equals(session.getRol()) || "BIBLIOTECARIO".equals(session.getRol());
        btnInsertar.setDisable(!puedeGestionar);
        btnActualizar.setDisable(!puedeGestionar);
        btnEliminar.setDisable(!puedeGestionar);
        txtTitulo.setDisable(!puedeGestionar);
        txtAutor.setDisable(!puedeGestionar);
        txtIsbn.setDisable(!puedeGestionar);
        txtStock.setDisable(!puedeGestionar);
        txtAnio.setDisable(!puedeGestionar);
        btnPrestamos.setVisible(puedeGestionar);
        btnPrestamos.setManaged(puedeGestionar);
        colId.setCellValueFactory(new PropertyValueFactory<>("idLibro"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        cargarTabla();

        tblLibros.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null) {
                txtTitulo.setText(nuevo.getTitulo());
                txtAutor.setText(nuevo.getAutor());
                txtIsbn.setText(nuevo.getIsbn());
                txtStock.setText(String.valueOf(nuevo.getStock()));
                txtAnio.setText(String.valueOf(nuevo.getAnioPublicacion()));
            }
        });
    }

    private void cargarTabla() {
        listaLibros.clear();
        listaLibros.addAll(libroDAO.listar());
        tblLibros.setItems(listaLibros);
    }

    @FXML
    private void handleInsertar(ActionEvent event) {
        if (txtTitulo.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "El título es obligatorio.");
            return;
        }
        Libro l = new Libro();
        l.setTitulo(txtTitulo.getText());
        l.setAutor(txtAutor.getText());
        l.setIsbn(txtIsbn.getText());
        try {
            l.setStock(txtStock.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtStock.getText().trim()));
            l.setAnioPublicacion(txtAnio.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtAnio.getText().trim()));
            if (l.getStock() < 0 || l.getAnioPublicacion() < 0) throw new NumberFormatException();
            libroDAO.insertar(l);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Datos inválidos", "Stock y año deben ser números enteros no negativos.");
            return;
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "No se pudo guardar", ex.getMessage());
            return;
        }
        cargarTabla();
        limpiarCampos();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Libro registrado correctamente.");
    }

    @FXML
    private void handleActualizar(ActionEvent event) {
        Libro sel = tblLibros.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Selecciona un libro para actualizar.");
            return;
        }
        if (txtTitulo.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo obligatorio", "El título es obligatorio.");
            return;
        }
        sel.setTitulo(txtTitulo.getText().trim());
        sel.setAutor(txtAutor.getText());
        sel.setIsbn(txtIsbn.getText());
        try {
            sel.setStock(Integer.parseInt(txtStock.getText().trim()));
            sel.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
            if (sel.getStock() < 0 || sel.getAnioPublicacion() < 0) throw new NumberFormatException();
            libroDAO.actualizar(sel);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Datos inválidos", "Stock y año deben ser números enteros no negativos.");
            return;
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "No se pudo actualizar", ex.getMessage());
            return;
        }
        cargarTabla();
        limpiarCampos();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Libro actualizado correctamente.");
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        Libro sel = tblLibros.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Selecciona un libro para eliminar.");
            return;
        }
        try {
            libroDAO.eliminar(sel.getIdLibro());
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "No se pudo eliminar", "El libro puede tener préstamos asociados. " + ex.getMessage());
            return;
        }
        cargarTabla();
        limpiarCampos();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Libro eliminado correctamente.");
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        txtTitulo.clear();
        txtAutor.clear();
        txtIsbn.clear();
        txtStock.clear();
        txtAnio.clear();
        tblLibros.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML private void handlePrestamos(ActionEvent event) {
        AppNavigation.open(event, "/org/rp/view/PrestamoView.fxml", "BiblioTech - Préstamos");
    }

    @FXML private void handleCerrarSesion(ActionEvent event) {
        AppNavigation.logout(event);
    }
}
