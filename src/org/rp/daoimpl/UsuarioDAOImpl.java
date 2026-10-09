package org.rp.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.rp.dao.UsuarioDAO;
import org.rp.model.Usuario;
import org.rp.util.Conexion;

public class UsuarioDAOImpl implements UsuarioDAO {

    private Connection getConn() {
        return Conexion.getInstance().getConnection();
    }

    @Override
    public void insertar(Usuario u) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_insertarusuario(?, ?, ?, ?)}")) {
            cs.setString(1, u.getNombreCompleto());
            cs.setString(2, u.getEmail());
            cs.setString(3, u.getPassword());
            cs.setString(4, u.getRol());
            cs.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Usuario u) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_actualizarusuario(?, ?, ?, ?)}")) {
            cs.setInt(1, u.getIdUsuario());
            cs.setString(2, u.getNombreCompleto());
            cs.setString(3, u.getEmail());
            cs.setString(4, u.getRol());
            cs.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_eliminarusuario(?)}")) {
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        try (CallableStatement cs = getConn().prepareCall("{call sp_listarusuarios()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setEmail(rs.getString("email"));
                u.setRol(rs.getString("rol"));
                u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                lista.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Usuario buscarPorId(int id) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_buscarusuario(?)}")) {
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    u.setEmail(rs.getString("email"));
                    u.setRol(rs.getString("rol"));
                    return u;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Usuario autenticar(String email, String password) {
        try {
            CallableStatement cs = getConn().prepareCall(
                "SELECT id_usuario, nombre_completo, email, password, rol, fecha_creacion " +
                "FROM usuarios WHERE email = ? AND password = ?");
            cs.setString(1, email);
            cs.setString(2, password);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setEmail(rs.getString("email"));
                u.setRol(rs.getString("rol"));
                u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
