package com.system.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.models.Persona;
import com.system.ui.utils.Routes;

import java.util.ArrayList;
import java.util.List;

public class PersonaService implements IPersonaService {

    private DatabaseReference dbReference;

    @Override
    public void save(Persona persona){
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreePersona);
             dbReference
                    .child(persona.getCodigo())
                    //.push()
                    .setValue(persona);
        }catch (Exception ex){
            throw ex;
        }

    }

    @Override
    public void delete(String personaId){
        try {
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreePersona);
            dbReference.child(personaId).removeValue();
            //mensaje confirmacion
        }catch (Exception ex){

        }
    }

    @Override
    public Persona getPersona(String personaId){
        final Persona[] persona = new Persona[1];
        try {
            dbReference.child(personaId).get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Persona p=task.getResult().getValue(Persona.class);
                            persona[0] =p;
                        }
                    });
            return persona[0];
        }catch (Exception ex){
            throw  ex;
        }
    }

    @Override
    public List<Persona> getAll(){
        List<Persona> personas= new ArrayList<Persona>();
        try {
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreePersona);
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data : snapshot.getChildren()){

                        Persona persona=data.getValue(Persona.class);
                        personas.add(persona);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SERVICIO PERSONA", "buscar personas", error.toException());
                }
            });

        }catch (Exception ex){
            throw ex;
        }
        return personas;
    }
}
