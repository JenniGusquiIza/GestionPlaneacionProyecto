package com.system.ui.procesos;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.R;
import com.system.models.Fase;
import com.system.models.Proyecto;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProyectoFragment extends Fragment {

    private Unbinder unbinder;
    private DatabaseReference dbReference;
    private NavController navController;
    private static List<Proyecto> proyectoList;

    private ProyectoAdapter proyectoAdapter;
    @Nullable @BindView(R.id.recyclerProyecto) RecyclerView recyclerView;
    @Nullable @BindView(R.id.bnCrear) Button bnCrear;
    @Nullable @BindView(R.id.bnAprobar) Button bnAprobar;
    TextView txtCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_proyecto, container, false);
        unbinder= ButterKnife.bind(this, root);
        txtCount=(TextView) root.findViewById(R.id.txtCount);
        permisoView();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        proyectoAdapter=new ProyectoAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(proyectoAdapter);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }


    @OnClick(R.id.bnCrear)
    void submitCrear(){
        navController.navigate(R.id.nav_proyectoAdd);
    }
    @OnClick(R.id.bnAprobar)
    void submitCrar(){
        navController.navigate(R.id.nav_proyectoAprobar);
    }


    //region METODOS ADICIONAL
    private void initData() {
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                proyectoList=new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){

                    Proyecto proyecto=data.getValue(Proyecto.class);
                    proyectoList.add(proyecto);
                }
                txtCount.setText(""+proyectoList.size());
                proyectoAdapter.setProyectoList(proyectoList);
                proyectoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO PROYECTO", "buscar proyectos", error.toException());
                Utils.messageError(getContext(),"Error:"+error.getMessage());
            }
        });
    }

    public NavController getNavController() {
        return navController;
    }

    private void permisoView(){
        for (String rol:Utils.auth.getRoles()){
            char cadena=rol.toUpperCase().charAt(0);
            if(cadena=='G' || cadena=='A' || cadena=='J'){
                bnAprobar.setVisibility(View.VISIBLE);
                bnCrear.setVisibility(View.VISIBLE);
                if(cadena=='J'){
                    bnAprobar.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        bnAprobar.setTooltipText("Permiso denegado");
                    }
                }

                break;
            }else if(cadena=='S'){
                bnCrear.setVisibility(View.GONE);
            }
        }
    }
    //endregion
}