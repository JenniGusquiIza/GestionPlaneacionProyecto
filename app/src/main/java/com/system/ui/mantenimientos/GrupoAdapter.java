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
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.system.R;
import com.system.models.Fase;
import com.system.models.Grupo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.ViewHolder>{
    private Context context;
    private  GrupoFragment fragment;
    private List<Grupo> grupoList;
    private  Grupo grupo;

    public GrupoAdapter(Context context, GrupoFragment fragment) {
        this.context=context;
        this.fragment=fragment;

    }

    public void setGrupoList(List<Grupo> grupoList) {
        this.grupoList = grupoList;
    }

    @NonNull
    @Override
    public GrupoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_fases, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoAdapter.ViewHolder holder, int position) {
        if (grupoList != null && grupoList.size() > 0) {
            grupo= grupoList.get(position);
            holder.itemCodigo.setText(""+(position+1));
            holder.itemDescripcion.setText(grupo.getDescripcion());
            holder.itemEstado.setText(grupo.getEstado().equals("A")? "ACTIVO":"INACTIVO");
            holder.itemEstado.setTextColor(grupo.getEstado().equals("A")?Color.BLACK:Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return grupoList!=null? grupoList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

        @BindView(R.id.txtCodigo) TextView itemCodigo;
        @BindView(R.id.txtDescripcion)  TextView itemDescripcion;
        @BindView(R.id.txtEstado)  TextView itemEstado;
        @BindView(R.id.bnMenu) ImageButton itemMenu;

        //DIALOGO FASE
        private AlertDialog dialog;
        private AlertDialog.Builder builder;
        TextInputLayout txtDescripcion;
        RadioGroup rgEstado;
        Button bnCancelar, bnGuardar;

        public ViewHolder(@NonNull View itemView) {
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
        public boolean onMenuItemClick(@NonNull MenuItem item) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view;
            switch (item.getItemId()) {
                case R.id.itemViewer:
                    view = inflater.inflate(R.layout.dialog_grupo, null);
                    View_Update(view,R.id.itemViewer);
                    builder.setView(view); //agregamos el dialogo
                    builder.setCancelable(false); //configuramos para que no se pueda quitar
                    dialog = builder.create();
                    dialog.show();//abrimos el dialog
                    return true;
                case R.id.itemUpdate:
                    view = inflater.inflate(R.layout.dialog_fase, null);
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
            grupo= grupoList.get(getAdapterPosition());
            txtDescripcion=  view.findViewById(R.id.txtDescripcion);
            rgEstado= view.findViewById(R.id.rbGroupEstado);
            rgEstado.check(grupo.getEstado().equals("A")?R.id.rbActivo:R.id.rbInactivo);
            txtDescripcion.getEditText().setText(grupo.getDescripcion());
            bnGuardar = view.findViewById(R.id.bnGuardar);
            bnCancelar  = view.findViewById(R.id.bnCancelar);
            bnGuardar.setOnClickListener(this);
            bnCancelar.setOnClickListener(this);
            //
            bnGuardar.setVisibility(R.id.itemUpdate==value?View.VISIBLE:View.INVISIBLE);
            txtDescripcion.setEnabled(R.id.itemUpdate==value?true:false);
            rgEstado.setEnabled(R.id.itemUpdate==value?true:false);
        }
        private void Delete(@NonNull View view) {
            grupo= grupoList.get(getAdapterPosition());
            TextView txtMensaje=  view.findViewById(R.id.txtMensaje);
            txtMensaje.setText(grupo.getDescripcion());
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
                    if (true) {
                        RadioButton estado = dialog.findViewById(rgEstado.getCheckedRadioButtonId());
                        grupo.setDescripcion(txtDescripcion.getEditText().getText().toString());
                        grupo.setEstado(""+estado.getText().charAt(0));
                        fragment.editar(grupo);
                        dialog.dismiss();
                    }
                case R.id.bnNo: // Layout dialog_delete
                    dialog.dismiss();
                    break;
                case R.id.bnSi: // Layout dialog_delete
                    fragment.eliminar(grupo);
                    dialog.dismiss();
                    break;
            }
        }
    }
}
