package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button btnGame, btnLogout, btnGlobalHighScores, btnMyLocalHighScores, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        btnGame = findViewById(R.id.btnGame);
        btnLogout = findViewById(R.id.btnLogout);
        btnGlobalHighScores = findViewById(R.id.btnGlobalHighScores);
        btnMyLocalHighScores = findViewById(R.id.btnMyLocalHighScores);
        btnProfile = findViewById(R.id.btnProfile); // Dodato novo dugme Profile

        // Dohvatanje korisničkog imena iz Intenta
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
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        // Dugme za odjavu
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        });

        // Dugme za prikaz globalnih rezultata
        btnGlobalHighScores.setOnClickListener(v -> {
            Log.d("MainActivity", "Opening Global High Scores for user: " + username);
            Intent intent = new Intent(MainActivity.this, MyHighScoresActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        // Dugme za prikaz lokalnih rezultata
        btnMyLocalHighScores.setOnClickListener(v -> {
            Log.d("MainActivity", "Opening Local High Scores");
            Intent intent = new Intent(MainActivity.this, LocalHighScoresActivity.class);
            startActivity(intent);
        });

        // Dugme za profil
        btnProfile.setOnClickListener(v -> {
            Log.d("MainActivity", "Opening Profile for user: " + username);
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("USERNAME", username);  // Prosleđivanje korisničkog imena
            startActivity(intent);
        });
    }
}
