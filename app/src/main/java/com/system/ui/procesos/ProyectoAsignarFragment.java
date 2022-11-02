package com.system.ui.procesos;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Util;
import com.system.R;
import com.system.models.Fase;
import com.system.models.MaterialSelect;
import com.system.models.Persona;
import com.system.models.Producto;
import com.system.models.Proyecto;
import com.system.services.IProyectoService;
import com.system.services.ProyectoService;
import com.system.ui.mantenimientos.ProductoAdapter;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProyectoAsignarFragment extends Fragment {

    private ArrayAdapter<Persona> adapterPersona;

    private DatabaseReference dbReference;
    Proyecto proyecto;
    private Unbinder unbinder;
    private IProyectoService service;
    List<Producto>productos;
    @Nullable @BindView(R.id.txtNumProyecto) TextView txtNumProyecto;
    @Nullable @BindView(R.id.txtNombreProyecto) TextView txtNombreProyecto;
    @Nullable @BindView(R.id.txtFechaRegistro) TextView txtFechaRegistro;
    @Nullable @BindView(R.id.txtCliente) TextView txtCliente;
    @BindView(R.id.lvTrabajadores) ListView lvTrabajadores;
    @BindView(R.id.recyclerMaterial) RecyclerView recyclerMaterial;
    private MaterialAdapter materialAdapter;
    @BindView(R.id.bnGuardar) ProgressBar bnGuardar;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_proyecto_asignar, container, false);
        unbinder= ButterKnife.bind(this, root);
        getParentFragmentManager().setFragmentResultListener("proyectoKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
              proyecto= result.getParcelable("proyecto");
              txtNombreProyecto.setText(txtNombreProyecto.getText()+" "+proyecto.getNombre());
              txtCliente.setText(proyecto.getCliente().getApellidos()+" "+proyecto.getCliente().getNombres());
              txtNumProyecto.setText(proyecto.getNumero());
              txtFechaRegistro.setText(proyecto.getFechaRegistro().substring(0,10));

              List<Persona> personas=proyecto.getTrabajadores()!=null?
                      proyecto.getTrabajadores().values().stream().collect(Collectors.toList()):
                      new ArrayList<>();
              //ADAPTER CONFIGURATION
              adapterPersona.addAll(personas);

              Map<String,MaterialSelect>materiales= proyecto.getMateriales();;
              if(proyecto.getMateriales()!=null){
                  for(String key: materiales.keySet()){
                      MaterialSelect material=materiales.get(key);
                      productos.add(new Producto(
                              key,
                              material.getMaterial(),
                              null,
                              null,
                              0,
                              0,
                              material.getOcupado())
                      );
                  }
              }

            }
        });
        initData();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productos= new ArrayList<>();
        service=new ProyectoService();
        adapterPersona=new ArrayAdapter<Persona>(getContext(), android.R.layout.simple_list_item_multiple_choice);
        lvTrabajadores.setAdapter(adapterPersona);
        materialAdapter=new MaterialAdapter(getContext());
        recyclerMaterial.setHasFixedSize(true);
        recyclerMaterial.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerMaterial.setAdapter(materialAdapter);

    }

    //region METODOS ONCLICK
    @OnClick(R.id.bnGuardar)
    void submitGuardar(){
        //CAMBIAR IMAGEN DEL PROGRESS_BAR
        Rect bounds = bnGuardar.getIndeterminateDrawable().getBounds();
        bnGuardar.setIndeterminateDrawable(getActivity().getDrawable(R.drawable.loading));
        bnGuardar.getIndeterminateDrawable().setBounds(bounds);

        List<Producto>materialesSelect=materialAdapter.getSelectAllMaterial();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int contT=lvTrabajadores.getCheckedItemCount();
                    if(contT >0 && materialesSelect.size()>0){
                        service.deleteAsignaciones(proyecto.getCodigo()
                        );
                        for (int i=0; i< lvTrabajadores.getCount();i++){
                            if(lvTrabajadores.isItemChecked(i)){
                                Persona persona= (Persona) lvTrabajadores.getItemAtPosition(i);
                                service.saveTrabajador(proyecto.getCodigo(),persona);
                            }
                        }
                        for (Producto producto: materialesSelect){
                            MaterialSelect material=new MaterialSelect(producto.getNombre(),producto.getOcupado());
                            int valorOcupado=producto.getOcupado()-producto.getOldOcupado();
                            service.saveMaterial(proyecto.getCodigo(), producto.getCodigo(),valorOcupado,material);
                        }
                        Utils.messageConfirmation(getContext(),"Asignación, guardado con éxito");
                    }else{
                        Utils.messageError(getContext(),"Debe seleccionar información trabajadores/materiales");
                    }

                    bnGuardar.setIndeterminateDrawable(getActivity().getDrawable(R.drawable.ic_save_24));
                    bnGuardar.getIndeterminateDrawable().setBounds(bounds);
                }catch (Exception ex){
                    Utils.messageError(getContext(),"Fragmento asignación:"+ex.getMessage());
                }

            }
        },2000);

    }
    //endregion

    //region METODOS ADICIONAL
    private void initData() {
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreePersona);
        Query q = dbReference.orderByChild("trabajador").equalTo(true);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int total=lvTrabajadores.getCount();
                    for(DataSnapshot data : snapshot.getChildren()){
                        Persona persona=data.getValue(Persona.class);
                        if(persona.isEstado()){
                            boolean exit=false;
                            if(total>0)
                                for(int i=0;i<lvTrabajadores.getCount();i++){
                                    Persona p=(Persona) lvTrabajadores.getItemAtPosition(i);
                                    if(persona.getCodigo().equals(p.getCodigo())){
                                        exit=true;
                                        lvTrabajadores.setItemChecked(i,exit);
                                        break;
                                    }
                                }
                            if(!exit)
                                adapterPersona.add(persona);
                        }
                    }
                    adapterPersona.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO PERSONA", "Fragmento asignar proyecto", error.toException());
                Utils.messageError(getContext(),"Fragmento asignar proyecto lista persona:"+error.getMessage());
            }
        });

        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProducto);
        Query q2 =dbReference
                .orderByChild("disponible")
                .startAfter(0);
        q2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot data : snapshot.getChildren()){
                        Producto producto=data.getValue(Producto.class);
                        boolean exit=false;
                        int index=0;
                        for(int x=0; x<productos.size();x++){
                            Producto p=productos.get(x);
                            if(p.getCodigo().equals(producto.getCodigo())){
                                producto.setOcupado(p.getOcupado());
                                producto.setOldOcupado(p.getOcupado());
                                exit=true;
                                index=x;
                                break;
                             }
                        }
                        if(!exit){
                            producto.setOcupado(0);
                            producto.setOldOcupado(0);
                            productos.add(producto);
                        }else{
                            productos.set(index,producto);
                        }


                    }
                    materialAdapter.setProductoList(productos);
                    materialAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO PERSONA", "Fragmento asignar proyecto", error.toException());
                Utils.messageError(getContext(),"Fragmento asignar proyecto lista persona:"+error.getMessage());
            }
        });

    }

    //endregion
}