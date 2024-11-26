package com.example.igraracunanja;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MathMiners.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    //private static final String TABLE_RESULTS = "results";

    // Users Table columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FIRSTNAME = "firstname";
    private static final String COLUMN_LASTNAME = "lastname";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_AGE = "age";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FIRSTNAME + " TEXT,"
                + COLUMN_LASTNAME + " TEXT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_AGE + " INTEGER"
                + ")";


        db.execSQL(CREATE_TABLE_USERS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }


    public void addUser(Users user){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Hešovanje lozinke sa Bcrypt
        String hashedPassword = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());

        values.put(COLUMN_FIRSTNAME, user.getFirstName());
        values.put(COLUMN_LASTNAME, user.getLastName());
        values.put(COLUMN_USERNAME, user.getUserName());
        values.put(COLUMN_EMAIL, user.getEmaill());
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_AGE, user.getAge());

        db.insert(TABLE_USERS, null, values);
        db.close();

    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_PASSWORD + " FROM " +
                        TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            String storedHashedPassword = cursor.getString(0);

            // Verifikacija lozinke sa Bcrypt
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHashedPassword);

            cursor.close();
            db.close();

            return result.verified;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }


    public boolean checkIfUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean userExists = cursor.moveToFirst();  // Ako postoji, kursor će biti na prvom redu
        cursor.close();
        db.close();

        return userExists;
    }




}