package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class level extends AppCompatActivity {

    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        btnStart = findViewById(R.id.btnStart);

        // Postavljanje onClickListener za dugme "Start Game"
        btnStart.setOnClickListener(view -> {
            // Preuzmi korisničko ime iz Intenta
            String username = getIntent().getStringExtra("USERNAME");

            // Pokreni PlayGameActivity sa prosleđenim korisničkim imenom
            Intent intent = new Intent(level.this, PlayGameActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
    }
}
