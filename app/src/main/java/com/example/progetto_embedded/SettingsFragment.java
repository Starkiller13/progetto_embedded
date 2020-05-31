package com.example.progetto_embedded;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String KEY_PREF_APP_THEME = "Theme";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
