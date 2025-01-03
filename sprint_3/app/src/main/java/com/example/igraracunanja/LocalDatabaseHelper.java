package com.example.igraracunanja;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game_results.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RESULTS = "results";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TIME_SPENT = "timeSpent";
    private static final String COLUMN_WRONG_ANSWERS = "wrongAnswers";

    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RESULTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_TIME_SPENT + " REAL, "
                + COLUMN_WRONG_ANSWERS + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
    }

    // Metoda za dodavanje rezultata
    public void addGameResult(String username, int score, double timeSpent, int wrongAnswers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIME_SPENT, timeSpent);
        values.put(COLUMN_WRONG_ANSWERS, wrongAnswers);

        db.insert(TABLE_RESULTS, null, values);
        db.close();
    }

    // Metoda za dohvatanje najboljih rezultata svakog korisnika
    public List<String> getBestResultsByUser() {
        List<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT username, MAX(score) AS maxScore, timeSpent, wrongAnswers " +
                "FROM results " +
                "GROUP BY username " +
                "ORDER BY maxScore DESC, wrongAnswers ASC, timeSpent ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String result = "User: " + cursor.getString(cursor.getColumnIndexOrThrow("username"))
                        + "\nScore: " + cursor.getInt(cursor.getColumnIndexOrThrow("maxScore"))
                        + "\nWrong Answers: " + cursor.getInt(cursor.getColumnIndexOrThrow("wrongAnswers"))
                        + "\nTime Spent: " + cursor.getDouble(cursor.getColumnIndexOrThrow("timeSpent")) + "s";
                results.add(result);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return results;
    }
}
