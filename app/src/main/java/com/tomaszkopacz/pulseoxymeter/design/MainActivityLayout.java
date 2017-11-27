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
import com.tomaszkopacz.pulseoxymeter.controller.DevicesListFragment;
import com.tomaszkopacz.pulseoxymeter.controller.DiaryFragment;
import com.tomaszkopacz.pulseoxymeter.controller.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tomaszkopacz on 27.11.17.
 */

public class MainActivityLayout {

    private Activity activity;

    //layout
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.mainToolbar)
    Toolbar toolbar;

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    private FragmentManager manager;
    private Fragment fragment;

    //util fonts
    public static final String FONT_THIN_NAME = "Comfortaa_Thin.ttf";
    public static final String FONT_REGULAR_NAME = "Comfortaa_Regular.ttf";
    public static final String FONT_BOLD_NAME = "Comfortaa_Bold.ttf";

    public static Typeface FONT_THIN;
    public static Typeface FONT_REGULAR;
    public static Typeface FONT_BOLD;


    /*==============================================================================================
                                            INITIALIZING
    ==============================================================================================*/

    public MainActivityLayout(Activity activity){
        this.activity = activity;
        manager = ((MainActivity)activity).getSupportFragmentManager();

        activity.setContentView(R.layout.activity_main);
        ButterKnife.bind(this, this.activity);

        customizeLayout();
        createFonts();
    }


    /*==============================================================================================
                                            ACTIONS
    ==============================================================================================*/

    private View.OnClickListener navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    };

    private NavigationView.OnNavigationItemSelectedListener itemSelectedListener
            = new NavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()){

                case R.id.connect_item:
                    prepareFragmentView(DevicesListFragment.class);
                    toolbar.setTitle(R.string.devices_fragment_title);
                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;

                case R.id.diary_item:
                    prepareFragmentView(DiaryFragment.class);
                    toolbar.setTitle(R.string.diary_fragment_title);
                    item.setChecked(true);
                    drawerLayout.closeDrawers();
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
                                            PRIVATE UTIL METHODS
    ==============================================================================================*/

    private void customizeLayout(){

        //start fragment and title
        prepareFragmentView(DevicesListFragment.class);
        toolbar.setTitle(R.string.devices_fragment_title);

        //toolbar and menu
        setToolbarFeatures();
        createDrawerContent();
    }

    private void setToolbarFeatures(){
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(navigationOnClickListener);
    }

    private void createDrawerContent(){
        navigationView.setNavigationItemSelectedListener(itemSelectedListener);
    }

    private void prepareFragmentView(Class fragmentClass){

        //get fragment class
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {}

        //replace fragments
        manager.beginTransaction().replace(frameLayout.getId(), fragment).commit();
    }

    private void createFonts(){
        FONT_THIN = Typeface.createFromAsset(activity.getAssets(), FONT_THIN_NAME);
        FONT_REGULAR = Typeface.createFromAsset(activity.getAssets(), FONT_REGULAR_NAME);
        FONT_BOLD = Typeface.createFromAsset(activity.getAssets(), FONT_BOLD_NAME);
    }
}
