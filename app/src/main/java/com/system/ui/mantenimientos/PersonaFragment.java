package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class PersonaFragment extends Fragment {

    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private Unbinder unbinder, unbinderDialog;
    private PersonaService service;
    private DatabaseReference dbReference;
    private static List<Persona> personaList;
    //Fragmento
    @Nullable @BindView(R.id.recyclerPersona) RecyclerView personaRecycler;
    private PersonaAdapter personaAdapter;

    //Layout dialog_persona
    @Nullable @BindView(R.id.txtNombre) TextInputLayout txtNombres;
    @Nullable @BindView(R.id.txtApellido) TextInputLayout txtApellidos;
    @Nullable @BindView(R.id.txtFechaNac) TextInputLayout txtFechaNac;
    @Nullable @BindView(R.id.txtCedula) TextInputLayout txtCedula;
    @Nullable @BindView(R.id.txtCorreo) TextInputLayout txtCorreo;
    @Nullable @BindView(R.id.rbGroupEstado) RadioGroup rbGroupEstado;
    @Nullable @BindView(R.id.bnGuardar) Button bnGuardar;
    @Nullable @BindView(R.id.bnCancelar) Button bnCancelar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_persona, container, false);
        unbinder= ButterKnife.bind(this, root);
        service= new PersonaService();
        personaAdapter= new PersonaAdapter(getContext());
        personaRecycler.setHasFixedSize(true);
        personaRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        personaRecycler.setAdapter(personaAdapter);
        initData();
        setHasOptionsMenu(true);
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
                return true;
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

        builder.setView(dialogPersona); //agregamos el dialogo
        builder.setCancelable(true); //configuramos para que se pueda quitar
        dialog=builder.create();
        dialog.show();//abrimos el dialog
    }

    @Optional
    @OnClick(R.id.bnGuardar)
    void submitSave(){
        new Thread(){
            public void run(){
                Looper.prepare();//Call looper.prepare()
                try {
                    RadioButton estado = dialog.findViewById(rbGroupEstado.getCheckedRadioButtonId());
                    String cedula=txtCedula.getEditText().getText().toString();
                    String nombres= txtNombres.getEditText().getText().toString().trim();
                    String apellidos=txtApellidos.getEditText().getText().toString().trim();
                    int indexNombre = nombres != null ? nombres.indexOf(" ") : -1;
                    int indexApellido = apellidos!= null ? apellidos.indexOf(" ") : -1;
                    String _nombre = indexNombre > 0 ?""+nombres.charAt(0) + nombres.charAt(indexNombre + 1) :""+ nombres.charAt(0);
                    String _apellido = indexApellido > 0 ? ""+apellidos.charAt(0) + apellidos.charAt(indexApellido + 1) : ""+apellidos.charAt(0);
                    String codigo=_apellido+_nombre+cedula;
                    Persona persona=new Persona(
                            codigo,
                            nombres,
                            apellidos,
                            estado.getText().charAt(0)=='A',
                            txtFechaNac.getEditText().getText().toString(),
                            ""+txtCedula.getEditText().getText().toString().charAt(0),
                            txtCedula.getEditText().getText().toString(),
                            txtCorreo.getEditText().getText().toString(),
                            "",
                            false,
                            true
                    );
                    service.save(persona);
                    Utils.messageConfirmation (getContext(),"Persona,guardado con exito.");
                } catch (Exception ex) {
                    Utils.messageError(getContext(),"Error persona:["+ex.getMessage()+"]");
                    Log.e("FRAGMENTO PERSONA",ex.getMessage());
                }
                Looper.loop();
            }
        }.start();
        dialog.dismiss();
    }

    @Optional
    @OnClick(R.id.bnCancelar)
    void submitCancel(){
        dialog.dismiss();
    }
    //endregion

    //region METODOS ADICIONAL
    public void initData(){
        if(personaList ==null){
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreePersona);
            dbReference.child("trabajadores").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    personaList=new ArrayList<Persona>();
                    for(DataSnapshot data : snapshot.getChildren()){
                        Persona persona=data.getValue(Persona.class);
                        personaList.add(persona);
                    }
                    personaAdapter.setPersonaList(personaList);
                    personaAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SERVICIO PERSONA", "buscar personas", error.toException());
                }
            });

        }


    }

    //endregion
}