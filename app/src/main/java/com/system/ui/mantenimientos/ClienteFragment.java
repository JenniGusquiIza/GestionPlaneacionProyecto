package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.system.R;
import com.system.models.Persona;
import com.system.services.PersonaService;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class ClienteFragment extends Fragment {

    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private Unbinder unbinder, unbinderDialog;
    private PersonaService service;
    private DatabaseReference dbReference;
    private static List<Persona> personaList;
    //Fragmento
    @Nullable @BindView(R.id.recyclerCliente) RecyclerView personaRecycler;
    private SwipeRefreshLayout refresh;
    private ClienteAdapter personaAdapter;

    //Layout dialog_cliente

    @Nullable @BindView(R.id.llApellidos) LinearLayout llApellidos;
    @Nullable @BindView(R.id.llFechaNac) LinearLayout llFechaNac;
    @Nullable @BindView(R.id.txtTitulo) TextView txtTitulo;
    @Nullable @BindView(R.id.txtNombre) TextInputLayout txtNombres;
    @Nullable @BindView(R.id.txtTelefono) TextInputLayout txtTelefono;
    @Nullable @BindView(R.id.txtApellido) TextInputLayout txtApellidos;
    @Nullable @BindView(R.id.txtFechaNac) TextInputLayout txtFechaNac;
    @Nullable @BindView(R.id.txtCedula) TextInputLayout txtCedula;
    @Nullable @BindView(R.id.txtCorreo) TextInputLayout txtCorreo;
    @Nullable @BindView(R.id.rbGroupEstado) RadioGroup rbGroupEstado;
    @Nullable @BindView(R.id.rbGroupValidacion) RadioGroup rbGroupValidacion;
    @Nullable @BindView(R.id.bnGuardar) Button bnGuardar;
    @Nullable @BindView(R.id.bnCancelar) Button bnCancelar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_cliente, container, false);
        unbinder= ButterKnife.bind(this, root);
        service= new PersonaService();
        personaAdapter= new ClienteAdapter(this,personaList);
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.layer));
        personaRecycler.addItemDecoration(dividerItemDecoration);
        personaRecycler.setHasFixedSize(true);
        personaRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        personaRecycler.setAdapter(personaAdapter);
        initData();
        setHasOptionsMenu(true);
        refresh=(SwipeRefreshLayout) root.findViewById(R.id.rlCliente);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                personaList=null;
                initData();
                Utils.messageConfirmation(getContext(),"actualizando");
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //region METODOS OVERRIDE
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.navigation, menu);
        MenuItem item=menu.findItem(R.id.bnSearch);
        SearchView searchView=(SearchView) item.getActionView();
        searchView.setQueryHint("BUSCAR...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                personaAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    //endregion

    //region METODOS ON_CLICK
    @Optional
    @OnClick(R.id.fbAddPersona)
    void showtDialog(){
        builder = new AlertDialog.Builder(getContext()); //asignacion de instancia de Alert.Builder
        LayoutInflater inflater=getLayoutInflater();
        View dialogPersona= inflater.inflate(R.layout.dialog_persona,null);
        unbinderDialog=ButterKnife.bind(this, dialogPersona);
        txtTitulo.setText("CLIENTE");
        rbGroupValidacion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                txtCedula.getEditText().setText("");
                if(i==R.id.rbRazon){
                    llApellidos.setVisibility(View.GONE);
                    llFechaNac.setVisibility(View.GONE);
                    txtNombres.setHint("Razón Social");
                    txtCedula.setHint("RUC");
                    txtCedula.setCounterMaxLength(13);
                    txtCedula.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
                }else  if(i==R.id.rbNatural){
                    llApellidos.setVisibility(View.VISIBLE);
                    llFechaNac.setVisibility(View.VISIBLE);
                    txtNombres.setHint("Nombres");
                    txtCedula.setHint("Cédula");
                    txtCedula.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                }
            }
        });
        builder.setView(dialogPersona); //agregamos el dialogo
        builder.setCancelable(true); //configuramos para que se pueda quitar
        dialog=builder.create();
        dialog.show();//abrimos el dialog
    }

    @Optional
    @OnClick(R.id.bnGuardar)
    void submitSave(){
        try {
            RadioButton identidad= dialog.findViewById(rbGroupValidacion.getCheckedRadioButtonId());
            RadioButton estado = dialog.findViewById(rbGroupEstado.getCheckedRadioButtonId());
            String nombres= txtNombres.getEditText().getText().toString().trim();
            String apellidos=txtApellidos.getEditText().getText().toString().trim();
            String fecha=txtFechaNac.getEditText().getText().toString();
            String cedula=txtCedula.getEditText().getText().toString();
            String correo=txtCorreo.getEditText().getText().toString();
            String telefono=txtTelefono.getEditText().getText().toString();
            if(validateText(apellidos,nombres,fecha,correo,cedula,telefono)){
                int indexNombre = nombres != null ? nombres.indexOf(" ") : -1;

                int indexApellido = apellidos!= null ? apellidos.indexOf(" ") : -1;
                String _nombre = indexNombre > 0 ?""+nombres.charAt(0) + nombres.charAt(indexNombre + 1) :""+ nombres.charAt(0);
                String _apellido = indexApellido > 0 ?
                        ""+apellidos.charAt(0) + apellidos.charAt(indexApellido + 1) :
                        apellidos.trim().isEmpty()? "":""+apellidos.charAt(0);
                String codigo=_apellido+_nombre+cedula;
                Persona persona=new Persona(
                        codigo,
                        nombres,
                        apellidos,
                        estado.getText().charAt(0)=='A',
                        fecha,
                        ""+identidad.getText().charAt(0),
                        cedula,
                        correo,
                        telefono,
                        true,
                        false
                );
                service.save(persona);
                Utils.messageConfirmation (getContext(),"Cliente, guardado con exito.");
                clearText();
                dialog.dismiss();
            }
        } catch (Exception ex) {
            Utils.messageError(getContext(),"Error persona:["+ex.getMessage()+"]");
            Log.e("FRAGMENTO CLIENTE",ex.getMessage());
        }


    }

    @Optional
    @OnClick(R.id.bnCancelar)
    void submitCancel(){
        dialog.dismiss();
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
        txtNombres.setErrorEnabled(false);
        txtApellidos.setErrorEnabled(false);
        txtFechaNac.setErrorEnabled(false);
        txtCorreo.setErrorEnabled(false);
        txtCedula.setErrorEnabled(false);
        txtTelefono.setErrorEnabled(false);

        if(apellidos.isEmpty()){
            txtApellidos.setError("ingresar apellidos");
            isApellido=false;
        }

        if(nombres.isEmpty()){
            txtApellidos.setError("ingresar nombres");
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

    public void initData(){
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreePersona);
            Query q=dbReference.orderByChild("cliente").equalTo(true);
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    personaList=new ArrayList<Persona>();
                    for(DataSnapshot data : snapshot.getChildren()){
                        Persona persona=data.getValue(Persona.class);
                        personaList.add(persona);
                    }
                    personaAdapter.setPersonaList(personaList);
                    personaAdapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SERVICIO CLIENTE", "buscar personas", error.toException());
                }
            });

    }

    public void editar(Persona persona){
        try{
            service.save(persona);
            Utils.messageConfirmation(getContext(),"Cliente, guardado con éxito");
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error ["+ex.getMessage()+"]");
        }

    }

    public void eliminar(Persona persona){
        try{
            service.delete(persona.getCodigo());
            Utils.messageConfirmation(getContext(),"Cliente, eliminado con éxito");
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error ["+ex.getMessage()+"]");
        }
    }

    public void  clearText(){
        txtApellidos.getEditText().setText("");
        txtNombres.getEditText().setText("");
        txtCedula.getEditText().setText("");
        txtFechaNac.getEditText().setText("");
        txtCorreo.getEditText().setText("");
        txtTelefono.getEditText().setText("");
    }
    //endregion
}