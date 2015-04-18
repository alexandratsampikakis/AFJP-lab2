package com.example.afjp.menuSettings;

import java.util.List;

import com.example.afjp.R;
import com.example.afjp.R.xml;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;

public class SettingActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onBuildHeaders(List<Header> target) {
		// TODO Auto-generated method stub
		loadHeadersFromResource(R.xml.settings_headers, target);
	}

}
