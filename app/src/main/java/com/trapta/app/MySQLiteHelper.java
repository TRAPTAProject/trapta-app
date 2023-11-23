package com.trapta.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	  private static final String DATABASE_NAME = "trapta.db";
	  private static final int DATABASE_VERSION = 6;
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("create table `archertable` (`id` INTEGER PRIMARY KEY, `license` char(10), `category` char(8), `name` char(100), `letter` INTEGER, `trispot` INTEGER)");
		database.execSQL("create table `volleytable` (`index` INTEGER PRIMARY KEY AUTOINCREMENT, `id` INTEGER, `run` INTEGER, `volley` INTEGER, `arrow0` INTEGER, `arrow1` INTEGER, `arrow2` INTEGER, `arrow3` INTEGER, `arrow4` INTEGER, `arrow5` INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(MySQLiteHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    resetDB(db);
		    onCreate(db);
	}
	
	public void resetDB(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS `archertable`");
	    db.execSQL("DROP TABLE IF EXISTS `volleytable`");
	}

}
