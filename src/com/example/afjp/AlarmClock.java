package com.example.afjp;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.example.afjp.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.TextureView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * 
 * @author Alexandra
 *
 */
public class AlarmClock extends Activity {
	AlarmManagerService ams;
	TextView timeNowToShow;
	TimePicker timePicker;
	Context context;
	int alarmHour, alarmMinute, hour, minute, seconds;
	boolean isAlarmSet = false;
	Time time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_clock);
		timeNowToShow = (TextView) findViewById(R.id.textViewShowTime);
		
		ams = new AlarmManagerService(this);
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(true);		
		
		countingClock();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm_clock, menu);
		return true;
	}
	
	/**
	 * When click button set alarm.
	 * @param view
	 */
	public void onClick(View view) {
		switch (view.getId()) {
	        case R.id.buttonSetAlarm:	        	
	        	setAlarmTime();
	    		break;
		}
    	
    }
	
	/**
	 * Displasys the current time on display.
	 */
	public void showCurrentTime() {
		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		seconds = c.get(Calendar.SECOND);		
		
		// set current time into textview
		timeNowToShow.setText(new StringBuilder().append(pad(hour))
				.append(":").append(pad(minute)).append(":").append(pad(seconds)));
	}
	
	/**
	 * Gets the alarm time from the timepicker.
	 */
	public void getAlarmTime() {
		alarmHour  = timePicker.getCurrentHour();
    	alarmMinute = timePicker.getCurrentMinute();
	}
	
	/**
	 * Checks when to set the alarm. If the time has passed today --> set alarm tomorrow.
	 */
	public void setAlarmTime() {
		Calendar now = Calendar.getInstance();
		Date dNow = now.getTime();
		
		getAlarmTime();
		
		Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, alarmHour);
        c.set(Calendar.MINUTE, alarmMinute);
        c.set(Calendar.SECOND, 00);
        
		long time = c.getTimeInMillis();
		Date dAlarm = c.getTime();		
		
		if(dAlarm.after(dNow)) {
			ams.setAlarm(time);
			if(isAlarmSet) {
				openDialog("Note", "The alarm is changed to \n" + c.getTime());
			} else {
				openDialog("Note", "Alarm set to \n" + c.getTime());
			}
			isAlarmSet = true;
			
		} else if(dNow.equals(dAlarm) || dNow.after(dAlarm)) {
			// If the time has passed, set alarm next day, add 24 hours to alarmtime.
			c.add(Calendar.DATE, 1);
			time = c.getTimeInMillis();
			ams.setAlarm(time);
			if(isAlarmSet) {
				openDialog("Note", "The alarm is changed to \n" + c.getTime());
			} else {
				openDialog("Note", "Alarm set to \n" + c.getTime());
			}
			isAlarmSet = true;
		}
	}
	
	/**
	 * Needed for the digital clock to present time correct. If time is:
	 * 11:5:45, changes it to 11:05:45.
	 * @param c
	 * @return
	 */
	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
	
	/**
     * Opens a dialog with information.
     */
    public void openDialog(String title, String message) {
    	new AlertDialog.Builder(this)
    	    .setTitle(title)
    	    .setMessage(message)
    	    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) { 
    	            dialog.cancel();	        }
    	    })
    	    .show();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub    
		if(requestCode == 4) {
			if(resultCode == RESULT_OK) {
				isAlarmSet = false;
				openDialog("ALARM", "The alarm is now off.");
			}
		}
	}
    
    Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			showCurrentTime();
		}
	};
    
	/**
	 * Creates a message every five seconds to the handler to keep it from sleep.
	 */
    public void countingClock() {
		int initialDelay = 0;
		int period = 5000;

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				Message msg = new Message();
				mHandler.sendMessage(msg);
			}
		};

		timer.scheduleAtFixedRate(task, initialDelay, period);
	}

}
