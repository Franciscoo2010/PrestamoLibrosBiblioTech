package org.rp.daoimpl;
import org.rp.dao.MultaDAO;
import org.rp.model.Multa;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

public class MultaDAOImpl implements MultaDAO {
    private Connection connection; // Supongamos que se inyecta o se obtiene del singleton

    @Override
    public void insertar(Multa t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_insertar_Multa(?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void actualizar(Multa t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_actualizar_Multa(?, ?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void eliminar(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_eliminar_Multa(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public List<Multa> listar() {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_listar_Multa()}");
            cs.execute();
        } catch (Exception e) {}
        return new ArrayList<>();
    }
    @Override
    public Multa buscarPorId(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_buscar_Multa(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
        return new Multa();
    }
}
