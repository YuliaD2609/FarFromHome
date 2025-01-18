package com.example.farfromhome;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.farfromhome.suitcase.SuitcaseItem;

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
    public static final String TABLE_SUITCASE = "suitcase";
    public static final String TABLE_CATEGORIES = "categories"; // New categories table
    public static final String TABLE_SUITCASE_CATEGORIES = "suitcase_categories"; // New categories table

    // Common column names
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_EXPIRY = "expiry";

    private static final String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
            "category_name TEXT PRIMARY KEY" + // Il nome della categoria è la chiave primaria
            ")";
    private static final String CREATE_SUITCASE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_SUITCASE_CATEGORIES + " (" +
            "category_name TEXT PRIMARY KEY" + // Il nome della categoria è la chiave primaria
            ")";

    private static final String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " + // Il nome dell'item è unico
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_EXPIRY + " TEXT, " +
            "category_name TEXT, " + // La categoria dell'item è il nome della categoria
            "FOREIGN KEY(category_name) REFERENCES categories(category_name)" + // Referenza alla categoria
            ")";

    private static final String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_SHOPPING_LIST + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_EXPIRY + " TEXT," +
            "category_name TEXT, " + // La categoria dell'item è il nome della categoria
            "FOREIGN KEY(category_name) REFERENCES categories(category_name)" + // Referenza alla categoria
            ")";

    private static final String CREATE_SUITCASE_TABLE = "CREATE TABLE " + TABLE_SUITCASE + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_EXPIRY + " TEXT," +
            "category_name TEXT, " + // La categoria dell'item è il nome della categoria
            "FOREIGN KEY(category_name) REFERENCES categories(category_name)" + // Referenza alla categoria
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_SUITCASE_CATEGORIES_TABLE);
        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_SHOPPING_LIST_TABLE);
        db.execSQL(CREATE_SUITCASE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUITCASE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUITCASE_CATEGORIES);
        onCreate(db);
    }

    // Aggiungi una categoria
    public boolean addCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);

        long result = db.insert(TABLE_CATEGORIES, null, values);

        System.out.println(getAllCategories());
        db.close();



        return result != -1; // Success if result is not -1
    }

    // Aggiungi una categoria
    public boolean addSuitcaseCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);

        long result = db.insert(TABLE_SUITCASE_CATEGORIES, null, values);

        System.out.println(getAllSuitcaseCategories());
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

    @SuppressLint("Range")
    public List<String> getAllSuitcaseCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> categories = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT category_name FROM " + TABLE_SUITCASE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex("category_name")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categories;
    }

    // Funzione per eliminare una categoria e tutti gli item associati
    public boolean deleteCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, "category_name = ?", new String[]{categoryName});
        db.delete(TABLE_SHOPPING_LIST, "category_name = ?", new String[]{categoryName});
        int rowsDeleted = db.delete(TABLE_CATEGORIES, "category_name = ?", new String[]{categoryName});

        db.close();

        // Ritorna true se la categoria è stata eliminata con successo
        return rowsDeleted > 0;
    }

    // Funzione per eliminare una categoria e tutti gli item associati
    public boolean deleteSuitcaseCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUITCASE, "category_name = ?", new String[]{categoryName});
        int rowsDeleted = db.delete(TABLE_SUITCASE_CATEGORIES, "category_name = ?", new String[]{categoryName});

        db.close();

        // Ritorna true se la categoria è stata eliminata con successo
        return rowsDeleted > 0;
    }

    public boolean doesProductPantryExist(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PANTRY, // Nome della tabella
                new String[]{"name"}, // Colonne da selezionare
                "name = ?", // Clausola WHERE
                new String[]{productName}, // Argomento
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }



    // Aggiungi un item alla pantry con una categoria
    public boolean addPantryItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        ContentValues categoryValues = new ContentValues();
        categoryValues.put("category_name", item.getCategory());
        values.put("category_name", item.getCategory());

        if (item.getExpiry() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expiryString = dateFormat.format(item.getExpiry());
            values.put(COLUMN_EXPIRY, expiryString);
        } else {
            values.putNull(COLUMN_EXPIRY);
        }

        long result = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return result != -1;
    }

    // Funzione per ottenere tutti gli articoli nella pantry
    public List<Item> getAllPantryItems() {
        return getItemsFromPantryTable();
    }

    public boolean doesProductShoppingListExist(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_SHOPPING_LIST, // Nome della tabella
                new String[]{"name"}, // Colonne da selezionare
                "name = ?", // Clausola WHERE
                new String[]{productName}, // Argomento
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean addShoppingListItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        ContentValues categoryValues = new ContentValues();
        categoryValues.put("category_name", item.getCategory());
        values.put("category_name", item.getCategory());

        if (item.getExpiry() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expiryString = dateFormat.format(item.getExpiry());
            values.put(COLUMN_EXPIRY, expiryString);
        } else {
            values.putNull(COLUMN_EXPIRY);
        }

        long result = db.insert(TABLE_SHOPPING_LIST, null, values);
        db.close();
        return result != -1;
    }

    public List<Item> getAllShoppingListItems() {
        return getItemsFromShoppingListTable();
    }

    public boolean doesProductSuitcaseExist(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_SUITCASE, // Nome della tabella
                new String[]{"name"}, // Colonne da selezionare
                "name = ?", // Clausola WHERE
                new String[]{productName}, // Argomento
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean addSuitcaseItem(SuitcaseItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        ContentValues categoryValues = new ContentValues();
        categoryValues.put("category_name", item.getCategory());
        values.put("category_name", item.getCategory());

        long result = db.insert(TABLE_SUITCASE, null, values);
        db.close();
        return result != -1;
    }
    public List<SuitcaseItem> getAllSuitcaseItems() {
        return getItemsFromSuitcaseTable();
    }


    // Method to remove an item from the pantry
    public boolean removePantryItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PANTRY, COLUMN_NAME + " = ?", new String[]{itemName});
        db.close();
        return rowsDeleted > 0; // Return true if at least one row was deleted
    }

    // Method to remove an item from the shopping list
    public boolean removeShoppingListItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SHOPPING_LIST, COLUMN_NAME + " = ?", new String[]{itemName});
        db.close();
        return rowsDeleted > 0; // Return true if at least one row was deleted
    }

    public boolean removeSuitcaseItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SUITCASE, COLUMN_NAME + " = ?", new String[]{itemName});
        db.close();
        return rowsDeleted > 0; // Return true if at least one row was deleted
    }

    // Metodo per rimuovere tutti gli elementi dalla dispensa
    public boolean removeAllPantryItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PANTRY, null, null); // Rimuove tutte le righe
        db.close();
        return rowsDeleted > 0; // Ritorna true se almeno una riga è stata eliminata
    }

    // Metodo per rimuovere tutti gli elementi dalla lista della spesa
    public boolean removeAllShoppingListItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SHOPPING_LIST, null, null); // Rimuove tutte le righe
        db.close();
        return rowsDeleted > 0; // Ritorna true se almeno una riga è stata eliminata
    }

    // Metodo per rimuovere tutti gli elementi dalla lista della spesa
    public boolean removeAllSuitcaseItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SUITCASE, null, null); // Rimuove tutte le righe
        db.close();
        return rowsDeleted > 0; // Ritorna true se almeno una riga è stata eliminata
    }



    public List<Item> getItemsFromPantryTable() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Aggiunta di ORDER BY per ordinare per data di scadenza crescente
        String query = "SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COLUMN_EXPIRY + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Recupero dei dati dal cursor
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                // Converto la stringa di scadenza in oggetto Date
                Date expiryDate = null;
                if (expiryStr != null && !expiryStr.isEmpty()) {
                    try {
                        expiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiryStr);
                    } catch (ParseException e) {
                        e.printStackTrace(); // Log dell'errore
                        expiryDate = null;  // Imposta null se la conversione fallisce
                    }
                }

                // Aggiungo l'item alla lista
                Item item = new Item(name, quantity, expiryDate, category);
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
        @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));
        @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

        Date expiryDate = null;
        if (expiryStr != null && !expiryStr.isEmpty()) {
            try {
                expiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiryStr);
            } catch (ParseException e) {
                e.printStackTrace(); // Log dell'errore per il debug
                expiryDate = null;  // Imposta a null in caso di errore
            }
        }

        return new Item(name, quantity, expiryDate,category);
    }

    // Funzione generica per ottenere gli item da qualsiasi tabella
    public List<Item> getItemsFromShoppingListTable() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SHOPPING_LIST;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Recupero dei dati dal cursor
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                // Aggiungo l'item alla lista
                Item item = new Item(name, quantity, null, category);
                items.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    public List<Item> getShoppingListItemsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Item> items = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SHOPPING_LIST + " WHERE category_name = ?", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToShoppingListItem(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    // Funzione per convertire il cursor in un oggetto Item
    private Item cursorToShoppingListItem(Cursor cursor) {
        @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
        @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));
        @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

        Date expiryDate = null;
        if (expiryStr != null && !expiryStr.isEmpty()) {
            try {
                expiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiryStr);
            } catch (ParseException e) {
                e.printStackTrace(); // Log dell'errore per il debug
                expiryDate = null;  // Imposta a null in caso di errore
            }
        }

        return new Item(name, quantity, expiryDate,category);
    }

    // Funzione generica per ottenere gli item da qualsiasi tabella
    public List<SuitcaseItem> getItemsFromSuitcaseTable() {
        List<SuitcaseItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SUITCASE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Recupero dei dati dal cursor
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                // Aggiungo l'item alla lista
                SuitcaseItem item = new SuitcaseItem(name, quantity, category);
                items.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    public List<SuitcaseItem> getSuitcaseItemsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<SuitcaseItem> items = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SHOPPING_LIST + " WHERE category_name = ?", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToSuitcaseItem(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    // Funzione per convertire il cursor in un oggetto Item
    private SuitcaseItem cursorToSuitcaseItem(Cursor cursor) {
        @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
        @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));


        return new SuitcaseItem(name, quantity,category);
    }
}