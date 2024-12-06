package com.example.mobileproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    // Database and table details
    private static final String DATABASE_NAME = "UserAuth.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role"; // New column for role

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_ROLE + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String adminEmail = "admin@example.com";
        String adminPassword = HashUtils.hashPassword("1234"); // Hash the admin password
        String adminRole = "admin";

        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_NAME, "Admin");
        adminValues.put(COLUMN_EMAIL, adminEmail);
        adminValues.put(COLUMN_PASSWORD, adminPassword);
        adminValues.put(COLUMN_ROLE, adminRole);

        db.insert(TABLE_USERS, null, adminValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Create tables again
        onCreate(db);
    }




    // Add a new user to the database
    public boolean addUser(String name, String email, String hashedPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_ROLE, "user"); // Default role is user

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // Return true if insertion was successful
    }


    // Check if a user already exists by email
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,                  // Table name
                new String[]{COLUMN_EMAIL},   // Columns to return
                COLUMN_EMAIL + " = ?",        // WHERE clause
                new String[]{email},          // WHERE clause arguments
                null,                         // GROUP BY clause
                null,                         // HAVING clause
                null                          // ORDER BY clause
        );

        boolean exists = cursor.getCount() > 0; // Check if any result is returned
        cursor.close();                         // Close the cursor
        db.close();                             // Close the database
        return exists;
    }

    // Update a user's password
    public boolean updatePassword(String email, String newHashedPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newHashedPassword);

        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
        return rowsUpdated > 0; // Return true if any row was updated
    }

    // Retrieve a user's hashed password (for login verification)
    public String getUserPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,                  // Table name
                new String[]{COLUMN_PASSWORD}, // Columns to return
                COLUMN_EMAIL + " = ?",        // WHERE clause
                new String[]{email},          // WHERE clause arguments
                null,                         // GROUP BY clause
                null,                         // HAVING clause
                null                          // ORDER BY clause
        );

        String password = null;
        if (cursor.moveToFirst()) {
            password = cursor.getString(0); // Get the hashed password
        }
        cursor.close();
        db.close();
        return password;
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,                  // Table name
                new String[]{COLUMN_ROLE},    // Columns to return
                COLUMN_EMAIL + " = ?",        // WHERE clause
                new String[]{email},          // WHERE clause arguments
                null,                         // GROUP BY clause
                null,                         // HAVING clause
                null                          // ORDER BY clause
        );

        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(0); // Get the role
        }
        cursor.close();
        db.close();
        return role;
    }


    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,                 // Table name
                new String[]{COLUMN_NAME},   // Columns to return
                COLUMN_EMAIL + " = ?",       // WHERE clause
                new String[]{email},         // WHERE clause arguments
                null,                        // GROUP BY clause
                null,                        // HAVING clause
                null                         // ORDER BY clause
        );

        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(0); // Get the name
        }
        cursor.close();
        db.close();
        return name;
    }


}
