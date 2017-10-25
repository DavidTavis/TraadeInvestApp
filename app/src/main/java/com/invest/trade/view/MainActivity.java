package com.invest.trade.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.invest.trade.R;

public class MainActivity extends AppCompatActivity {

    private static final String SAVE_INSTANCE_ID = "fragment_item_id";
    private Fragment contentFragment;
    private ActiveListFragment activeListFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        handleOrientationChanged(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (contentFragment instanceof ChartFragment) {
            outState.putString(SAVE_INSTANCE_ID, ChartFragment.ARG_ITEM_ID);
        } else {
            outState.putString(SAVE_INSTANCE_ID, ActiveListFragment.ARG_ITEM_ID);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else if (contentFragment instanceof ActiveListFragment || fm.getBackStackEntryCount() == 0) {
            finish();
        }
    }

    private void handleOrientationChanged(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVE_INSTANCE_ID)) {

                String content = savedInstanceState.getString(SAVE_INSTANCE_ID);

                if (content.equals(ActiveListFragment.ARG_ITEM_ID)) {
                    if (fragmentManager.findFragmentByTag(ActiveListFragment.ARG_ITEM_ID) != null) {
//                        setFragmentTitle(R.string.add_contact);
                        activeListFragment = (ActiveListFragment) fragmentManager.findFragmentByTag(ActiveListFragment.ARG_ITEM_ID);
                        contentFragment = activeListFragment;
                    }
                }else if(content.equals(ChartFragment.ARG_ITEM_ID)){
                    contentFragment = fragmentManager.findFragmentByTag(ChartFragment.ARG_ITEM_ID);
                }
            }

        } else {
            activeListFragment = new ActiveListFragment();
//            setFragmentTitle(R.string.app_name);
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

}
