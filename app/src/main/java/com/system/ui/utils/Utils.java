package com.system.ui.utils;

import static java.time.temporal.ChronoUnit.DAYS;

import android.content.Context;
import android.os.Build;
import android.util.Patterns;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.system.models.Persona;
import com.system.models.Usuario;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static Usuario auth=null;

    public static Animation animation1(){
        RotateAnimation rotateAnimation= new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setDuration(1000);
        rotateAnimation.cancel();
        return rotateAnimation;
    }

    public static String[] Roles() {
        return new String[]{"Gerente General","Jefe Planeación","Supervisor"};
    }
    public static void messageConfirmation(Context context, String cadena){
        Toast toast= Toast.makeText(context, cadena, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.show();
    }

    public static void messageError(Context context, String cadena){
        Toast toast= Toast.makeText(context, cadena, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.show();
    }

    @NonNull
    public static String convertDefaultDateToString() {
        SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sf.format(Calendar.getInstance().getTime());
    }
    @NonNull
    public static String convertDefaultDateToString2() {
        SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
        return sf.format(Calendar.getInstance().getTime());
    }
    @NonNull
    public static String calculateDay(Date fecha1, Date fecha2){

        long startTime = fecha1.getTime();
        long endTime = fecha2.getTime();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);
        return diffDays==1? diffDays+" día":diffDays+" días";
    }


    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String calculateAge(Date date1,Date date2) {
        if (date1 != null && date2!=null) {
            LocalDate start = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Period edad = Period.between(start, end);
            if(edad.getYears()>0){
                return String.format("%d años, %d meses y %d días",
                        edad.getYears(),
                        edad.getMonths(),
                        edad.getDays());
            }else{
                return String.format("%d meses y %d días",
                        edad.getMonths(),
                        edad.getDays());
            }
        }
        return "";
    }

    @NonNull
    public static  String convertDateToString(Date date){
        SimpleDateFormat sf=new SimpleDateFormat("dd-MM-yyyy");
        return sf.format(date) ;
    }

    public static boolean validateEmail(String string) {
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        //boolean isValid=string.matches(emailPattern);
        boolean isValid= Patterns.EMAIL_ADDRESS.matcher(string).matches();
        return  isValid;
    }
    public static boolean validateDateString(@NonNull String cadena){
        String regex="[0-9]{2}+/[0-9]{2}+/[0-9]{4}";
        return cadena.matches(regex);
    }

    public static boolean validateCedula(String cedula) {
        boolean verificar = false;
        char[] LenghtCedula = cedula.toCharArray();
        String DigitRegion = cedula.substring(0, 2);
        int sumatotal = 0;
        if ((Integer.parseInt(DigitRegion) >= 1) && (Integer.parseInt(DigitRegion) <= 24)) {
            for (int i = 0; i < cedula.length() - 1; i++) {
                int numero = Integer.parseInt(Character.toString(LenghtCedula[i]));
                if ((i + 1) % 2 == 1) {
                    numero = Integer.parseInt(Character.toString(LenghtCedula[i])) * 2;
                    if (numero > 9) {
                        numero = numero - 9;
                    }
                }
                sumatotal += numero;
            }
            sumatotal = 10 - (sumatotal % 10);

            if (sumatotal != Integer.parseInt(cedula.substring(9))) {
                verificar = false;

            } else {
                verificar = true;
            }
        } else {
            verificar = false;
        }
        return verificar;

    }

    public static String validateRUC(@NonNull String numero) {
        int longitud = numero.length();
        String cadena="";
        if (longitud != 13) {
            cadena= "identificación es incorrecto";
        } else if (longitud == 13) {
            String ruc = numero.substring(10);
            if (ruc.equals("001")) {
                if (validateCedula(numero.substring(0, 10)) != true) {
                    cadena= "Ruc incorrecto Verifique los 10ero números";
                }
            } else {
                cadena= "identificación ruc es incorrecto";
            }

        }
        return cadena;
    }
}
