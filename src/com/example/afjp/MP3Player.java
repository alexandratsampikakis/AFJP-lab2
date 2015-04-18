package com.example.afjp;

import java.util.ArrayList;

import com.example.afjp.R;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;

public class MP3Player extends Activity {
	ArrayList<Song> songList;
	ArrayAdapter<Song> adapter;
	int nrOfSongs;
	int currentPlaying = -1;
	Button button;
	MP3Service mp3Service = new MP3Service();
	Boolean paused = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mp3_player);
		
		ListView listview = (ListView) findViewById(R.id.list);	    
		button = (Button) findViewById(R.id.buttonPausePlay);
		
	    songList = songList();
	    nrOfSongs = songList.size();
	    
	    adapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, songList);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	      @Override
	      public void onItemClick(AdapterView<?> parent, final View view,
	          final int position, long id) {	        
	    	  currentPlaying = position;
	    	  play(songList.get(position));
	    	  button.setText("Pause");
	    	  paused = false;
	      }

	    });
		
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Check state of media storage. True if mounted;
	 * @return
	 */
	private boolean isStoreageAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	/**
	 * Reads song list from media storage.
	 * @return
	 */
	private ArrayList<Song> songList() {
		ArrayList<Song> songs = new ArrayList<Song>();

		if(!isStoreageAvailable()) //Check for media storage
		{
			//Toast.makeText(this, R.string.nosd, Toast.LENGTH_SHORT).show();
			return songs;
		}

		Cursor music = getContentResolver().query( //using content resolver to read music from media storage
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[]{
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.DATA },
				MediaStore.Audio.Media.IS_MUSIC + " > 0 ",
				null, null
		);

		if(music.getCount() > 0) {
			music.moveToFirst();
			Song prev = null;
			do {
				Song song = new Song(music.getString(0), music.getString(1), music.getString(2), music.getString(3));

				if(prev != null) //here prev song linked to current one. To simple play them in list
					prev.setNext(song);

				prev = song;
				songs.add(song);
			}
			while(music.moveToNext());
			
			prev.setNext(songs.get(0)); //play in cycle;
		}
		music.close();

		return songs;
	}
	
	/**
	 * use mediaPlayer to play selected song.
	 * Operation sequence with media player is mandatory. 
	 * @param song
	 */
	private void play(final Song song) {
		// Start background service.
		Intent intent = new Intent(this, MP3Service.class);
		intent.putExtra("action", MP3Service.PLAY_SONG);
		intent.putExtra("song", currentPlaying);
		startService(intent);
	}
	
	/**
     * When clicking one of the buttons.
     * @param view
     */
    public void onClick(View view) {
		switch (view.getId()) {
		
			// Play previous song in list.
	        case R.id.buttonPrev:
	        	if(currentPlaying != -1) {
		        	currentPlaying--;
		        	if(currentPlaying < nrOfSongs) play(songList.get(currentPlaying));
		        	button.setText("Pause");
		        	paused = false;
	        	}
	            return;
	            
		    case R.id.buttonPausePlay:
		    	if(currentPlaying == -1) {
		    		currentPlaying = 0;
		    		play(songList.get(currentPlaying)); // If no song is playing and user presses play button, first song in list starts playing.
		    		button.setText("Pause");
		    		paused = false;
		    	} else {		    		
		    		// Play or pause.
		    		if(paused) {
		        		// Show pause button.
		    			Intent intent = new Intent(this, MP3Service.class);
		    			intent.putExtra("action", MP3Service.PLAY);
		    			startService(intent);
		    			button.setText("Pause");
		    			paused = false;
		        	} else {
		        		// Show play button.
		        		Intent intent = new Intent(this, MP3Service.class);
		    			intent.putExtra("action", MP3Service.PAUSE);
		    			startService(intent);
		        		button.setText("Play");
		        		paused = true;
		        	}
		    	}
		        return;
		        
		    // Play next song in list.
		    case R.id.buttonNext:
		    	if(currentPlaying != -1) {
			    	currentPlaying++;
			    	if(currentPlaying < nrOfSongs) {
			    		play(songList.get(currentPlaying));
			    		button.setText("Pause");
			    		paused = false;
			    	}
		    	}
		        return;
		}    	
    }
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("currentPlaying", currentPlaying);
		outState.putString("buttonText", (String) button.getText());
		outState.putBoolean("paused", paused);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		currentPlaying = savedInstanceState.getInt("currentPlaying");
		String s = savedInstanceState.getString("buttonText");
		button.setText(s);
		paused = savedInstanceState.getBoolean("paused");
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MP3Service.class);
		intent.putExtra("stop", false);
		startService(intent);
		super.onDestroy();
	}

}
