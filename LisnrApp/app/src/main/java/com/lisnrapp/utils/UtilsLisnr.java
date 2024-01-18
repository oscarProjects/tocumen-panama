package com.lisnrapp.utils;

import android.os.Build;

public class UtilsLisnr {

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return buildUpperCase(model);
        } else {
            return buildUpperCase(manufacturer) + " " + model;
        }
    }


    private static String buildUpperCase(String cadena) {
        if (cadena == null || cadena.length() == 0) {
            return "";
        }
        char primeraLetra = cadena.charAt(0);
        if (Character.isUpperCase(primeraLetra)) {
            return cadena;
        } else {
            return Character.toUpperCase(primeraLetra) + cadena.substring(1);
        }
    }

}
