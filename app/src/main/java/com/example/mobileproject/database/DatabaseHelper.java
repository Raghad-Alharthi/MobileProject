package com.example.mobileproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.mobileproject.HashUtils;
import com.example.mobileproject.R;
import com.example.mobileproject.models.Plant;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "floristia.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;

    private static final String PLANTS_TABLE_CREATE =
            "CREATE TABLE plants (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "category TEXT CHECK(category IN ('Indoor', 'Outdoor')) NOT NULL, " +
                    "description TEXT, " +
                    "price REAL NOT NULL, " +
                    "quantity_available INTEGER NOT NULL, " +
                    "image BLOB);";

    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL,"+
                    "role TEXT CHECK(role IN ('user', 'admin')) NOT NULL);";

    private static final String ORDERS_TABLE_CREATE =
            "CREATE TABLE orders (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "total REAL NOT NULL, " +
                    "customer_id INTEGER NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "city TEXT NOT NULL CHECK(city IN ('Dammam', 'Al-Ahsa', 'Al-Khobar', 'Al-Dhahran', 'Al-Qatif')), " +
                    "payment TEXT CHECK(payment IN ('MADA', 'ApplePay')) NOT NULL, " +
                    "FOREIGN KEY(customer_id) REFERENCES users(_id));";

    private static final String ORDER_ITEMS_TABLE_CREATE =
            "CREATE TABLE order_items (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INTEGER NOT NULL, " +
                    "product_id INTEGER NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "FOREIGN KEY(order_id) REFERENCES orders(_id), " +
                    "FOREIGN KEY(product_id) REFERENCES plants(_id));";

    private static final String CART_TABLE_CREATE =
            "CREATE TABLE cart (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "plant_id INTEGER NOT NULL, " +
                    "quantity INTEGER NOT NULL CHECK(quantity > 0), " +
                    "FOREIGN KEY(user_id) REFERENCES users(_id), " +
                    "FOREIGN KEY(plant_id) REFERENCES plants(_id));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PLANTS_TABLE_CREATE);
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(ORDERS_TABLE_CREATE);
        db.execSQL(ORDER_ITEMS_TABLE_CREATE);
        db.execSQL(CART_TABLE_CREATE);

        initializePlantsTable(db);
        initializeUsersTable(db);
    }

    private void initializePlantsTable(SQLiteDatabase db) {
        addPlant(db, "Bonsai Tree", "Indoor", "A small and decorative bonsai tree.", 50.99, 5, readImageAsBlob(R.drawable.bonsai_tree));
        addPlant(db, "Ficus lyrata Bambino", "Indoor", "A trendy fiddle-leaf fig plant.", 35.50, 10, readImageAsBlob(R.drawable.ficus_lyrata_bambino));
        addPlant(db, "Maranta leuconeura", "Indoor", "Known for its striking leaf patterns.", 25.75, 15, readImageAsBlob(R.drawable.maranta_leuconeura));
        addPlant(db, "Monstera adansonii", "Indoor", "Popular for its unique, holey leaves.", 40.00, 8, readImageAsBlob(R.drawable.monstera_adansonii));
        addPlant(db, "Monstera deliciosa", "Indoor", "The classic Swiss Cheese Plant.", 45.00, 12, readImageAsBlob(R.drawable.monstera_deliciosa));
        addPlant(db, "Peace Lily", "Indoor", "An elegant plant that purifies the air.", 20.00, 20, readImageAsBlob(R.drawable.peace_lily));
    }

    private void initializeUsersTable(SQLiteDatabase db) {
        ContentValues admin = new ContentValues();
        String adminPassword = HashUtils.hashPassword("1234");
        admin.put("name", "Admin");
        admin.put("role", "admin");
        admin.put("email", "admin@example.com");
        admin.put("password", adminPassword);
        db.insert("users", null, admin);

        ContentValues user = new ContentValues();
        String userPassword = HashUtils.hashPassword("1234");
        user.put("name", "User");
        user.put("role", "user");
        user.put("email", "user@example.com");
        user.put("password", userPassword);
        db.insert("users", null, user);
    }

    private void addPlant(SQLiteDatabase db, String name, String category, String description, double price, int quantity, byte[] imageBlob) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("description", description);
        values.put("price", price);
        values.put("quantity_available", quantity);
        values.put("image", imageBlob);
        db.insert("plants", null, values);
    }

    private byte[] readImageAsBlob(int drawableId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addToCart(int userId, int plantId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the item already exists in the cart
        Cursor cursor = db.rawQuery(
                "SELECT quantity FROM cart WHERE user_id = ? AND plant_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(plantId)}
        );

        if (cursor.moveToFirst()) {
            // Update the quantity if the item exists
            int currentQuantity = cursor.getInt(0);
            ContentValues values = new ContentValues();
            values.put("quantity", currentQuantity + quantity);
            db.update("cart", values, "user_id = ? AND plant_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(plantId)});
        } else {
            // Insert a new row if the item does not exist
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("plant_id", plantId);
            values.put("quantity", quantity);
            db.insert("cart", null, values);
        }
        cursor.close();
        db.close();
    }

    // Add a new user to the database
    public boolean addUser(String name, String email, String hashedPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", hashedPassword);
        values.put("role", "user"); // Default role is user

        long result = db.insert("users", null, values);
        db.close();
        return result != -1; // Return true if insertion was successful
    }

    // Check if a user already exists by email
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(
                "users",                  // Table name
                new String[]{"email"},    // Columns to return
                "LOWER(email) = LOWER(?)",              // WHERE clause
                new String[]{email},      // WHERE clause arguments
                null,                     // GROUP BY clause
                null,                     // HAVING clause
                null                      // ORDER BY clause
        )) {
            return cursor.getCount() > 0; // Check if any result is returned
        }
    }

    // Update a user's password
    public boolean updatePassword(String email, String newHashedPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newHashedPassword);

        int rowsUpdated = db.update("users", values, "LOWER(email) = LOWER(?)", new String[]{email});
        db.close();
        return rowsUpdated > 0; // Return true if any row was updated
    }

    // Retrieve a user's hashed password (for login verification)
    public String getUserPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(
                "users",                  // Table name
                new String[]{"password"}, // Columns to return
                "LOWER(email) = LOWER(?)",              // WHERE clause
                new String[]{email},      // WHERE clause arguments
                null,                     // GROUP BY clause
                null,                     // HAVING clause
                null                      // ORDER BY clause
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0); // Get the hashed password
            }
        }
        return null; // Return null if no user found
    }

    // Retrieve a user's role
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(
                "users",                  // Table name
                new String[]{"role"},     // Columns to return
                "LOWER(email) = LOWER(?)",              // WHERE clause
                new String[]{email},      // WHERE clause arguments
                null,                     // GROUP BY clause
                null,                     // HAVING clause
                null                      // ORDER BY clause
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0); // Get the role
            }
        }
        return null; // Return null if no user found
    }

    // Retrieve a user's name
    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(
                "users",                 // Table name
                new String[]{"name"},    // Columns to return
                "LOWER(email) = LOWER(?)",             // WHERE clause
                new String[]{email},     // WHERE clause arguments
                null,                    // GROUP BY clause
                null,                    // HAVING clause
                null                     // ORDER BY clause
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0); // Get the name
            }
        }
        return null; // Return null if no user found
    }

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "users",                  // Table name
                new String[]{"id"},       // Column to return
                "LOWER(email) = LOWER(?)",              // WHERE clause
                new String[]{email},      // WHERE arguments
                null,                     // GROUP BY clause
                null,                     // HAVING clause
                null                      // ORDER BY clause
        );

        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(0); // Get the user ID from the first column
            cursor.close();
        }
        db.close();
        return userId;
    }



    // Fetch detailed cart items for a user
    public Cursor getDetailedCartItems(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.plant_id AS product_id, " +
                "p.name AS plant_name, " +
                "p.price AS plant_price, " +
                "p.image AS plant_image, " +
                "c.quantity AS cart_quantity, " +
                "p.quantity_available AS quantity_available " +
                "FROM cart c " +
                "INNER JOIN plants p ON c.plant_id = p._id " +
                "WHERE c.user_id = ?";
        Log.d("DatabaseHelper", "Fetching cart items for user_id: " + userId);
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }


    public void clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void updateCartItemQuantity(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);

        db.update("cart", values, "user_id = ? AND plant_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        db.close();
    }

    public void deleteCartItem(int userId, int plantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "user_id = ? AND plant_id = ?", new String[]{String.valueOf(userId), String.valueOf(plantId)});
        db.close();
    }

    public List<Plant> getIndoorPlants() {
        List<Plant> indoorPlants = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM plants WHERE category = 'Indoor'", null);
        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex("_id");
            int nameIndex = cursor.getColumnIndex("name");
            int categoryIndex = cursor.getColumnIndex("category");
            int descriptionIndex = cursor.getColumnIndex("description");
            int priceIndex = cursor.getColumnIndex("price");
            int quantityAvailableIndex = cursor.getColumnIndex("quantity_available");
            int imageIndex = cursor.getColumnIndex("image");
            // Fetch the values
            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String category = cursor.getString(categoryIndex);
            String description = cursor.getString(descriptionIndex);
            double price = cursor.getDouble(priceIndex);
            int quantityAvailable = cursor.getInt(quantityAvailableIndex);
            byte[] image = cursor.getBlob(imageIndex);
            Plant plant = new Plant(id, name, category, description, price, quantityAvailable, image);
            indoorPlants.add(plant);
        }
        cursor.close();
        db.close();
        return indoorPlants;
    }

    public List<Plant> getOutdoorPlants() {
        List<Plant> outdoorPlants = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM plants WHERE category = 'Outdoor'", null);
        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex("_id");
            int nameIndex = cursor.getColumnIndex("name");
            int categoryIndex = cursor.getColumnIndex("category");
            int descriptionIndex = cursor.getColumnIndex("description");
            int priceIndex = cursor.getColumnIndex("price");
            int quantityAvailableIndex = cursor.getColumnIndex("quantity_available");
            int imageIndex = cursor.getColumnIndex("image");
            // Fetch the values
            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String category = cursor.getString(categoryIndex);
            String description = cursor.getString(descriptionIndex);
            double price = cursor.getDouble(priceIndex);
            int quantityAvailable = cursor.getInt(quantityAvailableIndex);
            byte[] image = cursor.getBlob(imageIndex);
            Plant plant = new Plant(id, name, category, description, price, quantityAvailable, image);
            outdoorPlants.add(plant);
        }
        cursor.close();
        db.close();
        return outdoorPlants;
    }



    public void addOrderItem(int orderId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("product_id", productId);
        values.put("quantity", quantity);

        db.insert("order_items", null, values);
        db.close();
    }


    public long addOrder(int userId, double total, String date,String city, String paymentMethod) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_id", userId);
        values.put("total", total);
        values.put("date", date); // Store current timestamp as the order date
        values.put("city", city);
        values.put("payment", paymentMethod);

        long orderId = db.insert("orders", null, values);
        db.close();
        return orderId; // Return the newly created order ID
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS plants");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

}
