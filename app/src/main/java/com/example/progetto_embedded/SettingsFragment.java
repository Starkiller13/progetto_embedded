package com.example.progetto_embedded;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_APP_THEME = "Theme";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key)
        {
            case "Theme":
                boolean isChecked = sharedPreferences.getBoolean(KEY_PREF_APP_THEME,false);
                    Intent i = new Intent(getContext(),MainActivity.class);
                    i.putExtra("ThemeChanged",Boolean.toString(!isChecked));
                    startActivity(i);
                break;
        }
    }
}
