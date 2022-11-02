package com.system.services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.system.models.Persona;
import com.system.models.Usuario;
import com.system.ui.utils.Routes;

import java.util.concurrent.Future;

public class UsuarioService implements IUsuarioService{
    private DatabaseReference dbReference;
    private FirebaseAuth auth;
    @Override
    public void save(Usuario usuario) {
        try{
            auth=FirebaseAuth.getInstance();
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeUsuario);
            dbReference
                    .child(usuario.getCodigo())
                    //.push()
                    .setValue(usuario);

            auth.createUserWithEmailAndPassword(usuario.getCorreo(),usuario.getPassword());
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void delete(String codigo) {
        try {
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeUsuario);
            dbReference.child(codigo).removeValue();
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public Usuario get(@NonNull String correo) {
        String codigo= correo.substring(0,correo.indexOf("@"));
        final Usuario[] usuario = new Usuario[1];
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeUsuario);

        try {
            dbReference.child(codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                Usuario p=task.getResult().getValue(Usuario.class);
                                p.setCodigo(codigo);
                                usuario [0] =p;
                            }
                        }
                    });

            return usuario[0];
        }catch (Exception ex){
            throw  ex;
        }
    }

    @Override
    public boolean login(String correo, String contraseña) {
        return false;
    }
}
