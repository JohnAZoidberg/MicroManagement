package de.struckmeierfliesen.ds.micromanagement;

import android.text.format.DateFormat;
import android.util.Log;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Food {
    public static final int PROTEINS = 1;
    public static final int CARBS = 2;
    public static final int FATS = 4;
    public static final int VEGGIES = 8;
    public static final int FRUITS = 16;
    public static final int HIDDEN = 32;
    private static final Set<Integer> types = new HashSet<Integer>(Arrays.asList(PROTEINS, CARBS, FATS, VEGGIES, FRUITS, HIDDEN));

    private int id;
    private String name;
    private int type;
    private long lastEaten;
    private int eatenThisWeek;

    private Food() {}

    public Food(int id, String name, int type) {
        this(id, name, type, -1, 0);
    }

    public Food(int id, String name, int type, long lastEaten, int eatenThisWeek) {
        setId(id);
        setName(name);
        setType(type);
        setLastEaten(lastEaten);
        setEatenThisWeek(eatenThisWeek);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.isEmpty()) throw new IllegalArgumentException("The name cannot be empty!");
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if (!isValidType(type)) throw illegalFoodTypeExcpetion(type);
        if (type == HIDDEN) Log.w("Food", "This Food is hidden with type " + (-1 * type));
        this.type = type;
    }

    public long getLastEaten() {
        return lastEaten;
    }

    public void setLastEaten(long lastEaten) {
        //if (lastEaten < 0) throw new IllegalArgumentException("lastEaten timestamp must be positive!");
        this.lastEaten = lastEaten;
    }

    public String getLastEatenDate() {
        Date date = new Date(lastEaten);
        if (lastEaten == -1)
            return "never"; // TODO: translation
        else if (Util.getDayDifference(new Date(), date) <= 7)
            return Util.getWeekday(date);
        else
            return DateFormat.format("dd.MM.yy", date).toString();
    }

    public int getEatenThisWeek() {
        return eatenThisWeek;
    }

    public void setEatenThisWeek(int eatenThisWeek) {
        this.eatenThisWeek = eatenThisWeek;
    }

    public void incrementEatenThisWeek() {
        this.eatenThisWeek++;
    }

    public static boolean isValidType(int type) {
        return types.contains(type);
    }

    public static Set<Integer> getTypes() {
        return types;
    }

    public static IllegalArgumentException illegalFoodTypeExcpetion(int type)  {
        return new IllegalArgumentException(
                "Type(" + type + ") has to be either Food.CARBS, Food.FATS, Food.FRUITS, Food.PROTEINS, Food.VEGGIES or Food.HIDDEN!"
        );
    }

    @Override
    public String toString() {
        return getName() + "(" + getId() + ") is " + getType() + " last eaten at: " + getLastEatenDate();
    }
}
