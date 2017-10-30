package com.invest.trade.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;

import com.invest.trade.data.model.Active;
import com.invest.trade.util.Utils;
import com.invest.trade.view.ActiveListFragment;

import java.util.ArrayList;


public class ActivesRepository {

    private SQLite dataBase;
    //table
    private static final String TABLE_NAME = "ActivesTable";
    //columns
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_RATE = "rate";
    private static final String COLUMN_ASSET = "asset";
    private static final String COLUMN_ID = BaseColumns._ID;

    public ActivesRepository(Context context) {

        this.dataBase = new SQLite(context);

    }

    private class SQLite extends SQLiteOpenHelper {

        // data base and table
        private static final String DATABASE_NAME = "ActivesDataBase.db";
        private static final int DATABASE_VERSION = 1;


        public SQLite(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createDatabase(sqLiteDatabase);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            createDatabase(sqLiteDatabase);
        }

        private void createDatabase(SQLiteDatabase db) {

            db.execSQL(String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME));

            String query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT);",
                                        TABLE_NAME, COLUMN_ID, COLUMN_ASSET, COLUMN_TIME, COLUMN_RATE);
            db.execSQL(query);

        }

    }

    public void addActives(ArrayList<Active> actives){

        SQLiteDatabase db = dataBase.getWritableDatabase();
        for(Active active: actives) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TIME, active.getTimestamp());
            values.put(COLUMN_RATE, active.getCurrentRate());
            values.put(COLUMN_ASSET, active.getAssets());

            db.insert(TABLE_NAME, null, values);
        }

    }

    public Active getLastActive(String asset){
        SQLiteDatabase db = dataBase.getReadableDatabase();
        String query;
        asset = "'" + asset + "'";
        query = String.format("SELECT %s, %s, %s, %s FROM %s  WHERE %s=%s ORDER BY %s DESC LIMIT 1",
                COLUMN_TIME, COLUMN_RATE, COLUMN_ASSET, COLUMN_ID, TABLE_NAME, COLUMN_ASSET, asset, COLUMN_ID);

        Cursor cursor = db.rawQuery(query,null);

        Active active = new Active();
        while(cursor.moveToNext()){
            active.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
            active.setCurrentRate(cursor.getString(cursor.getColumnIndex(COLUMN_RATE)));
            active.setAssets(cursor.getString(cursor.getColumnIndex(COLUMN_ASSET)));
        }
        return active;
    }

    public ArrayList<Active> getActives(String asset, int contRow){

        SQLiteDatabase db = dataBase.getReadableDatabase();
        String query;

        asset = "'" + asset + "'";
        query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s=%s ORDER BY %s DESC LIMIT %s",
                COLUMN_TIME, COLUMN_RATE, COLUMN_ASSET, COLUMN_ID, TABLE_NAME, COLUMN_ASSET, asset, COLUMN_ID, contRow);

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Active> actives = new ArrayList<>();

        while(cursor.moveToNext()){
            Active active = new Active();
            active.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
            active.setCurrentRate(cursor.getString(cursor.getColumnIndex(COLUMN_RATE)));
            active.setAssets(cursor.getString(cursor.getColumnIndex(COLUMN_ASSET)));
            actives.add(active);
        }

        return actives;

    }

    public void close(){
        SQLiteDatabase db = dataBase.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        dataBase.close();
    }

    public int count(){
        SQLiteDatabase db = dataBase.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
