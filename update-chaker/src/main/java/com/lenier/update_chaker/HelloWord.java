package com.lenier.update_chaker;

import android.util.Log;

public class HelloWord {
    public static void main(String[] args){
        prueba();
    }

        public static void prueba() {
            System.out.println("Probando");
    }

    public static void mensaje(String tag,String mensaje) {
        Log.e(tag,mensaje);
    }


}
