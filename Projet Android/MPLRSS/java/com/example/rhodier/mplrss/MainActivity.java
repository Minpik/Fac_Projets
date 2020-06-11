package com.example.rhodier.mplrss;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        MySelectAdapter.OnAdapterInteractionListener {

    private DownloadManager dm;
    private long id;
    private BroadcastReceiver receiver;
    private ArrayList<String> downloadedFiles;
    private MySelectAdapter adapter1, adapter2;
    private DataAccess dataAccess;
    private boolean checkOldFeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadedFiles = new ArrayList<>();
        dataAccess = new DataAccess(this);

        adapter1 = new MySelectAdapter(this,
                DataAccess.COLONNE_ADDRESS_VALUE, "adapter_addresses");

        RecyclerView recyclerView = findViewById(R.id.recyclage1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter1);

        //------------------------------------------------------------------------
        adapter2 = new MySelectAdapter(this,
                DataAccess.COLONNE_FEED_ADDRESS_TITLE, "adapter_feed_addresses");

        recyclerView = findViewById(R.id.recyclage2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter2);

        LoaderManager manager = getSupportLoaderManager();
        manager.initLoader(0, null, this);
        manager.initLoader(1, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        if (i == 0) {
            Uri uri = builder.scheme("content").authority(DataAccess.authority)
                    .appendPath("address").build();
            return new CursorLoader(this, uri, null, null, null, null);
        } else {
            Uri uri = builder.scheme("content").authority(DataAccess.authority)
                    .appendPath("feed_address").build();
            return new CursorLoader(this, uri, null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == 0) {
            adapter1.swapCursor(cursor);
        } else {
            boolean reload = false;
            if (!checkOldFeeds) {
                reload = deleteOldFeeds(cursor);
                checkOldFeeds = true;
            }
            if (!reload)
                adapter2.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == 0)
            adapter1.swapCursor(null);
        else
            adapter2.swapCursor(null);
    }

    @Override
    public void onAdapterInteraction(String tag, View view, boolean selection) {
        if (selection) {
            invalidateOptionsMenu();
        } else {
            if (tag.equals("adapter_addresses")) {
                String value = ((TextView) view).getText().toString();
                ((EditText) findViewById(R.id.eT_address)).setText(value);
            } else if (tag.equals("adapter_feed_addresses")) {
                Intent i = new Intent(MainActivity.this, RSSActivity.class);
                Cursor c = adapter2.getCursor();
                if (c.moveToPosition(view.getId())) {
                    i.putExtra("feed_address", c.getString(c.getColumnIndex(
                            DataAccess.COLONNE_FEED_ADDRESS_VALUE)));
                    i.putExtra("title", c.getString(c.getColumnIndex(
                            DataAccess.COLONNE_FEED_ADDRESS_TITLE)));
                }
                startActivity(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (adapter1.isSelection() || adapter2.isSelection())
            menu.findItem(R.id.action_delete).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteValuesOfLists();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (adapter1.isSelection() || adapter2.isSelection()) {
            adapter1.clearSelection();
            adapter2.clearSelection();
            invalidateOptionsMenu();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        LoaderManager manager = getSupportLoaderManager();
        manager.restartLoader(0, null, this);
        manager.restartLoader(1, null, this);
    }

    public void download(View button) {
        final String address = ((EditText) findViewById(R.id.eT_address)).getText()
                .toString();

        Uri uri = Uri.parse(address);
        DownloadManager.Request req;
        try {
            req = new DownloadManager.Request(uri);
        } catch (IllegalArgumentException e) {
            return;
        }

        req.setDestinationInExternalFilesDir(this,
                Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
        id = dm.enqueue(req);

        Toast.makeText(getApplicationContext(), "Debut du téléchargement",
                Toast.LENGTH_SHORT).show();

        final ProgressBar pBar = findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;
                pBar.setProgress(0);

                while (downloading) {
                    DownloadManager.Query question = new DownloadManager.Query();
                    question.setFilterById(id);
                    Cursor c = dm.query(question);
                    if (c.moveToFirst()) {
                        int bytes_downloaded = c.getInt(
                                c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                        }
                        c.close();

                        final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                        Log.d("DEBUG", Integer.toString(bytes_downloaded) + " "
                                + Integer.toString(bytes_total));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pBar.setProgress(dl_progress);
                            }
                        });
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ref = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == ref) {
                    unregisterReceiver(this);
                    DownloadManager.Query question = new DownloadManager.Query();
                    question.setFilterById(id)
                            .setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                    Cursor cur = dm.query(question);
                    if (cur.moveToFirst()) {
                        Toast.makeText(getApplicationContext(),
                                "Fin du téléchargement", Toast.LENGTH_SHORT).show();
                        String path = cur.getString(cur.getColumnIndex(
                                DownloadManager.COLUMN_LOCAL_URI));
                        if (path != null) {
                            downloadedFiles.add(path);
                            Intent i = new Intent(MainActivity.this, RSSActivity.class);
                            i.putExtra("path", path);
                            i.putExtra("address", address);
                            startActivity(i);
                        }
                    }

                    cur.close();
                }
            }
        };

        IntentFilter filter =
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        this.registerReceiver(receiver, filter);
    }

    public void cancel(View button) {
        if (id > 0) {
            dm.remove(id);
            Toast.makeText(getApplicationContext(),
                    "Téléchargement annulé", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteValuesOfLists() {
        LoaderManager manager = getSupportLoaderManager();
        Cursor c;
        if (adapter1.isSelection()) {
            c = adapter1.getCursor();
            for (int i : adapter1.getSelected()) {
                if (c.moveToPosition(i)) {
                    String s = c.getString(c.getColumnIndex(DataAccess.COLONNE_ADDRESS_VALUE));
                    System.out.println("DELETE " + dataAccess.deleteAddress(s));
                }
            }
            adapter1.notifyDataSetChanged();
            adapter1.clearSelection();
            manager.restartLoader(0, null, this);
        }
        if (adapter2.isSelection()) {
            c = adapter2.getCursor();
            for (int i : adapter2.getSelected()) {
                if (c.moveToPosition(i)) {
                    String s = c.getString(c.getColumnIndex(
                            DataAccess.COLONNE_FEED_ADDRESS_VALUE));
                    System.out.println("DELETE " + dataAccess.deleteFeedAddress(s));
                }
            }
            adapter2.notifyDataSetChanged();
            adapter2.clearSelection();
            manager.restartLoader(1, null, this);
        }
    }

    boolean deleteOldFeeds(Cursor c) {
        boolean reload = false;

        while (c.moveToNext()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm")
                        .parse(c.getString(c.getColumnIndex(
                                DataAccess.COLONNE_FEED_ADDRESS_DATE)));
                Date now = new Date();
                long diffInMillies = Math.abs(now.getTime() - date.getTime());
                long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (diff > 1) {
                    dataAccess.deleteFeedAddress(c.getString(
                            c.getColumnIndex(DataAccess.COLONNE_FEED_ADDRESS_VALUE)));
                    reload = true;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (reload) {
            LoaderManager manager = getSupportLoaderManager();
            manager.restartLoader(1, null, this);
            return true;
        }

        return false;
    }
}
