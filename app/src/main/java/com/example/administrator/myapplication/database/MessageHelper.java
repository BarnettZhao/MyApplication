package com.example.administrator.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kkkkk on 2016/4/15.
 */
public class MessageHelper extends SQLiteOpenHelper {
	public MessageHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	public MessageHelper (Context context, String name) {
		this(context, name ,null, 1);

	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String create="create table if not exists messagedata(id Integer primary key autoincrement,tochat varchar (30),words varchar(100)," +
				"type Integer(10))";
		db.execSQL(create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
