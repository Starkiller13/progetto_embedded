package com.corgilab.corgiOCR;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private MainActivity activity;
    public static final String KEY_PREF_APP_THEME = "Theme";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        this.activity = (MainActivity)getActivity();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(isAdded()){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch(key)
        {
            case "Theme":
                boolean isChecked = sharedPreferences.getBoolean(KEY_PREF_APP_THEME,true);
                Intent i = new Intent(activity,MainActivity.class);
                        editor.putBoolean("DarkThemeOn",isChecked);
                        editor.apply();
                        activity.finish();
                        i.putExtra("settingsChanged",1);
                        startActivity(i);

                break;
        }
        }
    }

}
