package de.struckmeierfliesen.ds.micromanagement;

import android.util.Log;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.struckmeierfliesen.ds.calendarpager.DateUtil;

public class Food {
    public static final int PROTEINS = 1;
    public static final int CARBS = 2;
    public static final int FATS = 4;
    public static final int VEGGIES = 8;
    public static final int FRUITS = 16;
    /**
     * This field is only used to query hidden foods and it is not a valid type!
     * @see de.struckmeierfliesen.ds.micromanagement.sqlite.DatabaseConnection#loadFood(int, Date)
     */
    public static final int HIDDEN = 32;
    private static final Set<Integer> types = new HashSet<Integer>(Arrays.asList(PROTEINS, CARBS, FATS, VEGGIES, FRUITS));

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
        if (isHidden(type)) return -type;
        return type;
    }

    public void setType(int type) {
        if (isHidden(type)) {
            Log.w("Food", "This Food is hidden with type " + (-type) + "(" + toString() + ")");
        } else if (!isValidType(type)) {
            throw illegalFoodTypeExcpetion(type);
        }
        this.type = type;
    }

    public long getLastEaten() {
        return lastEaten;
    }

    public void setLastEaten(long lastEaten) {
        //if (lastEaten < 0) throw new IllegalArgumentException("lastEaten timestamp must be positive!");
        this.lastEaten = lastEaten;
    }

    public Date getLastEatenDate() {
        return new Date(lastEaten);
    }

    public String getLastEatenDateString() {
        Date date = new Date(lastEaten);
        if (lastEaten == -1)
            return "never"; // TODO: translation
        else if (DateUtil.getDayDifference(new Date(), date) <= 7)
            return DateUtil.getWeekday(date);
        else
            return DateUtil.formatDate(date);
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
                "Type(" + type + ") has to be either Food.PROTEINS(1), Food.CARBS(2), Food.FATS(4), Food.FRUITS(8), Food.VEGGIES(16)!"
        );
    }

    @Override
    public String toString() {
        return getName() + " (ID: " + getId() + ") is of type " + getType() + " and was last eaten: " + getLastEatenDateString();
    }

    public boolean isHidden() {
        return isHidden(type);
    }

    public static boolean isHidden(int type) {
        return isValidType(-type);
    }
}
