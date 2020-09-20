package com.ipss.worldbank.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ipss.worldbank.database.db.DBHelper;
import com.ipss.worldbank.database.db.DBSchema;
import com.ipss.worldbank.database.db.PointCursorWrapper;
import com.ipss.worldbank.entity.DataChart;
import com.ipss.worldbank.entity.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.ipss.worldbank.database.db.DBSchema.*;
import static com.ipss.worldbank.database.db.DBSchema.WorldBankTable.*;

public class Dao {
    private static final String TAG = "Dao";
    private static Dao dao;
    private Context context;
    private SQLiteDatabase database;

    private Dao(Context context) {
        this.context = context;
        database = new DBHelper(context).getWritableDatabase();
    }

    public static Dao get(Context context) {
        if (dao == null) {
            dao = new Dao(context);
        }
        return dao;
    }

    /**
     * convert data chart to to content values
     * @param dataChart
     * @return
     */
    private static List<ContentValues> getContentValues(DataChart dataChart) {
        List<ContentValues> valuesList = new ArrayList<>();
        Iterator<Point> i = dataChart.getPointList().iterator();
        while (i.hasNext()) {
            Point p = i.next();
            ContentValues values = new ContentValues();
            values.put(Cols.COUNTRY, dataChart.getCountry());
            values.put(WorldBankTable.Cols.INDICATOR, dataChart.getIndicator());
            values.put(WorldBankTable.Cols.YEAR, p.getYear());
            values.put(WorldBankTable.Cols.VALUE, p.getValue());
            values.put(WorldBankTable.Cols.UNIT, dataChart.getUnit());
            valuesList.add(values);
        }
        return valuesList;
    }

    private PointCursorWrapper queryItems() {
        String query = "select distinct " + WorldBankTable.Cols.COUNTRY + ", " +
                WorldBankTable.Cols.INDICATOR + " from " + WorldBankTable.NAME;
        Cursor cursor = database.rawQuery(query, null);
        return new PointCursorWrapper(cursor);
    }

    private PointCursorWrapper queryUnit(String country, String indicator) {
        String query = "select " + WorldBankTable.Cols.UNIT + " from " + WorldBankTable.NAME +
                " where " + WorldBankTable.Cols.COUNTRY + " = " + "'" + country + "'" + " and " + WorldBankTable.Cols.INDICATOR + " = " +  "'" + indicator + "'";
        Cursor cursor = database.rawQuery(query, null);
        return new PointCursorWrapper(cursor);
    }

    /** used to query data chart by country and indicator
    */
    private PointCursorWrapper queryDataChart(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(WorldBankTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new PointCursorWrapper(cursor);
    }

    public void addDataChart(DataChart dataChart) throws Exception {
        List<ContentValues> valuesList = getContentValues(dataChart);
        Iterator<ContentValues> i = valuesList.iterator();
        database.beginTransaction();
        try {
            while (i.hasNext()) {
                ContentValues values = i.next();
                database.insertWithOnConflict(WorldBankTable.NAME, null,
                        values, SQLiteDatabase.CONFLICT_IGNORE);
//                database.insert(WorldBankTable.NAME, null, values);
            }
            database.setTransactionSuccessful(); // complete transaction only if every point can be inserted
        } catch (SQLException e) {
            Log.e(TAG, "error while inserting data");
            e.printStackTrace();
            throw new Exception();
        } finally {
            database.endTransaction();
        }
    }

    public DataChart getDataChart(String country, String indicator) {
        PointCursorWrapper cursor = queryDataChart(WorldBankTable.Cols.COUNTRY +
                " = ? and " + WorldBankTable.Cols.INDICATOR + " = ?",
                new String[]{country, indicator});
        List<Point> points = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Point p = cursor.getPoint();
                points.add(p);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        DataChart dataChart = new DataChart(country, indicator, points, getUnit(country, indicator));
        return dataChart;
    }

    public List<Item> getListOfSavedItems() {
        List<Item> items = new ArrayList<>();
        PointCursorWrapper cursor = queryItems();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Item i = cursor.getItem();
                items.add(i);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    public String getUnit(String country, String indicator) {
        String unit = "";
        PointCursorWrapper cursor =  queryUnit(country, indicator);
        try {
            cursor.moveToFirst();
            if(!cursor.isAfterLast()) {
                unit = cursor.getUnit();
            }
        } finally {
            cursor.close();
        }
        return unit;
    }
}
