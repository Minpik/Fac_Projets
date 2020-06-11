package com.example.rhodier.mplrss;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView title, descitpion, date;
    private WebView webView;
    private int itemId;

    public ItemFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param itemId Parameter 1.
     * @return A new instance of fragment ItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(int itemId) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt("itemId", itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getInt("itemId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item, container, false);
        title = view.findViewById(R.id.tV_title);
        descitpion = view.findViewById(R.id.tV_description);
        date = view.findViewById(R.id.tV_date);
        webView = view.findViewById(R.id.webView);

        LoaderManager manager = getLoaderManager();
        manager.initLoader(0, null, this);

        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(DataAccess.authority)
                .appendPath("item")
                .build();

        String selection = "rowid = ?";
        String[] selectionArgs = new String[]{Integer.toString(itemId)};
        return new CursorLoader(getActivity(), uri, null,
                selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor cursor) {
        if (cursor.moveToFirst()) {
            title.setText(cursor.getString(cursor.getColumnIndex(
                    DataAccess.COLONNE_ITEM_TITLE)));
            descitpion.setText(cursor.getString(cursor.getColumnIndex(
                    DataAccess.COLONNE_ITEM_DESCRIPTION)));
            date.setText("Publié le " + cursor.getString(cursor.getColumnIndex(
                    DataAccess.COLONNE_ITEM_PUB_DATE)));

            descitpion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(cursor.getString(cursor.getColumnIndex(
                            DataAccess.COLONNE_ITEM_LINK)));
                }
            });

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}