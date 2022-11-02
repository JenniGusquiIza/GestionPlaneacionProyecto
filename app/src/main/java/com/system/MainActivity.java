package com.system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.internal.Util;
import com.system.models.Persona;
import com.system.models.Usuario;
import com.system.services.IPersonaService;
import com.system.services.IUsuarioService;
import com.system.services.PersonaService;
import com.system.services.UsuarioService;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.util.Arrays;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    @BindView(R.id.txtEmail) TextInputEditText email;
    @BindView(R.id.txtPassword) TextInputEditText password;
    @BindView(R.id.bnLogin) Button bnIngresar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        builder = new AlertDialog.Builder(this);
        auth=FirebaseAuth.getInstance();
        permisos();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            reload(currentUser);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },3000);

        }
    }

    private void reload(@NonNull FirebaseUser auth) {
        Intent home = new Intent(getApplicationContext(), NavigationActivity.class);

        if(auth.getEmail().equals("sserver.codesteven@gmail.com")){
            Usuario usuario=new Usuario();
            usuario.setCorreo(auth.getEmail());
            usuario.setCodigo("ADMINISTRADOR");
            usuario.setRoles(Arrays.asList("A"));
            Utils.auth=usuario;
            startActivity(home);
        }else{
            String codigo= auth.getEmail().substring(0,auth.getEmail().indexOf("@"));
            DatabaseReference dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeUsuario);
            dbReference.child(codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                Usuario p=task.getResult().getValue(Usuario.class);
                                p.setCodigo(codigo);
                                Utils.auth=p;
                                startActivity(home);
                            }
                        }
                    });

        }


    }

    @OnClick(R.id.bnLogin)
    void submit() {
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading, null)); //agregamos el dialogo
        builder.setCancelable(false); //configuramos para que no pueda quitar
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(200, 200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(validateText()){
                    String correo=email.getText().toString();
                    String pass=password.getText().toString();
                    auth.signInWithEmailAndPassword(correo,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                reload(user);
                            }else{
                                String error=task.getException().toString();
                                Utils.messageConfirmation(getApplicationContext(),error);
                            }

                        }
                    })/*.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.messageError(getApplicationContext(),"Credenciales incorrectas");
                        }
                    })*/;

                }
                dialog.dismiss();


            }
        },2000);
    }


    private void permisos(){
        int write= ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read=ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        2000);

        }
    }
    private boolean validateText(){
        String correo = email.getText().toString().trim();
        String pass= password.getText().toString().trim();
        boolean isCorreo= true;
        boolean isPass=true;
        if(correo.isEmpty()){
            email.setError("Ingrese correo");
            isCorreo=false;
        }else if(!Utils.validateEmail(correo)){
            email.setError("Correo inválido");
            isCorreo=false;
        }
        if(pass.isEmpty()){
            password.setError("Ingrese contraseña");
            isPass=false;
        }
        return (isCorreo && isPass);
    }
}