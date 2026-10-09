package org.rp.dao;

import org.rp.model.Usuario;

public interface UsuarioDAO extends GenericDAO<Usuario> {
    Usuario autenticar(String email, String password);
}
