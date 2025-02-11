package com.example.farfromhome;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
            "category_name TEXT PRIMARY KEY" +
            ")";
    private static final String CREATE_SUITCASE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_SUITCASE_CATEGORIES + " (" +
            "category_name TEXT PRIMARY KEY" +
            ")";

    private static final String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_EXPIRY + " TEXT, " +
            "category_name TEXT, " +
            "FOREIGN KEY(category_name) REFERENCES categories(category_name)" +
            ")";

    private static final String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_SHOPPING_LIST + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_EXPIRY + " TEXT," +
            "category_name TEXT, " +
            "FOREIGN KEY(category_name) REFERENCES categories(category_name)" +
            ")";

    private static final String CREATE_SUITCASE_TABLE = "CREATE TABLE " + TABLE_SUITCASE + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_EXPIRY + " TEXT," +
            "category_name TEXT, " +
            "FOREIGN KEY(category_name) REFERENCES categories(category_name)" +
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

    public boolean addCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);

        long result = db.insert(TABLE_CATEGORIES, null, values);

        System.out.println(getAllCategories());
        db.close();



        return result != -1;
    }

    public boolean addSuitcaseCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);

        long result = db.insert(TABLE_SUITCASE_CATEGORIES, null, values);

        System.out.println(getAllSuitcaseCategories());
        db.close();

        return result != -1;
    }

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

    public boolean deleteCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, "category_name = ?", new String[]{categoryName});
        db.delete(TABLE_SHOPPING_LIST, "category_name = ?", new String[]{categoryName});
        int rowsDeleted = db.delete(TABLE_CATEGORIES, "category_name = ?", new String[]{categoryName});

        db.close();

        return rowsDeleted > 0;
    }

    public boolean deleteSuitcaseCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUITCASE, "category_name = ?", new String[]{categoryName});
        int rowsDeleted = db.delete(TABLE_SUITCASE_CATEGORIES, "category_name = ?", new String[]{categoryName});

        db.close();

        return rowsDeleted > 0;
    }

    public boolean doesProductPantryExist(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PANTRY,
                new String[]{"name"},
                "LOWER(name) = ?",
                new String[]{productName},
                null,
                null,
                null
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

    public List<Item> getAllPantryItems() {
        return getItemsFromPantryTable();
    }

    public boolean doesProductShoppingListExist(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_SHOPPING_LIST,
                new String[]{"name"},
                "LOWER(name) = ?",
                new String[]{productName},
                null,
                null,
                null
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
                TABLE_SUITCASE,
                new String[]{"name"},
                "LOWER(name) = ?",
                new String[]{productName},
                null,
                null,
                null
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


    public boolean removePantryItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PANTRY, COLUMN_NAME + " = ?", new String[]{itemName});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean removeShoppingListItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SHOPPING_LIST, COLUMN_NAME + " = ?", new String[]{itemName});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean removeSuitcaseItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SUITCASE, COLUMN_NAME + " = ?", new String[]{itemName});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean removeAllPantryItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PANTRY, null, null);
        db.close();
        return rowsDeleted > 0;
    }

    public boolean removeAllShoppingListItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SHOPPING_LIST, null, null);
        db.close();
        return rowsDeleted > 0;
    }

    public boolean removeAllSuitcaseItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SUITCASE, null, null);
        db.close();
        return rowsDeleted > 0;
    }



    public List<Item> getItemsFromPantryTable() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COLUMN_EXPIRY + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                Date expiryDate = null;
                if (expiryStr != null && !expiryStr.isEmpty()) {
                    try {
                        expiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiryStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        expiryDate = null;
                    }
                }

                Item item = new Item(name, quantity, expiryDate, category);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

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
                e.printStackTrace();
                expiryDate = null;
            }
        }

        return new Item(name, quantity, expiryDate,category);
    }

    public List<Item> getItemsFromShoppingListTable() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SHOPPING_LIST;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

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
                e.printStackTrace();
                expiryDate = null;
            }
        }

        return new Item(name, quantity, expiryDate,category);
    }

    public List<SuitcaseItem> getItemsFromSuitcaseTable() {
        List<SuitcaseItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SUITCASE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));


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
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUITCASE + " WHERE category_name = ?", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToSuitcaseItem(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    private SuitcaseItem cursorToSuitcaseItem(Cursor cursor) {
        @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
        @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));


        return new SuitcaseItem(name, quantity,category);
    }

    public boolean updatePantryItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put("category_name", item.getCategory());

        if (item.getExpiry() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expiryString = dateFormat.format(item.getExpiry());
            values.put(COLUMN_EXPIRY, expiryString);
        } else {
            values.putNull(COLUMN_EXPIRY);
        }

        int rowsAffected = db.update(TABLE_PANTRY, values, COLUMN_NAME + " = ?", new String[]{item.getName()});
        db.close();
        return rowsAffected > 0;
    }

    public void updateShoppingListItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put("category_name", item.getCategory());

        if (item.getExpiry() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String expiryString = dateFormat.format(item.getExpiry());
            values.put(COLUMN_EXPIRY, expiryString);
        } else {
            values.putNull(COLUMN_EXPIRY);
        }

        db.update(TABLE_SHOPPING_LIST, values, COLUMN_NAME + " = ?", new String[]{item.getName()});
        db.close();
    }

    public void updateSuitcaseItem(SuitcaseItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put("category_name", item.getCategory());

        db.update(TABLE_SUITCASE, values, COLUMN_NAME + " = ?", new String[]{item.getName()});
        db.close();
    }

    public Item getPantryItem(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Item item = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " WHERE " + COLUMN_NAME + " = ?", new String[]{itemName});

        if (cursor.moveToFirst()) {
            item = cursorToPantryItem(cursor);
        }

        cursor.close();
        db.close();
        return item;
    }

    public Item getShoppingListItem(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Item item = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SHOPPING_LIST + " WHERE " + COLUMN_NAME + " = ?", new String[]{itemName});

        if (cursor.moveToFirst()) {
            item = cursorToShoppingListItem(cursor);
        }

        cursor.close();
        db.close();
        return item;
    }

    public SuitcaseItem getSuitcaseItem(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        SuitcaseItem item = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUITCASE + " WHERE " + COLUMN_NAME + " = ?", new String[]{itemName});

        if (cursor.moveToFirst()) {
            item = cursorToSuitcaseItem(cursor);
        }

        cursor.close();
        db.close();
        return item;
    }

    @SuppressLint("Range")
    public String getCategory(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String category = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES + " WHERE category_name = ?", new String[]{categoryName});

        if (cursor.moveToFirst()) {
            category = cursor.getString(cursor.getColumnIndex("category_name"));
        }

        cursor.close();
        db.close();
        return category;
    }

    @SuppressLint("Range")
    public String getSuitcaseCategory(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String category = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUITCASE_CATEGORIES + " WHERE category_name = ?", new String[]{categoryName});

        if (cursor.moveToFirst()) {
            category = cursor.getString(cursor.getColumnIndex("category_name"));
        }

        cursor.close();
        db.close();
        return category;
    }

    @SuppressLint("Range")
    public List<Item> searchPantryItemsByName(String namePrefix) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+ TABLE_PANTRY +" WHERE name LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{namePrefix + "%"});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                Date expiryDate = null;
                if (expiryStr != null && !expiryStr.isEmpty()) {
                    try {
                        expiryDate = new SimpleDateFormat("dd-MM-yyy").parse(expiryStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        expiryDate = null;
                    }
                }


                Item item= new Item(name,quantity,expiryDate,category);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    @SuppressLint("Range")
    public List<Item> searchPantryItemsByCategoryAndName(String categoryPrefix, String namePrefix) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+ TABLE_PANTRY +" WHERE category_name= ? AND name LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{categoryPrefix, namePrefix + "%"});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                String expiryStr = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRY));
                String category = cursor.getString(cursor.getColumnIndex("category_name"));

                Date expiryDate = null;
                if (expiryStr != null && !expiryStr.isEmpty()) {
                    try {
                        expiryDate = new SimpleDateFormat("dd-MM-yyyy").parse(expiryStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        expiryDate = null;
                    }
                }


                Item item = new Item(name, quantity, expiryDate, category);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }



    @SuppressLint("Range")
    public List<Item> searchShoppingListItemsByName(String namePrefix) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+ TABLE_SHOPPING_LIST +" WHERE name LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{namePrefix + "%"});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                Date expiryDate = null;

                Item item= new Item(name,quantity,expiryDate,category);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    @SuppressLint("Range")
    public List<Item> searchShoppingListItemsByCategoryAndName(String categoryPrefix,String namePrefix) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+ TABLE_SHOPPING_LIST +" WHERE category_name= ? AND name LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{categoryPrefix, namePrefix + "%"});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                Date expiryDate = null;

                Item item= new Item(name,quantity,expiryDate,category);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    @SuppressLint("Range")
    public List<SuitcaseItem> searchSuitcaseListItemsByName(String namePrefix) {
        List<SuitcaseItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+ TABLE_SUITCASE +" WHERE name LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{namePrefix + "%"});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                SuitcaseItem item= new SuitcaseItem(name,quantity,category);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    @SuppressLint("Range")
    public List<SuitcaseItem> searchSuitcaseListItemsByCategoryAndName(String categoryPrefix,String namePrefix) {
        List<SuitcaseItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+ TABLE_SUITCASE +" WHERE category_name= ? AND name LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{categoryPrefix, namePrefix + "%"});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category_name"));

                SuitcaseItem item= new SuitcaseItem(name,quantity,category);
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    public Item modifyPantryItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String expiryString = dateFormat.format(item.getExpiry());
        values.put(COLUMN_EXPIRY, expiryString);
        values.put("category_name", item.getCategory());

        int rowsAffected = db.update(TABLE_PANTRY, values, COLUMN_NAME + " = ?", new String[]{item.getName()});
        db.close();

        if (rowsAffected > 0) {
            return item;
        } else {
            return null;
        }
    }

    public boolean deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isDeleted = false;

        int suitcaseResult = db.delete(TABLE_SUITCASE, null, null);
        int pantryResult = db.delete(TABLE_PANTRY, null, null);
        int shoppingListResult = db.delete(TABLE_SHOPPING_LIST, null, null);

        if (suitcaseResult > 0 || pantryResult > 0 || shoppingListResult > 0) {
            isDeleted = true;
        }

        db.close();
        return isDeleted;
    }

    public Item modifyShoppingListItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_EXPIRY, item.getExpiry().getTime());
        values.put("category_name", item.getCategory());

        int rowsAffected = db.update(TABLE_SHOPPING_LIST, values, COLUMN_NAME + " = ?", new String[]{item.getName()});
        db.close();

        if (rowsAffected > 0) {
            return item;
        } else {
            return null;
        }
    }

    public SuitcaseItem modifySuitcaseItem(SuitcaseItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put("category_name", item.getCategory());

        int rowsAffected = db.update(TABLE_SUITCASE, values, COLUMN_NAME + " = ?", new String[]{item.getName()});
        db.close();

        if (rowsAffected > 0) {
            return item;
        } else {
            return null;
        }
    }



}