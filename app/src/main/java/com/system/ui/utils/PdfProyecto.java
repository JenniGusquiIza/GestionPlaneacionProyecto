package com.system.ui.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

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
import com.system.R;
import com.system.models.Actividad;
import com.system.models.Fase;
import com.system.models.MaterialSelect;
import com.system.models.Persona;
import com.system.models.Producto;
import com.system.models.Proyecto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class PdfProyecto {
    private Proyecto proyecto;
    private Context context;
    //PRINT PDF
    File pdfFile;
    private Document document;
    private Paragraph paragraph;
    private Font fontTitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fontsubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    private Font fontDefault= new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);


    public PdfProyecto(Proyecto proyecto, Context context) {
        this.proyecto = proyecto;
        this.context = context;
    }

    public Uri  createPDF(){
        try{
            createFile();
            document = new Document(PageSize.A4);
            FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            MetaData();
            title();
            document.add(pageHeader());
            document.add(detailHeader());
            document.close();
            fileOutputStream.close();
        }catch (Exception ex){
            Utils.messageError(context,"Error creacion pdf: "+ex.getMessage());
        }
        //VIEW PDF

        if (pdfFile.exists()) {
            Uri uri;
            if (Build.VERSION.SDK_INT < 24) {
                uri = Uri.fromFile(pdfFile);
            } else {
                uri = Uri.parse(pdfFile.getPath()); // My work-around for new SDKs, worked for me in Android 10 using Solid Explorer Text Editor as the external editor.
            }
            return  uri;
        }
        return  null;
    }
    //region CREACION DOCUMENTO
    private void createFile() {
        //CREACION DEL DIRECTORIO
        String urlString= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            /*File folde
            r = new File(Environment.getExternalStorageDirectory(), "PDF");
            if (!folder.exists())
                folder.mkdirs();*/

        //RUTA DEL DOCUMENTO
        pdfFile = new File(urlString,  "libro.pdf");
        if (pdfFile.exists())
            pdfFile.delete();
    }

    private void MetaData() {
        document.addTitle("Libro Obra");
        document.addSubject(proyecto.getNombre());
        document.addAuthor(Utils.auth.getCodigo());
        document.addCreator("CodeSteven");
        document.addCreationDate();
    }

    private void title() throws DocumentException {
        paragraph = new Paragraph();
        try {
            Drawable d= ContextCompat.getDrawable(context, R.drawable.logo_electro);
            Bitmap bmp=((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
            Image logo=Image.getInstance(stream.toByteArray());
            //logo.setWidthPercentage(80);
            logo.scaleToFit(90,90);
            logo.setAlignment(Chunk.ALIGN_MIDDLE);
            Paragraph titulo = new Paragraph("LIBRO OBRA", fontTitle);
            titulo.setAlignment(Element.ALIGN_CENTER);
            paragraph.add(titulo); //add
            document.add(logo);
            document.add(paragraph);

        } catch (Exception e) {
            Utils.messageError(context,"Error crear header pdf: "+e.getMessage());
        }

    }

    @NonNull
    private PdfPTable pageHeader(){
        PdfPTable pdfPTable = new PdfPTable(new float[]{0.8f,0.8f,0.7f,1f});
        pdfPTable.setWidthPercentage(90);
        dateTable(pdfPTable,20f,Element.ALIGN_LEFT,false,"Programa Inversión: ",0,1);
        infoTable(pdfPTable,20f,Element.ALIGN_LEFT,"RECURSOS PROPIOS",0,1);
        dateTable(pdfPTable,20f,Element.ALIGN_LEFT,false,"Registro Nº: ",0,1);
        infoTable(pdfPTable,20f,Element.ALIGN_LEFT,proyecto.getCodigo(),0,1);

        dateTable(pdfPTable,20f,Element.ALIGN_LEFT,false,"Contrato Obra: ",0,1);
        infoTable(pdfPTable,20f,Element.ALIGN_LEFT,proyecto.getNumero(),0,1);
        dateTable(pdfPTable,20f,Element.ALIGN_LEFT,false,"Monto del Contrato: ",0,1);
        infoTable(pdfPTable,20f,Element.ALIGN_LEFT,""+(Math.round(proyecto.getMonto()*100.0)/100.0),0,1);

        return  pdfPTable;
    }

    @NonNull
    private PdfPTable  detailHeader(){
        PdfPTable pdfPTable= new PdfPTable(1);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setSpacingBefore(10);

        PdfPTable pdfPTable1= new PdfPTable(new float[]{0.6f,1f,0.6f,1f});

        dateTable(pdfPTable1,20f,Element.ALIGN_CENTER,true,"OBJETO DEL CONTRATO OBRA: ",1,4);
        infoTable(pdfPTable1,40f,Element.ALIGN_JUSTIFIED,proyecto.getDescripcion(),1,4);
        pdfPTable1.setSpacingAfter(30);
        pdfPTable.addCell(pdfPTable1);
        //
        PdfPTable pdfPTable2= new PdfPTable(new float[]{0.6f,1f,0.6f,1f});

        dateTable(pdfPTable2,20f,Element.ALIGN_CENTER,true,"UBICACIÓN DE LA OBRA: ",1,4);

        dateTable(pdfPTable2,20f,Element.ALIGN_JUSTIFIED,false,"Fecha de Inicio: ",1,2);
        dateTable(pdfPTable2,20f,Element.ALIGN_JUSTIFIED,false,"Fecha de Terminacion: ",1,2);
        infoTable(pdfPTable2,20f,Element.ALIGN_JUSTIFIED,proyecto.getFechaInicio(),1,2);
        infoTable(pdfPTable2,20f,Element.ALIGN_JUSTIFIED,proyecto.getFechaFin(),1,2);

        dateTable(pdfPTable2,20f,Element.ALIGN_JUSTIFIED,false,"Nombre Ingeniero - Empresa Constructora",1,2);
        dateTable(pdfPTable2,20f,Element.ALIGN_JUSTIFIED,false,"Nombre de la Arq. Fiscalizadora",1,2);
        infoTable(pdfPTable2,20f,Element.ALIGN_JUSTIFIED,proyecto.getNombre(),1,2);
        infoTable(pdfPTable2,20f,Element.ALIGN_JUSTIFIED,proyecto.getCliente().getApellidos()+" "+ proyecto.getCliente().getNombres(),1,2);
        pdfPTable.addCell(pdfPTable2);
        //
        PdfPTable pdfPTable3= new PdfPTable(1);
        dateTable(pdfPTable3,20f,Element.ALIGN_CENTER,true,"DESCRIPCIÓN DEL PROCESO DE CONSTRUCCIÓN(ACTIVIDADES REALIZADAS): ",1,1);
        for (Fase fase: proyecto.getFases().values()){
            dateTable(pdfPTable3,20f,Element.ALIGN_JUSTIFIED,false,"  "+fase.getDescripcion().toUpperCase(),1,1);
            for (Actividad actividad: fase.getActividades().values()){
                infoTable(pdfPTable3,20f,Element.ALIGN_BASELINE,"   -   "+actividad.getDescripcion(),0,1);
            }
        }
        pdfPTable3.setSpacingAfter(20);
        pdfPTable.addCell(pdfPTable3);

        //MATERIALES Y PERSONAL
        PdfPTable pdfPTable4= new PdfPTable(1);
        dateTable(pdfPTable4,20f,Element.ALIGN_CENTER,true,"MATERIALES, EQUIPOS Y HERRAMIENTAS",1,1);
        for(MaterialSelect material:proyecto.getMateriales().values()){
            infoTable(pdfPTable4,20f,Element.ALIGN_BASELINE,"  - "+material.getMaterial()+"("+material.getOcupado()+")",0,1);
        }
        PdfPTable pdfPTable5= new PdfPTable(1);
        dateTable(pdfPTable5,20f,Element.ALIGN_CENTER,true,"PERSONAL",1,1);
        for(Persona persona:proyecto.getTrabajadores().values()){
            infoTable(pdfPTable5,20f,Element.ALIGN_BASELINE,"  - "+persona.getApellidos().toUpperCase()+" "+persona.getNombres().toUpperCase(),0,1);
        }
        PdfPTable pdfPTable6= new PdfPTable(2);
        pdfPTable6.addCell(pdfPTable4);
        pdfPTable6.addCell(pdfPTable5);
        pdfPTable3.setSpacingAfter(20);
        pdfPTable.addCell(pdfPTable6);

        //OBSERVACIONES
       if(proyecto.getObservaciones()!=null){
           PdfPTable pdfPTable7= new PdfPTable(1);
           dateTable(pdfPTable7,20f,Element.ALIGN_CENTER,true,"OBSERVACIONES O COMENTARIOS: ",1,1);

           for(String key : proyecto.getObservaciones().keySet()){
               String cadena=proyecto.getObservaciones().get(key);
               infoTable(pdfPTable7,20f,Element.ALIGN_JUSTIFIED,"  - "+cadena,0,1);
           }
           pdfPTable7.setSpacingAfter(30);
           pdfPTable.addCell(pdfPTable7);
       }
        //
        PdfPTable pdfPTable8= new PdfPTable(2);
        dateTable(pdfPTable8,20f,Element.ALIGN_CENTER,true,"CONDICIONES CLIMÁTICAS",1,1);
        dateTable(pdfPTable8,20f,Element.ALIGN_CENTER,true,"TIEMPO TRABAJADOS(DURACI)",1,1);
        infoTable(pdfPTable8,20f,Element.ALIGN_CENTER,"FAVORABLE",0,1);
        infoTable(pdfPTable8,20f,Element.ALIGN_CENTER,proyecto.getDuracion()+ " LUNES-VIERNES",0,1);
        pdfPTable.addCell(pdfPTable8);
        //
        PdfPTable pdfPTable9= new PdfPTable(2);
        dateTable(pdfPTable9,20f,Element.ALIGN_CENTER,true,"FIRMA DEL REPRESENTANTE LEGAL",1,1);
        dateTable(pdfPTable9,20f,Element.ALIGN_CENTER,true,"FIRMA DEL ARQ. FISCALIZADOR(A)",1,1);
        infoTable(pdfPTable9,40f,Element.ALIGN_BOTTOM,"",0,1);
        infoTable(pdfPTable9,40f,Element.ALIGN_BOTTOM,"",0,1);
        infoTable(pdfPTable9,30f,Element.ALIGN_CENTER,"________________________________________\n\nFIRMA"
                ,0,1);
        infoTable(pdfPTable9,30f,Element.ALIGN_CENTER,"________________________________________\n\nFIRMA "
                ,0,1);
        pdfPTable.addCell(pdfPTable9);

        return  pdfPTable;
    }
    private void dateTable(@NonNull PdfPTable pdfPTable,float height,int element,boolean isColor, String titulo,int border,int colSpan) {
        PdfPCell cell = new PdfPCell(new Phrase(titulo, fontsubTitle));
        cell.setBorder(border);
        cell.setFixedHeight(height);
        cell.setColspan(colSpan);
        cell.setHorizontalAlignment(element);
        if(isColor){
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        }

        pdfPTable.addCell(cell);
    }

    private void infoTable(@NonNull PdfPTable pdfPTable,float height,int element, String subtitulo,int border,int colSpan) {
        PdfPCell cell = new PdfPCell(new Phrase(subtitulo, fontDefault));
        cell.setBorder(border);
        cell.setFixedHeight(height);
        cell.setColspan(colSpan);
        cell.setHorizontalAlignment(element);
        pdfPTable.addCell(cell);
    }

}
