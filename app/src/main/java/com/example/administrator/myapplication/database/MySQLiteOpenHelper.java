package com.example.administrator.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/1/10.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public MySQLiteOpenHelper(Context context,String name){
        this(context,name,null,1);//由于后边两个参数基本不变，所以再写一个简单的构造方法来调用上边的构造方法。
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String create="create table if not exists foot(id Integer primary key autoincrement,name varchar(20)," +
                "time varchar(10),content varchar (150),imagePath varchar(30))";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
