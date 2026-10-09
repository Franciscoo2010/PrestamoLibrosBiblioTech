package org.rp.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.rp.dao.PrestamoDAO;
import org.rp.model.Prestamo;
import org.rp.util.Conexion;

public class PrestamoDAOImpl implements PrestamoDAO {

    private Connection getConn() {
        return Conexion.getInstance().getConnection();
    }

    @Override
    public void insertar(Prestamo p) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_insertarprestamo(?, ?, ?, ?, ?)}")) {
            cs.setInt(1, p.getIdUsuario());
            cs.setInt(2, p.getIdLibro());
            cs.setDate(3, p.getFechaPrestamo());
            cs.setDate(4, p.getFechaDevolucionEsperada());
            cs.setString(5, p.getEstado());
            cs.execute();
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
    }

    @Override
    public void actualizar(Prestamo p) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_actualizarprestamo(?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, p.getIdPrestamo());
            cs.setInt(2, p.getIdUsuario());
            cs.setInt(3, p.getIdLibro());
            cs.setDate(4, p.getFechaPrestamo());
            cs.setDate(5, p.getFechaDevolucionEsperada());
            cs.setDate(6, p.getFechaDevolucionReal());
            cs.setString(7, p.getEstado());
            cs.execute();
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_eliminarprestamo(?)}")) {
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
    }

    @Override
    public List<Prestamo> listar() {
        List<Prestamo> lista = new ArrayList<>();
        try (PreparedStatement cs = getConn().prepareStatement(
                "SELECT p.id_prestamo, p.id_usuario, p.id_libro, p.fecha_prestamo, " +
                "p.fecha_devolucion_esperada, p.fecha_devolucion_real, p.estado, " +
                "u.nombre_completo, l.titulo FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "INNER JOIN libros l ON p.id_libro = l.id_libro ORDER BY p.id_prestamo DESC")) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Prestamo p = new Prestamo();
                    p.setIdPrestamo(rs.getInt("id_prestamo"));
                    p.setIdUsuario(rs.getInt("id_usuario"));
                    p.setIdLibro(rs.getInt("id_libro"));
                    p.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                    p.setFechaDevolucionEsperada(rs.getDate("fecha_devolucion_esperada"));
                    p.setFechaDevolucionReal(rs.getDate("fecha_devolucion_real"));
                    p.setEstado(rs.getString("estado"));
                    p.setNombreUsuario(rs.getString("nombre_completo"));
                    p.setTituloLibro(rs.getString("titulo"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
        return lista;
    }

    @Override
    public Prestamo buscarPorId(int id) {
        try (PreparedStatement cs = getConn().prepareStatement(
                "SELECT * FROM prestamos WHERE id_prestamo = ?")) {
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
              if (rs.next()) {
                Prestamo p = new Prestamo();
                p.setIdPrestamo(rs.getInt("id_prestamo"));
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setIdLibro(rs.getInt("id_libro"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                p.setFechaDevolucionEsperada(rs.getDate("fecha_devolucion_esperada"));
                p.setFechaDevolucionReal(rs.getDate("fecha_devolucion_real"));
                p.setEstado(rs.getString("estado"));
                return p;
              }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
        return null;
    }
}
