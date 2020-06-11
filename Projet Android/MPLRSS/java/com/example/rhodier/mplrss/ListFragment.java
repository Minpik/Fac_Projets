package com.example.rhodier.mplrss;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        MyAdapter.OnAdapterInteractionListener {
    private OnFragmentInteractionListener mListener;
    private MyAdapter adapter;
    private String feedAddress;

    public ListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param feedAddress Parameter 1.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String feedAddress) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("feedAddress", feedAddress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feedAddress = getArguments().getString("feedAddress");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        adapter = new MyAdapter(this, DataAccess.COLONNE_ITEM_TITLE, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        LoaderManager manager = getLoaderManager();
        manager.initLoader(0, savedInstanceState, this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(DataAccess.authority)
                .appendPath("item")
                .build();

        String search = "";
        if (bundle != null)
            search = bundle.getString("search");

        String selection = DataAccess.COLONNE_ITEM_FEED_ADDRESS + " = ?";
        String[] selectionsArgs;
        if (!search.equals("")) {
            selection += " AND " + DataAccess.COLONNE_ITEM_TITLE + " LIKE ?";
            search = "%" + search + "%";
            selectionsArgs = new String[]{feedAddress, search};
        } else
            selectionsArgs = new String[]{feedAddress};

        String[] projection = new String[]{"rowid", DataAccess.COLONNE_ITEM_TITLE};

        return new CursorLoader(getActivity(), uri, projection, selection,
                selectionsArgs, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onAdapterInteraction(String adapterTag, View view) {
        mListener.onFragmentInteraction(getTag(), view);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String fragmentTag, View view);
    }

    Cursor getCursor() {
        return adapter.getCursor();
    }

    void search(String s) {
        LoaderManager manager = getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString("search", s);
        manager.restartLoader(0, bundle, this);
    }
}