package com.webalexx.prj_mechanik.ui.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.webalexx.prj_mechanik.CustomException.CustomException;
import com.webalexx.prj_mechanik.R;
import com.webalexx.prj_mechanik.content.AppConstants;
import com.webalexx.prj_mechanik.content.model.Section;
import com.webalexx.prj_mechanik.services.MechanicDataSource;
import com.webalexx.prj_mechanik.ui.MainActivity;
import com.webalexx.prj_mechanik.ui.adapter.ListAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSection.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSection extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final boolean DEVELOPER_MODE = true;
    private ListAdapter adapter;
    private final CompositeSubscription subscription = new CompositeSubscription();
    private static String ID_EXTRA;
    private static String SECTION_NAME_EXTRA;
    private static String SERIALIZABLE_CATALOG_ITEM_EXTRA;
    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Long itemId = null;
    private ListView lv_fragment_section;
    private View.OnClickListener snackOnClickListener;
    private Snackbar mSnackbar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentSection() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSection.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSection newInstance(String param1, String param2) {
        FragmentSection fragment = new FragmentSection();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CustomException.PrintLog("Fragment LifeCycle", "---onAttach---");

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //TODO CAll customexception class via lambdas
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ID_EXTRA = AppConstants.getContext().getResources().getString(R.string.ID_EXTRA);
        SECTION_NAME_EXTRA = AppConstants.getContext().getResources().getString(R.string.SECTION_NAME_EXTRA);
        SERIALIZABLE_CATALOG_ITEM_EXTRA = AppConstants.getContext().getResources().getString(R.string.SERIALIZABLE_CATALOG_ITEM_EXTRA);
        CustomException.PrintLog("Fragment LifeCycle", "---onCreate---");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_section, container, false);
        lv_fragment_section = (ListView) rootView.findViewById(R.id.lv_fragment_section);
        adapter = new ListAdapter(getActivity());
        lv_fragment_section.setAdapter(adapter);


        //TODO make styling like here is recomended https://www.google.com/design/spec/components/snackbars-toasts.html#snackbars-toasts-specs
        //snack bar styling
        mSnackbar = Snackbar.make((FloatingActionButton) getActivity().findViewById(R.id.fab), R.string.loading_error, Snackbar.LENGTH_SHORT);
        Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto Medium.ttf");

        TextView snackbarActionTextView = (TextView) (mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_action));
        snackbarActionTextView.setTextSize(14);
        snackbarActionTextView.setTypeface(font);

        TextView snackbarTextView = (TextView) (mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text));
        snackbarTextView.setTextSize(14);
        snackbarTextView.setMaxLines(2);
        snackbarTextView.setTypeface(font);

        //SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


//        ID_EXTRA = AppConstants.getContext().getResources().getString(R.string.ID_EXTRA);
//        SECTION_NAME_EXTRA = AppConstants.getContext().getResources().getString(R.string.SECTION_NAME_EXTRA);
//        SERIALIZABLE_CATALOG_ITEM_EXTRA = AppConstants.getContext().getResources().getString(R.string.SERIALIZABLE_CATALOG_ITEM_EXTRA);

        lv_fragment_section.setOnItemClickListener((parent, view, position, id) -> {

            //   Toast.makeText(rootView.getContext(), "Метод -> setOnClickListener", Toast.LENGTH_LONG);
//            Snackbar.make((FloatingActionButton) getActivity().findViewById(R.id.fab), "Обработка setOnClickListener", Snackbar.LENGTH_LONG)
//
//                    .setDuration(4000)
//                    .show();
            CustomException.PrintLog("Fragment LifeCycle", "---lv_fragment_section.setOnItemClickListener---");
            if (adapter.isSection(position)) {

//                args.putString(ARG_PARAM1, param1);
//                args.putString(ARG_PARAM2, param2);
//                fragment.setArguments(args);

                // to restart an activity with a new arguments

//                  startActivity(new Intent(FragmentSection.this, FragmentSection.class)
//                        .putExtra(ID_EXTRA, id)
//                        .putExtra(SECTION_NAME_EXTRA,
//                                ((Section) adapter.getItem(position)).getName()));

                onListLoad(id);
//                args.putString(ID_EXTRA, id);
//                args.putString(SECTION_NAME_EXTRA,
//                        ((Section) adapter.getItem(position)).getName());

            } else {
                Bundle extras = savedInstanceState;
                extras = (extras != null) ? extras : new Bundle();
                itemId = extras.containsKey(ID_EXTRA) ? extras.getLong(ID_EXTRA) : null;

//                startActivity(
//                        new Intent(MainActivity.this, DetailActivity.class)
//                                .putExtra(SERIALIZABLE_CATALOG_ITEM_EXTRA,
//                                        (CatalogItem) adapter.getItem(position)
//                                )
//                );
            }
        });

        Bundle extras = savedInstanceState;
        extras = (extras != null) ? extras : new Bundle();
        itemId = extras.containsKey(ID_EXTRA) ? extras.getLong(ID_EXTRA) : null;
