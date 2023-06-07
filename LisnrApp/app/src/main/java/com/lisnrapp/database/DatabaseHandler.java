package com.lisnrapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final String DB_NAME = "recibidosbd";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;

    public static final String TABLE_NAME = "recibidosTable";

    public static final String TITLE_PROMO = "title";
    public static final String HOUR_PROMO = "hour";
    public static final String URL_PROMO = "url";

    public static final String QUERY_FOR_CREATE_TABLE =
            "create table "+TABLE_NAME+"("+TITLE_PROMO+" text,"+HOUR_PROMO+ " text,"+URL_PROMO+" text);";


    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION );
        Log.d("DATABASE", "Promo agregada");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_FOR_CREATE_TABLE);
        Log.d("DATABASE", "Table created");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
    }

    public void insertUsers(SQLiteDatabase sqLiteDatabase, String title, String hour, String url) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE_PROMO, title);
        contentValues.put(HOUR_PROMO, hour);
        contentValues.put(URL_PROMO, url);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        Log.d("DATABASE", "Data123 inserted. Title: " + title + ", Hour: " + hour + ", URL: " + url);
    }


    public static Cursor getAllData(SQLiteDatabase sqLiteDatabase){
        Cursor cursor;
        String column[] = {TITLE_PROMO,HOUR_PROMO,URL_PROMO};
        cursor = sqLiteDatabase.query(TABLE_NAME,column, null,null,null,null,null);
        return  cursor;
    }

}
