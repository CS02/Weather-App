package com.example.getweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
This class is responsible for initializing a database,
and inserting and reading data values from database
This class has methods to both read and write data to the datas=base
*/
public class DataBaseHandler extends SQLiteOpenHelper {

    //singleton pattern allows only 1 instance of a database file
    private static DataBaseHandler instance = null;


    public static DataBaseHandler getInstance(Context context) {
        //if instance has not been created
        if(instance == null){
            //call private constructor to initialize the database
            instance = new DataBaseHandler(context.getApplicationContext());

        }
        return instance;
    }
    //initialize database
    private DataBaseHandler(Context context){
        //initializes database
        super(context, "CityData.db", null, 1);
        //TODO insert temporary city which can be updated when location loads

    }

    //Creates Table with specified columns
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table CityDetails(" +
                "city TEXT primary key," +
                " description Text," +
                " temperature Text," +
                " feelsLike Text," +
                " humidity TEXT," +
                " pressure TEXT," +
                " windSpeed TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists CityDetails");
    }

    //adds passed values to database
    public Boolean insertCityData(String city, String description, String temperature, String feelsLike, String humidity, String pressure, String windSpeed){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("city", city);
        contentValues.put("description", description);
        contentValues.put("temperature", temperature);
        contentValues.put("feelsLike", feelsLike);
        contentValues.put("humidity", humidity);
        contentValues.put("pressure", pressure);
        contentValues.put("windSpeed", windSpeed);

        //insert values into table
        long result = sqLiteDatabase.insertWithOnConflict("CityDetails",null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        sqLiteDatabase.close();
        //returns true if insertion was successful, else returns false
        return result != -1;
    }

    //returns a cursor which can be used to access values from database
    public Cursor getdata(){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM CityDetails", null);
        return cursor;
    }

    //Used to update a city's weather data
    public Boolean updateCityData(String city, String description, String temperature, String feelsLike, String humidity, String pressure, String windSpeed){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", description);
        contentValues.put("temperature", temperature);
        contentValues.put("feelsLike", feelsLike);
        contentValues.put("humidity", humidity);
        contentValues.put("pressure", pressure);
        contentValues.put("windSpeed", windSpeed);


        long result = sqLiteDatabase.update("CityDetails", contentValues, "city=?", new String[]{city});
        Log.i("result", String.valueOf(result));
        //if update was successfull returns true, else returns false
        return result != -1;
    }

    //Returns a cursor which points to a specific city's values
    public Cursor getSpecificCityData(String cityName){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("Select * from CityDetails where city = ?", new String[] {cityName});
        return cursor;
    }
}
