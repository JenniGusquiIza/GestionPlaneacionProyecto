package com.system.services;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.models.Fase;
import com.system.ui.utils.Routes;

public class FaseService implements  IFaseService{
    private DatabaseReference dbReference;



    @Override
    public void save(Fase fase) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeFase);
            fase.setCodigo(fase.getCodigo());
            dbReference
                    .child(fase.getCodigo())
                    //.push()
                    .setValue(fase);
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void edit(Fase fase) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeFase);
            fase.setCodigo(fase.getCodigo());
            dbReference
                    .child(fase.getCodigo())
                    .setValue(fase);
        }catch (Exception ex){
            throw ex;
        }
    }


    @Override
    public void delete(String codigo) {
        try {
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeFase);
            dbReference.child(codigo).removeValue();
            //mensaje confirmacion
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public long max() {
        final long[] max = new long[1];
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeFase);
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
