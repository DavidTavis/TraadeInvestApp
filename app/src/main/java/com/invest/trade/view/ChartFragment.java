package com.invest.trade.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.Plot;
import com.androidplot.util.PlotStatistics;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;
import com.invest.trade.R;
import com.invest.trade.data.model.Active;
import com.invest.trade.data.repository.ActivesRepository;
import com.invest.trade.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by TechnoA on 24.10.2017.
 */

public class ChartFragment extends Fragment {

    public static final String ARG_ITEM_ID = "chart";
    public static final String BUNDLE_ACTIVE_KEY = "bundle_active_key";
    public static final String BUNDLE_LIST_ACTIVES_KEY = "bundle_list_actives_key";
    public static final String BUNDLE_ASSET_KEY = "bundle_asset_key";
    public static final String BUNDLE_SERIES_KEY = "bundle_series_key";

    private static final int X_CORD_LENGTH = 60;
    private View view;
    private Redrawer redrawer;
    private XYPlot plot;
    private SimpleXYSeries series;
    private ArrayList<Float> cordY;
    private ArrayList<Long> cordX;
    private float maxRange;
    private float minRange;

    private ScheduledThreadPoolExecutor executor;

    private ActivesRepository repository;
    private ArrayList<Active> activeHistoryCurrentRate;
    private String currentAsset;
    private Active currentActive;
    private ArrayList<Active> lastListActives;

    private ArrayList<String> menuListAssets = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initData(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_chart, container, false);
        plot = view.findViewById(R.id.aprHistoryPlot);
        customizePlot(currentAsset);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        for(String asset: menuListAssets){
            MenuItem item = menu.add(asset);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    currentAsset = String.valueOf(item.getTitle());
                    return showChartFromMenuItem(currentAsset);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_ASSET_KEY,currentAsset);
        outState.putSerializable(BUNDLE_SERIES_KEY,getSavedCordY());
    }

    @Override
    public void onResume() {
        super.onResume();
        startRealTimeDraw();
    }

    @Override
    public void onPause() {
        pauseRealTimeDraw();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        redrawer.finish();
    }

    private ArrayList<Long> getSavedCordX() {
        ArrayList<Long> listX = new ArrayList<>();
        LinkedList<Number> llX = series.getyVals();
        for(Number number:llX){
            listX.add((Long) number);
        }
        return listX;
    }

    private ArrayList<Float> getSavedCordY() {
        ArrayList<Float> listY = new ArrayList<>();
        LinkedList<Number> llY = series.getyVals();
        for(Number number:llY){
            listY.add((Float) number);
        }
        return listY;
    }

    private void initData(Bundle savedInstanceState) {

        currentActive = (Active) getArguments().getSerializable(BUNDLE_ACTIVE_KEY);
        lastListActives = (ArrayList<Active>) getArguments().getSerializable(BUNDLE_LIST_ACTIVES_KEY);
        currentAsset = currentActive.getAssets();

        if(savedInstanceState!=null) {
            currentAsset = savedInstanceState.getString(BUNDLE_ASSET_KEY);
            cordY = (ArrayList<Float>) savedInstanceState.getSerializable(BUNDLE_SERIES_KEY);

        }
        repository = new ActivesRepository(getActivity());
        activeHistoryCurrentRate = getListActivesFromRepo(currentAsset);

        setCordY();
        setCordX();
        menuListAssets = getMenuListAssets();

    }

    private void setCordY() {
        if(cordY==null) {
            cordY = new ArrayList<>();
            for (Active active : activeHistoryCurrentRate) {
                Float currentRate = Utils.getCurrentRate(active);
                cordY.add(currentRate);
            }
        }
    }

    private void setCordX() {
        if(cordX==null) {
            cordX = new ArrayList<>();
            for (Active active : activeHistoryCurrentRate) {
                long currentTime = Utils.getCurrentTime(active);
                cordX.add(currentTime);
            }
        }
    }

    private void customizePlot(String asset) {

        series = new SimpleXYSeries(cordY, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Currency");
//        series = new SimpleXYSeries(cordX, cordY, "Currency");
        setCurrentRangeBoundaries();
        plot.setRangeBoundaries(minRange,maxRange,BoundaryMode.FIXED);
        plot.setDomainBoundaries(null,null,BoundaryMode.AUTO);
        plot.addSeries(series, new LineAndPointFormatter(Color.RED, null, null, null));
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL,5);
        plot.setTitle(asset);
        plot.setDomainLabel("Date");
        plot.setRangeLabel("Currency");
        plot.setBackgroundColor(Color.parseColor("#ddaaaa"));
        plot.setDrawingCacheBackgroundColor(Color.parseColor("#ddaaaa"));

        final PlotStatistics plot1Stat = new PlotStatistics(1000, false);
        plot.addListener(plot1Stat);

        redrawer = new Redrawer(Arrays.asList(new Plot[]{plot}),100, false);
    }

    private boolean showChartFromMenuItem(String asset) {
        stopRealTimeDraw();
        activeHistoryCurrentRate = getListActivesFromRepo(asset);
        cordY=null;
        setCordY();
        setCordX();
        customizePlot(asset);
        startRealTimeDraw();
        return true;
    }

    private void startRealTimeDraw(){
        redrawer.start();
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Active active = repository.getLastActive(currentAsset);
                if(series.size() >= X_CORD_LENGTH){
                    series.removeFirst();
                }
                series.addLast(null,Utils.getCurrentRate(active));
            }
        }, 0, MainActivity.TIME_UPDATE, TimeUnit.MILLISECONDS);

    }

    private void pauseRealTimeDraw(){
        if(executor!=null) executor.shutdown();
        redrawer.pause();
    }

    private void stopRealTimeDraw(){
        if(executor!=null) executor.shutdown();
        plot.clear();
        redrawer.pause();
        redrawer.finish();
    }

    public void setCurrentRangeBoundaries(){
        maxRange = 0;
        minRange = Float.MAX_VALUE;
        for(Active active: activeHistoryCurrentRate){
            float currentRate = Utils.getCurrentRate(active);
            if(maxRange<currentRate){
                maxRange = currentRate;
            }
            if(minRange>currentRate){
                minRange = currentRate;
            }
        }

        maxRange = maxRange + maxRange*0.0001f;
        minRange = minRange - minRange*0.0001f;
    }

    public ArrayList<String> getMenuListAssets() {

        for(Active active: lastListActives){
            menuListAssets.add(active.getAssets());
        }

        return menuListAssets;
    }

    public ArrayList<Active> getListActivesFromRepo(String asset) {
        return repository.getActives(asset,X_CORD_LENGTH);
    }

}
