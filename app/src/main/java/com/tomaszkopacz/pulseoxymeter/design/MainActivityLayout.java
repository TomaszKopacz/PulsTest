package com.tomaszkopacz.pulseoxymeter.design;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.controller.ConnectionFragment;
import com.tomaszkopacz.pulseoxymeter.controller.MainActivity;
import com.tomaszkopacz.pulseoxymeter.listeners.MainActivityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tomaszkopacz on 27.11.17.
 */

public class MainActivityLayout {

    //general
    private Activity activity;
    private MainActivityListener listener;
    private FragmentManager manager;
    private Fragment fragment;

    //layout
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.mainToolbar)
    Toolbar toolbar;

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    //items
    public static final int CONNECT_ITEM = 10;
    public static final int DIARY_ITEM = 20;


    /*==============================================================================================
                                            INITIALIZING
    ==============================================================================================*/

    public MainActivityLayout(Activity activity){
        this.activity = activity;
        manager = ((MainActivity)activity).getSupportFragmentManager();

        activity.setContentView(R.layout.activity_main);
        ButterKnife.bind(this, this.activity);

        //start design
        setToolbarContent(R.string.devices_fragment_title, R.drawable.ic_menu);
        setFragmentContent(ConnectionFragment.class);
    }

    public void setListener(MainActivityListener listener){

        this.listener = listener;

        toolbar.setNavigationOnClickListener(onNavigationIconClick);
        navigationView.setNavigationItemSelectedListener(itemSelectedListener);
    }


    /*==============================================================================================
                                            ACTIONS
    ==============================================================================================*/

    private View.OnClickListener onNavigationIconClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.openDrawer(GravityCompat.START);
            listener.onNavigationIconClick();
        }
    };

    private NavigationView.OnNavigationItemSelectedListener itemSelectedListener
            = new NavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()){

                case R.id.connect_item:
                    item.setChecked(true);
                    toolbar.setTitle(R.string.devices_fragment_title);
                    drawerLayout.closeDrawers();
                    listener.onMenuItemSelected(CONNECT_ITEM);
                    return true;

                case R.id.diary_item:
                    item.setChecked(true);
                    toolbar.setTitle(R.string.diary_fragment_title);
                    drawerLayout.closeDrawers();
                    listener.onMenuItemSelected(DIARY_ITEM);
                    return true;

                case R.id.guide_item:
                    return true;

                case R.id.info_item:
                    return true;

                default:
                    return false;
            }
        }
    };


    /*==============================================================================================
                                            SET CONTENT
    ==============================================================================================*/

    public void setFragmentContent(Class fragmentClass){
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {}

        manager.beginTransaction().replace(frameLayout.getId(), fragment).commit();
    }

    private void setToolbarContent(int title, int icon){
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(icon);
    }
}
