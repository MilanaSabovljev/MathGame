package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText usernameInput, passwordInput;
    private Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent2 = getIntent();
        if (intent2.getBooleanExtra("registration_success", false)) {
            Toast.makeText(Login.this, "Registration successful, you can log in now", Toast.LENGTH_SHORT).show();
        }

        databaseHelper = new DatabaseHelper(this);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);

        registerButton.setOnClickListener(view -> {

            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (databaseHelper.checkUser(username, password)) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}