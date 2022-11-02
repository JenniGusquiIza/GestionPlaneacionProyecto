package com.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.system.models.Producto;
import com.system.ui.utils.Routes;

public class ProductoService implements IProductoService{
    private DatabaseReference dbReference;


    @Override
    public void save(Producto producto) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProducto);
            dbReference
                    .child(producto.getCodigo())
                    //.push()
                    .setValue(producto);
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void edit(Producto producto) {
        try{
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProducto);
            dbReference
                    .child(producto.getCodigo())
                    .setValue(producto);
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void delete(String codigo) {
        try {
            dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProducto);
            dbReference.child(codigo).removeValue();
        }catch (Exception ex){
            throw ex;
        }
    }
}
