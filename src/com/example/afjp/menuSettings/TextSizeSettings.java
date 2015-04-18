package com.example.afjp.menuSettings;

import com.example.afjp.R;
import com.example.afjp.R.xml;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@SuppressLint("NewApi")
public class TextSizeSettings extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the location_preferences from an XML resource
        addPreferencesFromResource(R.xml.textseize_preferences);
    }

}
