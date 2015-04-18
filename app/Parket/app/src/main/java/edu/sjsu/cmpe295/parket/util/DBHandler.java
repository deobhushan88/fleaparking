package edu.sjsu.cmpe295.parket.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.cmpe295.parket.model.ParkingSpace;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;

/**
 * Created by amodrege on 4/17/15.
 */
public class DBHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "parket.db";

    // Tables and their columns
    public static final String TABLE_SEARCH = "search";
    public static final String COLUMN_SEARCH_ID = "parkingSpaceId";
    public static final String COLUMN_SEARCH_ADDRESS = "parkingSpaceAddress";
    public static final String COLUMN_SEARCH_LAT = "parkingSpaceLat";
    public static final String COLUMN_SEARCH_LONG = "parkingSpaceLong";
    public static final String COLUMN_SEARCH_DISABLEDPARKING = "disabledParkingFlag";
    public static final String COLUMN_SEARCH_RATE = "parkingSpaceRate";
    public static final String COLUMN_SEARCH_STARTTIME = "startDateTime";
    public static final String COLUMN_SEARCH_ENDTIME = "endDateTime";
    public static final String COLUMN_SEARCH_DESCRIPTION= "parkingSpaceDescription";
    public static final String COLUMN_SEARCH_PHOTOS= "parkingSpacePhoto";
    public static final String COLUMN_SEARCH_QRCODE= "qrCode";

    public static final String TABLE_PARKINGSPACES = "parkingSpaces";

    public DBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME+"."+TABLE_SEARCH+";");
        String query = "CREATE TABLE "+ TABLE_SEARCH + "(" +
                COLUMN_SEARCH_ID + " TEXT PRIMARY KEY" +
                COLUMN_SEARCH_ADDRESS + " TEXT" +
                COLUMN_SEARCH_LAT + " REAL" +
                COLUMN_SEARCH_LONG + " REAL" +
                COLUMN_SEARCH_DISABLEDPARKING + " INTEGER" +
                COLUMN_SEARCH_RATE + " REAL" +
                COLUMN_SEARCH_STARTTIME + "TEXT" +
                COLUMN_SEARCH_ENDTIME + "TEXT" +
                COLUMN_SEARCH_PHOTOS + " TEXT" +
                COLUMN_SEARCH_DESCRIPTION + " TEXT" +
                COLUMN_SEARCH_QRCODE + " TEXT" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }

    public void setSearchResponse(SearchResponse searchResponse)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        List<ParkingSpace> ps = searchResponse.getParkingSpaces();
        int count = searchResponse.getCount();
        int dpStatus;
        for(int i=0;i<count;i++)
        {
            cv.put(COLUMN_SEARCH_ID,ps.get(i).getParkingSpaceId());
            cv.put(COLUMN_SEARCH_ADDRESS,ps.get(i).getParkingSpaceAddress());
            cv.put(COLUMN_SEARCH_LAT, ps.get(i).getParkingSpaceLat());
            cv.put(COLUMN_SEARCH_LONG,ps.get(i).getParkingSpaceLong());
            dpStatus = (ps.get(i).isDisabledParkingFlag()) ? 1 : 0;
            cv.put(COLUMN_SEARCH_DISABLEDPARKING, dpStatus);
            cv.put(COLUMN_SEARCH_RATE,ps.get(i).getParkingSpaceRate());
            cv.put(COLUMN_SEARCH_STARTTIME, ps.get(i).getStartDateTime());
            cv.put(COLUMN_SEARCH_ENDTIME, ps.get(i).getEndDateTime());
            cv.put(COLUMN_SEARCH_PHOTOS, ps.get(i).getParkingSpacePhoto());
            cv.put(COLUMN_SEARCH_DESCRIPTION, ps.get(i).getParkingSpaceDescription());
            cv.put(COLUMN_SEARCH_QRCODE, ps.get(i).getQrCode());
            db.insert(TABLE_SEARCH, null, cv);
        }
        db.close();
    }

    public SearchResponse getSearchResponse()
    {
        // 0 -> false  &  1 -> true
        boolean dpStatus;
        SearchResponse sr = null;
        ParkingSpace ps;
        List<ParkingSpace> parkingSpaces = new ArrayList();
        String query = "SELECT * from " + TABLE_SEARCH;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        int count = getRowsCount(TABLE_SEARCH);
        while(c.moveToNext())
        {
            if(c.getInt(4) == 0)
                dpStatus = false;
            else
                dpStatus = true;
            ps = new ParkingSpace(c.getString(0), c.getString(1), c.getDouble(2), c.getDouble(3),
                    dpStatus, c.getDouble(5), c.getString(6), c.getString(7), c.getString(8),
                    c.getString(9), c.getString(10));
            parkingSpaces.add(ps);
        }
        db.close();
        sr = new SearchResponse();
        sr.setParkingSpaces(parkingSpaces);
        sr.setCount(count);
        return sr;
    }

    public int getRowsCount(String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT COUNT(*) FROM "+ tableName;
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst())
            return c.getCount();
        return 0;
    }
}
