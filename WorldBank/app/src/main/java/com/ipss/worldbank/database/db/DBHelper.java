package com.ipss.worldbank.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ipss.worldbank.database.db.DBSchema.*;
import static com.ipss.worldbank.database.db.DBSchema.WorldBankTable.NAME;

public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "WorldBankDataBase.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NAME + " (" +
                WorldBankTable.Cols.COUNTRY + ", " +
                WorldBankTable.Cols.INDICATOR + ", " +
                WorldBankTable.Cols.YEAR + ", " +
                WorldBankTable.Cols.VALUE + ", " +
                WorldBankTable.Cols.UNIT + ", " +
                "primary key (" + WorldBankTable.Cols.COUNTRY +
                    ", " + WorldBankTable.Cols.INDICATOR +
                    ", " + WorldBankTable.Cols.YEAR +
                    ")" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
