package com.example.afjp;

import java.util.ArrayList;

import com.example.afjp.R;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MP3Service extends Service {
	public final MediaPlayer mediaPlayer = new MediaPlayer();
	ArrayList<Song> songList;
	
	final static int PLAY = 1;
	final static int PAUSE = 2;
	final static int PLAY_SONG = 3;

	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread("ServiceStartArguments");
		thread.start();		
		songList= songList();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		Boolean start = intent.getBooleanExtra("stop", true);
		sendOngoingNotification(start);
		
		int position = intent.getIntExtra("song", 0);
		int action = intent.getIntExtra("action", 0);
		
		switch (action) {
			case PLAY:
				mediaPlayer.start();
				break;
			case PAUSE:
				mediaPlayer.pause();
				break;
			case PLAY_SONG:
				play(songList.get(position));
				break;
			default:
				break;
		}

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
		stopForeground(true);
	}

	/**
	 * use mediaPlayer to play selected song. Operation sequence with media
	 * player is mandatory.
	 * 
	 * @param song
	 */
	private void play(final Song song) {
		if (song == null)
			return;

		try {
			if (mediaPlayer.isPlaying())
				mediaPlayer.stop(); // Stop current song.

			mediaPlayer.reset(); // reset resource of player
			mediaPlayer.setDataSource(this, Uri.parse(song.getPath())); // set Song to play
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION); // select audio stream
			mediaPlayer.prepare(); // prepare resource
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() // handler
																			// onDone
					{
						@Override
						public void onCompletion(MediaPlayer mp) {
							play(song.getNext());
						}
					});
			mediaPlayer.start(); // play!
		} catch (Exception e) {
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/**
	 * Reads song list from media storage.
	 * 
	 * @return
	 */
	private ArrayList<Song> songList() {
		ArrayList<Song> songs = new ArrayList<Song>();

		if (!isStoreageAvailable()) // Check for media storage
		{
			// Toast.makeText(this, R.string.nosd, Toast.LENGTH_SHORT).show();
			return songs;
		}

		Cursor music = getContentResolver().query(
				// using content resolver to read music from media storage
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.DATA },
				MediaStore.Audio.Media.IS_MUSIC + " > 0 ", null, null);

		if (music.getCount() > 0) {
			music.moveToFirst();
			Song prev = null;
			do {
				Song song = new Song(music.getString(0), music.getString(1),
						music.getString(2), music.getString(3));

				if (prev != null) // here prev song linked to current one. To
									// simple play them in list
					prev.setNext(song);

				prev = song;
				songs.add(song);
			} while (music.moveToNext());

			prev.setNext(songs.get(0)); // play in cycle;
		}
		music.close();

		return songs;
	}

	/**
	 * Check state of media storage. True if mounted;
	 * 
	 * @return
	 */
	private boolean isStoreageAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	/**
	 * Sending ongoing clickable notification.
	 * @param start
	 */
	public void sendOngoingNotification(Boolean start) {
		final int myID = 1234;

		//The intent to launch when the user clicks the expanded notification
		Intent intent = new Intent(this, MP3Player.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

		//This constructor is deprecated. Use Notification.Builder instead
		Notification notice = new Notification(R.drawable.ic_launcher, "Mp3Player started", System.currentTimeMillis());

		//This method is deprecated. Use Notification.Builder instead.
		notice.setLatestEventInfo(this, "Mp3Player", "Click to open", pendIntent);

		notice.flags |= Notification.FLAG_NO_CLEAR;
		
		if(start) {
			startForeground(myID, notice);
		} else {
			stopForeground(true);
		}
	}

}
