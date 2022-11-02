package com.system.ui.mantenimientos;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.system.R;
import com.system.models.Persona;
import com.system.services.PersonaService;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
public class PersonaAdapter extends RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder> implements Filterable {
    private Context context;
    private List<Persona> personaList;
    private List<Persona> personaFilterList;
    private  Persona persona;

    public PersonaAdapter(Context context) {
        this.context=context;

    }

    public void setPersonaList(List<Persona> personaList) {
        this.personaList = personaList;
        this.personaFilterList=personaList;
    }


    @NonNull
    @Override
    public PersonaAdapter.PersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_personas, parent, false);
        return new PersonaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonaAdapter.PersonaViewHolder holder, int position) {
        if (personaList != null && personaList.size() > 0) {
            persona= personaList.get(position);
            holder.itemCodigo.setText(persona.getCodigo());
            holder.itemNombre.setText(persona.getNombres()+" "+persona.getApellidos());
            holder.itemEstado.setText(persona.isEstado()? "ACTIVO":"INACTIVO");
            holder.itemEstado.setTextColor(persona.isEstado()?Color.BLACK:Color.RED);
            holder.cardPersona.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition));

        }
    }

    @Override
    public int getItemCount() {
        return personaList!=null? personaList.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter =new Filter() {
        @Nullable
        @Contract(pure = true)
        @Override
        protected FilterResults performFiltering(@NonNull CharSequence constraint) {
            List<Persona> filteredLis=new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredLis.addAll(personaList);
            }else{
                for (Persona persona: personaList) {
                    if(persona.getApellidos().toUpperCase().contains(constraint.toString().toUpperCase())){
                        filteredLis.add(persona);
                    }

                }
            }
            FilterResults filterResult= new FilterResults();
            filterResult.values=filteredLis;
            return  filterResult;
        }

        @Override
        protected void publishResults(CharSequence constraint, @NonNull FilterResults results) {
            personaFilterList.clear();
            personaFilterList.addAll((Collection<? extends Persona>) results.values);
            notifyDataSetChanged();
        }
    };

    public class PersonaViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtCodigo)  TextView itemCodigo;
        @BindView(R.id.txtNombre)  TextView itemNombre;
        @BindView(R.id.txtEstado)  TextView itemEstado;
        @BindView(R.id.card_personas) RelativeLayout cardPersona;
        public PersonaViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
