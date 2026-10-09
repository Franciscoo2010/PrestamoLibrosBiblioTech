package org.rp.model;

import java.sql.Timestamp;

public class Usuario {

    private int idUsuario;
    private String nombreCompleto;
    private String email;
    private String password;
    private String rol;
    private Timestamp fechaCreacion;

    public Usuario() {}

    public Usuario(int idUsuario, String nombreCompleto, String email, String password, String rol, Timestamp fechaCreacion) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public String toString() {
        return "Usuario{id=" + idUsuario + ", nombre=" + nombreCompleto + ", rol=" + rol + "}";
    }
}
