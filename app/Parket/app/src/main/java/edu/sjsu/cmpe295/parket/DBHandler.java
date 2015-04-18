package edu.sjsu.cmpe295.parket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.sjsu.cmpe295.parket.model.ParkingSpace;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;

/**
 * Created by amodrege on 4/17/15.
 */
public class DBHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "parket.db";
    public static final String TABLE_SEARCH = "search";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LONG = "lon";
    public static final String COLUMN_DISABLEDPARKING = "disabledparking";
    public static final String COLUMN_RATE = "rate";
    public static final String COLUMN_STARTTIME = "starttime";
    public static final String COLUMN_ENDTIME = "endtime";
    public static final String COLUMN_DESCRIPTION= "description";
    public static final String COLUMN_PHOTOS= "photos";
    public static final String COLUMN_QRCODE= "qrcode";

    public DBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME+"."+TABLE_SEARCH+";");
        String query = "CREATE TABLE "+ TABLE_SEARCH + "(" +
                COLUMN_ID + " TEXT PRIMARY KEY" +
                COLUMN_ADDRESS + " TEXT" +
                COLUMN_LAT + " REAL" +
                COLUMN_LONG + " REAL" +
                COLUMN_DISABLEDPARKING + " INTEGER" +
                COLUMN_RATE + " REAL" +
                COLUMN_STARTTIME + "TEXT" +
                COLUMN_ENDTIME + "TEXT" +
                COLUMN_PHOTOS + " TEXT" +
                COLUMN_DESCRIPTION + " TEXT" +
                COLUMN_QRCODE + " TEXT" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SEARCH);
        onCreate(db);

    }

    public void insertSearchResponse(SearchResponse searchResponse)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        List<ParkingSpace> ps = searchResponse.getParkingSpaces();
        int count = searchResponse.getCount();
        for(int i=0;i<count;i++)
        {
            cv.put("parkingid",ps.get(i).getParkingSpaceId());
            cv.put("address",ps.get(i).getParkingSpaceAddress());
            cv.put("lat", ps.get(i).getParkingSpaceLat());
            cv.put("lon",ps.get(i).getParkingSpaceLong());
            cv.put("disabledparking", "true");
            cv.put("rate",ps.get(i).getParkingSpaceRate());
            cv.put("starttime", ps.get(i).getStartDateTime());
            cv.put("endtime", ps.get(i).getEndDateTime());
            cv.put("photos", ps.get(i).getParkingSpacePhoto());
            cv.put("description", ps.get(i).getParkingSpaceDescription());
            cv.put("qrcode", ps.get(i).getQrCode());
            db.insert(TABLE_SEARCH, null, cv);
        }
        db.close();
    }

    public SearchResponse getAllData()
    {
        // 0 -> false  &  1 -> true
        boolean dpstatus;
        SearchResponse sr = null;
        ParkingSpace ps;
        List<ParkingSpace> parkingSpaces = new ArrayList();
        String query = "SELECT * from "+TABLE_SEARCH;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        int count = getRowsCount(TABLE_SEARCH);
        if(c.moveToFirst())
        {
            if(c.getInt(4)==0)
                dpstatus = false;
            else
                dpstatus = true;
            ps = new ParkingSpace(c.getString(0),c.getString(1),c.getDouble(2),c.getDouble(3),dpstatus,c.getDouble(5),c.getString(6),c.getString(7),
                    c.getString(8),c.getString(9),c.getString(10));
            parkingSpaces.add(ps);
            sr = new SearchResponse();
            sr.setParkingSpaces(parkingSpaces);
            sr.setCount(count);
        }
        db.close();
        return sr;

    }

    public int getRowsCount(String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT COUNT(*) FROM "+tableName;
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst())
            return c.getCount();
        return 0;
    }





}
