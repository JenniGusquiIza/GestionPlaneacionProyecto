package com.system.ui.procesos;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.system.R;
import com.system.models.Actividad;
import com.system.ui.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProyectoAprobarAdapter extends RecyclerView.Adapter<ProyectoAprobarAdapter.ProyectoAprobarViewHolder>{
    private Context context;
    private ProyectoAprobarFragment fragment;
    private Actividad actividad;
    private List<Actividad> actividadList;

    public ProyectoAprobarAdapter(@NonNull ProyectoAprobarFragment fragment) {
        this.fragment=fragment;
        this.context = fragment.getContext();
    }

    public void setActividadList(List<Actividad> actividadList) {
        this.actividadList = actividadList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProyectoAprobarAdapter.ProyectoAprobarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_proyectos_aprobar, parent, false);
        return new ProyectoAprobarAdapter.ProyectoAprobarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoAprobarAdapter.ProyectoAprobarViewHolder holder, int position) {

        if (actividadList != null && actividadList.size() > 0) {
            Rect bounds = holder.itemEstado.getIndeterminateDrawable().getBounds();
            actividad= actividadList.get(position);
            holder.itemCodigo.setText(actividad.getCodigo());
            holder.itemDescripcion.setText(actividad.getDescripcion());

            //holder.itemCarView.setEnabled(actividad.isEstado()?false:true);
            holder.itemCarView.setCardBackgroundColor(actividad.isEstado()? context.getColor(R.color.blue200)
                    :context.getColor(R.color.white));
            holder.itemMenu.setColorFilter(R.color.red);
            holder.itemDescripcion.setTextColor(actividad.isEstado()? Color.WHITE:Color.BLACK);
            holder.itemEstado.setIndeterminateDrawable(actividad.isEstado()? ContextCompat.getDrawable(context,R.drawable.ic_check_circle_24)
                    : ContextCompat.getDrawable(context,R.drawable.ic_check_circle_outline_24));
            holder.itemEstado.getIndeterminateDrawable().setTint(actividad.isEstado()? Color.WHITE:Color.RED);
            holder.itemEstado.getIndeterminateDrawable().setBounds(bounds);

        }

    }

    @Override
    public int getItemCount() {
        return actividadList!=null? actividadList.size():0;
    }

    public class ProyectoAprobarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,PopupMenu.OnMenuItemClickListener {
        @BindView(R.id.txtCodigo) TextView itemCodigo;
        @BindView(R.id.txtDescripcion) TextView itemDescripcion;
        @BindView(R.id.pbEstado) ProgressBar itemEstado;
        @BindView(R.id.cvActividad) CardView itemCarView;
        @BindView(R.id.bnMenu) ImageButton itemMenu;
        private AlertDialog dialog;
        private AlertDialog.Builder builder;
        private EditText txtComentario;
        Button bnCancelar, bnGuardar;

        public ProyectoAprobarViewHolder(@NonNull View itemView) {
            super(itemView);
            builder = new AlertDialog.Builder(itemView.getContext()); //asignacion de instancia de Alert.Builder
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cvActividad)
        void submitActividad(){
            actividad=actividadList.get(getAdapterPosition());
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_confirmation, null);
            TextView txtMensaje=  view.findViewById(R.id.txtMensaje);
            txtMensaje.setText("Desea aprobar actividad: "+actividad.getDescripcion().toUpperCase());
            Button bnSi = view.findViewById(R.id.bnSi);
            Button bnNo = view.findViewById(R.id.bnNo);
            bnSi.setOnClickListener(this);
            bnNo.setOnClickListener(this);
            builder.setView(view); //agregamos el dialogo
            builder.setCancelable(false); //configuramos para que se no pueda quitar
            dialog = builder.create();
            dialog.show();

            /*
            Rect bounds =itemEstado.getIndeterminateDrawable().getBounds();
            itemEstado.setIndeterminateDrawable(actividad.isEstado()?fragment.getActivity().getDrawable(R.drawable.ic_check_circle_24):
                    fragment.getActivity().getDrawable(R.drawable.ic_check_circle_outline_24));
            itemEstado.getIndeterminateDrawable().setTint(actividad.isEstado()? Color.WHITE:Color.RED);
            itemEstado.getIndeterminateDrawable().setBounds(bounds);*/
        }

        @OnClick(R.id.bnMenu)
        void submitMenu(){
            PopupMenu popupMenu = new PopupMenu(itemMenu.getContext(), itemMenu);
            popupMenu.inflate(R.menu.popup_proyecto_aprobar);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }
        @Override
        public void onClick(@NonNull View view) {
            switch (view.getId()) {
                case R.id.bnSi: // Layout dialog_confirmacion
                    actividad.setEstado(!actividad.isEstado());
                    fragment.setActividadStatus(actividad);
                    dialog.dismiss();
                    break;
                case R.id.bnNo: // Layout dialog_confirmacion
                    dialog.dismiss();
                    break;
                case R.id.bnCancelar:// Layout dialog_proyecto_aprobar
                    dialog.dismiss();
                    break;
                case R.id.bnGuardar: // Layout dialog_proyecto_aprobar
                   String texto= txtComentario.getText().toString();
                    if (!texto.isEmpty()) {
                        Actividad actividad=actividadList.get(getAdapterPosition());
                        fragment.addComentario(actividad,texto);
                        dialog.dismiss();
                    }else{
                        Utils.messageConfirmation(context,"Debe ingresar comentario(s)");
                    }
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view;
            switch (item.getItemId()) {
                case R.id.itemComentario:
                    view = inflater.inflate(R.layout.dialog_proyecto_aprobar, null);
                    txtComentario=  view.findViewById(R.id.txtComentario);
                    bnGuardar = view.findViewById(R.id.bnGuardar);
                    bnCancelar  = view.findViewById(R.id.bnCancelar);
                    bnGuardar.setOnClickListener(this);
                    bnCancelar.setOnClickListener(this);
                    builder.setView(view); //agregamos el dialogo
                    builder.setCancelable(false); //configuramos para que no se pueda quitar
                    dialog = builder.create();
                    dialog.show();//abrimos el dialog
                    return true;
                default:
                    return false;
            }
        }
    }
}
