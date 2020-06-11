package com.example.rhodier.mplrss;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

public class DataAccess {
    private ContentResolver resolver;

    public static String authority = "fr.rhodier.mplrss";
    public final static String COLONNE_ADDRESS_VALUE = "value";
    public final static String COLONNE_FEED_ADDRESS_VALUE = "value";
    public final static String COLONNE_FEED_ADDRESS_TITLE = "title";
    public final static String COLONNE_FEED_ADDRESS_DATE = "date";
    public final static String COLONNE_ITEM_FEED_ADDRESS = "feed_address";
    public final static String COLONNE_ITEM_TITLE = "title";
    public final static String COLONNE_ITEM_DESCRIPTION = "description";
    public final static String COLONNE_ITEM_LINK = "link";
    public final static String COLONNE_ITEM_PUB_DATE = "pub_date";

    DataAccess(Context context) {
        resolver = context.getContentResolver();
    }

    boolean addAddress(String address) {
        ContentValues values = new ContentValues();
        values.put(COLONNE_ADDRESS_VALUE, address);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("address");
        Uri uri = builder.build();
        try {
            uri = resolver.insert(uri, values);
            Log.d("DEBUG", uri.getEncodedPath());
        } catch (SQLException e) {
            Log.d("DEBUG", e.getMessage());
            return false;
        }

        return true;
    }

    int deleteAddress(String address) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("address");
        Uri uri = builder.build();
        return resolver.delete(uri,
                DataAccess.COLONNE_ADDRESS_VALUE + " = ?",
                new String[]{address});
    }

    boolean addFeedAddress(String itemAddress, String title) {
        ContentValues values = new ContentValues();
        values.put(COLONNE_FEED_ADDRESS_VALUE, itemAddress);
        values.put(COLONNE_FEED_ADDRESS_TITLE, title);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("feed_address");
        Uri uri = builder.build();
        try {
            uri = resolver.insert(uri, values);
            Log.d("DEBUG", uri.getEncodedPath());
        } catch (SQLException e) {
            Log.d("DEBUG", e.getMessage());
            return false;
        }

        return true;
    }

    int deleteFeedAddress(String feedAddress) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("delete_feed_address")
                .appendPath(feedAddress);
        Uri uri = builder.build();
        return resolver.delete(uri, null, null);
    }

    boolean addItem(Item item, String address) {
        ContentValues values = new ContentValues();
        values.put(COLONNE_ITEM_FEED_ADDRESS, address);
        values.put(COLONNE_ITEM_TITLE, item.getTitle());
        values.put(COLONNE_ITEM_DESCRIPTION, item.getDescription());
        values.put(COLONNE_ITEM_LINK, item.getLink());
        values.put(COLONNE_ITEM_PUB_DATE, item.getPubDate());

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        try {
            uri = resolver.insert(uri, values);
            Log.d("DEBUG", uri.getEncodedPath());
        } catch (SQLException e) {
            Log.d("DEBUG", e.getMessage());
            return false;
        }

        return true;
    }
}
