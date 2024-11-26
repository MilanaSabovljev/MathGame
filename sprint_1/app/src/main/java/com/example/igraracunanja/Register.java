package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText firstnameInput, lastnameInput, usernameInput, emailInput, passwordInput, ageInput;
    private Button registerButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();

        firstnameInput = findViewById(R.id.firstnameInput);
        lastnameInput = findViewById(R.id.lastnameInput);
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        ageInput = findViewById(R.id.ageInput);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> {
            String firstname = firstnameInput.getText().toString().trim();
            String lastname = lastnameInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String ageStr = ageInput.getText().toString().trim();

            if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || ageStr.isEmpty()) {
                Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(Register.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(Register.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(Register.this, "Invalid age", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proveri da li korisnik postoji pre registracije
            FirestoreHelper firestoreHelper = new FirestoreHelper();
            firestoreHelper.checkIfUserExists(username, exists -> {
                if (exists) {
                    Toast.makeText(Register.this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(firstname, lastname, username, email, password, age);
                }
            });
        });
    }

    private void registerUser(String firstname, String lastname, String username, String email, String password, int age) {
        // Hesovanje lozinke pre čuvanja
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstname);
        user.put("lastName", lastname);
        user.put("username", username);
        user.put("email", email);
        user.put("password", hashedPassword); // Hesovana lozinka
        user.put("age", age);

        db.collection("users").document(username).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Register.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                });
    }
}
