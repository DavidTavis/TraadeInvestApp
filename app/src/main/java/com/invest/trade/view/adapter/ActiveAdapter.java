package com.invest.trade.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.invest.trade.R;
import com.invest.trade.data.model.Active;
import com.invest.trade.view.ActiveListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TechnoA on 25.10.2017.
 */

public class ActiveAdapter extends ArrayAdapter<Active> {

    private Context context;
    private List<Active> activeList;
    private ActiveListFragment fragment;

    public ActiveAdapter(@NonNull Context context, List<Active> activeList, ActiveListFragment fragment) {
        super(context,  R.layout.active_list_item, activeList);
        this.context = context;
        this.activeList = activeList;
        this.fragment = fragment;
    }

    private class ViewHolder {
        TextView tvAssets;
        TextView tvRate1;
        TextView tvRate2;
        TextView tvRate3;
        TextView tvChange;
        ImageButton ibForward;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Active active = (Active) getItem(position);

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.active_list_item, null);

            holder = new ViewHolder();

            holder.tvAssets = (TextView) convertView.findViewById(R.id.tv_assets);
            holder.tvRate1 = (TextView) convertView.findViewById(R.id.tv_rate1);
            holder.tvRate2 = (TextView) convertView.findViewById(R.id.tv_rate2);
            holder.tvRate3 = (TextView) convertView.findViewById(R.id.tv_rate3);
            holder.tvChange = (TextView) convertView.findViewById(R.id.tv_change);
            holder.ibForward = (ImageButton) convertView.findViewById(R.id.ib_forward);

            holder.ibForward.setTag(position);
            holder.ibForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    Active currentActive = getItem(position);
                    fragment.startChartFragment(currentActive);
                }
            });
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String rate = active.getCurrentRate();
        String color = active.getColor();
        boolean bigSymbols = active.isBigSymbols();

        int lengthString = rate.length();

        holder.tvAssets.setText(active.getAssets());
        holder.tvChange.setText(active.getChange());

        if(!bigSymbols) {
            holder.tvRate1.setText(rate);
            holder.tvRate1.setTextColor(Color.parseColor(color));
            holder.tvRate2.setText("");
            holder.tvRate3.setText("");
        }else{
            holder.tvRate1.setText(rate.substring(0,lengthString-3));
            holder.tvRate1.setTextColor(Color.parseColor(color));

            holder.tvRate2.setText(rate.substring(lengthString-3,lengthString-1));
            holder.tvRate2.setTextColor(Color.parseColor(color));
            holder.tvRate2.setTextSize(20.0F);

            holder.tvRate3.setText(rate.substring(lengthString-1,lengthString));
            holder.tvRate3.setTextColor(Color.parseColor(color));
            holder.tvRate3.setTextSize(10.0F);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return activeList.size();
    }

    @Override
    public Active getItem(int position) {
        return activeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void add(Active active) {
        activeList.add(active);
        notifyDataSetChanged();
        super.add(active);
    }

    @Override
    public void remove(Active active) {
        activeList.remove(active);
        notifyDataSetChanged();
        super.remove(active);
    }

    public void setActiveList(ArrayList<Active> activeList){
        this.activeList = activeList;
        notifyDataSetChanged();
    }
}
