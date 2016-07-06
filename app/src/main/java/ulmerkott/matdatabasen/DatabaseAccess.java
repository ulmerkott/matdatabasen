package ulmerkott.matdatabasen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public Cursor getLivsmedel() {
        return database.rawQuery(
                String.format("SELECT rowid as _id,%s,%s FROM livsmedel %s",
                        KEY_NAME, SQL_GET_KCAL, SQL_DEFAULT_ORDER), null);
    }

    public Cursor searchLivsmedel(String value) {
        return database.rawQuery(
                "SELECT rowid as _id," + KEY_NAME + "," + SQL_GET_KCAL + " FROM livsmedel WHERE "
                        + KEY_NAME + " LIKE \"%" + value + "%\" " + SQL_DEFAULT_ORDER, null);
    }


    public Food GetFood(String matRowId) {
        Cursor cursor = database.rawQuery("SELECT \"" + KEY_NAME + "\"," + SQL_GET_KCAL + ",\"" +
                KEY_PROTEIN + "\",\"" + KEY_CARB + "\",\"" + KEY_FAT + "\", " + KEY_PORTIONS + " " +
                "FROM livsmedel WHERE rowid LIKE " + matRowId, null);
        cursor.moveToFirst();
        Food food = new Food(cursor.getString(cursor.getColumnIndex(KEY_NAME)), cursor.getInt(1),
                cursor.getFloat(2), cursor.getFloat(3), cursor.getFloat(4));
        food.Portions = parsePortionString(cursor.getString(5));
        return food;
    }

    private HashMap<String,Integer> parsePortionString(String string) {
        // TODO: Do this!!
        HashMap<String,Integer> portions = new HashMap<String, Integer>();
        return portions;
    }
}