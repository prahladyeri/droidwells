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
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

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
		SQLiteDatabase dbr=null;
		Cursor cur=null;
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
			dbr=(new DbHelper(this)).getReadableDatabase();
			cur=dbr.rawQuery("SELECT SITE_NAME, ID FROM SITES ORDER BY SITE_NAME", null);
			final String[] values= new String[cur.getCount()];
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
						//Device.ShowMessageDialog(MainActivity.this, siteid[which].toString());
					final Integer tsiteid=siteid[which];
					Device.ShowMessageDialog(MainActivity.this,"Are you sure you want to delete the site " + values[which] + " and related records?"
							,MessageBoxType.YesNo, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (which==DialogInterface.BUTTON_POSITIVE){
										SQLiteDatabase db=new DbHelper(MainActivity.this).getWritableDatabase();
										db.execSQL("DELETE FROM DAYENTRY WHERE SITE_ID=?", new String[]{tsiteid.toString()});
										db.execSQL("DELETE FROM TANKS WHERE SITE_ID=?", new String[]{tsiteid.toString()});
										db.execSQL("DELETE FROM SITES WHERE ID=?", new String[]{tsiteid.toString()});
										Toast.makeText(MainActivity.this, "Record Deleted successfully.", Toast.LENGTH_LONG).show();
									}
								}
							}
							);
				}
			});
			break;
		case R.id.cmdmainNewDayData:
			dbr=(new DbHelper(this)).getReadableDatabase();
			cur=dbr.rawQuery("SELECT SITE_NAME, ID FROM SITES ORDER BY SITE_NAME", null);
			final String[] zvalues= new String[cur.getCount()];
			final Integer[] zsiteid=new Integer[cur.getCount()];
			while(cur.moveToNext())
			{
				 zvalues[cur.getPosition()] = cur.getString(0);
				 zsiteid[cur.getPosition()] = cur.getInt(1);
			}
			
			Device.ShowListDialog(this, "SELECT SITE", zvalues, false, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
						//int i= Device.CheckedItems.get(0);
						//Device.ShowMessageDialog(MainActivity.this, siteid[which].toString());
					final Integer tsiteid=zsiteid[which];
					Intent intent=new Intent(MainActivity.this, NewDayActivity.class);
					Bundle b=new Bundle();
					b.putString("SITE_NAME", zvalues[which]);
					b.putInt("SITE_ID", zsiteid[which]);
					intent.putExtras(b);
					startActivityForResult(intent, 0);
				}
			});

			break;
		case R.id.cmdmainExportCSVData:
			dbr=(new DbHelper(this)).getReadableDatabase();
			//cur=dbr.rawQuery("SELECT DAYENTRY.ID, SITE_NAME + FDATE FROM DAYENTRY,SITES WHERE DAYENTRY.SITE_ID=SITES.ID ORDER BY FDATE DESC, SITE_NAME ASC", null);
			cur=dbr.rawQuery("SELECT ID, SITE_NAME FROM SITES ORDER BY SITE_NAME", null);
			final Integer[] ykeys=new Integer[cur.getCount()];
			final String[] yvalues= new String[cur.getCount()];
			while(cur.moveToNext())
			{
				 ykeys[cur.getPosition()] = cur.getInt(0);
				 yvalues[cur.getPosition()] = cur.getString(1);
			}
			
			Device.ShowListDialog(this, "SELECT SITE", yvalues, false, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					SQLiteDatabase dbr=(new DbHelper(MainActivity.this)).getReadableDatabase();
					String s=ykeys[which].toString();
					Cursor cur=dbr.rawQuery("SELECT FDATE,TP, CP, CHK, FLW, LP , TEMP , MCF , TOTAL,COMMENT  FROM DAYENTRY,SITES WHERE DAYENTRY.SITE_ID=SITES.ID AND SITE_ID=? ORDER BY FDATE DESC LIMIT 500", new String[]{s});
					Export.ExportData(MainActivity.this, cur);
				}
			});
			
			break;
		}
		
	}
}
