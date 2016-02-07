package de.struckmeierfliesen.ds.micromanagement.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.struckmeierfliesen.ds.calendarpager.DateUtil;
import de.struckmeierfliesen.ds.micromanagement.Food;

import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.DATABASE_NAME;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.EATEN_COLUMN_FOOD_ID;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.EATEN_COLUMN_ID;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.EATEN_COLUMN_LAST_EATEN;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.FOOD_COLUMN_ID;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.FOOD_COLUMN_NAME;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.FOOD_COLUMN_TYPE;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.TABLES;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.TABLE_EATEN;
import static de.struckmeierfliesen.ds.micromanagement.sqlite.MySqliteHelper.TABLE_FOOD;

public class DatabaseConnection {
    private static final int DAY_RANGE = 7;

    // Database fields
    private SQLiteDatabase database;
    private MySqliteHelper dbHelper;

    public DatabaseConnection(Context context) {
        dbHelper = new MySqliteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public String exportDatabase() {
        JSONObject databaseJSON = new JSONObject();
        try {
            for (String table : TABLES) {
                databaseJSON.put(table, tableToJSON(table));
            }
            String jsonString = databaseJSON.toString();
            Log.d("Exported JSON: ", jsonString);
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray tableToJSON(String tableName) {
        JSONArray resultSet = new JSONArray();

        String searchQuery = "SELECT  * FROM " + tableName;
        Cursor cursor = database.rawQuery(searchQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            Log.d(cursor.getColumnName(i) + ": ", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public static void dropDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    public void importDatabase(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            for (String table : TABLES) {
                importTable(jsonObject, table);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void importTable(JSONObject jsonObject, String tableName) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(tableName);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entry = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();

                Iterator<String> iter = entry.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = entry.get(key);
                        if (value == null) values.putNull(key);
                        else if (value instanceof String) values.put(key, (String) value);
                        else if (value instanceof Integer) values.put(key, (int) value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                database.insert(tableName, null, values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static Food cursorToFood(Cursor cursor) {
        return cursorToFood(cursor, -1, true);
    }

    private static Food cursorToFood(Cursor cursor, int type) {
        return cursorToFood(cursor, type, true);
    }

    private static Food cursorToFood(Cursor cursor, boolean eaten) {
        return cursorToFood(cursor, -1, eaten);
    }

    private static Food cursorToFood(Cursor cursor, int type, boolean eaten) {
        int id = cursor.getInt(cursor.getColumnIndex(FOOD_COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(FOOD_COLUMN_NAME));
        if (type == -1) {
            type = cursor.getInt(cursor.getColumnIndex(FOOD_COLUMN_TYPE)); // TODO do automatically and not with -1
        }
        if (eaten) {
            long lastEaten = cursor.getLong(cursor.getColumnIndex(EATEN_COLUMN_LAST_EATEN));
            return new Food(id, name, type, lastEaten, 1);
        } else {
            return new Food(id, name, type);
        }
    }

    public List<Food> loadFood(Date date) {
        open();
        // Determine the earliest date for the query
        long earliestDate = DateUtil.getStartOfDay(DateUtil.addDays(date, -DAY_RANGE)).getTime();
        long latestDate = DateUtil.getEndOfDay(date).getTime();

        // Load food eating times
        Cursor eatenCursor = database.rawQuery(
                "SELECT " +
                        TABLE_FOOD + "." + FOOD_COLUMN_ID + ", " +
                        TABLE_FOOD + "." + FOOD_COLUMN_NAME + ", " +
                        TABLE_EATEN + "." + EATEN_COLUMN_LAST_EATEN + ", " +
                        TABLE_FOOD + "." + FOOD_COLUMN_TYPE + ", " +
                        TABLE_EATEN + "." + EATEN_COLUMN_ID + // TODO: Unnecessary
                        " FROM " + TABLE_FOOD + ", " + TABLE_EATEN +
                        " WHERE " +
                        TABLE_FOOD + "." + FOOD_COLUMN_ID + " = " + TABLE_EATEN + "." + EATEN_COLUMN_FOOD_ID +
                        " AND " +
                        TABLE_EATEN + "." + EATEN_COLUMN_LAST_EATEN + " > " + earliestDate +
                        " AND " +
                        TABLE_EATEN + "." + EATEN_COLUMN_LAST_EATEN + " < " + latestDate +
                        " ORDER BY " +
                        TABLE_EATEN + "." + EATEN_COLUMN_LAST_EATEN + " DESC"
                , null);
        Map<Integer, Food> foods = new LinkedHashMap<>();
        MySqliteHelper.displayCursor(eatenCursor, false, "eatenCursor");
        if (eatenCursor != null && eatenCursor.moveToFirst()) {
            while (!eatenCursor.isAfterLast()) {
                Food food = cursorToFood(eatenCursor);
                int id = food.getId();
                if (foods.containsKey(id)) {
                    food = foods.get(id);
                    food.incrementEatenThisWeek();
                } else if (DateUtil.isSameDay(date, food.getLastEatenDate())) {
                    foods.put(id, food);
                }
                eatenCursor.moveToNext();
            }
            eatenCursor.close();
        }

        close();
        return new ArrayList<Food>(foods.values());
    }

    public List<Food> loadFood(int type, Date date) {
        open();
        // Determine the type selection
        String where = TABLE_FOOD + "." + FOOD_COLUMN_TYPE;
        if (type == Food.HIDDEN ||Food.isHidden(type)) {
            where += " < 0";
        } else {
            where += " = " + type;
        }
        // Determine the earliest date for the query
        long earliestDate = DateUtil.getStartOfDay(DateUtil.addDays(date, -DAY_RANGE)).getTime();
        long latestDate = DateUtil.getEndOfDay(date).getTime();

        // Load food eating times
        Cursor eatenCursor = database.rawQuery(
                "SELECT " +
                        TABLE_FOOD + "." + FOOD_COLUMN_ID + ", " +
                        TABLE_FOOD + "." + FOOD_COLUMN_NAME + ", " +
                        TABLE_FOOD + "." + FOOD_COLUMN_TYPE + ", " +
                        TABLE_EATEN + "." + EATEN_COLUMN_LAST_EATEN +", " +
                        TABLE_EATEN + "." + EATEN_COLUMN_ID + // TODO: Unnecessary - only for debugging
                        " FROM " + TABLE_FOOD + ", " + TABLE_EATEN +
                        " WHERE " +
                        where +
                        " AND " +
                        TABLE_FOOD + "." + FOOD_COLUMN_ID + " = " + TABLE_EATEN + "." + EATEN_COLUMN_FOOD_ID +
                        " AND " +
                        TABLE_EATEN + "." + EATEN_COLUMN_LAST_EATEN + " > " + earliestDate +
                        " AND " +
                        TABLE_EATEN + "." + EATEN_COLUMN_LAST_EATEN + " < " + latestDate +
                        " ORDER BY " +
                        TABLE_EATEN + "." + EATEN_COLUMN_LAST_EATEN + " DESC"
                , null);
        Map<Integer, Food> foods = new LinkedHashMap<>();
        MySqliteHelper.displayCursor(eatenCursor, false, "eatenCursor");
        if (eatenCursor != null && eatenCursor.moveToFirst()) {
            while (!eatenCursor.isAfterLast()) {
                Food food = cursorToFood(eatenCursor);
                int id = food.getId();
                if (foods.containsKey(id)) {
                    food = foods.get(id);
                    food.incrementEatenThisWeek();
                } else if (DateUtil.isSameDay(date, food.getLastEatenDate())) {
                    foods.put(id, food);
                }
                foods.put(id, food);
                eatenCursor.moveToNext();
            }
            eatenCursor.close();
        }

        // Load foods that have not yet been eaten
        Cursor foodCursor = database.rawQuery(
                "SELECT " +
                        FOOD_COLUMN_ID + ", " +
                        FOOD_COLUMN_NAME + ", " +
                        FOOD_COLUMN_TYPE +
                        " FROM " + TABLE_FOOD +
                        " WHERE " + where
                , null);
        MySqliteHelper.displayCursor(foodCursor, false, "foodCursor");

        if (foodCursor != null && foodCursor.moveToFirst()) {
            while (!foodCursor.isAfterLast()) {
                Food food = cursorToFood(foodCursor,false);
                int id = food.getId();
                if (!foods.containsKey(id)) {
                    foods.put(id, food);
                }
                foodCursor.moveToNext();
            }
            foodCursor.close();
        }
        close();
        return new ArrayList<Food>(foods.values());
    }

    public void eatFood(Food food, Date date) {
        open();
        ContentValues values = new ContentValues();
        values.put(EATEN_COLUMN_LAST_EATEN, date.getTime());
        values.put(EATEN_COLUMN_FOOD_ID, food.getId());
        database.insert(TABLE_EATEN, null, values);
        close();
    }

    public long addFood(String name) {
        open();
        ContentValues values = new ContentValues();
        values.put(FOOD_COLUMN_NAME, name);
        long id = database.insert(TABLE_FOOD, null, values);
        close();
        return id;
    }

    public void clearHistory() {
        open();
        database.execSQL("DELETE FROM " + TABLE_EATEN);
        database.execSQL("VACUUM");
        close();
    }

    public void unEatFood(int id) {
        open();
        String querySql = "SELECT " + EATEN_COLUMN_ID + " FROM " + TABLE_EATEN +
                " WHERE " + EATEN_COLUMN_FOOD_ID + " = " + id +
                " ORDER BY " + EATEN_COLUMN_LAST_EATEN + " DESC" +
                " LIMIT 1";
        String deleteSql = "DELETE FROM " + TABLE_EATEN +
                " WHERE " + EATEN_COLUMN_ID + " = (" + querySql + ")";
        database.execSQL(deleteSql);
        close();
    }

    public void hideFood(int id) {
        open();
        database.execSQL("UPDATE " + TABLE_FOOD +
                " SET " + FOOD_COLUMN_TYPE + " = -1*" + FOOD_COLUMN_TYPE +
                " WHERE " + FOOD_COLUMN_ID + " = " + id);
        close();
    }
}
