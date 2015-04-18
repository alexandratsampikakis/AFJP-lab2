package com.example.afjp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import com.example.afjp.R;
import com.example.afjp.R.color;
import com.example.afjp.menuSettings.BackgroundColorFragment;
import com.example.afjp.menuSettings.SettingActivity;

import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This activity shows a list of visited countries and what year the countries was visited.
 * @author Alexandra
 *
 */
@SuppressLint("ResourceAsColor")
public class MyCountries extends Activity {
	ArrayList<String> list;
	ListView listview;
	ArrayAdapter<String> adapter;
	CountriesDataSource cds;
	String orderBy;
	private int clickedItem;
	SharedPreferences prefs;
	View colorLayout;
	String color;
	String textSize;
	TextView size;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_my_countries);

		listview = (ListView) findViewById(R.id.listViewCountries);
		registerForContextMenu(listview);

		cds = new CountriesDataSource(this);
		cds.open();
		
		orderBy = prefs.getString("sortBy", "year");
		list = (ArrayList<String>) cds.getAllCountries(orderBy);
		
		listview.setAdapter(adapter = new ArrayAdapter<String>(this, R.layout.layout_row, list) {
		    @Override
		    public View getView(int position, View row, ViewGroup parent) {
		    	String data = getItem(position);
		    	row = getLayoutInflater().inflate(R.layout.layout_row, parent, false);
		    	size = (TextView) row.findViewById(R.id.label);
		    	textSize = prefs.getString("sizePref", "1");
				
		    	if(textSize.equals("1")) {
					size.setTextSize(10);
				} else if(textSize.equals("2")) {
					size.setTextSize(20);
				} else if(textSize.equals("3")) {
					size.setTextSize(30);
				}		    	
		    	size.setText(data);
		        return size;
		    }		    
		});

		colorLayout = (RelativeLayout)findViewById(R.id.laidout);		
		color = prefs.getString("colorPref", "1");
		setBackgroundColor(color);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_countries, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.action_settings:
        		Intent settings = new Intent(this, com.example.afjp.menuSettings.SettingActivity.class);
        		startActivity(settings);
        		return true;
            case R.id.order_by_year:
            	orderBy = "year";
            	saveSortBy(orderBy);
                sort(orderBy);
                return true;
            case R.id.order_by_name:
            	orderBy = "name";
            	saveSortBy(orderBy);
            	sort(orderBy);
                return true;
            case R.id.add_new_country:
            	Intent addNewCountryIntent = new Intent(this, AddCountry.class);
    			startActivityForResult(addNewCountryIntent, 1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		clickedItem = acmi.position;
		String item = adapter.getItem(clickedItem);
		menu.setHeaderTitle("Edit or delete" + item);
		menu.add(1,1,1,"Edit");
		menu.add(1,2,2, "Delete");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		String title = (String) item.getTitle();
		String[] rString = adapter.getItem(clickedItem).split("  ");
		String country = rString[1];
		int year = Integer.parseInt(rString[0]);
		if(title.equals("Edit")) {
			edit(year,country);
		} else if(title.equals("Delete")){
			cds.delete(year,country);
			sort(orderBy);
		}
		return true;
	}
	
	/**
	 * 
	 * @param year
	 * @param country
	 */
	public void edit(int year, String country) {
		String[] values = new String[2];
		values[0] = year+"";
    	values[1] = country;
		Intent addNewCountryIntent = new Intent(this, AddCountry.class);
		addNewCountryIntent.putExtra("countryToEdit", values);
		addNewCountryIntent.putExtra("RequestCode", 2);
		startActivityForResult(addNewCountryIntent, 2);
		sort(orderBy);
	}
	
	/**
	 * 
	 * @param orderBy
	 */
	public void sort(String orderBy) {
		list.clear();
        list.addAll(cds.getAllCountries(orderBy));
        adapter.notifyDataSetChanged();
	}
	
	@SuppressLint("NewApi")
	public void saveSortBy(String orderBy) {
		SharedPreferences.Editor edit = prefs.edit();
		prefs.edit().putString("sortBy", orderBy).commit();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putStringArrayList("countries", list);
		outState.putString("colorPref", color);
		outState.putString("sizePref", textSize);
		saveSortBy(orderBy);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		list = savedInstanceState.getStringArrayList("countries");
		textSize = savedInstanceState.getString("sizePref");
		color = savedInstanceState.getString("colorPref");
		setBackgroundColor(color);
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		color = prefs.getString("colorPref", color);
		setBackgroundColor(color);
		orderBy = prefs.getString("sortBy", orderBy);		
		textSize = prefs.getString("sizePref", textSize);
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		saveSortBy(orderBy);
		super.onPause();
	}

	
	@Override
	/**
	 * Call Back method to get the Message form other Activity - override the method
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub    
		
		// New country
		if(requestCode == 1) {
			if(resultCode == RESULT_OK) {
				String[] message = data.getStringArrayExtra("MESSAGE");
				list.add(message[0] + "  " + message[1]);
				cds.saveCountry(Integer.parseInt(message[0]), message[1]);
    			sort(orderBy);

			} else if(resultCode == RESULT_CANCELED) {

			}
			
		// Edit country
		} else if(requestCode == 2) {
			if(resultCode == RESULT_OK) {
				String[] message = data.getStringArrayExtra("MESSAGE");
				String[] oldCountry = data.getStringArrayExtra("oldCountry");
				cds.updateDataBase(oldCountry[0], oldCountry[1], message[0], message[1]);
    			sort(orderBy);

			} else if(resultCode == RESULT_CANCELED) {

			}
		}
	}

	public void setBackgroundColor(String color) {
		if(color.equals("1")) {
			colorLayout.setBackgroundColor(Color.WHITE);
		} else if(color.equals("2")) {
			colorLayout.setBackgroundColor(Color.YELLOW);
		} else if(color.equals("3")) {
			colorLayout.setBackgroundColor(Color.MAGENTA);
		} else if(color.equals("4")) {
			colorLayout.setBackgroundColor(Color.BLUE);
		} else if(color.equals("5")) {
			colorLayout.setBackgroundColor(Color.GREEN);
		} else {
			colorLayout.setBackgroundColor(Color.WHITE);
		}
	}

}
