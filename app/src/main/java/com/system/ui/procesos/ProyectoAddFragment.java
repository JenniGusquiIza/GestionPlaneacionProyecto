package com.system.ui.procesos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.system.R;
import com.system.models.Fase;
import com.system.models.Persona;
import com.system.models.Proyecto;
import com.system.services.IProyectoService;
import com.system.services.ProyectoService;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ProyectoAddFragment extends Fragment {

    private static final int PICK_PDF_FILE = 1;
    private NavController navController;
    private Uri uri;
    private Unbinder unbinder;
    private DatabaseReference dbReference;
    private StorageReference storageRef;
    private DatePickerDialog datePickerDialog=null;
    private Calendar fechaDesde,fechaHasta;
    private IProyectoService service;
    private List<Fase> faseList;
    private ArrayAdapter<Fase> adapterFase;
    private ArrayAdapter<Persona> adapterCliente;
    private AutoCompleteTextView autoCompletePersona;
    private Persona cliente;
    @BindView(R.id.txtNombreProyecto) TextInputLayout txtNombreProyecto;
    @BindView(R.id.txtNumProyecto) TextInputLayout txtNumProyecto;
    @BindView(R.id.txtCliente) TextInputLayout txtCliente;
    @BindView(R.id.txtMonto) TextInputLayout txtMonto;
    @BindView(R.id.txtFechaInicio) TextInputLayout txtFechaInicio;
    @BindView(R.id.txtFechaFin) TextInputLayout txtFechaFin;
    @BindView(R.id.txtCalculo) TextView txtCalculo;
    @BindView(R.id.txtFile) TextView txtFile;
    @BindView(R.id.txtUbicacionProy) TextInputLayout txtUbicacion;
    @BindView(R.id.txtDescripcion) TextInputLayout txtDescripcion;
    @BindView(R.id.lvFase) ListView lvFase;
    @BindView(R.id.pbFile) ProgressBar pbFile;


    //region METODOS OVERRIDE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_proyecto_add, container, false);
        unbinder= ButterKnife.bind(this, root);
        autoCompletePersona= (AutoCompleteTextView) txtCliente.getEditText();
        initData();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        adapterFase=new ArrayAdapter<Fase>(getContext(), android.R.layout.simple_list_item_multiple_choice);
        adapterCliente = new ArrayAdapter<Persona>(getActivity(), android.R.layout.simple_list_item_1); //Adaptamos el Layout dropdown_tipo
        autoCompletePersona.setAdapter(adapterCliente);
        autoCompletePersona.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cliente=(Persona) parent.getItemAtPosition(position);
            }
        });
        lvFase.setAdapter(adapterFase);
        service=new ProyectoService();
        fechaDesde= Calendar.getInstance();
        fechaHasta=Calendar.getInstance();
        txtFechaInicio.getEditText().setText(Utils.convertDateToString(fechaDesde.getTime()));
        txtFechaFin.getEditText().setText(Utils.convertDateToString(fechaHasta.getTime()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_PDF_FILE && resultCode== Activity.RESULT_OK
                && data!=null && data.getData()!=null){
            uri=data.getData();
            txtFile.setText(uri.getPath()+".pdf");
            txtFile.setTextColor(Color.RED);
        }

    }

    //endregion


    //region METODOS ON_CLICK
    @OnClick(R.id.txtFechaInicio_)
    void submitFechaInicio(){
        datePickerDialog = new DatePickerDialog(getContext(),R.style.Theme_DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                fechaDesde.set(year,month,day);
                txtFechaInicio.getEditText().setText(Utils.convertDateToString(fechaDesde.getTime()));
            }
        }, fechaDesde.get(Calendar.YEAR),fechaDesde.get(Calendar.MONTH),fechaDesde.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @OnClick(R.id.txtFechaFin_)
    void submitFechaFin(){
        datePickerDialog = new DatePickerDialog(getContext(),R.style.Theme_DialogTheme, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                fechaHasta.set(year,month,day);
                txtFechaFin.getEditText().setText(Utils.convertDateToString(fechaHasta.getTime()));
                String calculo= null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    calculo = Utils.calculateAge(fechaDesde.getTime(),fechaHasta.getTime());
                }else{
                    calculo=Utils.calculateDay(fechaDesde.getTime(),fechaHasta.getTime());
                }
                txtCalculo.setText(calculo);
            }
        }, fechaHasta.get(Calendar.YEAR),fechaHasta.get(Calendar.MONTH),fechaHasta.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.bnGuardar)
    void submitGuardar(){
        List<Fase> fases=new ArrayList<>();
        for (int i=0; i< lvFase.getCount();i++){
            if(lvFase.isItemChecked(i)){
                Fase f= (Fase) lvFase.getItemAtPosition(i);
                fases.add(f);
            }
        }
        Map<String,Fase> mapa= fases.stream().collect(
                Collectors.toMap(x -> x.getCodigo(),x->x));
        long numero=System.currentTimeMillis();
        String nombreProyecto= txtNombreProyecto.getEditText().getText().toString();
        String numeroProyecto=txtNumProyecto.getEditText().getText().toString();
        String cliente_ =txtCliente.getEditText().getText().toString();

        if(uri !=null){
            boolean valueT=validateText( nombreProyecto, numeroProyecto, cliente_);
            if(valueT){
                storageRef= FirebaseStorage.getInstance().getReference("documentos/"+Routes.TreeProyecto+"/"+numero);
                storageRef.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        String urlFile=storageRef.getDownloadUrl().toString();
                                        //GUARDAR PROYECTO
                                        Proyecto proyecto= new Proyecto("LO-"+numero,
                                                Utils.convertDefaultDateToString(),
                                                nombreProyecto,
                                                numeroProyecto,
                                                cliente,
                                                Double.parseDouble(txtMonto.getEditText().getText().toString()),
                                                txtFechaInicio.getEditText().getText().toString(),
                                                txtFechaFin.getEditText().getText().toString(),
                                                txtCalculo.getText().toString(),
                                                urlFile,
                                                txtUbicacion.getEditText().getText().toString(),
                                                txtDescripcion.getEditText().getText().toString(),
                                                true,
                                                mapa,
                                                null
                                        );

                                        service.save(proyecto);
                                        Utils.messageConfirmation(getContext(),"Proyecto guardado con éxito");
                                        //clearForm();
                                        navController.navigate(R.id.nav_proyecto);
                                    }
                                },5000);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utils.messageError(getContext(),"Error carga de archivo["+e.getMessage()+"]");
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress= (100* snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                                pbFile.setProgress((int)progress);
                            }
                        });
            }
        }
    }
    @OnClick(R.id.bnUpload)
    void submitUpload(){
        //IMAGENES:ACTION_GET_CONTENT   - DOCUMENTO: ACTION_OPEN_DOCUMENT
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //intent.setType("image/*"); --imagen
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent,PICK_PDF_FILE);
    }

    @OnClick(R.id.bnCancelar)
    void submitCancel(){
        navController.navigate(R.id.nav_proyecto);
        getFragmentManager().beginTransaction().remove(this).commit();
    }



    //endregion

    //region METODOS ADICIONAL
    private boolean validateText(String nombreProyecto,String numeroProyecto,String cliente_){
        boolean isNombreProyecto=true;
        boolean isNumeroProyecto=true;
        boolean isCliente=true;
        txtNombreProyecto.setErrorEnabled(false);
        txtNumProyecto.setErrorEnabled(false);
        txtCliente.setErrorEnabled(false);
        if(nombreProyecto.isEmpty()){
            txtNombreProyecto.setError("ingrese nombre proyecto");
            isNombreProyecto=false;
        }
        if(numeroProyecto.isEmpty()){
            txtNombreProyecto.setError("ingrese número proyecto");
            isNumeroProyecto=false;
        }
        if(cliente_.isEmpty()){
            txtCliente.setError("ingrese cliente");
            isCliente=false;
        }
        return  (isNombreProyecto && isNumeroProyecto && isCliente);
    }
    private void initData() {
        Query q;
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeFase);
        q= dbReference.orderByChild("estado").equalTo(true);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    faseList=new ArrayList<>();
                    for(DataSnapshot data : snapshot.getChildren()){
                        Fase fase=data.getValue(Fase.class);
                        fase.setCodigo(data.getKey());
                        faseList.add(fase);

                    }
                    adapterFase.addAll(faseList);
                    adapterFase.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO FASE", "buscar fase", error.toException());
                Utils.messageError(getContext(),"Error lista fase:"+error.getMessage());
            }
        });

        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreePersona);
        q=dbReference.orderByChild("cliente").equalTo(true);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Persona> personaList=new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    Persona persona=data.getValue(Persona.class);
                    personaList.add(persona);
                }
                adapterCliente.addAll(personaList);
                adapterCliente.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO CLIENTE", "buscar cliente", error.toException());
            }
        });
    }

    private void clearForm(){
        pbFile.setProgress(0);
        txtFechaInicio.getEditText().setText(Utils.convertDefaultDateToString());
        txtFechaFin.getEditText().setText(Utils.convertDefaultDateToString());
        txtCliente.getEditText().setText("");
        txtUbicacion.getEditText().setText("");
        txtDescripcion.getEditText().setText("");
        txtNombreProyecto.getEditText().setText("");
        txtMonto.getEditText().setText("");
        txtFile.setText("");
        txtCalculo.setText("");
        initData();
    }

    //endregion

}