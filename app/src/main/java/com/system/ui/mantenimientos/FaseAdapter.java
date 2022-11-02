package com.system.ui.mantenimientos;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.reflect.KVisibility;

public class FaseAdapter extends RecyclerView.Adapter<FaseAdapter.FaseViewHolder>{
    private Context context;
    private  FaseFragment fragment;
    private List<Fase> faseList;
    private  Fase fase;

    public FaseAdapter(Context context,FaseFragment fragment) {
        this.context=context;
        this.fragment=fragment;

    }

    public void setFaseList(List<Fase> faseList) {
        this.faseList = faseList;
    }

    @NonNull
    @Override
    public FaseAdapter.FaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_fases, parent, false);
        return new FaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaseAdapter.FaseViewHolder holder, int position) {
        if (faseList != null && faseList.size() > 0) {
            fase= faseList.get(position);
            holder.itemCodigo.setText(""+(position+1));
            holder.itemDescripcion.setText(fase.getDescripcion());
            holder.itemEstado.setText(fase.isEstado()? "ACTIVO":"INACTIVO");
            holder.itemEstado.setTextColor(fase.isEstado()?Color.BLACK:Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return faseList!=null? faseList.size() : 0;
    }

    public class FaseViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

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

        public FaseViewHolder(@NonNull View itemView) {
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
                    view = inflater.inflate(R.layout.dialog_fase, null);
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
            fase= faseList.get(getAdapterPosition());
            txtDescripcion=  view.findViewById(R.id.txtDescripcion);
            rgEstado= view.findViewById(R.id.rbGroupEstado);
            rgEstado.check(fase.isEstado()?R.id.rbActivo:R.id.rbInactivo);
            txtDescripcion.getEditText().setText(fase.getDescripcion());
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
            fase= faseList.get(getAdapterPosition());
            TextView txtMensaje=  view.findViewById(R.id.txtMensaje);
            txtMensaje.setText(fase.getDescripcion());
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
                        fase.setDescripcion(txtDescripcion.getEditText().getText().toString());
                        fase.setEstado(estado.getText().charAt(0)=='A');
                        fragment.editar(fase);
                        dialog.dismiss();
                    }
                case R.id.bnNo: // Layout dialog_delete
                    dialog.dismiss();
                    break;
                case R.id.bnSi: // Layout dialog_delete
                    fragment.eliminar(fase);
                    dialog.dismiss();
                    break;
            }
        }
    }
}
