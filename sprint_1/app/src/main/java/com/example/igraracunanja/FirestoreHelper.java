package com.example.igraracunanja;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {
    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // Provera da li korisnik postoji
    public void checkIfUserExists(String username, FirestoreCallback callback) {
        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onCallback(true); // Korisnik postoji
                    } else {
                        callback.onCallback(false); // Korisnik ne postoji
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreHelper", "Error checking user", e);
                    callback.onCallback(false);
                });
    }

    public interface FirestoreCallback {
        void onCallback(boolean exists);
    }
}
