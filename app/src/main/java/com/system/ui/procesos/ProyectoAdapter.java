package com.system.ui.procesos;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.system.R;
import com.system.models.Proyecto;
import com.system.ui.utils.PdfProyecto;
import com.system.ui.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder>{
    private Context context;
    private ProyectoFragment fragment;
    private Proyecto proyecto;
    private List<Proyecto> proyectoList;


    public ProyectoAdapter(@NonNull ProyectoFragment fragment) {
        this.fragment=fragment;
        this.context=fragment.getContext();

    }

    public void setProyectoList(List<Proyecto> proyectoList) {
        this.proyectoList = proyectoList;
    }

    @NonNull
    @Override
    public ProyectoAdapter.ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_proyectos, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoAdapter.ProyectoViewHolder holder, int position) {
        if (proyectoList != null && proyectoList.size() > 0) {
            proyecto= proyectoList.get(position);
            holder.itemCodigo.setText(proyecto.getCodigo());
            holder.itemNombre.setText(proyecto.getNombre());
            holder.itemFechaInicio.setText(proyecto.getFechaInicio());
            holder.itemFechaFin.setText(proyecto.getFechaFin());
            holder.itemEstado.setText(proyecto.isEstado()? "ACTIVO":"INACTIVO");
            holder.itemEstado.setTextColor(proyecto.isEstado()? Color.BLACK:Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return proyectoList!=null? proyectoList.size() : 0;
    }

    public class ProyectoViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener{
        Bundle result;
        @BindView(R.id.txtCodigo) TextView itemCodigo;
        @BindView(R.id.txtFechaInicio) TextView itemFechaInicio;
        @BindView(R.id.txtFechaFin) TextView itemFechaFin;
        @BindView(R.id.txtNombreProyectp) TextView itemNombre;
        @BindView(R.id.txtEstado) TextView itemEstado;
        @BindView(R.id.bnMenu) ImageButton itemMenu;
        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            result = new Bundle();
        }

        @OnClick(R.id.bnMenu)
        void clickMenu(){
            PopupMenu popupMenu = new PopupMenu(itemMenu.getContext(), itemMenu);
            popupMenu.inflate(R.menu.popup_proyecto);
            popupMenu.setOnMenuItemClickListener(this);
            Menu nav_Menu= popupMenu.getMenu();
            proyecto=proyectoList.get(getAdapterPosition());
            if(!proyecto.isEstado())
                nav_Menu.findItem(R.id.itemImprimir).setVisible(true);
            for (String rol:Utils.auth.getRoles()){
                char cadena=rol.toUpperCase().charAt(0);
                if(cadena=='S'){
                    nav_Menu.findItem(R.id.itemActividad).setVisible(false);
                }
            }

            popupMenu.show();
        }



        @Override
        public boolean onMenuItemClick(MenuItem item) {
            LayoutInflater inflater = LayoutInflater.from(context);
            Proyecto proyecto;
            switch (item.getItemId()) {

                case R.id.itemActividad:
                    proyecto= proyectoList.get(getAdapterPosition());
                    result.putParcelable("proyecto", proyecto);
                    fragment.getParentFragmentManager().setFragmentResult("proyectoKey",result);
                    fragment.getNavController().navigate(R.id.nav_proyectoActivity);
                    return true;
                case R.id.itemAsignacion:
                    proyecto= proyectoList.get(getAdapterPosition());
                    result.putParcelable("proyecto", proyecto);
                    fragment.getParentFragmentManager().setFragmentResult("proyectoKey",result);
                    fragment.getNavController().navigate(R.id.nav_proyectoAsign);
                    return true;
                case R.id.itemImprimir:
                    proyecto= proyectoList.get(getAdapterPosition());
                    PdfProyecto pdf=new PdfProyecto(proyecto,context);
                    Uri uri=pdf.createPDF();
                    if(uri!=null){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        try {
                            fragment.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Utils.messageConfirmation(context, "No cuenta con una aplicación para visualizar archivo pdf");
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                        }
                    }else{
                        Utils.messageError(context,"No existe archivo para visualizar");
                    }
                default:
                    return false;
            }
        }
    }
}
