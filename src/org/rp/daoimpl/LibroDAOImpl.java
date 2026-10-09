package org.rp.daoimpl;
import org.rp.dao.LibroDAO;
import org.rp.model.Libro;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

public class LibroDAOImpl implements LibroDAO {
    private Connection connection; // Supongamos que se inyecta o se obtiene del singleton

    @Override
    public void insertar(Libro t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_insertar_Libro(?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void actualizar(Libro t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_actualizar_Libro(?, ?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void eliminar(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_eliminar_Libro(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public List<Libro> listar() {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_listar_Libro()}");
            cs.execute();
        } catch (Exception e) {}
        return new ArrayList<>();
    }
    @Override
    public Libro buscarPorId(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_buscar_Libro(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
        return new Libro();
    }
}
