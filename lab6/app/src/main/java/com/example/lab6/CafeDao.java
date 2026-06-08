package com.example.lab6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CafeDao {

    private final DatabaseHelper helper;

    public CafeDao(Context context) {
        helper = new DatabaseHelper(context);
    }

    public ArrayList<CafeItem> getAll(String type) {
        ArrayList<CafeItem> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(getTable(type), null, null, null, null, null, "id DESC");

        while (cursor.moveToNext()) {
            list.add(readItem(cursor, type));
        }

        cursor.close();
        return list;
    }

    public CafeItem getById(String type, int id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                getTable(type),
                null,
                "id=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        CafeItem item = null;

        if (cursor.moveToFirst()) {
            item = readItem(cursor, type);
        }

        cursor.close();
        return item;
    }

    public void insert(String type, CafeItem item) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert(getTable(type), null, toValues(type, item));
    }

    public void update(String type, CafeItem item) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.update(
                getTable(type),
                toValues(type, item),
                "id=?",
                new String[]{String.valueOf(item.id)}
        );
    }

    public void delete(String type, int id) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.delete(
                getTable(type),
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    private String getTable(String type) {
        if (type.equals("drinks")) return "drinks";
        if (type.equals("snacks")) return "snacks";
        return "cafes";
    }

    private CafeItem readItem(Cursor cursor, String type) {
        CafeItem item = new CafeItem();

        item.type = type;
        item.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        item.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        item.imageId = cursor.getInt(cursor.getColumnIndexOrThrow("image_id"));

        if (type.equals("cafes")) {
            item.address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            item.openingHours = cursor.getString(cursor.getColumnIndexOrThrow("opening_hours"));
        } else {
            item.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            item.price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        }

        return item;
    }

    private ContentValues toValues(String type, CafeItem item) {
        ContentValues values = new ContentValues();

        values.put("name", item.name);
        values.put("image_id", item.imageId);

        if (type.equals("cafes")) {
            values.put("address", item.address);
            values.put("opening_hours", item.openingHours);
        } else {
            values.put("description", item.description);
            values.put("price", item.price);
        }

        return values;
    }
}