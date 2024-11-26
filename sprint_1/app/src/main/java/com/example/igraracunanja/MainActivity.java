package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button btnGame, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        btnGame = findViewById(R.id.btnGame);
        btnLogout = findViewById(R.id.btnLogout);

        // Pokretanje muzike
        Intent startMusic = new Intent(this, MusicService.class);
        startService(startMusic);

        // Dohvati korisničko ime iz Intenta
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            welcomeText.setText("Welcome, " + username + "!");
        } else {
            Toast.makeText(this, "Error: Username not provided.", Toast.LENGTH_SHORT).show();
            welcomeText.setText("Welcome!");
        }

        // Dugme za pokretanje igre
        btnGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, level.class);
            intent.putExtra("USERNAME", username); // Prosledi korisničko ime
            startActivity(intent);
        });

        // Dugme za odjavu
        btnLogout.setOnClickListener(v -> {
            // Muzika ostaje aktivna prilikom prelaska na Login
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish(); // Završava MainActivity
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Osiguranje da muzika nastavlja da svira pri povratku u MainActivity
        Intent startMusic = new Intent(this, MusicService.class);
        startService(startMusic);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Muzika ostaje aktivna – ne zaustavlja se
    }
}
