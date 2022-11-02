package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.system.R;
import com.system.models.Persona;
import com.system.ui.utils.Utils;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ClienteFragment fragment;
    private List<Persona> personaList;
    private List<Persona> personaFilterList;
    private  Persona persona;

    public ClienteAdapter(ClienteFragment fragment,List<Persona>personaList) {
        this.fragment=fragment;
        this.context=fragment.getContext();
        this.personaList=personaList;
        this.personaFilterList=personaList;
   }
    public void setPersonaList(List<Persona> personaList) {
        this.personaList = personaList;
        this.personaFilterList=personaList;
    }

    @NonNull
    @Override
    public ClienteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list01, parent, false);
        return new ClienteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (personaFilterList != null && personaFilterList.size() > 0) {
            persona= personaList.get(position);
            String estado= persona.isEstado()?"ACTIVO":"INACTIVO";
            holder.itemCodigo.setText(persona.getCodigo()+"     "+estado);

            holder.itemDescripcion.setText(persona.getTipo_identidad().equals("C")?
                    persona.getApellidos()+" "+persona.getNombres():persona.getNombres());
        }
    }

    @Override
    public int getItemCount() {
        return personaFilterList!=null? personaFilterList.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter =new Filter() {
        @Override
        protected FilterResults performFiltering(@NonNull CharSequence constraint) {
            List<Persona> filteredLis=new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredLis.addAll(personaList);
            }else{
                for (Persona persona: personaList) {
                    if(persona.getApellidos().toUpperCase().contains(constraint.toString().toUpperCase())){
                        filteredLis.add(persona);
                    }

                }
            }
            FilterResults filterResult= new FilterResults();

            filterResult.values=filteredLis;
            filterResult.count=filteredLis.size();
            return  filterResult;
        }

        @Override
        protected void publishResults(CharSequence constraint, @NonNull FilterResults results) {
            personaFilterList=results.values!=null? (List<Persona>) results.values:null;
            notifyDataSetChanged();

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder  implements  View.OnClickListener {
        @BindView(R.id.imageView) CircularImageView itemImagen;
        @BindView(R.id.txtCodigo) TextView itemCodigo;
        @BindView(R.id.txtDescripcion)  TextView itemDescripcion;
        @BindView(R.id.bnEditar) ImageButton bnEditar;
        @BindView(R.id.bnEliminar) ImageButton bnEliminar;
        //DIALOGO PERSONA
        private AlertDialog dialog;
        private AlertDialog.Builder builder;
        private TextView txtTitulo;
        private TextInputLayout txtNombre,txtApellido,txtFechaNac,txtCorreo,txtCedula,txtTelefono;
        private RadioGroup rbGroupValidacion, rbGroupEstado;
        private Button bnGuardar,bnCancelar;
        private LinearLayout llApellidos;
        private LinearLayout llFechaNac;
        public ViewHolder(View itemView) {
            super(itemView);
            builder = new AlertDialog.Builder(itemView.getContext()); //asignacion de instancia de Alert.Builder
            ButterKnife.bind(this, itemView);
        }

        //region METODOS ADICIONAL
        //endregion

        //region METODOS ON_CLICK
        @OnClick(R.id.bnEditar)
        void  submitEditar(){
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_persona, null);
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
                case R.id.bnCancelar:
                    dialog.dismiss();
                    break;
                case R.id.bnGuardar:
                    String apellidos=txtApellido.getEditText().getText().toString();
                    String nombres=txtNombre.getEditText().getText().toString();
                    String fecha=txtFechaNac.getEditText().getText().toString();
                    String cedula=txtCedula.getEditText().getText().toString();
                    String correo=txtCorreo.getEditText().getText().toString();
                    String telefono=txtTelefono.getEditText().getText().toString();
                    RadioButton estado = dialog.findViewById(rbGroupEstado.getCheckedRadioButtonId());
                    if(validateText(apellidos,nombres,fecha,correo,cedula,telefono)){
                        persona.setNombres(nombres);
                        persona.setApellidos(apellidos);
                        persona.setFecha_nac(fecha);
                        persona.setTipo_identidad(""+txtCedula.getEditText().getText().toString().charAt(0));
                        persona.setCedula(cedula);
                        persona.setCorreo(correo);
                        persona.setTelefono(telefono);
                        persona.setEstado(estado.getText().charAt(0)=='A');
                        fragment.editar(persona);
                        dialog.dismiss();
                    }
                    break;
                case R.id.bnNo: // Layout dialog_delete
                    dialog.dismiss();
                    break;
                case R.id.bnSi: // Layout dialog_delete
                    fragment.eliminar(persona);
                    dialog.dismiss();
                    break;
            }
        }
        //endregion

        //region METODOS ADICIONAL

        private boolean validateText(@NonNull String apellidos, String nombres, String fechaNac, String correo, String cedula,String telefono){
            int lenght=cedula.length();
            boolean isCorreo=true;
            boolean isApellido=true;
            boolean isNombre=true;
            boolean isFechaNac=true;
            boolean isCedula=true;
            boolean isTelefono=true;
            txtNombre.setErrorEnabled(false);
            txtApellido.setErrorEnabled(false);
            txtFechaNac.setErrorEnabled(false);
            txtCorreo.setErrorEnabled(false);
            txtCedula.setErrorEnabled(false);
            txtTelefono.setErrorEnabled(false);

            if(apellidos.isEmpty()){
                txtApellido.setError("ingresar apellidos");
                isApellido=false;
            }

            if(nombres.isEmpty()){
                txtApellido.setError("ingresar nombres");
                isNombre=false;
            }

            if(fechaNac.isEmpty()){
                txtFechaNac.setError("ingresar fecha nacimiento");
                isFechaNac=false;
            }else if(!Utils.validateDateString(fechaNac)){
                txtFechaNac.setError("fecha inválida dd/MM/yyyy");
                isFechaNac=false;
            }

            if(correo.isEmpty()){
                txtCorreo.setError("ingresar correo");
                isCorreo=false;
            }else if(!Utils.validateEmail(correo)){
                txtCorreo.setError("correo inválido");
                isCorreo=false;
            }

            if(cedula.isEmpty()){
                txtCedula.setError("ingresar cedula");
                isCorreo=false;
            }else{
                if(lenght==10){
                    boolean validate=Utils.validateCedula(cedula);
                    if(!validate){
                        txtCedula.setError("cédula inválida");
                        isCedula=false;
                    }
                }else if(lenght==13){
                    String validate=Utils.validateRUC(cedula);
                    if(!validate.isEmpty()){
                        txtCedula.setError("ruc inválida");
                        isCedula=false;
                    }else{
                        isCedula=true;
                    }
                }else{
                    txtCedula.setError("cédula/ruc inválida");
                    isCedula=false;
                }
            }
            if(telefono.isEmpty()){
                txtTelefono.setError("ingresar teléfono");
                isTelefono=false;
            }
            if(lenght==10){
                return  (isApellido && isNombre && isCedula && isCorreo && isTelefono && isFechaNac);
            }if(lenght==13){
                return  (isNombre && isCedula && isCorreo && isTelefono);
            }else{
                return false;
            }

        }

        private void View_Update(@NonNull View view) {
            persona= personaList.get(getAdapterPosition());
            String tipo=persona.getTipo_identidad();
            llApellidos=view.findViewById(R.id.llApellidos);
            llFechaNac=view.findViewById(R.id.llFechaNac);
            txtTitulo=view.findViewById(R.id.txtTitulo);
            txtTitulo.setText("CLIENTE");
            txtNombre=view.findViewById(R.id.txtNombre);
            txtApellido=view.findViewById(R.id.txtApellido);
            txtFechaNac=view.findViewById(R.id.txtFechaNac);
            txtCedula=view.findViewById(R.id.txtCedula);
            txtTelefono=view.findViewById(R.id.txtTelefono);
            txtCorreo=view.findViewById(R.id.txtCorreo);
            rbGroupEstado=view.findViewById(R.id.rbGroupEstado);
            rbGroupValidacion=view.findViewById(R.id.rbGroupValidacion);
            bnGuardar=view.findViewById(R.id.bnGuardar);
            bnCancelar=view.findViewById(R.id.bnCancelar);
            txtNombre.getEditText().setText(persona.getNombres());
            txtNombre.setHint(tipo.equals("C")?"Nombres":"Razon social");
            txtApellido.getEditText().setText(persona.getApellidos());
            llApellidos.setVisibility(tipo.equals("C")?View.VISIBLE:View.GONE);
            llFechaNac.setVisibility(tipo.equals("C")?View.VISIBLE:View.GONE);
            txtFechaNac.getEditText().setText(persona.getFecha_nac());
            txtCedula.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(tipo.equals("C")?10:13)});
            txtCedula.getEditText().setText(persona.getCedula());
            txtCedula.setHint(tipo.equals("C")?"Cédula":"RUC");
            txtTelefono.getEditText().setText(persona.getTelefono());
            txtCorreo.getEditText().setText(persona.getCorreo());
            rbGroupEstado.check(persona.isEstado()?R.id.rbActivo:R.id.rbInactivo);
            rbGroupValidacion.check(tipo.equals("C")?R.id.rbNatural:R.id.rbRazon);
            rbGroupValidacion.setEnabled(false);
            bnGuardar.setOnClickListener(this);
            bnCancelar.setOnClickListener(this);
            rbGroupValidacion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    txtCedula.getEditText().setText("");
                    if(i==R.id.rbRazon){
                        llApellidos.setVisibility(View.GONE);
                        llFechaNac.setVisibility(View.GONE);
                        txtNombre.setHint("Razón Social");
                        txtCedula.setHint("RUC");
                        txtCedula.setCounterMaxLength(13);
                        txtCedula.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
                    }else  if(i==R.id.rbNatural){
                        llApellidos.setVisibility(View.VISIBLE);
                        llFechaNac.setVisibility(View.VISIBLE);
                        txtNombre.setHint("Nombres");
                        txtCedula.setHint("Cédula");
                        txtCedula.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    }
                }
            });

        }

        private void Delete(@NonNull View view) {
            persona= personaList.get(getAdapterPosition());
            TextView txtMensaje=  view.findViewById(R.id.txtMensaje);
            txtMensaje.setText("Cliente "+persona.getApellidos()+" "+persona.getNombres().charAt(0)+"...");
            Button bnSi = view.findViewById(R.id.bnSi);
            Button bnNo = view.findViewById(R.id.bnNo);
            bnSi.setOnClickListener(this);
            bnNo.setOnClickListener(this);
        }
        //endregion
    }
}
