package edu.sjsu.cmpe295.parket.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.cmpe295.parket.model.ParkingSpace;
import edu.sjsu.cmpe295.parket.model.UserParkingSpace;
import edu.sjsu.cmpe295.parket.model.response.QueryParkingSpacesResponse;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;
/**
 * Created by amodrege on 4/17/15.
 */
public class DBHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "parketdb";

    // Common Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IDTOKEN = "idToken";

    // Tables and their columns
    public static final String TABLE_SEARCH = "search";
    public static final String COLUMN_SEARCH_SPACEID = "parkingSpaceId";
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
    public static final String COLUMN_PARKINGSPACES_SPACEID = "parkingSpaceId";
    public static final String COLUMN_PARKINGSPACES_LABEL = "parkingSpaceLabel";
    public static final String COLUMN_PARKINGSPACES_DESCRIPTION = "parkingSpaceDescription";
    public static final String COLUMN_PARKINGSPACES_ADDRESS = "parkingSpaceAddress";
    public static final String COLUMN_PARKINGSPACES_DISABLEDPARKING= "disabledParkingFlag";
    public static final String COLUMN_PARKINGSPACES_PHOTOS= "parkingSpacePhoto";
    public static final String COLUMN_PARKINGSPACES_LAT= "parkingSpaceLat";
    public static final String COLUMN_PARKINGSPACES_LONG= "parkingSpaceLong";
    public static final String COLUMN_PARKINGSPACES_AVAILABILITYFLAG= "availabilityFlag";
    public static final String COLUMN_PARKINGSPACES_STARTDATETIME= "startDateTime";
    public static final String COLUMN_PARKINGSPACES_ENDDATETIME= "endDateTime";
    public static final String COLUMN_PARKINGSPACES_RATE= "parkingSpaceRate";


    public DBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SEARCH+";");
        String querySearch = "CREATE TABLE "+TABLE_SEARCH + "( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SEARCH_SPACEID + " TEXT, " +
                COLUMN_SEARCH_ADDRESS + " TEXT, " +
                COLUMN_SEARCH_LAT + " REAL, " +
                COLUMN_SEARCH_LONG + " REAL, " +
                COLUMN_SEARCH_DISABLEDPARKING + " INTEGER, " +
                COLUMN_SEARCH_RATE + " REAL, " +
                COLUMN_SEARCH_STARTTIME + " TEXT, " +
                COLUMN_SEARCH_ENDTIME + " TEXT, " +
                COLUMN_SEARCH_PHOTOS + " TEXT, " +
                COLUMN_SEARCH_DESCRIPTION + " TEXT, " +
                COLUMN_SEARCH_QRCODE + " TEXT " + ");";
        db.execSQL(querySearch);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PARKINGSPACES+";");
        String queryParkingSpaces = "CREATE TABLE "+TABLE_PARKINGSPACES + "( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PARKINGSPACES_SPACEID + " TEXT, " +
                COLUMN_PARKINGSPACES_LABEL + " TEXT, " +
                COLUMN_PARKINGSPACES_DESCRIPTION + " TEXT, " +
                COLUMN_PARKINGSPACES_ADDRESS + " TEXT, " +
                COLUMN_PARKINGSPACES_DISABLEDPARKING + " INTEGER, " +
                COLUMN_PARKINGSPACES_PHOTOS + " TEXT, " +
                COLUMN_PARKINGSPACES_LAT + " REAL " +
                COLUMN_PARKINGSPACES_LONG + " REAL " +
                COLUMN_PARKINGSPACES_AVAILABILITYFLAG + " INTEGER, " +
                COLUMN_PARKINGSPACES_STARTDATETIME + " TEXT, " +
                COLUMN_PARKINGSPACES_ENDDATETIME + " TEXT, " +
                COLUMN_PARKINGSPACES_RATE + " REAL " +");";
        db.execSQL(queryParkingSpaces);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }

    public void setParkingSpaceResponse(QueryParkingSpacesResponse parkingSpacesResponse)
    {
        clearTable(TABLE_PARKINGSPACES);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv;
        List<UserParkingSpace> ps = parkingSpacesResponse.getParkingSpaces();
        int count = parkingSpacesResponse.getCount();
        int dpStatus,availabilityStatus;

        for(int i=0;i<count;i++)
        {
            cv = new ContentValues();
            cv.put(COLUMN_PARKINGSPACES_SPACEID, ps.get(i).getParkingSpaceId());
            cv.put(COLUMN_PARKINGSPACES_LABEL,ps.get(i).getParkingSpaceLabel());
            cv.put(COLUMN_PARKINGSPACES_DESCRIPTION, ps.get(i).getParkingSpaceDescription());
            cv.put(COLUMN_PARKINGSPACES_ADDRESS,ps.get(i).getParkingSpaceAddress());
            dpStatus = (ps.get(i).isDisabledParkingFlag()) ? 1 : 0;
            cv.put(COLUMN_PARKINGSPACES_DISABLEDPARKING, dpStatus);
            cv.put(COLUMN_PARKINGSPACES_PHOTOS, ps.get(i).getParkingSpacePhoto());
            cv.put(COLUMN_PARKINGSPACES_LAT, ps.get(i).getParkingSpaceLat());
            cv.put(COLUMN_PARKINGSPACES_LONG,ps.get(i).getParkingSpaceLong());
            availabilityStatus = (ps.get(i).isParkingSpaceAvailabilityFlag()) ? 1 : 0;
            cv.put(COLUMN_PARKINGSPACES_AVAILABILITYFLAG,availabilityStatus);
            cv.put(COLUMN_PARKINGSPACES_STARTDATETIME,ps.get(i).getStartDateTime());
            cv.put(COLUMN_PARKINGSPACES_ENDDATETIME,ps.get(i).getEndDateTime());
            cv.put(COLUMN_PARKINGSPACES_RATE,ps.get(i).getParkingSpaceRate());
        }
        db.close();
    }

    public QueryParkingSpacesResponse getParkingSpaceResponse()
    {
        boolean dpStatus, availabilityStatus;
        QueryParkingSpacesResponse psresponse = null;
        UserParkingSpace ps;
        List<UserParkingSpace> parkingSpaces = new ArrayList();
        String query = "SELECT * from " + TABLE_PARKINGSPACES + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        int count = getRowsCount(TABLE_PARKINGSPACES);
        while (c.moveToNext())
        {
            if(c.getInt(5) == 0)
                dpStatus = false;
            else
                dpStatus = true;
            if(c.getInt(9) == 0)
                availabilityStatus = false;
            else
                availabilityStatus = true;
            ps = new UserParkingSpace(c.getString(1), c.getString(2), c.getString(3), c.getString(4),
                    dpStatus, c.getString(6), c.getDouble(7), c.getDouble(8), availabilityStatus,
                    c.getString(10), c.getString(11), c.getDouble(12));
            parkingSpaces.add(ps);

        }
        c.close();
        db.close();
        psresponse = new QueryParkingSpacesResponse();
        psresponse.setParkingSpaces(parkingSpaces);
        psresponse.setCount(count);
        return psresponse;
    }

    public void setSearchResponse(SearchResponse searchResponse)
    {
        // Clear the table first
        clearTable(TABLE_SEARCH);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv;
        List<ParkingSpace> ps = searchResponse.getParkingSpaces();
        int count = searchResponse.getCount();
        int dpStatus;

        for(int i=0;i<count;i++)
        {
            cv = new ContentValues();
            cv.put(COLUMN_SEARCH_SPACEID,ps.get(i).getParkingSpaceId());
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
        String query = "SELECT * from " + TABLE_SEARCH + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        int count = getRowsCount(TABLE_SEARCH);
        while(c.moveToNext())
        {
            if(c.getInt(5) == 0)
                dpStatus = false;
            else
                dpStatus = true;
            ps = new ParkingSpace(c.getString(1), c.getString(2), c.getDouble(3), c.getDouble(4),
                    dpStatus, c.getDouble(6), c.getString(7), c.getString(8), c.getString(9),
                    c.getString(10), c.getString(11));
            parkingSpaces.add(ps);
        }
        c.close();
        db.close();
        sr = new SearchResponse();
        sr.setParkingSpaces(parkingSpaces);
        sr.setCount(count);
        return sr;
    }

    public ParkingSpace getParkingSpaceFromSearchResponse(String parkingSpaceId) {
        // 0 -> false  &  1 -> true
        boolean dpStatus;
        ParkingSpace parkingSpace = new ParkingSpace();
        String query = "SELECT * from " + TABLE_SEARCH
                + " WHERE " + COLUMN_SEARCH_SPACEID + " = \"" + parkingSpaceId + "\";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        while(c.moveToNext())
        {
            if(c.getInt(5) == 0)
                dpStatus = false;
            else
                dpStatus = true;
            parkingSpace = new ParkingSpace(c.getString(1), c.getString(2), c.getDouble(3), c.getDouble(4),
                    dpStatus, c.getDouble(6), c.getString(7), c.getString(8), c.getString(9),
                    c.getString(10), c.getString(11));
        }
        c.close();
        db.close();
        return parkingSpace;
    }

    public void clearTable(String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tableName+";");

    }

    public int getRowsCount(String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT COUNT(*) FROM "+ tableName + ";";
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst())
            return c.getCount();
        return 0;
    }
}
