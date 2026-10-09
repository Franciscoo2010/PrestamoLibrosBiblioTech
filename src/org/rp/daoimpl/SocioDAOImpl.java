package org.rp.daoimpl;
import org.rp.dao.SocioDAO;
import org.rp.model.Socio;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

public class SocioDAOImpl implements SocioDAO {
    private Connection connection; // Supongamos que se inyecta o se obtiene del singleton

    @Override
    public void insertar(Socio t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_insertar_Socio(?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void actualizar(Socio t) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_actualizar_Socio(?, ?)}");
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public void eliminar(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_eliminar_Socio(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
    }
    @Override
    public List<Socio> listar() {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_listar_Socio()}");
            cs.execute();
        } catch (Exception e) {}
        return new ArrayList<>();
    }
    @Override
    public Socio buscarPorId(int id) {
        try {
            CallableStatement cs = connection.prepareCall("{call sp_buscar_Socio(?)}");
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {}
        return new Socio();
    }
}
