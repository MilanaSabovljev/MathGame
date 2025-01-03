package com.example.igraracunanja;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {

    private final FirebaseFirestore db;

    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Dodaje rezultat igre u Firestore bazu.
     *
     * @param username      Korisničko ime
     * @param score         Osvojeni poeni
     * @param timeSpent     Vreme rešavanja u sekundama
     * @param wrongAnswers  Broj netačnih odgovora
     */
    public void addGameResult(String username, int score, double timeSpent, int wrongAnswers) {
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("score", score);
        result.put("timeSpent", timeSpent);
        result.put("wrongAnswers", wrongAnswers);

        db.collection("game_results")
                .add(result)
                .addOnSuccessListener(documentReference -> {
                    Log.d("DatabaseHelper", "Game result added successfully: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("DatabaseHelper", "Error adding game result", e);
                });
    }

    /**
     * Dohvata najbolje rezultate svakog korisnika iz Firestore baze.
     *
     * @param callback Callback koji vraća sortiranu listu rezultata
     */
    public void getBestResultsByUser(FirestoreCallback callback) {
        db.collection("game_results")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, GlobalResult> userBestResults = new HashMap<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String username = document.getString("username");
                        int score = document.getLong("score").intValue();
                        double timeSpent = document.getDouble("timeSpent");
                        int wrongAnswers = document.getLong("wrongAnswers").intValue();

                        GlobalResult currentResult = new GlobalResult(username, score, timeSpent, wrongAnswers);
                        if (!userBestResults.containsKey(username) || userBestResults.get(username).score < score) {
                            userBestResults.put(username, currentResult);
                        }
                    }

                    List<GlobalResult> sortedResults = new ArrayList<>(userBestResults.values());
                    sortedResults.sort((r1, r2) -> {
                        if (r2.score != r1.score) return Integer.compare(r2.score, r1.score);
                        if (r1.wrongAnswers != r2.wrongAnswers) return Integer.compare(r1.wrongAnswers, r2.wrongAnswers);
                        return Double.compare(r1.timeSpent, r2.timeSpent);
                    });

                    List<String> resultStrings = new ArrayList<>();
                    for (GlobalResult result : sortedResults) {
                        resultStrings.add("User: " + result.username
                                + "\nScore: " + result.score
                                + "\nWrong Answers: " + result.wrongAnswers
                                + "\nTime Spent: " + result.timeSpent + "s");
                    }

                    callback.onCallback(resultStrings);
                })
                .addOnFailureListener(e -> {
                    Log.e("DatabaseHelper", "Error fetching results from Firestore", e);
                    callback.onCallback(new ArrayList<>());
                });
    }

    /**
     * Callback interfejs za Firestore operacije.
     */
    public interface FirestoreCallback {
        void onCallback(List<String> results);
    }

    /**
     * Klasa za predstavljanje rezultata u memoriji.
     */
    private static class GlobalResult {
        String username;
        int score;
        double timeSpent;
        int wrongAnswers;

        GlobalResult(String username, int score, double timeSpent, int wrongAnswers) {
            this.username = username;
            this.score = score;
            this.timeSpent = timeSpent;
            this.wrongAnswers = wrongAnswers;
        }
    }

    public void getUserDetails(String username, UserCallback callback) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Users user = document.toObject(Users.class);  // Mapiranje na Users klasu
                        callback.onCallback(user);
                    } else {
                        callback.onCallback(null);  // Korisnik nije pronađen
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DatabaseHelper", "Error fetching user details", e);
                    callback.onCallback(null);
                });
    }

    public interface UserCallback {
        void onCallback(Users user);
    }

    public void updateUserDetails(String username, Users updatedUser, UpdateCallback callback) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("users").document(documentId)
                                .set(updatedUser)
                                .addOnSuccessListener(unused -> callback.onCallback(true))
                                .addOnFailureListener(e -> {
                                    Log.e("DatabaseHelper", "Error updating user details", e);
                                    callback.onCallback(false);
                                });
                    } else {
                        callback.onCallback(false);  // Korisnik nije pronađen
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DatabaseHelper", "Error finding user", e);
                    callback.onCallback(false);
                });
    }



    public interface UpdateCallback {
        void onCallback(boolean success);
    }


}
