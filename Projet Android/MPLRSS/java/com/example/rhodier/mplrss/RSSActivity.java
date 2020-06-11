package com.example.rhodier.mplrss;

import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RSSActivity extends AppCompatActivity
        implements ListFragment.OnFragmentInteractionListener {
    private String address;
    private DataAccess dataAccess;
    private MyHandler handler;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        FragmentManager fragmentManager = getSupportFragmentManager();

        dataAccess = new DataAccess(this);
        String path = getIntent().getStringExtra("path");
        String feedAddress = getIntent().getStringExtra("feed_address");
        if (feedAddress != null) {
            address = feedAddress;
            String title = getIntent().getStringExtra("title");
            if (title != null)
                setTitle(title);
        } else if (path != null) {
            address = feedAddress = getIntent().getStringExtra("address");
            handler = new MyHandler();
            parsing(path);
            myDeleteFile(path);
            setTitle(handler.getRssTitle());
            saveItem(address);
            handler = null;
        }

        if (savedInstanceState != null) {
            itemId = savedInstanceState.getInt("item_id");
            fragmentManager.popBackStack("first", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        ListFragment fragment = ListFragment.newInstance(feedAddress);
        fragmentManager.beginTransaction()
                .addToBackStack("first")
                .add(R.id.fragment1, fragment, "list")
                .commit();

        if (itemId != 0)
            showItem(itemId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("item_id", itemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (itemId != 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack("first", 0);
            itemId = 0;
        } else
            super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ListFragment fragment =
                        (ListFragment) fragmentManager.findFragmentByTag("list");
                fragment.search(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_address:
                if (dataAccess.addAddress(address))
                    Toast.makeText(getApplicationContext(),
                            "Adresse ajoutée", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String fragmentTag, View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListFragment fragment =
                (ListFragment) fragmentManager.findFragmentByTag(fragmentTag);
        Cursor c = fragment.getCursor();
        if (c.moveToPosition(view.getId())) {
            itemId = c.getInt(c.getColumnIndex("rowid"));
            showItem(itemId);
        }
    }

    private void showItem(int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = ItemFragment.newInstance(id);

        int i;
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT)
            i = R.id.fragment1;
        else
            i = R.id.fragment2;

        fragmentManager.beginTransaction()
                .replace(i, newFragment)
                .addToBackStack(null)
                .commit();
    }

    private void parsing(String path) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            parser.parse(path, handler);
        } catch (Exception e) {
        }
    }

    private void myDeleteFile(String path) {
        try {
            (new File(new URI(path))).delete();
        } catch (URISyntaxException x) {
        }
    }

    private void saveItem(String feedAddress) {
        if (!dataAccess.addFeedAddress(feedAddress, handler.getRssTitle())) {
            dataAccess.addFeedAddress(feedAddress, handler.getRssTitle());
        }

        for (Item i : handler.getItemList())
            if (!dataAccess.addItem(i, feedAddress))
                return;
    }
}