package com.example.mobileproject.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "floristia.db";
    private static final int DATABASE_VERSION = 1;

    // Create Plants Table
    private static final String PLANTS_TABLE_CREATE =
            "CREATE TABLE plants (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "category TEXT CHECK(category IN ('Indoor', 'Outdoor')) NOT NULL, " +
                    "description TEXT, " +
                    "price REAL NOT NULL, " +
                    "quantity_available INTEGER NOT NULL, " +
                    "image BLOB);";

    //Create Users Table
    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE users (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "access TEXT CHECK(access IN ('Customer', 'Admin')) NOT NULL, " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "price REAL NOT NULL, " +
                    "phoneNumber TEXT NOT NULL UNIQUE, " +
                    "pass TEXT NOT NULL);";

    //Create Orders Table
    private static final String ORDERS_TABLE_CREATE =
            "CREATE TABLE orders (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "total REAL NOT NULL, " +
                    "customer_id INTEGER NOT NULL, " +
                    "date TEXT NOT NULL UNIQUE, " +
                    "location_latitude REAL NOT NULL, " + //read from google map
                    "location_longitude REAL NOT NULL, " + //read from google map
                    "payment TEXT CHECK(payment IN ('MADA', 'ApplePay')) NOT NULL,"+
                    "FOREIGN KEY(customer_id) REFERENCES users(_id))";

    //Create Order_Items Table
    private static final String ORDER_ITEMS_TABLE_CREATE =
            "CREATE TABLE order_items (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INTEGER NOT NULL, " +
                    "product_id INTEGER NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "FOREIGN KEY(order_id) REFERENCES orders(_id), " +
                    "FOREIGN KEY(product_id) REFERENCES plants(_id))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PLANTS_TABLE_CREATE); // Create the products table
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(ORDERS_TABLE_CREATE);
        db.execSQL(ORDER_ITEMS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "plants");
        db.execSQL("DROP TABLE IF EXISTS " + "users");
        db.execSQL("DROP TABLE IF EXISTS " + "orders");
        db.execSQL("DROP TABLE IF EXISTS " + "order_items");

        onCreate(db); // Create a new one
    }
}
