package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.R;
import com.system.models.Fase;
import com.system.models.Grupo;
import com.system.services.FaseService;
import com.system.services.GrupoService;
import com.system.services.IFaseService;
import com.system.services.IGrupoService;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class GrupoFragment extends Fragment {

    long idFase;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private Unbinder unbinder,unbinderDialog;
    private DatabaseReference dbReference;
    private  List<Grupo> grupoList;
    private IGrupoService service;
    @Nullable @BindView(R.id.recyclerGrupo) RecyclerView recyclerView;
    private GrupoAdapter grupoAdapter;
    //DIALOGO FASE
    @Nullable @BindView(R.id.txtDescripcion) TextInputLayout txtDescripcion;
    @Nullable @BindView(R.id.rbGroupEstado) RadioGroup rbGroupEstado;
    @Nullable @BindView(R.id.bnGuardar) Button bnGuardar;
    @Nullable @BindView(R.id.bnCancelar) Button bnCancelar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_grupo, container, false);
        unbinder= ButterKnife.bind(this, root);
        grupoAdapter=new GrupoAdapter(getContext(), this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(grupoAdapter);
        initData();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        service= new GrupoService();
    }

    @Optional
    @OnClick(R.id.fbAdd)
    void showtDialog(){
        builder = new AlertDialog.Builder(getContext()); //asignacion de instancia de Alert.Builder
        LayoutInflater inflater=getLayoutInflater();
        View dialogFase= inflater.inflate(R.layout.dialog_grupo,null);
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
                Grupo fase=new Grupo(
                        "F",
                        descripcion.toUpperCase(),
                        ""+estado.getText().charAt(0)
                );
                service.save(fase);
                Utils.messageConfirmation (getContext(),"Grupo,guardado con éxito.");
                dialog.dismiss();
            }
        } catch (Exception ex) {
            Utils.messageError(getContext(),"Error grupo:["+ex.getMessage()+"]");
            Log.e("FRAGMENTO GRUPO",ex.getMessage());
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
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeGrupo);
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    grupoList=new ArrayList<>();
                    for(DataSnapshot data : snapshot.getChildren()){
                        Grupo grupo=data.getValue(Grupo.class);
                        grupo.setCodigo(data.getKey());
                        grupoList.add(grupo);
                    }
                    grupoAdapter.setGrupoList(grupoList);
                    grupoAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SERVICIO GRUPO", "buscar grupo", error.toException());
                    Utils.messageError(getContext(),"Error:"+error.getMessage());
                }
            });
    }

    public void editar(Grupo grupo){
        try{
            service.edit(grupo);
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error["+ex.getMessage()+"]");
        }
    }
    public void eliminar(Grupo grupo){
        try{
            service.delete(grupo.getCodigo());
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error["+ex.getMessage()+"]");
        }
    }

}