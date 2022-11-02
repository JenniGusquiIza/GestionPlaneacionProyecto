package com.system.ui.procesos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.R;
import com.system.models.Actividad;
import com.system.models.Fase;
import com.system.models.Grupo;
import com.system.models.Persona;
import com.system.models.Proyecto;
import com.system.services.IProyectoService;
import com.system.services.ProyectoService;
import com.system.ui.home.HomeAdapter;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ProyectoAprobarFragment extends Fragment {

    private Unbinder unbinder;
    private DatabaseReference dbReference;
    private ArrayAdapter<Proyecto> proyectoArrayAdapter;
    private ArrayAdapter<Fase> faseArrayAdapter;
    private Proyecto proyecto;
    private Fase fase;
    private IProyectoService service;

    @Nullable @BindView(R.id.cbProyecto) TextInputLayout cbProyecto;
    @Nullable @BindView(R.id.cbFase) TextInputLayout cbFase;
    @Nullable @BindView(R.id.recyclerProyectoAprobar) RecyclerView recyclerView;
    private ProyectoAprobarAdapter adapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View root=inflater.inflate(R.layout.fragment_proyecto_aprobar, container, false);
       unbinder= ButterKnife.bind(this, root);
       return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        proyectoArrayAdapter = new ArrayAdapter<Proyecto>(getContext(), android.R.layout.simple_spinner_dropdown_item); //Adaptamos el Layout dropdown_tipo
        faseArrayAdapter = new ArrayAdapter<Fase>(getContext(), android.R.layout.simple_spinner_dropdown_item); //Adaptamos el Layout dropdown_tipo
        initData();
        service= new ProyectoService();
        adapter =new ProyectoAprobarAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);

        AutoCompleteTextView proTextView= (AutoCompleteTextView) cbProyecto.getEditText();
        proTextView.setAdapter(proyectoArrayAdapter);
        proTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                faseArrayAdapter.clear();
                proyecto= (Proyecto) parent.getItemAtPosition(position);
                List<Fase> fases=proyecto.getFases()!=null?
                        proyecto.getFases().values().stream().collect(Collectors.toList()):
                        new ArrayList<>();
                faseArrayAdapter.addAll(fases);
                faseArrayAdapter.notifyDataSetChanged();
            }
        });
        AutoCompleteTextView faseTextView= (AutoCompleteTextView) cbFase.getEditText();
        faseTextView.setAdapter(faseArrayAdapter);
        faseTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fase= (Fase) parent.getItemAtPosition(position);
                List<Actividad> actividads=fase.getActividades()!=null?
                        fase.getActividades().values().stream()
                                .sorted(Comparator.comparing(Actividad::getCodigo))
                                .collect(Collectors.toList()):
                        new ArrayList<>();
                adapter.setActividadList(actividads);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }
    //region METODO ADICIONAL
    private void initData() {
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Proyecto proyecto=data.getValue(Proyecto.class);
                    proyectoArrayAdapter.add(proyecto);
                }
                proyectoArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO PROYECTO", "buscar proyectos", error.toException());
                Utils.messageError(getContext(),"Error:"+error.getMessage());
            }
        });
    }

    public void setActividadStatus(Actividad actividad){
        try {
            service.aprobar(proyecto.getCodigo(),fase.getCodigo(),actividad.getCodigo(),actividad.isEstado());
            Utils.messageConfirmation(getContext(),"Aprobación, guardado con exito");
            adapter.notifyDataSetChanged();
        }catch (Exception ex){
            Utils.messageError(getContext(),"Aprobación proyecto error["+ex.getMessage()+"]");
        }

    }

    public void addComentario(@NonNull Actividad actividad, String text){
        String aux= actividad.getCodigo()+" "+text;
        Map<String, Object> map = new HashMap<>();
        long numero=System.currentTimeMillis();
        map.put(actividad.getCodigo()+numero,aux);
        service.saveObservacion(proyecto.getCodigo(),map);
    }
    //endregion

}