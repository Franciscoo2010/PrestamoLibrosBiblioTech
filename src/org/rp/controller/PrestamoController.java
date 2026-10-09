package org.rp.controller;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rp.dao.LibroDAO;
import org.rp.dao.PrestamoDAO;
import org.rp.dao.UsuarioDAO;
import org.rp.daoimpl.LibroDAOImpl;
import org.rp.daoimpl.PrestamoDAOImpl;
import org.rp.daoimpl.UsuarioDAOImpl;
import org.rp.model.Libro;
import org.rp.model.Prestamo;
import org.rp.model.Usuario;
import org.rp.util.SessionContext;

public class PrestamoController implements Initializable {

    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private ComboBox<Libro> cmbLibro;

    @FXML private TableView<Prestamo> tblPrestamos;
    @FXML private TableColumn<Prestamo, Integer> colId;
    @FXML private TableColumn<Prestamo, String> colUsuario;
    @FXML private TableColumn<Prestamo, String> colLibro;
    @FXML private TableColumn<Prestamo, Date> colFecha;
    @FXML private TableColumn<Prestamo, Date> colDevolucion;
    @FXML private TableColumn<Prestamo, String> colEstado;

    private PrestamoDAO prestamoDAO = new PrestamoDAOImpl();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private LibroDAO libroDAO = new LibroDAOImpl();
    private ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colLibro.setCellValueFactory(new PropertyValueFactory<>("tituloLibro"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucionEsperada"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cmbUsuario.setItems(FXCollections.observableArrayList(usuarioDAO.listar()));
        cmbLibro.setItems(FXCollections.observableArrayList(libroDAO.listar()));
        cargarTabla();
    }

    private void cargarTabla() {
        listaPrestamos.clear();
        listaPrestamos.addAll(prestamoDAO.listar());
        tblPrestamos.setItems(listaPrestamos);
    }

    @FXML
    private void handleRegistrarPrestamo(ActionEvent event) {
        Usuario u = cmbUsuario.getValue();
        Libro l = cmbLibro.getValue();
        if (u == null || l == null) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Selecciona un usuario y un libro.");
            return;
        }
        if (l.getStock() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Sin stock", "No hay ejemplares disponibles para este libro.");
            return;
        }
        Prestamo p = new Prestamo();
        p.setIdUsuario(u.getIdUsuario());
        p.setIdLibro(l.getIdLibro());
        p.setFechaPrestamo(Date.valueOf(LocalDate.now()));
        p.setFechaDevolucionEsperada(Date.valueOf(LocalDate.now().plusDays(7)));
        p.setEstado("ACTIVO");
        prestamoDAO.insertar(p);
        cargarTabla();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Préstamo registrado. Devolución esperada en 7 días.");
    }

    @FXML
    private void handleDevolver(ActionEvent event) {
        Prestamo sel = tblPrestamos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Selecciona un préstamo para devolver.");
            return;
        }
        sel.setFechaDevolucionReal(Date.valueOf(LocalDate.now()));
        sel.setEstado("DEVUELTO");
        prestamoDAO.actualizar(sel);
        cargarTabla();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Libro devuelto correctamente.");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
