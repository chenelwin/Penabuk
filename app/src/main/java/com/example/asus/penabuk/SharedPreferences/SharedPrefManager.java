package com.example.asus.penabuk.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String SP_PENABUK = "spPenabuk";

    public static final String SP_NAMA = "spNama";
    public static final String SP_EMAIL = "spEmail";
    public static final String SP_BALANCE = "spBalance";
    public static final String SP_ID = "spId";
    public static final String SP_NOHP = "spNohp";
    public static final String SP_SUDAH_LOGIN = "spSudahLogin";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_PENABUK, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPId(){
        return sp.getString(SP_ID, "");
    }

    public String getSPNama(){
        return sp.getString(SP_NAMA, "");
    }

    public String getSPEmail(){
        return sp.getString(SP_EMAIL, "");
    }

    public String getSPBalance(){
        return sp.getString(SP_BALANCE, "");
    }

    public String getSPNohp(){ return sp.getString(SP_NOHP, ""); }

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
