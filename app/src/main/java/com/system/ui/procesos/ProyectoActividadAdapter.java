package com.system.ui.procesos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.system.R;
import com.system.models.Actividad;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProyectoActividadAdapter extends RecyclerView.Adapter<ProyectoActividadAdapter.ProyectoActividadViewHolder>{
    private Context context;
    private ProyectoActividadFragment fragment;
    private Actividad actividad;
    private List<Actividad> actividadList;

    public ProyectoActividadAdapter(@NonNull ProyectoActividadFragment fragment,List<Actividad>actividadList) {
        this.fragment=fragment;
        this.context = fragment.getContext();
        this.actividadList=actividadList;
    }

    public void setActividadList(List<Actividad> actividadList) {
        this.actividadList = actividadList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProyectoActividadAdapter.ProyectoActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_proyectos_actividad, parent, false);
        return new ProyectoActividadAdapter.ProyectoActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoActividadAdapter.ProyectoActividadViewHolder holder, int position) {

        if (actividadList != null && actividadList.size() > 0) {
            actividad= actividadList.get(position);
            actividad.setCodigo("A-"+(position+1));
            holder.itemCodigo.setText(""+actividad.getCodigo());
            holder.itemFase.setText(actividad.getFase());
            holder.itemDescripcion.setText(actividad.getDescripcion());

        }

    }

    @Override
    public int getItemCount() {
        return actividadList!=null? actividadList.size():0;
    }

    public class ProyectoActividadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtCodigo) TextView itemCodigo;
        @BindView(R.id.txtFase) TextView itemFase;
        @BindView(R.id.txtDescripcion) TextView itemDescripcion;

        public ProyectoActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.bnEliminar)
        void submitEliminar(){
            Actividad a= actividadList.get(getAdapterPosition());
            fragment.eliminar(a);
            actividadList.remove(a);
            notifyDataSetChanged();
        }

    }
}
