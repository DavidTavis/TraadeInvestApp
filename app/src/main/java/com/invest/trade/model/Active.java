package com.invest.trade.model;

import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by TechnoA on 24.10.2017.
 */

public class Active implements Serializable {

    private String color;
    private String assets;
    private String currentRate;
    private String change;
    private boolean bigSymbols;

    public Active() {
    }

    public Active(String assets, String currentRate, String change, String color, Boolean bigSymbols) {
        this.assets = assets;
        this.currentRate = currentRate;
        this.change = change;
        this.color = color;
        this.bigSymbols = bigSymbols;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }

    public String getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(String currentRate) {
        this.currentRate = currentRate;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isBigSymbols() {
        return bigSymbols;
    }

    public void setBigSymbols(boolean bigSymbols) {
        this.bigSymbols = bigSymbols;
    }

    @Override
    public String toString() {
        return "Active{" +
                "color='" + color + '\'' +
                ", assets='" + assets + '\'' +
                ", currentRate='" + currentRate + '\'' +
                ", change='" + change + '\'' +
                ", bigSymbols=" + bigSymbols +
                '}';
    }
}
