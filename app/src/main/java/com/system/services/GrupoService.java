package com.system.services;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.models.Fase;
import com.system.models.Grupo;
import com.system.ui.utils.Routes;

public class GrupoService implements  IGrupoService{
    private DatabaseReference dbReference;



    @Override
    public void save(Grupo grupo) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeGrupo);
            dbReference
                    //.child(fase.getCodigo())
                    .push()
                    .setValue(grupo);
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void edit(Grupo grupo) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeGrupo);
            dbReference
                    .child(grupo.getCodigo())
                    .setValue(grupo);
        }catch (Exception ex){
            throw ex;
        }
    }


    @Override
    public void delete(String codigo) {
        try {
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeGrupo);
            dbReference.child(codigo).removeValue();
            //mensaje confirmacion
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public long max() {
        final long[] max = new long[1];
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeGrupo);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                max[0] =snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //long max=(long)dbReference.get().getResult().getChildrenCount()+1;
        return max[0];
    }
}
