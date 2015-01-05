package com.prahladyeri.android.droidwells;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.support.v7.app.ActionBarActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class NewDayTanksActivity extends ActionBarActivity implements android.view.View.OnClickListener {
	private HashMap<Integer, int[]> TANKS = null;
	private int SITE_ID = 0;
	private String[] FIELDS = new String[]{"TOP", "BTM"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_day_tanks);
		((Button)findViewById(R.id.cmdnewdaytanksAdd)).setOnClickListener(this);
		
		Bundle b= this.getIntent().getExtras();
		//Object TANKS  =  b.getSerializable("TANKS");
		//HashMap<Integer, Integer[]> TANKS  = (HashMap<Integer, Integer[]>) b.getSerializable("TANK");
		//HashMap<Integer, Integer[]> TANKS  = (HashMap<Integer, Integer[]>) this.getIntent().getSerializableExtra("TANK");
		this.TANKS =   (HashMap<Integer, int[]>) b.getSerializable("TANKS");
		this.SITE_ID  = b.getInt("SITE_ID");
		if (TANKS.keySet().size()==0) {
			SQLiteDatabase db=(new DbHelper(this)).getReadableDatabase();
			Cursor cursor= db.rawQuery("SELECT ID, TANK_NUMBER FROM TANKS WHERE SITE_ID=?",new String[] {Integer.toString(SITE_ID)});
			final int[] tankids=new int[cursor.getCount()];
			final String[] tanknos=new String[cursor.getCount()];
			while(cursor.moveToNext())
			{
				tankids[cursor.getPosition()] = cursor.getInt(0);
				tanknos[cursor.getPosition()] = cursor.getString(1);
			}
		}
		
		for (Entry<Integer, int[]> tank :  this.TANKS.entrySet())
		{
			//Device.ShowMessageDialog(this, tank.getKey().toString());
			//TODO: Add views for each of existing tanks
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_day_tanks, menu);
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
		switch(view.getId()){
		case R.id.cmdnewdaytanksAdd:
			SQLiteDatabase db=(new DbHelper(this)).getReadableDatabase();
			Cursor cursor= db.rawQuery("SELECT ID, TANK_NUMBER FROM TANKS WHERE SITE_ID=?",new String[] {Integer.toString(SITE_ID)});
			final int[] tankids=new int[cursor.getCount()];
			final String[] tanknos=new String[cursor.getCount()];
			while(cursor.moveToNext())
			{
				tankids[cursor.getPosition()] = cursor.getInt(0);
				tanknos[cursor.getPosition()] = cursor.getString(1);
			}
			
			Device.ShowListDialog(this, "Select Tank", tanknos, true, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for(int i : Device.CheckedItems) {
						Device.ShowMessageDialog(NewDayTanksActivity.this, tanknos[i]);
					}
				}
			});
		
		}
	}
}
