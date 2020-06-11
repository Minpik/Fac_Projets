package com.example.rhodier.mplrsscp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBase extends SQLiteOpenHelper {
    public final static int VERSION = 1;
    public final static String DB_NAME = "base_mplrsss";

    public final static String TABLE_ADDRESS = "address";
    public final static String COLONNE_ADDRESS_VALUE = "value";

    public final static String TABLE_FEED_ADDRESS = "feed_address";
    public final static String COLONNE_FEED_ADDRESS_VALUE = "value";
    public final static String COLONNE_FEED_ADDRESS_TITLE = "title";
    public final static String COLONNE_FEED_ADDRESS_DATE = "date";

    public final static String TABLE_ITEM = "item";
    public final static String COLONNE_ITEM_FEED_ADDRESS = "feed_address";
    public final static String COLONNE_ITEM_TITLE = "title";
    public final static String COLONNE_ITEM_DESCRIPTION = "description";
    public final static String COLONNE_ITEM_LINK = "link";
    public final static String COLONNE_ITEM_PUB_DATE = "pub_date";

    public final static String CREATE_ADRESS = "create table " + TABLE_ADDRESS + "(" +
            COLONNE_ADDRESS_VALUE + " string primary key);";

    public final static String CREATE_FEED_ADDRESS = "create table " + TABLE_FEED_ADDRESS + "(" +
            COLONNE_FEED_ADDRESS_VALUE + " string primary key, " +
            COLONNE_FEED_ADDRESS_TITLE + " string, " +
            COLONNE_FEED_ADDRESS_DATE + " string);";

    public final static String CREATE_ITEM = "create table " + TABLE_ITEM + "(" +
            COLONNE_ITEM_FEED_ADDRESS + " string references feed_address, " +
            COLONNE_ITEM_TITLE + " string, " +
            COLONNE_ITEM_DESCRIPTION + " string, " +
            COLONNE_ITEM_LINK + " string, " +
            COLONNE_ITEM_PUB_DATE + " string);";

    private static MyDataBase ourInstance;

    public static MyDataBase getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new MyDataBase(context);
        return ourInstance;
    }

    public MyDataBase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ADRESS);
        db.execSQL(CREATE_FEED_ADDRESS);
        db.execSQL(CREATE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_ADDRESS + ";");
            db.execSQL("drop table if exists " + TABLE_FEED_ADDRESS + ";");
            db.execSQL("drop table if exists " + TABLE_ITEM + ";");
            onCreate(db);
        }
    }
}
