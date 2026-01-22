package com.example.vroom.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class StorageManager {
    private static final String PREFS_NAME = "secure_prefs";
    private static SharedPreferences prefs;

    public static SharedPreferences getSharedPreferences(Context context){
        try{
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            prefs = EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        }catch(GeneralSecurityException | IOException e){
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

        return prefs;
    }

    public static void saveData(String key, String value){
        prefs.edit().putString(key, value).apply();
    }

    public static String getData(String key, String value){
        return prefs.getString(key, value);
    }

    public static void saveLong(String key, long value){
        prefs.edit().putLong(key, value).apply();
    }

    public static long getLong(String key, long value){
        return prefs.getLong(key, value);
    }

    public static void clearAll(){
        if(prefs != null){
            prefs.edit().clear().apply();
        }
    }
}
