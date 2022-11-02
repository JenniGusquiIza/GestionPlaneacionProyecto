package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.system.models.Fase;
import com.system.models.Persona;
import com.system.services.FaseService;
import com.system.services.IFaseService;
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

public class FaseFragment extends Fragment {

    long idFase;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private Unbinder unbinder,unbinderDialog;
    private DatabaseReference dbReference;
    private  List<Fase> faseList;
    private IFaseService service;
    @Nullable @BindView(R.id.recyclerFase) RecyclerView recyclerView;
    private FaseAdapter faseAdapter;
    //DIALOGO FASE
    @Nullable @BindView(R.id.txtDescripcion) TextInputLayout txtDescripcion;
    @Nullable @BindView(R.id.rbGroupEstado) RadioGroup rbGroupEstado;
    @Nullable @BindView(R.id.bnGuardar) Button bnGuardar;
    @Nullable @BindView(R.id.bnCancelar) Button bnCancelar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_fase, container, false);
        unbinder= ButterKnife.bind(this, root);
        faseAdapter=new FaseAdapter(getContext(),FaseFragment.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(faseAdapter);
        initData();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        service= new FaseService();
    }

    @Optional
    @OnClick(R.id.fbAdd)
    void showtDialog(){
        builder = new AlertDialog.Builder(getContext()); //asignacion de instancia de Alert.Builder
        LayoutInflater inflater=getLayoutInflater();
        View dialogFase= inflater.inflate(R.layout.dialog_fase,null);
        unbinderDialog=ButterKnife.bind(this, dialogFase);
        builder.setView(dialogFase); //agregamos el dialogo
        builder.setCancelable(true); //configuramos para que se pueda quitar
        dialog=builder.create();
        dialog.show();//abrimos el dialog
    }

    @Optional
    @OnClick(R.id.bnGuardar)
    void buttonSave(){
        try {
            RadioButton estado = dialog.findViewById(rbGroupEstado.getCheckedRadioButtonId());
            String descripcion=txtDescripcion.getEditText().getText().toString();
            boolean validate=validate(descripcion);
            if(validate){
                long numero=System.currentTimeMillis();
                Fase fase=new Fase(
                        "F-"+numero,
                        descripcion.toUpperCase(),
                        estado.getText().charAt(0)=='A'
                );
                service.save(fase);
                Utils.messageConfirmation (getContext(),"Fase,guardado con éxito.");
                dialog.dismiss();
            }
        } catch (Exception ex) {
            Utils.messageError(getContext(),"Error fase:["+ex.getMessage()+"]");
            Log.e("FRAGMENTO FASE",ex.getMessage());
        }
    }

    @Optional
    @OnClick(R.id.bnCancelar)
    void buttonCancel(){
        dialog.dismiss();
    }


    private boolean validate(String descripcion) {
        boolean isDescripcion=true;
        txtDescripcion.setErrorEnabled(false);
        if(descripcion.isEmpty()){
            txtDescripcion.setError("ingresar descripción");
            isDescripcion=false;
        }
        return  isDescripcion;
    }

    public void initData(){
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeFase);
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    faseList=new ArrayList<Fase>();
                    for(DataSnapshot data : snapshot.getChildren()){
                        Fase fase=data.getValue(Fase.class);
                        fase.setCodigo(data.getKey());
                        faseList.add(fase);
                    }
                    faseAdapter.setFaseList(faseList);
                    faseAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SERVICIO PERSONA", "buscar personas", error.toException());
                    Utils.messageError(getContext(),"Error:"+error.getMessage());
                }
            });
    }

    public void editar(Fase fase){
        try{
            service.edit(fase);
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error["+ex.getMessage()+"]");
        }
    }
    public void eliminar(Fase fase){
        try{
            service.delete(fase.getCodigo());
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error["+ex.getMessage()+"]");
        }
    }

}