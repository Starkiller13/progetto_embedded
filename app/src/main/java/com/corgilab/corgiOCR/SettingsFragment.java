package com.corgilab.corgiOCR;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import java.io.File;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private MainActivity activity;
    private static final String KEY_PREF_APP_THEME = "Theme";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        this.activity = (MainActivity)getActivity();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        Preference feed = (Preference) findPreference("Feedback");
        assert feed != null;
        feed.setOnPreferenceClickListener(preference -> {
            DialogFragment dialog = new FeedbackDialog();
            dialog.show(getParentFragmentManager(), "com.corgilab.corgiOCR.FeedbackDialog");
            return true;
        });
        Preference cache = (Preference) findPreference("Cache");
        assert cache != null;
        cache.setOnPreferenceClickListener(preference -> {
            File cacheDir = requireActivity().getCacheDir();
            if(cacheDir!=null){
                Camera_Gallery_activity.deleteTempFiles(cacheDir);
                Toast.makeText(getContext(),"Cache Cleared!", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"Failed!", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(isAdded()){
        SharedPreferences.Editor editor = sharedPreferences.edit();
            if ("Theme".equals(key)) {
                boolean isChecked = sharedPreferences.getBoolean(KEY_PREF_APP_THEME, false);
                Intent i = new Intent(activity, MainActivity.class);
                editor.putBoolean("DarkThemeOn", isChecked);
                editor.apply();
                activity.finish();
                i.putExtra("settingsChanged", 1);
                startActivity(i);
            }
        }
    }

}
