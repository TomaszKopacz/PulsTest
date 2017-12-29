package com.tomaszkopacz.pulseoxymeter.design;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.tomaszkopacz.pulseoxymeter.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by tomaszkopacz on 29.12.17.
 */

public class HRVDialogLayout extends LinearLayout{

    @BindView(R.id.mnn)
    TextView mnn;

    @BindView(R.id.sdnn)
    TextView sdnn;

    @BindView(R.id.rmssd)
    TextView rmssd;

    @BindView(R.id.nn50)
    TextView nn50;

    @BindView(R.id.pnn50)
    TextView pnn50;

    @BindView(R.id.sdsd)
    TextView sdsd;

    @BindView(R.id.poincareGraph)
    GraphView poincareGraph;

    private static final int PADDING = 5;

    public HRVDialogLayout(Context context) {
        super(context);
        inflate(context, R.layout.fragment_hrv, this);
        ButterKnife.bind(this);

        customizeLayout();
    }

    public TextView getMnn() {
        return mnn;
    }

    public TextView getSdnn() {
        return sdnn;
    }

    public TextView getRmssd() {
        return rmssd;
    }

    public TextView getNn50() {
        return nn50;
    }

    public TextView getPnn50() {
        return pnn50;
    }

    public TextView getSdsd() {
        return sdsd;
    }

    public GraphView getPoincareGraph() {
        return poincareGraph;
    }

    private void customizeLayout(){
        setOrientation(VERTICAL);
        setPadding(PADDING, PADDING, PADDING, PADDING);
        setGraph();
    }

    private void setGraph(){
        poincareGraph.setTitle("Wykres Poincare");
        poincareGraph.getGridLabelRenderer().setHorizontalAxisTitle("Aktualna wartość RR [s]");
        poincareGraph.getGridLabelRenderer().setVerticalAxisTitle("Poprzednia wartość RR [s]");
        Viewport viewport = poincareGraph.getViewport();
        viewport.setScalable(false);
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);
        viewport.scrollToEnd();
        viewport.setMinX(0);
        viewport.setMaxX(1.5);
        viewport.setMinY(0);
        viewport.setMaxY(1.5);
    }
}
