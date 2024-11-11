package com.example.igraracunanja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText firstnameInput, lastnameInput, usernameInput, emailInput, passwordInput, ageInput;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        firstnameInput = findViewById(R.id.firstnameInput);
        lastnameInput = findViewById(R.id.lastnameInput);
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        ageInput = findViewById(R.id.ageInput);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> {
            String firstname = firstnameInput.getText().toString();
            String lastname = lastnameInput.getText().toString();
            String username = usernameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String age = ageInput.getText().toString();


            if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty()) {

                Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

            } else if (databaseHelper.checkIfUserExists(email)) {

                Toast.makeText(Register.this, "Email already exists", Toast.LENGTH_SHORT).show();

            }  else {

                    Users newUser = new Users(0, firstname, lastname, username, email, password, Integer.parseInt(age));

                    databaseHelper.addUser(newUser);
                    //Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    Intent intent2 = new Intent(Register.this, Login.class);
                    intent2.putExtra("registration_success", true);
                    startActivity(intent2);

                    finish();

            }
        });
    }
}