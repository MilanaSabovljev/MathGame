package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Pokreni servis za muziku
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);

        // Prelazak na LoginActivity nakon 2 sekunde
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash.this, Login.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
