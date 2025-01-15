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
    public static final String TABLE_CATEGORIES = "categories"; // New categories table

    // Common column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_IMAGE_RESOURCE = "image_resource";
    public static final String COLUMN_EXPIRY = "expiry";

    private static final String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
            "category_name TEXT PRIMARY KEY" + // Il nome della categoria è la chiave primaria
            ")";

    private static final String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " + // Il nome dell'item è unico
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_IMAGE_RESOURCE + " TEXT, " +
            COLUMN_EXPIRY + " TEXT, " +
            "category_name TEXT, " + // La categoria dell'item è il nome della categoria
            "FOREIGN KEY(category_name) REFERENCES categories(category_name)" + // Referenza alla categoria
            ")";

    private static final String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_SHOPPING_LIST + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_IMAGE_RESOURCE + " TEXT, " +
            COLUMN_EXPIRY + " TEXT" +
            ")";

    private static final String CREATE_LUGGAGE_TABLE = "CREATE TABLE " + TABLE_LUGGAGE + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_IMAGE_RESOURCE + " TEXT, " +
            COLUMN_EXPIRY + " TEXT" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_SHOPPING_LIST_TABLE);
        db.execSQL(CREATE_LUGGAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUGGAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    // Aggiungi una categoria
    public boolean addCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);

        long result = db.insert(TABLE_CATEGORIES, null, values);
        db.close();

        return result != -1; // Success if result is not -1
    }

    // Ottieni tutte le categorie
    @SuppressLint("Range")
    public List<String> getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> categories = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT category_name FROM " + TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex("category_name")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categories;
    }

    // Aggiungi un item alla pantry con una categoria
    public boolean addItem(Item item, String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_IMAGE_RESOURCE, item.getImageResource());

        if (item.getExpiry() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expiryString = dateFormat.format(item.getExpiry());
            values.put(COLUMN_EXPIRY, expiryString);
        } else {
            values.putNull(COLUMN_EXPIRY);
        }

        // Inserisci l'item nella tabella pantry
        long result = db.insert(TABLE_PANTRY, null, values);

        // Se l'inserimento è avvenuto con successo, associamo l'item alla categoria
        if (result != -1 && categoryName != null && !categoryName.isEmpty()) {
            // Aggiungi il nome della categoria all'item
            ContentValues categoryValues = new ContentValues();
            categoryValues.put("category_name", categoryName);

            // Inserisci la categoria nell'item
            long categoryResult = db.update(TABLE_PANTRY, categoryValues, COLUMN_NAME + " = ?", new String[]{item.getName()});
            db.close();
            return categoryResult != -1;
        }

        db.close();
        return result != -1;
    }

    // Funzione per ottenere tutti gli articoli nella pantry
    public List<Item> getAllPantryItems() {
        return getItemsFromTable(TABLE_PANTRY);
    }

    // Funzione generica per ottenere gli item da qualsiasi tabella
    public List<Item> getItemsFromTable(String tableName) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Recupero dei dati dal cursor
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String imageUriStr = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_RESOURCE));
                @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));

                // Converto la stringa di scadenza in oggetto Date
                Date expiryDate = null;
                /*if (expiryStr != null && !expiryStr.isEmpty()) {
                    try {
                        expiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiryStr);
                    } catch (ParseException e) {
                        e.printStackTrace(); // Log dell'errore
                        expiryDate = null;  // Imposta null se la conversione fallisce
                    }
                }*/

                // Aggiungo l'item alla lista
                Item item = new Item(name, quantity, imageUriStr != null ? Integer.valueOf(imageUriStr) : null, expiryDate);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    // Funzione per ottenere gli articoli della pantry per categoria
    public List<Item> getPantryItemsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Item> items = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " WHERE category_name = ?", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToPantryItem(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    // Funzione per convertire il cursor in un oggetto Item
    private Item cursorToPantryItem(Cursor cursor) {
        @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
        @SuppressLint("Range") String imageUriStr = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_RESOURCE));
        @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));

        Date expiryDate = null;
        if (expiryStr != null && !expiryStr.isEmpty()) {
            try {
                expiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiryStr);
            } catch (ParseException e) {
                e.printStackTrace(); // Log dell'errore per il debug
                expiryDate = null;  // Imposta a null in caso di errore
            }
        }

        return new Item(name, quantity, imageUriStr != null ? Integer.valueOf(imageUriStr) : null, expiryDate);
    }
}
