package org.rp.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.rp.dao.LibroDAO;
import org.rp.model.Libro;
import org.rp.util.Conexion;

public class LibroDAOImpl implements LibroDAO {

    private Connection getConn() {
        return Conexion.getInstance().getConnection();
    }

    @Override
    public void insertar(Libro l) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_insertarlibro(?, ?, ?, ?, ?)}")) {
            cs.setString(1, l.getTitulo());
            cs.setString(2, l.getAutor());
            cs.setString(3, l.getIsbn());
            cs.setInt(4, l.getStock());
            cs.setInt(5, l.getAnioPublicacion());
            cs.execute();
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
    }

    @Override
    public void actualizar(Libro l) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_actualizarlibro(?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, l.getIdLibro());
            cs.setString(2, l.getTitulo());
            cs.setString(3, l.getAutor());
            cs.setString(4, l.getIsbn());
            cs.setInt(5, l.getStock());
            cs.setInt(6, l.getAnioPublicacion());
            cs.execute();
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        try (CallableStatement cs = getConn().prepareCall("{call sp_eliminarlibro(?)}")) {
            cs.setInt(1, id);
            cs.execute();
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
    }

    @Override
    public List<Libro> listar() {
        List<Libro> lista = new ArrayList<>();
        try (PreparedStatement cs = getConn().prepareStatement("SELECT * FROM libros ORDER BY titulo");
             ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Libro l = new Libro();
                    l.setIdLibro(rs.getInt("id_libro"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAutor(rs.getString("autor"));
                    l.setIsbn(rs.getString("isbn"));
                    l.setStock(rs.getInt("stock"));
                    l.setAnioPublicacion(rs.getInt("anio_publicacion"));
                    lista.add(l);
                }
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
        return lista;
    }

    @Override
    public Libro buscarPorId(int id) {
        try (PreparedStatement cs = getConn().prepareStatement("SELECT * FROM libros WHERE id_libro = ?")) {
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
              if (rs.next()) {
                Libro l = new Libro();
                l.setIdLibro(rs.getInt("id_libro"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                l.setIsbn(rs.getString("isbn"));
                l.setStock(rs.getInt("stock"));
                l.setAnioPublicacion(rs.getInt("anio_publicacion"));
                return l;
              }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder a la base de datos.", e);
        }
        return null;
    }
}