//        CustomException.PrintLog("ID_EXTRA", String.valueOf(itemId));
        onListLoad(itemId);

        CustomException.PrintLog("Fragment LifeCycle", "---onCreateView---");

        return rootView;
    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param l        The ListView where the click happened
     * @param v        The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     */

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CustomException.PrintLog("Fragment LifeCycle", "---onListItemClick---");
        super.onListItemClick(l, v, position, id);
        CustomException.PrintLog("Fragment LifeCycle", "---onListItemClick---");
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CustomException.PrintLog("Fragment LifeCycle", "---onSaveInstanceState---");

    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        CustomException.PrintLog("Fragment LifeCycle", "---onViewCreated---");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CustomException.PrintLog("Fragment LifeCycle", "---onActivityCreated---");
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
        CustomException.PrintLog("Fragment LifeCycle", "---onStart---");
    }


    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        CustomException.PrintLog("Fragment LifeCycle", "---onResume---");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        CustomException.PrintLog("Fragment LifeCycle", "---onButtonPressed---");
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        CustomException.PrintLog("Fragment LifeCycle", "---onPause---");
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();
        CustomException.PrintLog("Fragment LifeCycle", "---onStop---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CustomException.PrintLog("Fragment LifeCycle", "---onDestroy---");
        subscription.unsubscribe();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        CustomException.PrintLog("Fragment LifeCycle", "---onDetach---");
        mListener = null;
    }

    @Override
    public void onRefresh() {
        CustomException.PrintLog("Fragment LifeCycle", "---onRefresh---");
        onListLoad(itemId);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Implements a list loading logic
     *
     * @return A list or a messages depending on conditions
     */
    private void onListLoad(Long itemId) {

        //TODO запустить каждый элемент списка в отдельном потоке :from
        //TODO make a smooth scroling for a listview http://www.codota.com/android/methods/android.widget.ListView/smoothScrollToPosition


        subscription.add(MechanicDataSource.getInstance()
                .listSections(itemId)
                .subscribeOn(Schedulers.io())
                //                .map(i -> {
//                    ListIterator listiterator = i.listIterator();
//                    while (listiterator.hasNext()) {
//                    Log.d("RXTest", "Размещение " + listiterator.next() + "  номер-> "
//                            + listiterator.nextIndex()
//                            + " on thread -> " + Thread.currentThread().getName());
//                    }
//                    return i;
//                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::setSections, throwable ->
                        mSnackbar
                                .setDuration(10000)
                                //  .setActionTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.txActionTextColorSnackBar))
                                .setAction("ВКЛЮЧИТЬ", snackOnClickListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent sBarIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                        startActivity(sBarIntent);
                                    }
                                })
                                .show()));
        if (itemId != null) {
            subscription.add(MechanicDataSource.getInstance()
                    .listItems(itemId)
                    .subscribeOn(Schedulers.io())
                    //                .map(i -> {
//                    ListIterator listiterator = i.listIterator();
//                    while (listiterator.hasNext()) {
//                    Log.d("RXTest", "Размещение " + listiterator.next() + "  номер-> "
//                            + listiterator.nextIndex()
//                            + " on thread -> " + Thread.currentThread().getName());
//                    }
//                    return i;
//                })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(adapter::setCatalogItems, throwable ->
                            mSnackbar
                                    .setDuration(10000)
                                    //  .setActionTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.txActionTextColorSnackBar))
                                    .setAction("ВКЛЮЧИТЬ", snackOnClickListener = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent sBarIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                            startActivity(sBarIntent);
                                        }
                                    })
                                    .show()));
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
