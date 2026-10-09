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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rp.dao.LibroDAO;
import org.rp.daoimpl.LibroDAOImpl;
import org.rp.model.Libro;

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

    private LibroDAO libroDAO = new LibroDAOImpl();
    private ObservableList<Libro> listaLibros = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        l.setStock(txtStock.getText().isEmpty() ? 0 : Integer.parseInt(txtStock.getText()));
        l.setAnioPublicacion(txtAnio.getText().isEmpty() ? 0 : Integer.parseInt(txtAnio.getText()));
        libroDAO.insertar(l);
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
        sel.setTitulo(txtTitulo.getText());
        sel.setAutor(txtAutor.getText());
        sel.setIsbn(txtIsbn.getText());
        sel.setStock(Integer.parseInt(txtStock.getText()));
        sel.setAnioPublicacion(Integer.parseInt(txtAnio.getText()));
        libroDAO.actualizar(sel);
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
        libroDAO.eliminar(sel.getIdLibro());
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
}
