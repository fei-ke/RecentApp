package com.fei_ke.recentapp;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.inputmethod.EditorInfo;

import com.fei_ke.recentapp.provider.Settings;

/**
 * Created by 杨金阳 on 16/2/2015.
 */
public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
        setPreferenceScreen(preferenceScreen);

        Settings settings = new Settings(getActivity());

        EditTextPreference ep = new EditTextPreference(getActivity());
        ep.getEditText().setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        ep.setSummary(settings.getAppCount() + "");
        ep.setKey(Settings.KEY_APP_COUNT);
        ep.setDefaultValue("15");
        ep.setTitle("显示应用个数");
        ep.setDialogTitle("显示应用个数");
        ep.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        });
        preferenceScreen.addPreference(ep);
    }

}
