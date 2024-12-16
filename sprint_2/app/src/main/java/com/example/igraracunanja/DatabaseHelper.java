package com.example.igraracunanja;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {

    private final FirebaseFirestore db;

    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // Dodavanje korisnika u Firestore
    public void addUser(Users user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());
        userMap.put("age", user.getAge());

        db.collection("users").document(user.getUsername()).set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // Uspešno dodat korisnik
                })
                .addOnFailureListener(e -> {
                    // Greška prilikom dodavanja korisnika
                });
    }

    // Dohvatanje korisnika iz Firestore
    public void getUser(String username, FirestoreCallback callback) {
        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Users user = documentSnapshot.toObject(Users.class);
                        callback.onCallback(user);
                    } else {
                        callback.onCallback(null); // Korisnik ne postoji
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onCallback(null); // Greška prilikom dohvaćanja
                });
    }

    public interface FirestoreCallback {
        void onCallback(Users user);
    }
}
