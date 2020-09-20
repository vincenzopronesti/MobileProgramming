package com.ipss.worldbank.database.db;

public class DBSchema {
    public static final class WorldBankTable {
        public static final String NAME = "worldbanktable";

        public static final class Cols {
            public static final String COUNTRY = "country";
            public static final String INDICATOR = "indicator";
            public static final String YEAR = "year";
            public static final String VALUE = "value";
            public static final String UNIT = "unit";
        }
    }
}
