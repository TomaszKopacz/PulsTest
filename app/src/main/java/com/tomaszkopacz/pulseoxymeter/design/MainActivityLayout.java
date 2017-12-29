package com.tomaszkopacz.pulseoxymeter.design;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    //layout
    @BindView(R.id.mainToolbar)
    Toolbar toolbar;

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    //items
    public static final int CONNECT_ITEM = 10;
    public static final int GUIDE_ITEM = 20;
    public static final int INFO_ITEM = 30;


    /*==============================================================================================
                                            INITIALIZING
    ==============================================================================================*/

    public MainActivityLayout(Activity activity){

        this.activity = activity;

        activity.setContentView(R.layout.activity_main);
        ButterKnife.bind(this, this.activity);
    }

    public void setListener(final MainActivityListener listener){

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onNavigationIconClick();
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.connect_item:
                        item.setChecked(true);
                        listener.onMenuItemSelected(CONNECT_ITEM);
                        return true;

                    case R.id.guide_item:
                        item.setChecked(true);
                        listener.onMenuItemSelected(GUIDE_ITEM);
                        return true;

                    case R.id.info_item:
                        item.setChecked(true);
                        listener.onMenuItemSelected(INFO_ITEM);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }


    /*==============================================================================================
                                            CONTENT PROVIDERS
    ==============================================================================================*/

    public Toolbar getToolbar(){
        return toolbar;
    }

    public FrameLayout getFragmentFrame(){
        return frameLayout;
    }

    public DrawerLayout getDrawer(){
        return drawerLayout;
    }

    public NavigationView getNavigationView(){
        return navigationView;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public void setFrameLayout(FrameLayout frameLayout) {
        this.frameLayout = frameLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    public void setNavigationView(NavigationView navigationView) {
        this.navigationView = navigationView;
    }
}
