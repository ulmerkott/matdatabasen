package ulmerkott.matdatabasen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    static final String INTENT_ROW_ID = "rowid";

    // Values for fineli.fi database
    /* From the DB:
    "id" INTEGER,
    "name" TEXT,
    "energi, beräknad (kJ)" REAL,
    "kolhydrater, digererbara (g)" TEXT,
    "fett, totalt (g)" TEXT,
    "protein, totalt (g)" TEXT,
    "portions" TEXT DEFAULT ('NULL')
    */
    static final String KEY_NAME = "name";
    static final String KEY_ENERGY = "energi, beräknad (kJ)";
    static final String KEY_CARB = "kolhydrater, digererbara (g)";
    static final String KEY_FAT = "fett, totalt (g)";
    static final String KEY_PROTEIN = "kolhydrater, digererbara (g)";
    static final String KEY_PORTIONS = "portions";

    static final String KJ_TO_KCAL_FACTOR = "4.184";

    static final String SQL_GET_KCAL = "ROUND(\"" + KEY_ENERGY + "\"" + "/" + KJ_TO_KCAL_FACTOR + ") as " + "\"" + KEY_ENERGY + "\"";

    // Values for Livsmedelsverkets database.
    //    static final String KEY_NAME = "Namn";
    //    static final String KEY_KCAL = "Energi (kcal)";
    //    static final String SQL_GET_KCAL = "\"" + KEY_KCAL + "\"";

    static final String SQL_DEFAULT_ORDER = " ORDER BY " + KEY_NAME;
    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DatabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public Cursor GetLivsmedel() {
        return database.rawQuery(
                String.format("SELECT rowid as _id,%s,%s FROM livsmedel %s",
                        KEY_NAME, SQL_GET_KCAL, SQL_DEFAULT_ORDER), null);
    }

    public Cursor SearchLivsmedel(String value) {
        return database.rawQuery(
                "SELECT rowid as _id," + KEY_NAME + "," + SQL_GET_KCAL + " FROM livsmedel WHERE "
                        + KEY_NAME + " LIKE \"%" + value + "%\" " + SQL_DEFAULT_ORDER, null);
    }


    public Food GetFood(String matRowId) {
        Cursor cursor = database.rawQuery("SELECT \"" + KEY_NAME + "\"," + SQL_GET_KCAL + ",\"" +
                KEY_PROTEIN + "\",\"" + KEY_CARB + "\",\"" + KEY_FAT + "\", " + KEY_PORTIONS + " " +
                "FROM livsmedel WHERE rowid LIKE " + matRowId, null);
        cursor.moveToFirst();
        String[] nameInfo = ParseNameString(cursor.getString(0));
        Food food = new Food(nameInfo[0], nameInfo[1], cursor.getInt(1),
                cursor.getFloat(2), cursor.getFloat(3), cursor.getFloat(4));
        food.Portions = ParsePortionString(cursor.getString(5));
        return food;
    }

    private String[] ParseNameString(String nameString) {
        String name = "";
        String info = "";
        int separatorIndex = nameString.indexOf(",");
        if (separatorIndex != -1) {
            // Found extended info
            info = nameString.substring(separatorIndex+1).trim();
            // Capitalize first letter
            info = info.substring(0, 1).toUpperCase() + info.substring(1);
            name = nameString.substring(0,separatorIndex);
        }
        else {
            name = nameString;
        }
        return new String[] {name, info};
    }

    private HashMap<String, Integer> ParsePortionString(String portionsString) {
        // 1 Portion med 1000kJ:(202 g),1 liten portion:(100 g),1 medelstor portion:(160 g),1 stor portion:(215 g)
        HashMap<String, Integer> portions = new HashMap<String, Integer>();

        for (String portion: portionsString.split(",")) {
            if (!portion.isEmpty()) {
                int separatorIndex = portion.indexOf(":");
                String gramString = portion.substring(separatorIndex + 1).trim();
                String title = portion.substring(0, separatorIndex) + " " + gramString;
                Integer grams = Integer.parseInt(gramString.substring(gramString.indexOf("(") + 1, gramString.indexOf(" g)")));

                portions.put(title, grams);
            }
        }

        return portions;
    }
}