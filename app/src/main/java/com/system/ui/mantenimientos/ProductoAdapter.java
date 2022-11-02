package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.system.R;
import com.system.models.Fase;
import com.system.models.Producto;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>{
    private Context context;
    private  ProductoFragment fragment;
    private List<Producto> productoList;
    private  Producto producto;

    public ProductoAdapter(Context context, ProductoFragment fragment) {
        this.context=context;
        this.fragment=fragment;
    }

    public void setProductoList(List<Producto> productoList) {
        this.productoList = productoList;
    }

    @NonNull
    @Override
    public ProductoAdapter.ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_productos, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoAdapter.ProductoViewHolder holder, int position) {
        if (productoList != null && productoList.size() > 0) {
            producto= productoList.get(position);
            holder.itemCodigo.setText(""+(position+1));
            holder.itemDescripcion.setText(producto.getNombre().toUpperCase());
            holder.itemCantidad.setText(""+producto.getTotal());
            holder.itemGrupo.setText(producto.getGrupo().toUpperCase());
            holder.itemEstado.setText(producto.getEstado().equals("A")? "ACTIVO":"INACTIVO");
            holder.itemEstado.setTextColor(producto.getEstado().equals("A")?Color.BLACK:Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return productoList!=null? productoList.size() : 0;
    }

    public class ProductoViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

        @BindView(R.id.txtCodigo) TextView itemCodigo;
        @BindView(R.id.txtGrupo)  TextView itemGrupo;
        @BindView(R.id.txtDescripcion)  TextView itemDescripcion;
        @BindView(R.id.txtCantidad)  TextView itemCantidad;
        @BindView(R.id.txtEstado)  TextView itemEstado;
        @BindView(R.id.bnMenu) ImageButton itemMenu;

        //DIALOGO FASE
        private AlertDialog dialog;
        private AlertDialog.Builder builder;
        TextInputLayout txtDescripcion,txtTotal,cbGrupo;
        RadioGroup rgEstado;
        Button bnCancelar, bnGuardar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            builder = new AlertDialog.Builder(itemView.getContext()); //asignacion de instancia de Alert.Builder
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.bnMenu)
        void clickMenu(){
            PopupMenu popupMenu = new PopupMenu(itemMenu.getContext(), itemMenu);
            popupMenu.inflate(R.menu.popup_action);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        //@RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view;
            switch (item.getItemId()) {
                case R.id.itemViewer:
                    view = inflater.inflate(R.layout.dialog_producto, null);
                    View_Update(view,R.id.itemViewer);
                    builder.setView(view); //agregamos el dialogo
                    builder.setCancelable(false); //configuramos para que no se pueda quitar
                    dialog = builder.create();
                    dialog.show();//abrimos el dialog
                    return true;
                case R.id.itemUpdate:
                    view = inflater.inflate(R.layout.dialog_producto, null);
                    View_Update(view,R.id.itemUpdate);
                    builder.setView(view); //agregamos el dialogo
                    builder.setCancelable(false); //configuramos para que no se pueda quitar
                    dialog = builder.create();
                    dialog.show();//abrimos el dialog
                    return true;
                case R.id.itemDelete:
                    view = inflater.inflate(R.layout.dialog_delete, null);
                    Delete(view);
                    builder.setView(view); //agregamos el dialogo
                    builder.setCancelable(false); //configuramos para que se no pueda quitar
                    dialog = builder.create();
                    dialog.show();
                    //int width = view.getResources().getDisplayMetrics().widthPixels/4;
                    //int height = view.getResources().getDisplayMetrics().heightPixels/2;
                    //dialog.getWindow().setLayout(width, width);
                    return true;
                default:
                    return false;
            }
        }

        private void View_Update(@NonNull View view, int value) {
            producto= productoList.get(getAdapterPosition());
            cbGrupo= view.findViewById(R.id.cbGrupo);
            txtDescripcion=  view.findViewById(R.id.txtDescripcion);
            txtTotal=  view.findViewById(R.id.txtTotal);
            rgEstado= view.findViewById(R.id.rbGroupEstado);
            rgEstado.check(producto.getEstado().equals("A")?R.id.rbActivo:R.id.rbInactivo);
            cbGrupo.getEditText().setText(producto.getGrupo());
            txtDescripcion.getEditText().setText(producto.getNombre());
            txtTotal.getEditText().setText(""+producto.getTotal());
            bnGuardar = view.findViewById(R.id.bnGuardar);
            bnCancelar  = view.findViewById(R.id.bnCancelar);
            bnGuardar.setOnClickListener(this);
            bnCancelar.setOnClickListener(this);
            //
            bnGuardar.setVisibility(R.id.itemUpdate==value?View.VISIBLE:View.INVISIBLE);
            cbGrupo.setEnabled(R.id.itemUpdate==value?true:false);
            txtDescripcion.setEnabled(R.id.itemUpdate==value?true:false);
            txtTotal.setEnabled(R.id.itemUpdate==value?true:false);
            rgEstado.setEnabled(R.id.itemUpdate==value?true:false);
        }
        private void Delete(View view) {
            producto= productoList.get(getAdapterPosition());
            TextView txtMensaje=  view.findViewById(R.id.txtMensaje);
            txtMensaje.setText(producto.getNombre());
            Button bnSi = view.findViewById(R.id.bnSi);
            Button bnNo = view.findViewById(R.id.bnNo);
            bnSi.setOnClickListener(this);
            bnNo.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bnCancelar:// Layout dialog_usuario
                    dialog.dismiss();
                    break;
                case R.id.bnGuardar: // Layout dialog_usuario
                    String descripcion=txtDescripcion.getEditText().getText().toString();
                    String total=txtTotal.getEditText().getText().toString();
                    if(validateText(descripcion,total)){
                        RadioButton estado = dialog.findViewById(rgEstado.getCheckedRadioButtonId());
                        producto.setNombre(descripcion);
                        producto.setTotal(Integer.parseInt(total));
                        producto.setDisponible(Integer.parseInt(total));
                        producto.setEstado(""+estado.getText().charAt(0));
                        fragment.editar(producto);
                        dialog.dismiss();
                    }
                case R.id.bnNo: // Layout dialog_delete
                    dialog.dismiss();
                    break;
                case R.id.bnSi: // Layout dialog_delete
                    fragment.eliminar(producto);
                    dialog.dismiss();
                    break;
            }
        }
        private boolean validateText(String descripcion,String total) {
            boolean isDescripcion=true;
            boolean isTotal=true;
            txtDescripcion.setErrorEnabled(false);
            txtTotal.setErrorEnabled(false);
            if(descripcion.isEmpty()){
                txtDescripcion.setError("ingresar descripción");
                isDescripcion=false;
            }
            if(total.isEmpty()){
                txtTotal.setError("ingresar total material");
                isTotal=false;
            }
            return  isDescripcion && isTotal;
        }
    }
}
