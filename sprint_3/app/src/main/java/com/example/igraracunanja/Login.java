package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton, registerButton, forgetPasswordButton; // Dodato dugme za "Forget Password"
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        forgetPasswordButton = findViewById(R.id.forgetpasswordButton); // Povezivanje dugmeta "Forget Password"
        db = FirebaseFirestore.getInstance();

        // Funkcionalnost za Login
        loginButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(username, password);
        });

        // Funkcionalnost za Register
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        // Funkcionalnost za Forget Password
        forgetPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String username, String enteredPassword) {
        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String hashedPassword = documentSnapshot.getString("password");

                        if (org.mindrot.jbcrypt.BCrypt.checkpw(enteredPassword, hashedPassword)) {
                            Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("USERNAME", username);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Login.this, "Error logging in", Toast.LENGTH_SHORT).show();
                });
    }
}
