package com.system.ui.procesos;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.internal.Util;
import com.system.R;
import com.system.models.Actividad;
import com.system.models.Fase;
import com.system.models.Proyecto;
import com.system.services.IProyectoService;
import com.system.services.ProyectoService;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProyectoActividadFragment extends Fragment {
    private IProyectoService service;
    Proyecto proyecto;
    private Fase fase;
    private List<Actividad> actividadList;
    private Unbinder unbinder;
    private DatabaseReference dbReference;
    private NavController navController;
    private ProyectoActividadAdapter adapter;

    @Nullable @BindView(R.id.recyclerProyectoActividad) RecyclerView recyclerView;
    @Nullable @BindView(R.id.txtNumProyecto) TextView txtNumProyecto;
    @Nullable @BindView(R.id.txtNombreProyecto) TextView txtNombreProyecto;
    @Nullable @BindView(R.id.txtFechaRegistro) TextView txtFechaRegistro;
    @Nullable @BindView(R.id.txtCliente) TextView txtCliente;

    @Nullable @BindView(R.id.cbFase) TextInputLayout cbFase;
    @Nullable @BindView(R.id.txtDescripcion) TextInputLayout txtDescripcion;
    @Nullable @BindView(R.id.bnGuardar) ProgressBar bnGuardar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        actividadList=new ArrayList<>();
        getParentFragmentManager().setFragmentResultListener("proyectoKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                proyecto= result.getParcelable("proyecto");
                txtNombreProyecto.setText(txtNombreProyecto.getText()+" "+proyecto.getNombre());
                txtCliente.setText(proyecto.getCliente().getApellidos()!=null?
                        proyecto.getCliente().getApellidos():""
                        +" "+proyecto.getCliente().getNombres());
                txtNumProyecto.setText(proyecto.getNumero());
                txtFechaRegistro.setText(proyecto.getFechaRegistro().substring(0,10));
                List<Fase> fases= proyecto.getFases().values().stream().collect(Collectors.toList());
                ArrayAdapter<Fase> faseAdapter = new ArrayAdapter<Fase>(getContext(), android.R.layout.simple_spinner_dropdown_item,fases); //Adaptamos el Layout dropdown_tipo
                AutoCompleteTextView autoCompleteTextView= (AutoCompleteTextView) cbFase.getEditText();
                autoCompleteTextView.setAdapter(faseAdapter);
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        fase= (Fase) parent.getItemAtPosition(position);
                    }
                });
                for (Fase fase:fases){
                    if (fase.getActividades()!=null){

                        List<Actividad> actividades= fase.getActividades().values().stream().collect(Collectors.toList());
                        actividades.stream()
                                .peek(x ->x.idFase=fase.getCodigo())
                                .forEach(System.out::println);
                        actividadList.addAll(actividades);
                    }
                }

            }
        });
        View root=inflater.inflate(R.layout.fragment_proyecto_actividad, container, false);
        unbinder= ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        adapter=new ProyectoActividadAdapter(this,actividadList);
        service=new ProyectoService();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.bnAgregar)
    void submitAgregar(){
        if (validarText()){
            String descripcion=txtDescripcion.getEditText().getText().toString();
            Actividad actividad=new Actividad("",fase.getDescripcion(),descripcion,false);
            actividadList.add(actividad);
            adapter.setActividadList(actividadList);
            cleanText();
            adapter.notifyDataSetChanged();
        }


    }
    @OnClick(R.id.bnGuardar)
    void submitGuardar(){
        setIconLoading(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    service.saveActividad(proyecto.getCodigo(),fase.getCodigo(),actividadList);
                    Utils.messageConfirmation(getContext(),"Guardado con éxito");
                    cleanText();
                    setIconLoading(false);
                }catch (Exception ex){
                    Utils.messageError(getContext(),"Proyecto-Actividad error["+ex.getMessage()+"]");
                }

            }
        },5000);

    }

    //region METODOS ADICIONAL
    public void eliminar(@NonNull Actividad a){
        try {
            service.deleteActividad(proyecto.getCodigo(),a.idFase,a.getCodigo());
        }catch (Exception ex){
            Utils.messageError(getContext(),"Eliminar actividad, error["+ex.getMessage()+"]");
        }
    }
    private boolean validarText() {
        boolean isDescripcion=false,isFase=false;
        String nombre= txtDescripcion.getEditText().getText().toString().trim();
        String fase= cbFase.getEditText().getText().toString().trim();
        //DESCRIPCION
        if(nombre.isEmpty()){
            txtDescripcion.setError("Llenar campo");
        }else{
            txtDescripcion.setErrorEnabled(false);
            isDescripcion=true;
        }

        if(fase.isEmpty()){
            cbFase.setError("Seleccionar");
        }else{
            cbFase.setErrorEnabled(false);
            isFase=true;
        }
        return (isDescripcion && isFase)? true:false;
    }
    private void cleanText(){
        txtDescripcion.getEditText().setText("");
        cbFase.getEditText().setText("");
    }
    private void setIconLoading(boolean status){
        Rect bounds = bnGuardar.getIndeterminateDrawable().getBounds();
        bnGuardar.setIndeterminateDrawable(status?
                getActivity().getDrawable(R.drawable.loading):
                getActivity().getDrawable(R.drawable.ic_save_24));
        bnGuardar.getIndeterminateDrawable().setBounds(bounds);
    }
    //endregion

}