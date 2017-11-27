package com.tomaszkopacz.pulseoxymeter.design;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.tomaszkopacz.pulseoxymeter.R;
import com.tomaszkopacz.pulseoxymeter.controller.CommunicateFragment;
import com.tomaszkopacz.pulseoxymeter.controller.CommunicationActivity;
import com.tomaszkopacz.pulseoxymeter.listeners.CommunicationActivityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tomaszkopacz on 27.11.17.
 */

public class CommunicationActivityLayout {

    //general
    private Activity activity;
    private FragmentManager manager;
    private Fragment fragment;

    //components
    @BindView(R.id.communicationToolbar)
    Toolbar toolbar;

    @BindView(R.id.communicationFrameLayout)
    FrameLayout frameLayout;


    /*==============================================================================================
                                            INITIALIZE
    ==============================================================================================*/

    public CommunicationActivityLayout(Activity activity){
        this.activity = activity;
        this.manager = ((CommunicationActivity)activity).getSupportFragmentManager();

        activity.setContentView(R.layout.activity_communication);
        ButterKnife.bind(this, this.activity);

        preapreToolbar(
                R.string.communicate_fragment_title,
                R.drawable.ic_back);

        prepareFragment(CommunicateFragment.class);
    }

    public void setListener(final CommunicationActivityListener listener){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onNavigationIconClick();
            }
        });
    }


    /*==============================================================================================
                                            CUSTOM METHODS
    ==============================================================================================*/

    private void preapreToolbar(int title, int icon) {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(icon);
    }

    private void prepareFragment(Class fragmentClass) {
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {}

        manager.beginTransaction().replace(frameLayout.getId(), fragment).commit();
    }
}
