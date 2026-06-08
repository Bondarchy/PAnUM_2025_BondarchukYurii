package com.example.lab6;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cafe.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE drinks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "image_id INTEGER, " +
                "price REAL)");

        db.execSQL("CREATE TABLE snacks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "image_id INTEGER, " +
                "price REAL)");

        db.execSQL("CREATE TABLE cafes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "address TEXT, " +
                "opening_hours TEXT, " +
                "image_id INTEGER)");

        insertDrink(db, "Espresso", "Mocna kawa czarna.", android.R.drawable.ic_menu_compass, 8.50);
        insertDrink(db, "Latte", "Kawa z mlekiem.", android.R.drawable.ic_menu_compass, 12.00);
        insertDrink(db, "Herbata", "Czarna herbata z cytryną.", android.R.drawable.ic_menu_compass, 7.00);

        insertSnack(db, "Sernik", "Ciasto z serem.", android.R.drawable.ic_menu_gallery, 14.00);
        insertSnack(db, "Croissant", "Francuskie ciastko maślane.", android.R.drawable.ic_menu_gallery, 9.50);
        insertSnack(db, "Kanapka", "Kanapka z serem i warzywami.", android.R.drawable.ic_menu_gallery, 13.00);

        insertCafe(db, "Kawiarnia Centrum", "ul. Główna 10", "08:00 - 20:00", android.R.drawable.ic_menu_mylocation);
        insertCafe(db, "Cafe Relax", "ul. Parkowa 5", "09:00 - 22:00", android.R.drawable.ic_menu_mylocation);
        insertCafe(db, "Coffee Point", "ul. Szkolna 2", "07:30 - 18:00", android.R.drawable.ic_menu_mylocation);
    }

    private void insertDrink(SQLiteDatabase db, String name, String description, int imageId, double price) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("image_id", imageId);
        values.put("price", price);
        db.insert("drinks", null, values);
    }

    private void insertSnack(SQLiteDatabase db, String name, String description, int imageId, double price) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("image_id", imageId);
        values.put("price", price);
        db.insert("snacks", null, values);
    }

    private void insertCafe(SQLiteDatabase db, String name, String address, String hours, int imageId) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("address", address);
        values.put("opening_hours", hours);
        values.put("image_id", imageId);
        db.insert("cafes", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS drinks");
        db.execSQL("DROP TABLE IF EXISTS snacks");
        db.execSQL("DROP TABLE IF EXISTS cafes");
        onCreate(db);
    }
}