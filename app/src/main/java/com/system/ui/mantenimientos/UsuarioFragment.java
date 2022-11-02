package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.R;
import com.system.models.Fase;
import com.system.models.Usuario;
import com.system.services.FaseService;
import com.system.services.IUsuarioService;
import com.system.services.UsuarioService;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class UsuarioFragment extends Fragment {
    private String rol;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private Unbinder unbinder,unbinderDialog;
    private DatabaseReference dbReference;
    private UsuarioAdapter usuarioAdapter;
    ArrayAdapter<String> adapterRoles;
    private IUsuarioService service;
    private List<Usuario> usuarioList;
    @Nullable @BindView(R.id.recyclerUsuario) RecyclerView recyclerView;
    //DIALOG_USUARIO
    @Nullable @BindView(R.id.txtPassword) TextInputLayout txtPassword;
    @Nullable @BindView(R.id.txtCorreo) TextInputLayout txtCorreo;
    @Nullable @BindView(R.id.cbRoles) TextInputLayout cbRoles;
    @Nullable @BindView(R.id.selectRol) ChipGroup selectRol;
    @Nullable @BindView(R.id.rbGroupEstado)  RadioGroup rbGroupEstado;
    @Nullable @BindView(R.id.bnGuardar)  Button bnGuardar;
    @Nullable @BindView(R.id.bnCancelar) Button bnCancelar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_usuario, container, false);
        unbinder= ButterKnife.bind(this, root);
        initData();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        service= new UsuarioService();
        usuarioAdapter=new UsuarioAdapter(getContext(),this);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration  dividerItemDecoration= new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.layer));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(usuarioAdapter);
        recyclerView.setHasFixedSize(true);

    }

    //region METODOS ONCLICK
    @Optional
    @OnClick(R.id.fbAdd)
    void showtDialog(){
        adapterRoles = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, Utils.Roles()); //Adaptamos el Layout dropdown_tipo
        builder = new AlertDialog.Builder(getContext()); //asignacion de instancia de Alert.Builder
        LayoutInflater inflater=getLayoutInflater();
        View dialogUsuario= inflater.inflate(R.layout.dialog_usuario,null);
        unbinderDialog=ButterKnife.bind(this, dialogUsuario);
        AutoCompleteTextView autoCompleteTextView= (AutoCompleteTextView) cbRoles.getEditText();
        autoCompleteTextView.setAdapter(adapterRoles);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String rol= parent.getItemAtPosition(position).toString();
                boolean exit=false;
                for(int i=0;i<selectRol.getChildCount();i++){
                    Chip chip=(Chip) selectRol.getChildAt(i);
                    if(rol.contains(chip.getText().toString())){
                        exit=true;
                        break;
                    }
                }
                if(!exit){
                    Chip chip= new Chip(getContext());
                    chip.setText(rol);
                    chip.setCloseIconVisible(true);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectRol.removeView(v);
                        }
                    });
                    selectRol.addView(chip);
                }
            }
        });
        builder.setView(dialogUsuario); //agregamos el dialogo
        builder.setCancelable(true); //configuramos para que se pueda quitar
        dialog=builder.create();
        dialog.show();//abrimos el dialog

    }

    @Optional
    @OnClick(R.id.bnGuardar)
    void submitSave(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String>roles= new ArrayList<>();
                    for(int i=0;i<selectRol.getChildCount();i++){
                        Chip chip=(Chip) selectRol.getChildAt(i);
                       roles.add(chip.getText().toString());
                    }
                    //List<String>roles= Arrays.asList(cbRoles.getEditText().getText().toString().split("\\s*,\\s*"));
                    RadioButton estado = dialog.findViewById(rbGroupEstado.getCheckedRadioButtonId());
                    String correo=txtCorreo.getEditText().getText().toString();
                    String password=txtPassword.getEditText().getText().toString();
                    if(validateText(correo,password)){
                        String codigo= correo.substring(0,correo.indexOf("@"));
                        Usuario usuario=new Usuario(codigo,password,correo,"A",true,roles);
                        service.save(usuario);
                        Utils.messageConfirmation (getContext(),"Usuario y credenciales,guardado con éxito.");
                        dialog.dismiss();
                    }
                } catch (Exception ex) {
                    Utils.messageError(getContext(),"Error usuario:["+ex.getMessage()+"]");
                    Log.e("FRAGMENTO USUARIO",ex.getMessage());
                }
            }
        },2000);


    }

    @Optional
    @OnClick(R.id.bnCancelar)
    void submitCancel(){
        dialog.dismiss();
    }

    //endregion




    //region METODOS ADICIONAL
    private boolean validateText(String correo, String password) {
        boolean isCorreo=true;
        boolean isPassword=true;
        txtCorreo.setErrorEnabled(false);
        txtPassword.setErrorEnabled(false);
        if(correo.isEmpty()){
            txtCorreo.setError("Ingrese Correo");
            isCorreo=false;
        }else if(!Utils.validateEmail(correo)){
            txtCorreo.setError("Correo Inválido");
            isCorreo=false;
        }
        if(password.isEmpty()){
            txtPassword.setError("Ingrese contraseña");
            isPassword=false;
        }else if(password.length()<6){
            txtPassword.setError("Mínimo 6 caracteres");
            isPassword=false;
        }
        return (isCorreo && isPassword);
    }
    public void initData(){
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeUsuario);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioList=new ArrayList<Usuario>();
                for(DataSnapshot data : snapshot.getChildren()){
                    Usuario usuario =data.getValue(Usuario.class);
                    usuario.setCodigo(data.getKey());
                    usuarioList.add(usuario);
                }
                usuarioAdapter.setUsuarioList(usuarioList);
                usuarioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO USUARIO", "buscar usuarios", error.toException());
                Utils.messageError(getContext(),"Error:"+error.getMessage());
            }
        });
    }
    public void editar(Usuario usuario){
        try{
            service.save(usuario);
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error["+ex.getMessage()+"]");
        }
    }
    public void eliminar(Usuario usuario){
        try{
            service.delete(usuario.getCodigo());
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error["+ex.getMessage()+"]");
        }
    }
    //endregion

}