package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Inicijalizacija UI elemenata
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Povratak na login ekran
        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgetPasswordActivity.this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}
