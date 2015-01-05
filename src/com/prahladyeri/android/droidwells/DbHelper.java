package com.prahladyeri.android.droidwells;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {
	
	public static final String DB_NAME = "droidwells";
	public static final int DB_VERSION = 2;

	public DbHelper(Context context) {
		super(context, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL("CREATE TABLE SITES(ID INTEGER PRIMARY KEY, COMPANY_NAME TEXT, SITE_NAME TEXT);");
		db.execSQL("CREATE TABLE TANKS(ID INTEGER PRIMARY KEY, SITE_ID INTEGER, TANK_VIEW_ID INTEGER, TANK_NUMBER TEXT, FOREIGN KEY(SITE_ID) REFERENCES SITES(ID));");
		db.execSQL("CREATE TABLE DAYENTRY(ID INTEGER PRIMARY KEY, SITE_ID INTEGER, FDATE DATE, TP INTEGER, CP INTEGER, CHK INTEGER, FLW INTEGER, LP INTEGER, TEMP INTEGER, MCF INTEGER, TOTAL INTEGER, COMMENT TEXT, FOREIGN KEY(SITE_ID) REFERENCES SITES(ID));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion==2 && oldVersion==1) {
			//VERSION 2:
			db.execSQL("CREATE TABLE DAYENTRY_TANKS(ID INTEGER PRIMARY KEY, DAYENTRY_ID INTEGER, TANK_ID INTEGER, TOP INTEGER, BTM INTEGER, FOREIGN KEY(DAYENTRY_ID) REFERENCES SITES(ID), FOREIGN KEY(TANK_ID) REFERENCES TANKS(ID) );");
			db.execSQL("ALTER TABLE DAYENTRY ADD DIFF INTEGER;");
			
		}
	}
}
