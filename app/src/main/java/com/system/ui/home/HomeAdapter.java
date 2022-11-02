package com.system.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.Cell;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.LegendLayout;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.system.R;
import com.system.models.Actividad;
import com.system.models.Fase;
import com.system.models.Persona;
import com.system.models.Producto;
import com.system.models.Proyecto;
import com.system.ui.ViewPDFActivity;
import com.system.ui.procesos.ProyectoAdapter;
import com.system.ui.utils.PdfProyecto;
import com.system.ui.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{

    private Context context;
    private HomeFragment fragment;
    private Proyecto proyecto;
    private List<Proyecto> proyectoList;



    public HomeAdapter(@NonNull HomeFragment fragment) {
        this.fragment=fragment;
        this.context=fragment.getContext();
    }
    public void setProyectoList(List<Proyecto> proyectoList) {
        this.proyectoList = proyectoList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_graficas, parent, false);
        return new HomeAdapter.HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        if (proyectoList != null && proyectoList.size() > 0) {
            proyecto= proyectoList.get(position);
            Pie pie=initAnyChartPie(proyecto);
            holder.itemChartPie.setChart(pie);
            holder.itemTitulo.setText(proyecto.getNombre().toUpperCase());
        }
    }
    @NonNull
    private Pie initAnyChartPie(Proyecto proyecto){
        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        List<Fase> fases= proyecto.getFases().values().stream().collect(Collectors.toList());
        double valorTotal=0;
        for (Fase fase:fases){
            int count=0;
            if(fase.getActividades()!=null){
                List<Actividad>actividads=  fase.getActividades().values().stream().collect(Collectors.toList());
                for(int i=0;i<actividads.size();i++){
                    Actividad actividad=actividads.get(i);
                    if(actividad.isEstado()){
                        count++;
                    }
                }
                double valor= ((100*count)/actividads.size())/ fases.size();
                valorTotal+=valor;
                data.add(new ValueDataEntry(fase.getDescripcion(), valor));
            }else{
                data.add(new ValueDataEntry(fase.getDescripcion(), 0));
            }

        }
        data.add(new ValueDataEntry("RESTANTE",100-valorTotal));
        pie.setData(data);
        pie.setPalette( new String[] {"#001085","#7C7C7C","#ff4800","#ff0d00"});
        //pie.setTitle();
        pie.getLabels()
                .setPosition("outside")
                .setMaxFontSize(9.0)
                .setFontSize(7.0);
        //pie.getLegend().getTitle().setEnabled(true);
        //pie.getLegend().getTitle().setText(proyecto.getCliente()).setPadding(0d, 0d, 10d, 0d);
        pie.getLegend()
                .setPosition("center-bottom")
                .setItemsLayout(LegendLayout.HORIZONTAL)
                .setIconSize(7.0)
                .setAlign("center")
                .setFontSize(8.0);
        return pie;
    }
    @Override
    public int getItemCount() {
        return proyectoList!=null? proyectoList.size() : 0;
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chartPie) AnyChartView itemChartPie;
        @BindView(R.id.txtTitulo) TextView itemTitulo;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.bnMenu)
        void submit(){
            proyecto=proyectoList.get(getAdapterPosition());
            PdfProyecto pdf=new PdfProyecto(proyecto,context);
            Uri uri=pdf.createPDF();
            if(uri!=null){
                   /*Intent intent= new Intent(context, ViewPDFActivity.class);
                intent.putExtra("pdf_url",uri.getPath());
                fragment.startActivity(intent);*/
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    fragment.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Utils.messageConfirmation(context, "No cuenta con una aplicación para visualizar archivo pdf");
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                }
            }else{
                Utils.messageError(context,"No existe archivo para visualizar");
            }

        }


    }


}
