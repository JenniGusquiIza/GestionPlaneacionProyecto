package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.system.R;
import com.system.models.Fase;
import com.system.models.Grupo;
import com.system.models.Producto;
import com.system.services.FaseService;
import com.system.services.IFaseService;
import com.system.services.IProductoService;
import com.system.services.ProductoService;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class ProductoFragment extends Fragment {

    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private Unbinder unbinder,unbinderDialog;
    private DatabaseReference dbReference;
    private List<Producto> productoList;
    private List<Grupo> grupoList;
    private ArrayAdapter<Grupo> grupoArrayAdapter;
    private  Grupo grupo;
    private IProductoService service;
    @Nullable @BindView(R.id.recyclerProducto) RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    //DIALOGO PRODUCTO
    @Nullable @BindView(R.id.cbGrupo) TextInputLayout txtGrupo;
    @Nullable @BindView(R.id.txtDescripcion) TextInputLayout txtDescripcion;
    @Nullable @BindView(R.id.txtTotal) TextInputLayout txtTotal;
    @Nullable @BindView(R.id.rbGroupEstado) RadioGroup rbGroupEstado;
    @Nullable @BindView(R.id.bnGuardar) Button bnGuardar;
    @Nullable @BindView(R.id.bnCancelar) Button bnCancelar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_producto, container, false);
        unbinder= ButterKnife.bind(this, root);
        productoAdapter=new ProductoAdapter(getContext(),this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(productoAdapter);
        initData();
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        service= new ProductoService();
        grupoArrayAdapter = new ArrayAdapter<Grupo>(getContext(), android.R.layout.simple_spinner_dropdown_item); //Adaptamos el Layout dropdown_tipo

    }

    @Optional
    @OnClick(R.id.fbAdd)
    void showtDialog(){
        builder = new AlertDialog.Builder(getContext()); //asignacion de instancia de Alert.Builder
        LayoutInflater inflater=getLayoutInflater();
        View dialogProducto= inflater.inflate(R.layout.dialog_producto,null);
        unbinderDialog=ButterKnife.bind(this, dialogProducto);
        AutoCompleteTextView autoCompleteTextView= (AutoCompleteTextView) txtGrupo.getEditText();
        autoCompleteTextView.setAdapter(grupoArrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                grupo= (Grupo) parent.getItemAtPosition(position);
            }
        });

        builder.setView(dialogProducto); //agregamos el dialogo
        builder.setCancelable(true); //configuramos para que se pueda quitar
        dialog=builder.create();
        dialog.show();//abrimos el dialog
    }
    @Optional
    @OnClick(R.id.bnGuardar)
    void buttonSave(){
        try {
            long numero=System.currentTimeMillis();
            RadioButton estado = dialog.findViewById(rbGroupEstado.getCheckedRadioButtonId());
            String descripcion=txtDescripcion.getEditText().getText().toString();
            String total=txtTotal.getEditText().getText().toString();
            if(validateText(descripcion,total)){

                Producto producto =new Producto(
                        "PR-"+numero,
                        descripcion,
                        grupo.getDescripcion(),
                        ""+estado.getText().charAt(0),
                        Integer.parseInt(total),
                        Integer.parseInt(total),
                        0
                );
                service.save(producto);
                Utils.messageConfirmation (getContext(),"Producto,guardado con éxito.");
                dialog.dismiss();
            }

        } catch (Exception ex) {
            Utils.messageError(getContext(),"Error fase:["+ex.getMessage()+"]");
            Log.e("FRAGMENTO PRODUCTO",ex.getMessage());
        }
    }
    @Optional
    @OnClick(R.id.bnCancelar)
    void buttonCancel(){
        dialog.dismiss();
    }


    //region METODO ADICIONAL
    private boolean validateText(String descripcion,String total) {
        boolean isDescripcion=true;
        boolean isTotal=true;
        txtDescripcion.setErrorEnabled(false);
        txtTotal.setErrorEnabled(false);
        if(descripcion.isEmpty()){
            txtDescripcion.setError("ingresar descripción");
            isDescripcion=false;
        }
        if(total.isEmpty()){
            txtTotal.setError("ingresar total material");
            isTotal=false;
        }
        return  isDescripcion && isTotal;
    }
    public void initData(){
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProducto);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productoList=new ArrayList<Producto>();
                for(DataSnapshot data : snapshot.getChildren()){
                    Producto producto=data.getValue(Producto.class);
                    producto.setCodigo(data.getKey());
                    productoList.add(producto);
                }
                productoAdapter.setProductoList(productoList);
                productoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO PRODUCTO", "buscar productos", error.toException());
                Utils.messageError(getContext(),"Error:"+error.getMessage());
            }
        });

        dbReference=FirebaseDatabase.getInstance().getReference().child(Routes.TreeGrupo);
        Query q = dbReference.orderByChild("estado").equalTo("A");
        q.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                grupoList=new ArrayList<Grupo>();
                for(DataSnapshot data : snapshot.getChildren()){
                    Grupo grupo=data.getValue(Grupo.class);
                    grupoList.add(grupo);
                }
                grupoArrayAdapter.addAll(grupoList);
                grupoArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO GRUPO", "buscar grupos", error.toException());
                Utils.messageError(getContext(),"Error:"+error.getMessage());
            }
        });
    }

    public void editar(Producto producto){
        try{
            service.edit(producto);
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error["+ex.getMessage()+"]");
        }
    }

    public void eliminar(Producto producto){
        try{
            service.delete(producto.getCodigo());
        }catch (Exception ex){
            Utils.messageError(getContext(),"Error["+ex.getMessage()+"]");
        }
    }
    //endregion
}