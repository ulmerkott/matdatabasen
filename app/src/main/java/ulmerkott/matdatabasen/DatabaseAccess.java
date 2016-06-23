package ulmerkott.matdatabasen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    static final String KEY_ID = "LivsmedelsNummer";
    static final String KEY_NAME = "Namn";
    static final String KEY_KCAL = "\"Energi (kcal)\"";


    /**
     * Private constructor to aboid object creation from outside classes.
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

    /** TODO: REMOVE THIS WHEN CURSOR IS IMPLEMENTED
     * Read all livsmedel from the database.
     *
     * @return a List of livsmedel
     */
    public List<String> getLivsmedelList() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT Namn,\"Energi (kcal)\" FROM livsmedel", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0) + "  " + cursor.getString(1) + " kcal/100g");
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public Cursor getLivsmedel() {
        return database.rawQuery(String.format("SELECT rowid as _id,%s,%s FROM livsmedel ORDER BY %s", KEY_NAME, KEY_KCAL, KEY_NAME), null);
    }
}