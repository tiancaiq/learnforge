package com.learnforge.learning.utils;

public class TableInfoContext {

    private static final ThreadLocal<String> TL = new ThreadLocal<>();


    public static void set(String value){
        TL.set(value);
    }

    public static String getInfo(){
        return TL.get();
    }
    public static void remove(){
        TL.remove();
    }


}
