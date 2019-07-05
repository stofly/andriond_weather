package xlr.com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import xlr.com.model.City;
import xlr.com.model.Temperature;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by baisoo on 16/9/24.
 */
public class DBManager {
    private static final String ASSETS_NAME = "china.db";
    private static final String DB_NAME = "china.db";
    private static final String TABLE_NAME = "city";
    private static final String TABLE_NAME2 = "temperature";
    private static final String NAME = "name";
    private static final String PINYIN = "pinyin";
    private static final int BUFFER_SIZE = 1024;
    private String DB_PATH;
    private Context mContext;


    public DBManager(Context context) {
        this.mContext = context;
        DB_PATH = File.separator + "data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + context.getPackageName() + File.separator + "databases" + File.separator;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void copyDBFile(){
        File dir = new File(DB_PATH);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File dbFile = new File(DB_PATH + DB_NAME);
        if (!dbFile.exists()){
            InputStream is;
            OutputStream os;
            try {
                is = mContext.getResources().getAssets().open(ASSETS_NAME);
                os = new FileOutputStream(dbFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = is.read(buffer, 0, buffer.length)) > 0){
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取所有城市
     * @return
     */
    public List<City> getAllCities(){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        List<City> result = new ArrayList<>();
        City city;
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
            city = new City(name, pinyin);
            result.add(city);
        }
        cursor.close();
        db.close();
        Collections.sort(result, new CityComparator());
        return result;
    }
    //增
    public void addTemperature(Temperature temperature){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        ContentValues cv=new ContentValues();
        cv.put("cname",temperature.getCname());
        cv.put("cweather",temperature.getCweather());
        cv.put("ctemperature",temperature.getCtemperature()+"℃");
        db.insert(TABLE_NAME2,null,cv);
        Log.d("cnm", "添加成功!");
    }
    //删
    public void deleteTemperature(String  cityName){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        db.execSQL("delete from temperature where cname = ?",new Object[]{cityName});
        Log.d("cnm", "删除成功!");
    }
    //条件查
    public int getCity(String  cityName){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor c = db.rawQuery("SELECT* FROM temperature WHERE cname = ?", new String[]{cityName});
        int i = 0;
        while (c.moveToNext()) {
            i++;
        }
        c.close();
        return i;
    }
    //查
    public List<Temperature> getAllTemperature(){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from  temperature", null);
        List<Temperature> result = new ArrayList<>();
        Temperature temperature;
        while (cursor.moveToNext()){
            String cname = cursor.getString(cursor.getColumnIndex("cname"));
            String cweather = cursor.getString(cursor.getColumnIndex("cweather"));
            String ctemperature = cursor.getString(cursor.getColumnIndex("ctemperature"));
            temperature= new Temperature(cname,cweather,ctemperature);
            result.add(temperature);
        }
        cursor.close();
        db.close();
        return result;
    }



    /**
     * a-z排序
     */
    private class CityComparator implements Comparator<City> {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }
}
