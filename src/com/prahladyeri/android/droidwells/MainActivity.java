package com.prahladyeri.android.droidwells;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private String[] values=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.cmdmainAddWellSite).setOnClickListener(this);
		findViewById(R.id.cmdmainDeleteWellSite).setOnClickListener(this);
		findViewById(R.id.cmdmainExportCSVData).setOnClickListener(this);
		findViewById(R.id.cmdmainNewDayData).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
		case R.id.cmdmainAddWellSite:
			Intent addwells=new Intent(this,AddWellsActivity.class);
			Bundle b=new Bundle();
			b.putInt("ID", -1);
			addwells.putExtras(b);
			startActivityForResult(addwells,0);
			break;
		case R.id.cmdmainDeleteWellSite:
			SQLiteDatabase db=(new DbHelper(this)).getReadableDatabase();
			Cursor cur=db.rawQuery("SELECT SITE_NAME, ID FROM SITES ORDER BY SITE_NAME", null);
			values= new String[cur.getCount()];
			final Integer[] siteid=new Integer[cur.getCount()];
			while(cur.moveToNext())
			{
				 values[cur.getPosition()] = cur.getString(0);
				 siteid[cur.getPosition()] = cur.getInt(1);
			}
			
			Device.ShowListDialog(this, "SELECT SITE", values, false, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
						//int i= Device.CheckedItems.get(0);
						Device.ShowMessageDialog(MainActivity.this, siteid[which].toString());
				}
			});
			break;
		case R.id.cmdmainNewDayData:
			break;
		case R.id.cmdmainExportCSVData:
			break;
		}
		
	}
}
