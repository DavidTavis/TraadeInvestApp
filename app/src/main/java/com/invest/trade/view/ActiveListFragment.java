package com.invest.trade.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.invest.trade.R;
import com.invest.trade.data.model.Active;
import com.invest.trade.util.Utils;
import com.invest.trade.view.adapter.ActiveAdapter;

import java.util.ArrayList;

/**
 * Created by TechnoA on 24.10.2017.
 */

public class ActiveListFragment extends Fragment {

    public static final String ARG_ITEM_ID = "active_list";
    public static final String KEY_LIST_ACTIVES_BUNDLE = "key_active_list_bundle";

    private Activity activity;
    private ListView activeListView;
    private ProgressBar progressBar;
    private ActiveAdapter adapter;

    private ArrayList<Active> listActives;
    private ActivityCallback activityCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (ActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ActivityCallback");
        }
    }

    public void startChartFragment(Active active) {
        activityCallback.startChartFragment(active);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            listActives = (ArrayList<Active>) savedInstanceState.getSerializable(KEY_LIST_ACTIVES_BUNDLE);
        }else {
            listActives = new ArrayList<>();
        }

        activity = getActivity();
        adapter = new ActiveAdapter(activity, listActives, this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_active_list, container, false);

        progressBar = view.findViewById(R.id.pb);
        if(listActives==null) {
            showProgressBar();
        }
        activeListView = view.findViewById(R.id.list_actives);
        activeListView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_LIST_ACTIVES_BUNDLE, listActives);
    }

    public void showProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(ProgressBar.GONE);
    }

    public void showListView(ArrayList<Active> listActives) {
        this.listActives = listActives;
        adapter.setActiveList(listActives);
        activeListView.setVisibility(View.VISIBLE);
    }

}
