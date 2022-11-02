package com.system.services;

import android.os.Handler;

import com.system.models.Actividad;
import com.system.models.Fase;
import com.system.models.MaterialSelect;
import com.system.models.Persona;
import com.system.models.Producto;
import com.system.models.Proyecto;

import java.util.List;
import java.util.Map;

public interface IProyectoService {

    void save(Proyecto proyecto);
    void delete(String codigo);
    void saveActividad(String idProyecto,String idFase,List<Actividad> actividades);
    void saveObservacion(String idProyecto, Map<String, Object>  observacion);
    void deleteActividad(String idProyecto,String idFase,String idActividad);
    void saveTrabajador(String idProyecto, Persona trabajadores);
    void saveMaterial(String idProyecto, String idProducto,int ocupado, MaterialSelect materialSelect);
    void deleteAsignaciones(String idProyecto);
    long max();
    void aprobar(String idProyecto,String idFase,String idActividad,boolean status);
}
