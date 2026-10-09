package org.rp.util;

public class SessionContext {

    private static SessionContext instance;
    private int idUsuario;
    private String nombreCompleto;
    private String email;
    private String rol;

    private SessionContext() {}

    public static synchronized SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public void login(int idUsuario, String nombreCompleto, String email, String rol) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
    }

    public void logout() {
        this.idUsuario = 0;
        this.nombreCompleto = null;
        this.email = null;
        this.rol = null;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
}
