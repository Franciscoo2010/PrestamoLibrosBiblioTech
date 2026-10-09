package org.rp.daoimpl;
import org.rp.dao.DetallePrestamoDAO;
import org.rp.model.DetallePrestamo;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

public class DetallePrestamoDAOImpl implements DetallePrestamoDAO {
    private Connection connection; // Supongamos que se inyecta o se obtiene del singleton

    @Override
    public void insertar(DetallePrestamo t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_insertar_DetallePrestamo(?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void actualizar(DetallePrestamo t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_actualizar_DetallePrestamo(?, ?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void eliminar(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_eliminar_DetallePrestamo(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public List<DetallePrestamo> listar() {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_listar_DetallePrestamo()}");
            cs.execute();
        } catch (Exception e) {}
        return new ArrayList<>();
    }
    @Override
    public DetallePrestamo buscarPorId(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_buscar_DetallePrestamo(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
        return new DetallePrestamo();
    }
}
