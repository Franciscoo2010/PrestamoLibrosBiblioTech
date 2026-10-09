package org.rp.daoimpl;
import org.rp.dao.EjemplarDAO;
import org.rp.model.Ejemplar;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

public class EjemplarDAOImpl implements EjemplarDAO {
    private Connection connection; // Supongamos que se inyecta o se obtiene del singleton

    @Override
    public void insertar(Ejemplar t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_insertar_Ejemplar(?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void actualizar(Ejemplar t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_actualizar_Ejemplar(?, ?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void eliminar(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_eliminar_Ejemplar(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public List<Ejemplar> listar() {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_listar_Ejemplar()}");
            cs.execute();
        } catch (Exception e) {}
        return new ArrayList<>();
    }
    @Override
    public Ejemplar buscarPorId(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_buscar_Ejemplar(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
        return new Ejemplar();
    }
}
