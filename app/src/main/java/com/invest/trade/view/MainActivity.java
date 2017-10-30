package com.invest.trade.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.invest.trade.R;
import com.invest.trade.data.model.Active;
import com.invest.trade.data.repository.ActivesRepository;
import com.invest.trade.util.HtmlParser;
import com.invest.trade.util.Utils;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by TechnoA on 25.10.2017.
 */

public class MainActivity extends AppCompatActivity implements ActivityCallback{

    private static final String PAGE_URL = "https://trade.tradeinvest90.com/trade/iframe?view=table";
    private static final String SAVE_BUNDLE_INSTANCE_ID = "save_bundle_instance_id";
    public static final int TIME_UPDATE = 1000;

    private Fragment contentFragment;
    private ActiveListFragment activeListFragment;
    private FragmentManager fragmentManager;

    private ActivesRepository repository;
    private ScheduledThreadPoolExecutor executor;
    private HtmlParser parser;

    private ArrayList<Active> listActives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new ActivesRepository(this);
        fragmentManager = getSupportFragmentManager();

        initView(savedInstanceState);

    }
    @Override
    protected void onResume() {
        super.onResume();
        startLoading();
    }
    @Override
    public void onStop() {
        super.onStop();
        stopLoading();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (contentFragment instanceof ChartFragment) {
            outState.putString(SAVE_BUNDLE_INSTANCE_ID, ChartFragment.ARG_ITEM_ID);
        } else {
            outState.putString(SAVE_BUNDLE_INSTANCE_ID, ActiveListFragment.ARG_ITEM_ID);
        }
        super.onSaveInstanceState(outState);

    }
    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else if (contentFragment instanceof ActiveListFragment || fragmentManager.getBackStackEntryCount() == 0) {
            finish();
        }

        if (contentFragment instanceof ChartFragment){
            contentFragment = fragmentManager.findFragmentByTag(ActiveListFragment.ARG_ITEM_ID);
        }

    }

    public void startLoading() {
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(new ParserRunnable(), 0, TIME_UPDATE, TimeUnit.MILLISECONDS);
    }

    public void stopLoading() {
        if(executor!=null)
            executor.shutdown();
    }

    private class ParserRunnable implements Runnable{
        @Override
        public void run() {
            parser = new HtmlParser(PAGE_URL);
            listActives = parser.getListActives();
            if(listActives!=null && listActives.size()!=0){
                repository.addActives(listActives);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(contentFragment instanceof ActiveListFragment) {
                            ((ActiveListFragment) contentFragment).hideProgressBar();
                            ((ActiveListFragment) contentFragment).showListView(listActives);
                        }
                    }
                });
            }
        }
    }

    private void initView(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String instanceID = savedInstanceState.getString(SAVE_BUNDLE_INSTANCE_ID);
            if (instanceID.equals(ActiveListFragment.ARG_ITEM_ID)) {
                if (fragmentManager.findFragmentByTag(ActiveListFragment.ARG_ITEM_ID) != null) {
                    activeListFragment = (ActiveListFragment) fragmentManager.findFragmentByTag(ActiveListFragment.ARG_ITEM_ID);
                    contentFragment = activeListFragment;
                }
            } else if (instanceID.equals(ChartFragment.ARG_ITEM_ID)) {
                contentFragment = fragmentManager.findFragmentByTag(ChartFragment.ARG_ITEM_ID);
            }
        } else {
            activeListFragment = new ActiveListFragment();
            switchContent(activeListFragment, ActiveListFragment.ARG_ITEM_ID);
        }
    }

    public void switchContent(Fragment fragment, String tag) {

        while (fragmentManager.popBackStackImmediate());

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, tag);
            // Only ChartFragment is added to the back stack.
            if (fragment instanceof ChartFragment) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
            contentFragment = fragment;
        }
    }
    @Override
    public void startChartFragment(Active active) {
        ChartFragment chartFragment = new ChartFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChartFragment.BUNDLE_ACTIVE_KEY,active);
        bundle.putSerializable(ChartFragment.BUNDLE_LIST_ACTIVES_KEY,listActives);
        chartFragment.setArguments(bundle);
        switchContent(chartFragment, ChartFragment.ARG_ITEM_ID);
    }

}
