package com.karthik.learnsql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBHandle extends SQLiteOpenHelper{
	
	private final Context Ctx;
	private static String DATABASE_CREATE;
	
	public DBHandle(Context c, String Create)
	{
		this(c, "detectors", null, 2, Create);
	}

	public DBHandle(Context context, String name, CursorFactory factory,
			int version, String create) {
		super(context, name, factory, version);
		this.Ctx = context;
		DBHandle.DATABASE_CREATE = create;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v("Karthik", "DB Created");
		//db.execSQL("DROP TABLE IF EXISTS detectors");
		 db.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w("Karthik","Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS detectors");
        onCreate(db);
	}

}
