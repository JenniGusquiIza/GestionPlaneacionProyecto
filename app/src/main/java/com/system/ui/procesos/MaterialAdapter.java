package com.system.ui.procesos;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPickerListener;
import com.system.R;
import com.system.models.MaterialSelect;
import com.system.models.Producto;
import com.system.ui.mantenimientos.ProductoAdapter;
import com.system.ui.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.ViewHolder>{

    private Context context;
    private List<Producto> productoList;
    private  Producto producto;

    public MaterialAdapter(Context context) {
        this.context = context;
    }


    public void addMateriales(Map<String,MaterialSelect> materiales){


    }
    public void setProductoList(List<Producto> productoList) {
        this.productoList = productoList;
    }

    public List<Producto> getSelectAllMaterial() {
        List<Producto>list= productoList.stream()
                .filter(m ->m.getOcupado()>0 )
                .collect(Collectors.toList());
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_material2, parent, false);
        return new MaterialAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(productoList!=null  && productoList.size()>0){
            producto= productoList.get(position);
            //holder.itemSelect.setChecked(producto.isSelect());
            holder.itemDisponible.setText(""+producto.getDisponible());
            holder.itemMaterial.setText(producto.getNombre());
            holder.itemOcupado.setValue(producto.getOcupado());
            holder.itemOcupado.setMaxValue(producto.getDisponible()+producto.getOcupado());
            //holder.itemOcupado.setEnabled(producto.isSelect());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.itemMaterial.setTooltipText(producto.getGrupo()+"-"+producto.getNombre());
            }
        }
    }

    @Override
    public int getItemCount() {
        return  productoList!=null? productoList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.itemMateriales) LinearLayoutCompat itemLayout;
        @BindView(R.id.txtMaterial) TextView itemMaterial;
        @BindView(R.id.txtDisponible) TextView itemDisponible;
        @BindView(R.id.npOcupado) ScrollableNumberPicker itemOcupado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemOcupado.setListener(new ScrollableNumberPickerListener() {
                @Override
                public void onNumberPicked(int value) {
                    producto=productoList.get(getAdapterPosition());
                    producto.setOcupado(value);
                    productoList.set(getAdapterPosition(),producto);

                    //notifyDataSetChanged();
                }
            });
        }


    }
}
