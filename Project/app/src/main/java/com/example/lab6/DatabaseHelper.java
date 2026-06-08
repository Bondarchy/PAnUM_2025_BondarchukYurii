package com.example.lab6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kafeteria.db";
    private static final int DATABASE_VERSION = 3;

    public static final String CATEGORY_DRINKS = "NAPOJE";
    public static final String CATEGORY_SNACKS = "PRZEKASKI";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "category TEXT NOT NULL, " +
                "icon TEXT NOT NULL)");

        db.execSQL("CREATE TABLE location (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cafe_name TEXT NOT NULL, " +
                "address TEXT NOT NULL, " +
                "opening_hours TEXT NOT NULL)");

        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_date TEXT NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "discount REAL NOT NULL, " +
                "total REAL NOT NULL, " +
                "email TEXT)");

        db.execSQL("CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "product_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "unit_price REAL NOT NULL, " +
                "item_total REAL NOT NULL)");

        insertProduct(db,
                "Espresso",
                "Aromatyczna, mocna kawa przygotowana ze świeżo mielonych ziaren.",
                8.00,
                CATEGORY_DRINKS,
                "☕");

        insertProduct(db,
                "Cappuccino",
                "Delikatna kawa espresso z gorącym mlekiem i aksamitną pianką.",
                12.00,
                CATEGORY_DRINKS,
                "☕");

        insertProduct(db,
                "Lemoniada",
                "Orzeźwiający napój cytrynowy z miętą i kostkami lodu.",
                10.00,
                CATEGORY_DRINKS,
                "🍋");

        insertProduct(db,
                "Croissant",
                "Maślany rogalik francuski, świeżo wypiekany każdego dnia.",
                9.00,
                CATEGORY_SNACKS,
                "🥐");

        insertProduct(db,
                "Tost z serem",
                "Chrupiący tost z serem, szynką i dodatkiem warzyw.",
                16.00,
                CATEGORY_SNACKS,
                "🥪");

        insertProduct(db,
                "Sernik",
                "Domowy sernik podawany z sosem owocowym.",
                14.00,
                CATEGORY_SNACKS,
                "🍰");

        ContentValues locationValues = new ContentValues();
        locationValues.put("cafe_name", "Cafe Aroma");
        locationValues.put("address",
                "Aleja Najświętszej Maryi Panny 24\n42-200 Częstochowa");
        locationValues.put("opening_hours",
                "Poniedziałek – Piątek: 08:00 – 20:00\n" +
                        "Sobota – Niedziela: 10:00 – 19:00");

        db.insert("location", null, locationValues);
    }

    private void insertProduct(SQLiteDatabase db,
                               String name,
                               String description,
                               double price,
                               String category,
                               String icon) {

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("price", price);
        values.put("category", category);
        values.put("icon", icon);

        db.insert("products", null, values);
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "products",
                null,
                "category = ?",
                new String[]{category},
                null,
                null,
                "id ASC"
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String description = cursor.getString(
                    cursor.getColumnIndexOrThrow("description"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String icon = cursor.getString(cursor.getColumnIndexOrThrow("icon"));

            products.add(new Product(id, name, description, price, icon));
        }

        cursor.close();
        return products;
    }

    public Location getLocation() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "location",
                null,
                null,
                null,
                null,
                null,
                "id ASC",
                "1"
        );

        Location location = null;

        if (cursor.moveToFirst()) {
            String cafeName = cursor.getString(
                    cursor.getColumnIndexOrThrow("cafe_name"));
            String address = cursor.getString(
                    cursor.getColumnIndexOrThrow("address"));
            String openingHours = cursor.getString(
                    cursor.getColumnIndexOrThrow("opening_hours"));

            location = new Location(cafeName, address, openingHours);
        }

        cursor.close();
        return location;
    }

    public long saveOrder(List<OrderItem> items,
                          double subtotal,
                          double discount,
                          double total,
                          String email) {

        SQLiteDatabase db = getWritableDatabase();
        long orderId;

        db.beginTransaction();

        try {
            String date = new SimpleDateFormat(
                    "dd.MM.yyyy HH:mm",
                    new Locale("pl", "PL")
            ).format(new Date());

            ContentValues orderValues = new ContentValues();
            orderValues.put("order_date", date);
            orderValues.put("subtotal", subtotal);
            orderValues.put("discount", discount);
            orderValues.put("total", total);
            orderValues.put("email", email);

            orderId = db.insertOrThrow("orders", null, orderValues);

            for (OrderItem item : items) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("order_id", orderId);
                itemValues.put("product_name", item.getProductName());
                itemValues.put("quantity", item.getQuantity());
                itemValues.put("unit_price", item.getUnitPrice());
                itemValues.put("item_total", item.getItemTotal());

                db.insertOrThrow("order_items", null, itemValues);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        return orderId;
    }

    public String getOrderHistoryText() {
        SQLiteDatabase db = getReadableDatabase();

        DecimalFormatSymbols symbols =
                DecimalFormatSymbols.getInstance(new Locale("pl", "PL"));
        DecimalFormat format = new DecimalFormat("0.00", symbols);

        Cursor orderCursor = db.query(
                "orders",
                null,
                null,
                null,
                null,
                null,
                "id DESC",
                "10"
        );

        if (!orderCursor.moveToFirst()) {
            orderCursor.close();
            return "Brak zapisanych zamówień.";
        }

        StringBuilder history = new StringBuilder();

        do {
            long orderId = orderCursor.getLong(
                    orderCursor.getColumnIndexOrThrow("id"));
            String date = orderCursor.getString(
                    orderCursor.getColumnIndexOrThrow("order_date"));
            double discount = orderCursor.getDouble(
                    orderCursor.getColumnIndexOrThrow("discount"));
            double total = orderCursor.getDouble(
                    orderCursor.getColumnIndexOrThrow("total"));

            history.append("Zamówienie nr ")
                    .append(orderId)
                    .append("  •  ")
                    .append(date)
                    .append("\n");

            Cursor itemCursor = db.query(
                    "order_items",
                    null,
                    "order_id = ?",
                    new String[]{String.valueOf(orderId)},
                    null,
                    null,
                    "id ASC"
            );

            while (itemCursor.moveToNext()) {
                String productName = itemCursor.getString(
                        itemCursor.getColumnIndexOrThrow("product_name"));
                int quantity = itemCursor.getInt(
                        itemCursor.getColumnIndexOrThrow("quantity"));
                double itemTotal = itemCursor.getDouble(
                        itemCursor.getColumnIndexOrThrow("item_total"));

                history.append("  ")
                        .append(productName)
                        .append(" x")
                        .append(quantity)
                        .append(" = ")
                        .append(format.format(itemTotal))
                        .append(" zł\n");
            }

            itemCursor.close();

            if (discount > 0) {
                history.append("  Rabat: -")
                        .append(format.format(discount))
                        .append(" zł\n");
            }

            history.append("  Suma: ")
                    .append(format.format(total))
                    .append(" zł\n\n");

        } while (orderCursor.moveToNext());

        orderCursor.close();

        return history.toString().trim();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS location");

        onCreate(db);
    }

    public static class Product {

        private final long id;
        private final String name;
        private final String description;
        private final double price;
        private final String icon;

        public Product(long id,
                       String name,
                       String description,
                       double price,
                       String icon) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.icon = icon;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public double getPrice() {
            return price;
        }

        public String getIcon() {
            return icon;
        }
    }

    public static class Location {

        private final String cafeName;
        private final String address;
        private final String openingHours;

        public Location(String cafeName, String address, String openingHours) {
            this.cafeName = cafeName;
            this.address = address;
            this.openingHours = openingHours;
        }

        public String getCafeName() {
            return cafeName;
        }

        public String getAddress() {
            return address;
        }

        public String getOpeningHours() {
            return openingHours;
        }
    }

    public static class OrderItem {

        private final String productName;
        private final int quantity;
        private final double unitPrice;
        private final double itemTotal;

        public OrderItem(String productName,
                         int quantity,
                         double unitPrice,
                         double itemTotal) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.itemTotal = itemTotal;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getItemTotal() {
            return itemTotal;
        }
    }
}