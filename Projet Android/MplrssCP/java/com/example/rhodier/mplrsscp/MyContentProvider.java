package com.example.rhodier.mplrsscp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyContentProvider extends ContentProvider {
    private MyDataBase dataBase;

    private static String authority = "fr.rhodier.mplrss";

    private static final int CODE_ADDRESS = 1;
    private static final int CODE_FEED_ADDRESS = 2;
    private static final int CODE_DELETE_FEED_ADDRESS = 3;
    private static final int CODE_ITEM = 4;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(authority, "address", CODE_ADDRESS);
        matcher.addURI(authority, "feed_address", CODE_FEED_ADDRESS);
        matcher.addURI(authority, "delete_feed_address/*", CODE_DELETE_FEED_ADDRESS);
        matcher.addURI(authority, "item", CODE_ITEM);
    }

    public MyContentProvider() {
    }

    @Override
    public boolean onCreate() {
        dataBase = MyDataBase.getInstance(getContext());
        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        int code = matcher.match(uri);
        String path;

        long id;
        switch (code) {
            case CODE_ADDRESS:
                id = db.insertOrThrow(MyDataBase.TABLE_ADDRESS, null, values);
                path = MyDataBase.TABLE_ADDRESS;
                break;
            case CODE_FEED_ADDRESS:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                values.put(MyDataBase.COLONNE_FEED_ADDRESS_DATE,
                        formatter.format(new Date()));
                id = db.insertOrThrow(MyDataBase.TABLE_FEED_ADDRESS, null, values);
                path = MyDataBase.TABLE_FEED_ADDRESS;
                break;
            case CODE_ITEM:
                id = db.insertOrThrow(MyDataBase.TABLE_ITEM, null, values);
                path = MyDataBase.TABLE_ITEM;
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        Uri.Builder builder = (new Uri.Builder())
                .authority(authority)
                .appendPath(path);
        return ContentUris.appendId(builder, id).build();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dataBase.getReadableDatabase();
        int code = matcher.match(uri);

        Cursor cursor;
        switch (code) {
            case CODE_ADDRESS:
                cursor = db.query(MyDataBase.TABLE_ADDRESS,
                        null, null, null, null, null, sortOrder);
                break;
            case CODE_FEED_ADDRESS:
                cursor = db.query(MyDataBase.TABLE_FEED_ADDRESS,
                        null, null, null, null, null, sortOrder);
                break;
            case CODE_ITEM:
                cursor = db.query(MyDataBase.TABLE_ITEM, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.d("Uri provider =", uri.toString());
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        int code = matcher.match(uri);

        int n;
        switch (code) {
            case CODE_ADDRESS:
                n = db.delete(MyDataBase.TABLE_ADDRESS,
                        selection, selectionArgs);
                break;
            case CODE_DELETE_FEED_ADDRESS:
                String s = uri.getLastPathSegment();
                n = db.delete(MyDataBase.TABLE_FEED_ADDRESS,
                        MyDataBase.COLONNE_FEED_ADDRESS_VALUE + " = ?",
                        new String[]{s});
                n += db.delete(MyDataBase.TABLE_ITEM,
                        MyDataBase.COLONNE_ITEM_FEED_ADDRESS + " = ?",
                        new String[]{s});
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return n;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
