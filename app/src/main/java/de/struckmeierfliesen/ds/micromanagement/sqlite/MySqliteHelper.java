package de.struckmeierfliesen.ds.micromanagement.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import de.struckmeierfliesen.ds.micromanagement.Food;

public class MySqliteHelper extends SQLiteOpenHelper {

    public static final String TABLE_FOOD = "food";
    public static final String TABLE_EATEN = "eaten";
    public static final String[] TABLES = {TABLE_FOOD, TABLE_EATEN};

    public static final String FOOD_COLUMN_ID = "food_id";
    public static final String FOOD_COLUMN_NAME = "name";
    public static final String FOOD_COLUMN_TYPE = "type";
    public static final String[] FOOD_COLUMNS = {FOOD_COLUMN_ID, FOOD_COLUMN_NAME, FOOD_COLUMN_TYPE};

    public static final String EATEN_COLUMN_ID = "eaten_id";
    public static final String EATEN_COLUMN_FOOD_ID = "food_foreign_id";
    public static final String EATEN_COLUMN_LAST_EATEN = "lastEaten";
    public static final String[] EATEN_COLUMNS = {EATEN_COLUMN_ID, EATEN_COLUMN_FOOD_ID, EATEN_COLUMN_LAST_EATEN};

    public static final String DATABASE_NAME = "micro_management.db";
    public static final int FIRST_VERSION = 1;
    public static final int DATABASE_VERSION = FIRST_VERSION;

    public MySqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // Create tables
        database.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_FOOD + "("
                + FOOD_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FOOD_COLUMN_NAME + " TEXT NOT NULL, "
                + FOOD_COLUMN_TYPE + " INTEGER NOT NULL)");
        database.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_EATEN + "("
                + EATEN_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EATEN_COLUMN_LAST_EATEN + " INTEGER NOT NULL DEFAULT -1, "
                + EATEN_COLUMN_FOOD_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + EATEN_COLUMN_FOOD_ID + ") REFERENCES " + TABLE_FOOD + "(" + FOOD_COLUMN_ID + ")"
                + ")");

        // Input default values
        Map<Integer, String[]> defaults = new HashMap<>();
        defaults.put(Food.PROTEINS, new String[] {
                "Pute", "Schwein", "Rind", "Kabeljau", "Lachs", "Thunfisch", "Quark",
                "Frischkäse", "Ei", "Käse", "Tilapia", "Eiweißpulver"
        });
        defaults.put(Food.CARBS, new String[] {
                "Toast", "Haferflocken", "Nudeln", "Reis", "Dinkelflocken", "Honig",
                "Kartoffel", "Quinoa", "Amaranth", "Milchreis", "Brot"
        });
        defaults.put(Food.FATS, new String[] {
                "Erdnuss", "Walnuss", "Mandel", "Avocado", "Haselnuss", "Olive"
        });
        defaults.put(Food.FRUITS, new String[] {
                "Apfel", "Banane", "Birne", "Orange", "Khaki", "Kiwi", "Pflaume", "Aprikose",
                "Ananas", "Beeren", "Rosine", "Traube", "Dattel", "Pfirsich", "Mango"
        });
        defaults.put(Food.VEGGIES, new String[] {
                "Brokkoli", "Spinat", "Rosenkohl", "Kidneybohne", "Stangenbohne", "Blumenkohl",
                "Karotte", "Mais", "Linse", "Zwiebel", "Pilze", "Lauch", "Schwarzwurzel",
                "Weißkohl", "Spitzkohl", "Wirsing", "Erbsen", "Zucchini", "Aubergine",
                "Kichererbsen", "Fenchel", "Spargel", "Sauerkraut", "Tomate", "Rotkohl", "Paprika",
                "Rote Beete"
        });
        for (Map.Entry<Integer, String[]> entry : defaults.entrySet()) {
            int type = entry.getKey();
            String[] values = entry.getValue();
            for (String value: values) {
                addFood(value, type, database);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("onUpgrade", "OldVersion: " + oldVersion + " - newVersion: " + newVersion);
        if (oldVersion == newVersion) throw new RuntimeException("Upgrade stays on the same version number! Weird!");
        final String TEMP_TABLE = "temp_table";
        switch (oldVersion) {
            default:
                break;
        }
    }

    public static void displayCursor(Cursor cursor, boolean close, String title) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int columnCount = cursor.getColumnCount();
            String row = "";
            for (int i = 0; i < columnCount; i++) {
                if (!row.equals("")) row += ", ";
                row += cursor.getColumnName(i) + ": " + cursor.getString(i);
            }
            Log.d("DisplayCursor(" + title + ")", row);
            cursor.moveToNext();
        }
        if (close) cursor.close();
    }

    private void addFood(String name, int type, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(FOOD_COLUMN_NAME, name);
        values.put(FOOD_COLUMN_TYPE, type);
        db.insert(TABLE_FOOD, null, values);
    }
}