package com.example.farfromhome;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "farfromhomedb.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_PANTRY = "pantry";
    public static final String TABLE_SHOPPING_LIST = "shopping_list";
    public static final String TABLE_LUGGAGE = "luggage";

    // Common column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_IMAGE_RESOURCE = "image_resource";
    public static final String COLUMN_EXPIRY = "expiry";

    // SQL query to create tables
    private static final String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_IMAGE_RESOURCE + " TEXT, " +
            COLUMN_EXPIRY + " TEXT" +
            ")";

    private static final String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_SHOPPING_LIST + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_IMAGE_RESOURCE + " TEXT, " +
            COLUMN_EXPIRY + " TEXT" +
            ")";

    private static final String CREATE_LUGGAGE_TABLE = "CREATE TABLE " + TABLE_LUGGAGE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_IMAGE_RESOURCE + " TEXT, " +
            COLUMN_EXPIRY + " TEXT" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the tables
        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_SHOPPING_LIST_TABLE);
        db.execSQL(CREATE_LUGGAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Dropping the existing tables if they exist and creating new ones
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUGGAGE);
        onCreate(db);
    }

    // Generic method to add items to any table
    public boolean addItem(String tableName, PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_IMAGE_RESOURCE, item.getImageResource() != null ? item.getImageResource().toString() : null);
        values.put(COLUMN_EXPIRY, item.getExpiry() != null ? item.getExpiry().toString() : null);

        long result = db.insert(tableName, null, values);
        db.close();
        return result != -1;
    }

    // Add a Pantry item specifically
    public boolean addPantryItem(PantryItem item) {
        return addItem(TABLE_PANTRY, item);
    }

    // Method to get all pantry items
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PANTRY;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Retrieving the data from the cursor
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String imageUriStr = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_RESOURCE));
                @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));

                // Converting expiry string to Date object
                Date expiryDate = null;
                if (expiryStr != null) {
                    try {
                        expiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiryStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                // Adding the item to the list
                PantryItem item = new PantryItem(name, quantity, null, expiryDate);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    // Method to get items from any table
    public List<PantryItem> getItemsFromTable(String tableName) {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Retrieving the data from the cursor
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String imageUriStr = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_RESOURCE));
                @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));

                // Converting expiry string to Date object
                Date expiryDate = null;
                if (expiryStr != null) {
                    try {
                        expiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiryStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                // Adding the item to the list
                PantryItem item = new PantryItem(name, quantity, null, expiryDate);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }
}
