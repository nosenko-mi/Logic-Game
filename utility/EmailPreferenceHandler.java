package com.ltl.mpmp_lab3.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.ltl.mpmp_lab3.constants.IntentExtra;

public class EmailPreferenceHandler {
    private EmailPreferenceHandler(){}

    public static void put(Context context, boolean isChecked){
        SharedPreferences settings = context.getSharedPreferences(IntentExtra.IS_EMAIL_ENABLED_EXTRA.getValue(), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("switchkey", isChecked);
        editor.apply();
    }

    public static boolean get(Context context){
        SharedPreferences settings = context.getSharedPreferences(IntentExtra.IS_EMAIL_ENABLED_EXTRA.getValue(), 0);
        return settings.getBoolean("switchkey", false);
    }
}
