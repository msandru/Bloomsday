package com.example.bloomsday.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.bloomsday.R;

import java.sql.Connection;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "locations.db";

    public DataBaseHelper(Context context) {
        super( context, DATABASE_NAME, null, 1 );

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTable = "CREATE TABLE IF NOT EXISTS locations_data ( ID INTEGER PRIMARY KEY AUTOINCREMENT, LATITUDE REAL, LONGITUDE REAL, LANDMARK TEXT, PHOTO INT, CHAPTER TEXT);";
        sqLiteDatabase.execSQL( createTable );
        addLocations(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if( i < 9)
            sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS locations_data" );
        onCreate( sqLiteDatabase );
        SQLiteDatabase database = this.getReadableDatabase();
        if(database.rawQuery( "SELECT * FROM locations_data", null ).getCount() == 0){
        addLocations(sqLiteDatabase);}
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if( i > 1)
            sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS locations_data" );
        onCreate( sqLiteDatabase );
        SQLiteDatabase database = this.getReadableDatabase();
        if(database.rawQuery( "SELECT * FROM locations_data", null ).getCount() == 0){
            addLocations(sqLiteDatabase);}
    }


    public void addLocation(SQLiteDatabase sqLiteDatabase, double latitude, double longitude, String landmark, int photo, String chapter) {
        ContentValues contentValues = new ContentValues();
        contentValues.put( "LATITUDE", latitude );
        contentValues.put( "LONGITUDE", longitude );
        contentValues.put( "LANDMARK", landmark );
        contentValues.put( "PHOTO", photo );
        contentValues.put( "CHAPTER", chapter );
        long result = sqLiteDatabase.insert( "locations_data", null, contentValues );
        if (result == -1)
            Log.d( "DataBaseHelper", "Data not inserted " + landmark );
        else
            Log.d( "DataBaseHelper", "Data inserted " + landmark );

    }

    public void addLocations(SQLiteDatabase sqLiteDatabase) {

        addLocation( sqLiteDatabase, 53.38765, -6.063619, "Martello Tower", R.drawable.picture1, "Chapter: Telemachus" );
        addLocation( sqLiteDatabase,53.277911, -6.105844, "Clifton School", R.drawable.picture2, "Chapter: Nestor" );
        addLocation( sqLiteDatabase,53.328541, -6.208925, "Sandymount Strand", R.drawable.picture3, "Chapter: Nausicaa" );
        addLocation( sqLiteDatabase,53.372621, -6.276828, "Glasnevin Cemetery", R.drawable.picture4, "Chapter: Hades" );
        addLocation(sqLiteDatabase,53.346327, -6.250293, "Princes Street", R.drawable.picture5, "Chapter: The Aleous" );
        addLocation( sqLiteDatabase,53.341098, -6.254482, "National Library of Ireland", R.drawable.picture6, "Chapter: Scylla and Charybdis" );
        addLocation( sqLiteDatabase,53.342292, -6.259751, "Grafton Street", R.drawable.picture7, "Chapter: The Wandering Rocks" );
        addLocation( sqLiteDatabase,53.349341, -6.269803, "Barney Kiernan's", R.drawable.picture9, "Chapter: The Cyclops" );
    }

    public Cursor getAllData() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res = sqLiteDatabase.rawQuery( "SELECT * FROM locations_data", null );
        return res;
    }


}
