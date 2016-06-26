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

    static final String SQL_DEFAULT_ORDER = " ORDER BY " + KEY_NAME;
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

    public Cursor getLivsmedel() {
        return database.rawQuery(
                String.format("SELECT rowid as _id,%s,%s FROM livsmedel %s",
                        KEY_NAME, KEY_KCAL, SQL_DEFAULT_ORDER), null);
    }

    public Cursor searchLivsmedel(String value) {
        return database.rawQuery(
                "SELECT rowid as _id,Namn,\"Energi (kcal)\" FROM livsmedel WHERE Namn LIKE \"%" + value + "%\" " + SQL_DEFAULT_ORDER, null);

        //String.format("SELECT rowid as _id,%s,%s FROM livsmedel WHERE %s LIKE '\\%%s\\%' %s", KEY_NAME, KEY_KCAL, KEY_NAME, value, SQL_DEFAULT_ORDER)
    }
}