package com.example.afjp;

import com.example.afjp.R;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class AlarmActivity extends Activity {
	private MediaPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		
		// Media player
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		player = MediaPlayer.create(this, R.raw.song);
		player.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm, menu);
		return true;
	}
	

	/**
     * When clicking the button stop the alarm.
     * @param view
     */
    public void onClick(View view) {
    	Intent intent = new Intent();
		switch (view.getId()) {
	        case R.id.buttonAlarm:
	        	player.stop();
	    		setResult(RESULT_OK, intent);
		        finish();
		        return;
		}
    	
    }

}
