package com.system.services;

import com.google.firebase.auth.FirebaseUser;
import com.system.models.Fase;
import com.system.models.Usuario;

public interface IUsuarioService {
    void save(Usuario usuario);
    void delete(String codigo);
    Usuario get(String codigo);

    boolean login(String correo,String contraseña);
}
