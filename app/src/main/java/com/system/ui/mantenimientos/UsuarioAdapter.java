package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.system.R;
import com.system.models.Usuario;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder>{
    private Context context;
    private UsuarioFragment fragment;
    private List<Usuario> usuarioList;
    private Usuario usuario;

    public UsuarioAdapter(Context context,UsuarioFragment fragment) {
        this.context=context;
        this.fragment=fragment;

    }

    public void setUsuarioList(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    @NonNull
    @Override
    public UsuarioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list01, parent, false);
        return new UsuarioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.ViewHolder holder, int position) {
        if (usuarioList != null && usuarioList.size() > 0) {
            usuario= usuarioList.get(position);
            String estado= usuario.getEstado().equals("A")?"ACTIVO":"INACTIVO";
            holder.itemCodigo.setText(usuario.getCodigo()+"     "+estado);
            holder.itemDescripcion.setText(usuario.getCorreo());
        }
    }

    @Override
    public int getItemCount() {
        return usuarioList!=null? usuarioList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        @BindView(R.id.imageView) CircularImageView itemImagen;
        @BindView(R.id.txtCodigo) TextView itemCodigo;
        @BindView(R.id.txtDescripcion)  TextView itemDescripcion;
        @BindView(R.id.bnEditar)  ImageButton bnEditar;
        @BindView(R.id.bnEliminar) ImageButton bnEliminar;

        //DIALOGO Usuario
        private AlertDialog dialog;
        private AlertDialog.Builder builder;
        TextInputLayout txtPassword,txtCorreo,cbRoles;
        ChipGroup selectRol;
        RadioGroup rbGroupEstado;
        Button bnCancelar, bnGuardar;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            builder = new AlertDialog.Builder(itemView.getContext()); //asignacion de instancia de Alert.Builder
            ButterKnife.bind(this, itemView);
        }

        //region METODOS ON_CLICK
        @OnClick(R.id.bnEditar)
        void  submitEditar(){
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_usuario, null);
            View_Update(view);
            builder.setView(view); //agregamos el dialogo
            builder.setCancelable(false); //configuramos para que no se pueda quitar
            dialog = builder.create();
            dialog.show();//abrimos el dialog
        }

        @OnClick(R.id.bnEliminar)
        void  submitELiminar(){
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_delete, null);
            Delete(view);
            builder.setView(view); //agregamos el dialogo
            builder.setCancelable(false); //configuramos para que se no pueda quitar
            dialog = builder.create();
            dialog.show();
        }
        @Override
        public void onClick(@NonNull View view) {
            switch (view.getId()) {
                case R.id.bnCancelar:// Layout dialog_usuario
                    dialog.dismiss();
                    break;
                case R.id.bnGuardar: // Layout dialog_usuario
                    List<String>roles= new ArrayList<>();
                    for(int i=0;i<selectRol.getChildCount();i++){
                        Chip chip=(Chip) selectRol.getChildAt(i);
                        roles.add(chip.getText().toString());
                    }
                    RadioButton estado = dialog.findViewById(rbGroupEstado.getCheckedRadioButtonId());
                    String correo=txtCorreo.getEditText().getText().toString();
                    String password=txtPassword.getEditText().getText().toString();
                    if(validateText(correo,password)){
                        usuario.setCorreo(correo);
                        usuario.setPassword(password);
                        usuario.setRoles(roles);
                        usuario.setEstado(""+estado.getText().charAt(0));
                        fragment.editar(usuario);
                        dialog.dismiss();
                    }
                    break;
                case R.id.bnNo: // Layout dialog_delete
                    dialog.dismiss();
                    break;
                case R.id.bnSi: // Layout dialog_delete
                    fragment.eliminar(usuario);
                    dialog.dismiss();
                    break;
            }
        }

        //endregion

        //region METODOS ADICIONAL
        private void View_Update(@NonNull View view) {
            ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, Utils.Roles()); //Adaptamos el Layout dropdown_tipo
            usuario= usuarioList.get(getAdapterPosition());
            txtPassword=view.findViewById(R.id.txtPassword);
            txtCorreo=view.findViewById(R.id.txtCorreo);
            cbRoles=  view.findViewById(R.id.cbRoles);
            selectRol= view.findViewById(R.id.selectRol);
            AutoCompleteTextView autoCompleteTextView= (AutoCompleteTextView) cbRoles.getEditText();
            autoCompleteTextView.setAdapter(adapterRoles);
            rbGroupEstado= view.findViewById(R.id.rbGroupEstado);
            bnGuardar= view.findViewById(R.id.bnGuardar);
            bnCancelar= view.findViewById(R.id.bnCancelar);


            txtCorreo.getEditText().setText(usuario.getCorreo());
            txtPassword.getEditText().setText(usuario.getPassword());
            for (String rol:usuario.getRoles()){
                Chip chip= new Chip(context);
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
                        Chip chip= new Chip(context);
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
            bnGuardar.setOnClickListener(this);
            bnCancelar.setOnClickListener(this);
        }
        private void Delete(@NonNull View view) {
            usuario= usuarioList.get(getAdapterPosition());
            TextView txtMensaje=  view.findViewById(R.id.txtMensaje);
            txtMensaje.setText("Usuario "+usuario.getCodigo());
            Button bnSi = view.findViewById(R.id.bnSi);
            Button bnNo = view.findViewById(R.id.bnNo);
            bnSi.setOnClickListener(this);
            bnNo.setOnClickListener(this);
        }
        private boolean validateText(@NonNull String correo, String password) {
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

        //endregion
    }
}
