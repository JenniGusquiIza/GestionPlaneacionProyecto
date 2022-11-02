package com.system.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.system.R;
import com.system.models.Proyecto;
import com.system.ui.utils.Routes;
import com.system.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    private Unbinder unbinder;
    private DatabaseReference dbReference;
    private  HomeAdapter homeAdapter;
    private SwipeRefreshLayout refresh;
    private static List<Proyecto> proyectoList;
    @Nullable @BindView(R.id.recyclerGrafica) RecyclerView recyclerView;
    //@BindView(R.id.PieChart) PieChart chart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder= ButterKnife.bind(this, root);
        initData();
        refresh=(SwipeRefreshLayout) root.findViewById(R.id.rlGrafico);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                Utils.messageConfirmation(getContext(),"actualizando");
            }
        });
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        homeAdapter =new HomeAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(homeAdapter);


    }

    //region METODOS ADICIONAL
    private void initData() {
        dbReference= FirebaseDatabase.getInstance().getReference().child(Routes.TreeProyecto);
        Query q=dbReference.orderByChild("estado").equalTo(true);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                proyectoList=new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    Proyecto proyecto=data.getValue(Proyecto.class);
                    String fecha=Utils.convertDefaultDateToString2();
                    if(proyecto.getFechaFin().equals(fecha)){
                        dbReference
                                .child(proyecto.getCodigo())
                                .child("estado")
                                .setValue(false);
                    }else{
                        proyectoList.add(proyecto);
                    }

                }
                homeAdapter.setProyectoList(proyectoList);
                homeAdapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SERVICIO PROYECTO", "buscar proyectos", error.toException());
                Utils.messageError(getContext(),"Error:"+error.getMessage());
            }
        });
    }
    //endregion

    /*
    void initPieChart(){
        ArrayList<PieEntry> pieEntries=new ArrayList<>();
        for (int i=0; i<5;i++){
            float value= i*10;
            pieEntries.add(new PieEntry(value,"Valor"+i));
        }
        PieDataSet pieDataSet=new PieDataSet(pieEntries,"Libros Obra");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //DATA
        PieData pieData=new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter(chart));
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);
        chart.setData(pieData);
        chart.animateXY(5000,5000);
        chart.invalidate();

        //
        chart.setDrawHoleEnabled(true);
        chart.setUsePercentValues(true);
        chart.setEntryLabelTextSize(10f);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setCenterText("Libro Obra");
        chart.setCenterTextSize(20);
        chart.getDescription().setEnabled(false);

        Legend l= chart.getLegend();
        l.setFormSize(5);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }
    void initAnyChartPie(){
        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("John", 10000));
        data.add(new ValueDataEntry("Jake", 12000));
        data.add(new ValueDataEntry("Peter", 18000));
        pie.setData(data);
        anyChartView.setChart(pie);
    }
    */


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}