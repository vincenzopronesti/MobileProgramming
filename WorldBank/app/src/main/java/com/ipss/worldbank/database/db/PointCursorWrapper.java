package com.ipss.worldbank.database.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ipss.worldbank.database.Item;
import com.ipss.worldbank.entity.Point;

public class PointCursorWrapper extends CursorWrapper {
    public PointCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /** use the cursor to get a country indicator pair
     */
    public Item getItem() {
        String country = getString(getColumnIndex(DBSchema.WorldBankTable.Cols.COUNTRY));
        String indicator = getString(getColumnIndex(DBSchema.WorldBankTable.Cols.INDICATOR));
        return new Item(country, indicator);
    }

    /** use the cursor to get a year value pair
     * */
    public Point getPoint() {
        int year = getInt(getColumnIndex(DBSchema.WorldBankTable.Cols.YEAR));
        double value = getDouble(getColumnIndex(DBSchema.WorldBankTable.Cols.VALUE));
        return new Point(year, value);
    }

    /** use the cursor to get the unit
     */
    public String getUnit() {
        String unit = getString(getColumnIndex(DBSchema.WorldBankTable.Cols.UNIT));
        return unit;
    }
}
