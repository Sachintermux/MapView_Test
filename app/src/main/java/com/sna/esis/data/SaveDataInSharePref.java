package com.sna.esis.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.prefs.Preferences;

public class SaveDataInSharePref {


    public void saveData( Context context, String  TAG, String data ){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG,data);
        editor.commit();
    }

    public String getData(Context context, String TAG, String defaultData){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
       return sharedPreferences.getString(TAG,defaultData).toString();
    }


}
