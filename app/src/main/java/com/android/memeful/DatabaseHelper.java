package com.android.memeful;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Imgur.db";
    private static final String TABLE_NAME1 = "PhotoDetailsViral";
    private static final String TABLE_NAME2 = "PhotoDetailsFeed";
    Context Context;
    SQLiteDatabase db;

    private static final String TABLE_CREATE1 = "create table " + TABLE_NAME1 + " (uID text not null, photoID text not null, id text not null, title text not null, userName text not null, views text not null, ups text not null, downs text not null, favCount text not null)";
    private static final String TABLE_CREATE2 = "create table " + TABLE_NAME2 + " (uID text not null, photoID text not null, id text not null, title text not null, userName text not null, views text not null, ups text not null, downs text not null, favCount text not null)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE1);
        db.execSQL(TABLE_CREATE2);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void initializeDB(int t) {
        db = this.getWritableDatabase();
        if (t == 1)
            db.execSQL("delete from " + TABLE_NAME1);
        else
            db.execSQL("delete from " + TABLE_NAME2);
        db.close();
    }

    public void storeImageData(String uID, String photoID, String id, String title, String userName, String views, String ups, String downs, String favCount, int t) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uID", uID);
        values.put("photoID", photoID);
        values.put("id", id);
        values.put("title", title);
        values.put("userName", userName);
        values.put("views", views);
        values.put("ups", ups);
        values.put("downs", downs);
        values.put("favCount", favCount);
        if (t == 1)
            db.insert(TABLE_NAME1, null, values);
        else
            db.insert(TABLE_NAME2, null, values);
        db.close();
    }

    public String getPhotoID(String uID, int t) {
        String photoid = "null";
        db = this.getReadableDatabase();
        Cursor cursor;
        if (t == 1)
            cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        else
            cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (uID.equals(cursor.getString(0))) {
                photoid = cursor.getString(1);
                break;
            }
            cursor.moveToNext();
        }
        db.close();
        return photoid;
    }

    public String getID(String uID, int t) {
        String id = "null";
        db = this.getReadableDatabase();
        Cursor cursor;
        if (t == 1)
            cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        else
            cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (uID.equals(cursor.getString(0))) {
                id = cursor.getString(2);
                break;
            }
            cursor.moveToNext();
        }
        db.close();
        return id;
    }

    public String getTitle(String uID, int t) {
        String title = "null";
        db = this.getReadableDatabase();
        Cursor cursor;
        if (t == 1)
            cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        else
            cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (uID.equals(cursor.getString(0))) {
                title = cursor.getString(3);
                break;
            }
            cursor.moveToNext();
        }
        db.close();
        return title;
    }

    public String getUserName(String uID, int t) {
        String userName = "null";
        db = this.getReadableDatabase();
        Cursor cursor;
        if (t == 1)
            cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        else
            cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (uID.equals(cursor.getString(0))) {
                userName = cursor.getString(4);
                break;
            }
            cursor.moveToNext();
        }
        db.close();
        return userName;
    }

    public String getViews(String uID, int t) {
        String views = "null";
        db = this.getReadableDatabase();
        Cursor cursor;
        if (t == 1)
            cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        else
            cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (uID.equals(cursor.getString(0))) {
                views = cursor.getString(5);
                break;
            }
            cursor.moveToNext();
        }
        db.close();
        return views;
    }

    public String getUps(String uID, int t) {
        String ups = "null";
        db = this.getReadableDatabase();
        Cursor cursor;
        if (t == 1)
            cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        else
            cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (uID.equals(cursor.getString(0))) {
                ups = cursor.getString(6);
                break;
            }
            cursor.moveToNext();
        }
        db.close();
        return ups;
    }

    public String getDowns(String uID, int t) {
        String downs = "null";
        db = this.getReadableDatabase();
        Cursor cursor;
        if (t == 1)
            cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        else
            cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (uID.equals(cursor.getString(0))) {
                downs = cursor.getString(7);
                break;
            }
            cursor.moveToNext();
        }
        db.close();
        return downs;
    }

    public String getFavCount(String uID, int t) {
        String fav = "null";
        db = this.getReadableDatabase();
        Cursor cursor;
        if (t == 1)
            cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        else
            cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (uID.equals(cursor.getString(0))) {
                fav = cursor.getString(8);
                break;
            }
            cursor.moveToNext();
        }
        db.close();
        return fav;
    }
}