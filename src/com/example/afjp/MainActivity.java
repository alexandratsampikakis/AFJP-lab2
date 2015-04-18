package com.example.afjp;

import java.util.ArrayList;

import com.example.afjp.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This is the main actvity, contains a menu list with different activities to choose from.
 * @author Alexandra
 *
 */
public class MainActivity extends ListActivity  {
	ArrayList<String> list;
	ArrayList<Intent> intentsList;
	ListView listview;
	ArrayAdapter<String> adapter;
	Intent mycountriesIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listview = (ListView) findViewById(android.R.id.list);
		
	    list = new ArrayList<String>();
	    intentsList = new ArrayList<Intent>();
        
        // All activities
        Intent mycountriesIntent = new Intent(this, MyCountries.class);
        Intent alarmClockIntent = new Intent(this, AlarmClock.class);
        Intent mp3 = new Intent(this, MP3Player.class);
        
	    Intent[] intents = {mycountriesIntent, alarmClockIntent, mp3};
	    String[] names = {"My Countries", "Alarm Clock", "MP3 Player"};
	    
	    for(int i=0; i<names.length; i++) {
 		   list.add(i,names[i]);
 		   intentsList.add(i, intents[i]);
 	   	}
	    
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
	    listview.setAdapter(adapter);
	    
 	   	adapter.notifyDataSetChanged(); 	   	
    }    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
   
    
    @Override
    /**
     * When a user clicks on the menu list, that activity will start.
     */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//get selected items		
		startActivity(intentsList.get(position));
	}
    
    
    
}

