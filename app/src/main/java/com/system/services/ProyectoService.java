package com.system.services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.models.Actividad;
import com.system.models.MaterialSelect;
import com.system.models.Persona;
import com.system.models.Producto;
import com.system.models.Proyecto;
import com.system.ui.utils.Routes;

import java.util.List;
import java.util.Map;

public class ProyectoService implements IProyectoService{
    private DatabaseReference dbReference;

    @Override
    public void save(Proyecto proyecto) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
            dbReference
                    .child(proyecto.getCodigo())
                    //.push()
                    .setValue(proyecto);
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void delete(String codigo) {
        try {
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
            dbReference.child(codigo).removeValue();
            //mensaje confirmacion
        }catch (Exception ex){
            throw ex;
        }

    }

    @Override
    public void deleteActividad(String idProyecto,String idFase,String idActividad){
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
        dbReference
                .child(idProyecto)
                .child("fases")
                .child(idFase)
                .child("actividades")
                .child(idActividad)
                .removeValue();
    }

    @Override
    public void deleteAsignaciones(String idProyecto){
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
        dbReference.child(idProyecto).child("trabajadores").removeValue();
        dbReference.child(idProyecto).child("materiales").removeValue();

    }
    @Override
    public void saveTrabajador(String idProyecto, Persona persona) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
            dbReference.child(idProyecto)
                    .child("trabajadores")
                    .child(persona.getCodigo())
                    .setValue(persona);
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void saveMaterial(String idProyecto, String idProducto,int ocupado, MaterialSelect materialSelect) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
            dbReference.child(idProyecto)
                    .child("materiales")
                    .child(idProducto)
                    .setValue(materialSelect);
            dbReference=FirebaseDatabase.getInstance().getReference().child(Routes.TreeProducto);
            if(ocupado!=0){
                dbReference.child(idProducto).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            Producto p=task.getResult().getValue(Producto.class);
                            p.setDisponible(p.getDisponible()-ocupado);
                            p.setOcupado(p.getTotal()-p.getDisponible());
                            dbReference
                                    .child(p.getCodigo())
                                    .setValue(p);
                        }
                    }
                });
            }

        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void saveActividad(String idProyecto,String idFase,List<Actividad> actividades) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
            for (Actividad actividad:actividades) {
                dbReference
                        .child(idProyecto)
                        .child("fases")
                        .child(idFase)
                        .child("actividades")
                        .child(actividad.getCodigo())
                        .setValue(actividad);
            }

        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void saveObservacion(String idProyecto, Map<String, Object>  observacion) {
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
        dbReference
                .child(idProyecto)
                .child("observaciones")
                .updateChildren(observacion);
    }

    @Override
    public long max() {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
            final long[] max = {0};//(long)dbReference.get().getResult().getChildrenCount()+1;
            dbReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                 max[0]=   task.getResult().getChildrenCount()+1;
                }
            });
            return max[0];
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void aprobar(String idProyecto, String idFase, String idActividad, boolean status) {
        try
        {
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
            dbReference
                    .child(idProyecto)
                    .child("fases")
                    .child(idFase)
                    .child("actividades")
                    .child(idActividad)
                    .child("estado")
                    .setValue(status);
        }catch (Exception ex){
            throw  ex;
        }
    }
}
