package com.example.afjp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 
 * @author Alexandra
 *
 */
public class CountriesDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SQLHelper dbHelper;
    private String[] allColumns = { SQLHelper.COLUMN_YEAR, SQLHelper.COLUMN_NAME};

    /**
     * Constructor.
     * @param context
     */
    public CountriesDataSource(Context context) {
        dbHelper = new SQLHelper(context);
    }

    /**
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     *
     */
    public void close() {
        dbHelper.close();
    }
    
    /**
     * 
     * @param year
     * @param name
     */
    public void saveCountry(int year, String name) {
    	ContentValues values = new ContentValues();
        values.put(SQLHelper.COLUMN_YEAR, year);
        values.put(SQLHelper.COLUMN_NAME, name);
        
      //Inserts the notification into database.
        long insertC = database.insert(SQLHelper.TABLE_COUNTRIES, null,
                values);

        Cursor cursor = database.query(SQLHelper.TABLE_COUNTRIES,
                allColumns, SQLHelper.COLUMN_YEAR + " = '" + year + "'"  + " AND "
                + SQLHelper.COLUMN_NAME + " = '" + name + "'", null, null, null, SQLHelper.COLUMN_YEAR);

        cursor.moveToLast();
        cursor.close();
        Log.i("CountriesDataSource", "Country saved");
    }
    
    public List<String> getAllCountries(String orderBy) {
    	Log.d("mmm", "getAllCountries() k�rs, sortera p�: " + orderBy);
    	List<String> list = new ArrayList<String>();
    	Cursor cursor;
    	if(orderBy.equals("name")) {
    		cursor = database.query(SQLHelper.TABLE_COUNTRIES, allColumns, null, null, null, null, SQLHelper.COLUMN_NAME);
    	} else {
    		cursor = database.query(SQLHelper.TABLE_COUNTRIES, allColumns, null, null, null, null, SQLHelper.COLUMN_YEAR);
    	}
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast()) {
    		list.add(cursor.getInt(0) + "  " + cursor.getString(1));
    		cursor.moveToNext();
    	}
    	cursor.close();
		return list;
    }
    
    public void delete(int year, String country) {
    	String[] whereArgs = new String[2];
    	whereArgs[0] = year+"";
    	whereArgs[1] = country;
    	database.delete(SQLHelper.TABLE_COUNTRIES, SQLHelper.COLUMN_YEAR + " = ? AND " + 
    			SQLHelper.COLUMN_NAME + " = ? ", whereArgs);
    }
    
    public void updateDataBase(String oldYear, String oldCountry, String newYear, String newCountry) {
    	Log.d("tagOY: ",oldYear);
    	Log.d("tagOC: ",oldCountry);
    	Log.d("tagNY: ",newYear);
    	Log.d("tagNC: ", newCountry);
    	int oYear = Integer.parseInt(oldYear);
    	int nYear = Integer.parseInt(newYear);
    	
    	String[] whereArgs = new String[2];
    	whereArgs[0] = oYear+"";
    	whereArgs[1] = oldCountry;
    	
    	ContentValues values = new ContentValues();
    	values.put(SQLHelper.COLUMN_YEAR, nYear);
    	values.put(SQLHelper.COLUMN_NAME, newCountry);
    	
    	database.update(SQLHelper.TABLE_COUNTRIES, values, SQLHelper.COLUMN_YEAR + " = ? AND " + 
    			SQLHelper.COLUMN_NAME + " = ? ", whereArgs);
    }

}
