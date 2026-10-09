package org.rp.daoimpl;
import org.rp.dao.PrestamoDAO;
import org.rp.model.Prestamo;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

public class PrestamoDAOImpl implements PrestamoDAO {
    private Connection connection; // Supongamos que se inyecta o se obtiene del singleton

    @Override
    public void insertar(Prestamo t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_insertar_Prestamo(?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void actualizar(Prestamo t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_actualizar_Prestamo(?, ?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void eliminar(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_eliminar_Prestamo(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public List<Prestamo> listar() {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_listar_Prestamo()}");
            cs.execute();
        } catch (Exception e) {}
        return new ArrayList<>();
    }
    @Override
    public Prestamo buscarPorId(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_buscar_Prestamo(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
        return new Prestamo();
    }
}
