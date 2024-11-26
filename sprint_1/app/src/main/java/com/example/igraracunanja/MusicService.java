package com.example.igraracunanja;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.math); // math.mp3 iz raw foldera
        mediaPlayer.setLooping(true); // Ponavljanje muzike
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Pokretanje muzike
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
