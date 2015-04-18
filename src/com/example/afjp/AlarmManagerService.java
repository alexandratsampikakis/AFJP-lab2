package com.example.afjp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 
 * @author Alexandra
 *
 */
public class AlarmManagerService {

    private final String alarmManagerAction = "com.example.dv106lab2.alarmManagerAction";
    BroadcastReceiver receiver;
    PendingIntent pi;
    private boolean registered = false;
    AlarmManager alarmManager;
    private PowerManager.WakeLock wl;
    private Context context;
    SharedPreferences prefs;

    /**
     * Cunstructor.
     * @param context
     */
    public AlarmManagerService(Context context) {
        this.context = context;
        this.receiver = null;

        prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    /**
     * Registers a broadcast receiver and creates a new WAKE LOCK
     */
    public void initialize() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(alarmManagerAction);
        if (this.receiver == null) {
            this.receiver = new BroadcastReceiver() {
            	
				@Override
                public void onReceive(Context context, Intent intent) {
					Log.d("mmm", "ja jag kommer hit");
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AlarmClock-AlarmManagerService");
                    wl.acquire();
                    
                    // ALARM
                    Intent alarmIntent = new Intent(context, AlarmActivity.class);
                    ((Activity)context).startActivityForResult(alarmIntent, 4);
                    Log.d("mmm", "Ny intent");
                    
                    wl.release();
                }
            };
            pi = PendingIntent.getBroadcast( context, 0, new Intent(alarmManagerAction),0 );
            context.registerReceiver(this.receiver, intentFilter);
            this.registered = true;
        }
    }

    
    protected void setAlarm(long alarmTime) {		
    	initialize();
    	Log.d("mmm", "Nu �r larmet satt");
        alarmManager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
        
    }

    /**
     * Unregisters the broadcast listener
     */
    protected void removeBroadCastListener() {
        if (this.receiver != null) {
            try {
                alarmManager.cancel(pi);
                context.unregisterReceiver(this.receiver);
                this.registered = false;
                this.receiver = null;
                
            } catch (Exception e) {
            }
        }
    }
}
