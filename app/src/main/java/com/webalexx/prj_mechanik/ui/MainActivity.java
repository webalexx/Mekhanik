package com.webalexx.prj_mechanik.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.webalexx.prj_mechanik.CustomException.CustomException;
import com.webalexx.prj_mechanik.R;
import com.webalexx.prj_mechanik.content.AppConstants;
import rx.subscriptions.CompositeSubscription;
import android.content.Intent;

import com.webalexx.prj_mechanik.ui.fragments.FragmentMyManager;
import com.webalexx.prj_mechanik.ui.fragments.FragmentSection;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentSection.OnFragmentInteractionListener {

    private FragmentMyManager fragmentMyManager;
    private static String ID_EXTRA;
    private static String SECTION_NAME_EXTRA;
    private static String SERIALIZABLE_CATALOG_ITEM_EXTRA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ID_EXTRA = AppConstants.getContext().getResources().getString(R.string.ID_EXTRA);
        SECTION_NAME_EXTRA = AppConstants.getContext().getResources().getString(R.string.SECTION_NAME_EXTRA);
        SERIALIZABLE_CATALOG_ITEM_EXTRA = AppConstants.getContext().getResources().getString(R.string.SERIALIZABLE_CATALOG_ITEM_EXTRA);


        fragmentMyManager = new FragmentMyManager(this);
        fragmentMyManager.setLayoutId(R.id.fragment_manager);
        fragmentMyManager.addFragment(new FragmentSection());

//        if(findViewById(R.id.fragment_manager)!=null)
//        {
//            fragmentManager = this.getFragmentManager();
//            //this.strFragmentSectionName = fragmentSection.getClass().getName().toString();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.add(R.id.fragment_manager, new FragmentSection());
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
//        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Набор номера Механик Ульяновск..." + view.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                Intent intent = new Intent(
//                        Intent.ACTION_DIAL,
//                        Uri.parse("48484848")
//                );
//                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

          //  fragmentMyManager.popBackStack();
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            startActivity(new Intent(this, MainActivity.class));

        } else if (id == R.id.nav_gallery) {

//        } else if (id == R.id.nav_slideshow) {

//        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

//        } else if (id == R.id.nav_send) {

        } else if (id==R.id.nav_feedback){

        }else if (id==R.id.nav_sales){

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();

//       /
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        CustomException.PrintLog("Activity LifeCycle", "---OnFragmentInteractionListener Call Back---");
    }
}
