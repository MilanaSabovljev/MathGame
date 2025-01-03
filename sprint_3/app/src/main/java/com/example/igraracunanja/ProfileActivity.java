package com.example.igraracunanja;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

public class ProfileActivity extends AppCompatActivity {

    private EditText editFirstname, editLastname, editUsername, editEmail, editPassword, editAge;
    private Button btnSave;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicijalizacija UI elemenata
        editFirstname = findViewById(R.id.editFirstname);
        editLastname = findViewById(R.id.editLastname);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editAge = findViewById(R.id.editAge);
        btnSave = findViewById(R.id.btnSave);

        databaseHelper = new DatabaseHelper();  // Inicijalizacija DatabaseHelper klase

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            loadUserDetails(username);  // Učitavanje detalja korisnika
        }

        btnSave.setOnClickListener(v -> saveChanges(username));
    }

    private void loadUserDetails(String username) {
        databaseHelper.getUserDetails(username, user -> {
            if (user != null) {
                editFirstname.setText(user.getFirstName());
                editLastname.setText(user.getLastName());
                editUsername.setText(user.getUsername());
                editUsername.setEnabled(false);  // Onemogućavanje menjanja korisničkog imena
                editEmail.setText(user.getEmail());
                editPassword.setText(""); // Prazno kako bi korisnik uneo novu lozinku
                editAge.setText(String.valueOf(user.getAge()));
            } else {
                Toast.makeText(this, "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges(String currentUsername) {
        String newPassword = editPassword.getText().toString().trim(); // Nova lozinka

        databaseHelper.getUserDetails(currentUsername, user -> {
            if (user != null) {
                String finalPassword = newPassword.isEmpty() ? user.getPassword() : BCrypt.hashpw(newPassword, BCrypt.gensalt());

                Users updatedUser = new Users(
                        editFirstname.getText().toString(),
                        editLastname.getText().toString(),
                        user.getUsername(), // Zadržavanje originalnog korisničkog imena
                        editEmail.getText().toString(),
                        finalPassword,
                        Integer.parseInt(editAge.getText().toString())
                );

                databaseHelper.updateUserDetails(currentUsername, updatedUser, success -> {
                    if (success) {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
