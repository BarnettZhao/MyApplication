package com.example.administrator.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by kkkkk on 2016/4/15.
 */
public class MessageDbManager {
	private static MessageDbManager manager;
	private MessageHelper messageOpenHelper;
	private SQLiteDatabase db;
	private Context mContext;

	/**
	 * 私有化构造器
	 */
	private MessageDbManager(Context context) {
		this.mContext=context;
		//创建数据库
		messageOpenHelper = new MessageHelper(context,"messagedata");
		if (db == null) {
			db = messageOpenHelper.getWritableDatabase();
		}
	}

	/**
	 * 单例DbManager类
	 *
	 * @return 返回DbManager对象
	 */
	public static MessageDbManager newInstances(Context context) {
		if (manager == null) {
			manager = new MessageDbManager (context);
		}
		return manager;
	}

	/**
	 * 查询数据库的名，数据库的添加
	 *
	 * @param tableName  查询的数据库的名字
	 * @param entityType 查询的数据库所对应的module
	 * @param fieldName  查询的字段名
	 * @param value      查询的字段值
	 * @param <T>        泛型代表AttendInformation，Customer，Order，User，WorkDaily类
	 * @return 返回查询结果，结果为AttendInformation，Customer，Order，User，WorkDaily对象
	 */
	public <T> ArrayList<T> query(String tableName, Class<T> entityType, String fieldName, String value) {

		ArrayList<T> list = new ArrayList();
		Cursor cursor = db.query(tableName, null, fieldName + " like ?", new String[]{value}, null, null, " id desc", null);
//		Cursor cursor=db.rawQuery("select * from "+tableName, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				T t = entityType.newInstance();
				Log.e("errorerror", t.toString());
				for (int i = 1; i < cursor.getColumnCount(); i++) {
					String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
					String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
					Field field = entityType.getDeclaredField(columnName);//获取该字段名的Field对象。
					field.setAccessible(true);//取消对age属性的修饰符的检查访问，以便为属性赋值
					field.set(t, content);
					field.setAccessible(false);//恢复对age属性的修饰符的检查访问
				}
				list.add(t);
				cursor.moveToNext();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 向数据库插入数据
	 *
	 * @param tableName 数据库插入数据的数据表
	 * @param object    数据库插入的对象
	 */
	public void insert(String tableName, Object object) {

		Class clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();//获取该类所有的属性
		ContentValues value = new ContentValues();

		for (Field field : fields) {
			try {
				field.setAccessible(true); //取消对age属性的修饰符的检查访问，以便为属性赋值
				String content = field.get(object).toString();//获取该属性的内容
				value.put(field.getName(), content);
				Log.e("value is ; ", field.getName()+" ,"+content);
				field.setAccessible(false);//恢复对age属性的修饰符的检查访问
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		db.insert(tableName, null, value);
		Toast.makeText(mContext, "数据库插入成功", Toast.LENGTH_SHORT).show();
	}
}
