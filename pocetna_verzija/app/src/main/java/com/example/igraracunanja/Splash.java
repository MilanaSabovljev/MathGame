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

        // Nakon 2 sekunde, prebacuje se na RegisterActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash.this, Login.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
