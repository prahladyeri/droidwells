package com.prahladyeri.android.droidwells;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.SparseIntArray;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewDayActivity extends ActionBarActivity implements OnClickListener {
	private int SITE_ID=0;
	private String[] FIELDS = {"TP", "CP", "CHK", "FLW", "DIFF", "LP" , "TEMP" , "MCF" , "TOTAL" };
	private HashMap<Integer, int[]> TANKS = new HashMap<Integer, int[]>(); //tank_id::TOP,BTM
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		Device.ShowMessageDialog(this, "onActivityResult()");
		Bundle b=data.getExtras();
		this.TANKS = (HashMap<Integer, int[]>) b.getSerializable("TANKS");
		for (Integer key : TANKS.keySet()){
//			Device.ShowMessageDialog(this, key.toString() + "::" + Integer.toString(TANKS.get(key)[0])
//					+ "::" + Integer.toString(TANKS.get(key)[1])
//					);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_day);
		((Button)findViewById(R.id.cmdnewdaySave)).setOnClickListener(this);
		((Button)findViewById(R.id.cmdnewdayCancel)).setOnClickListener(this);
		((Button)findViewById(R.id.cmdnewdayTanks)).setOnClickListener(this);
		
		Bundle b= this.getIntent().getExtras();
		this.SITE_ID= b.getInt("SITE_ID");
		String sitename = b.getString("SITE_NAME");
		((TextView)findViewById(R.id.lblnewdayDate)).setText(Device.sdf.format(new Date()));
		((TextView)findViewById(R.id.lblnewdaySiteName)).setText("Site: " + sitename);
		//SQLiteDatabase dbr=new DbHelper(this).getReadableDatabase();
		//dbr.rawQuery("SELECT * FROM DAYENTRY", selectionArgs)
		int lastcontrol = R.id.lblnewdaySiteName;
		RelativeLayout layout=((RelativeLayout)findViewById(R.id.newdayLayout));
		RelativeLayout.LayoutParams params = null;
		EditText ed=null;
		for(int i=0;i<this.FIELDS.length;i++)
		{
			ed=new EditText(this);
			ed.setInputType(InputType.TYPE_CLASS_NUMBER);
			int slen = (this.FIELDS[i] == "TOTAL" ? 8 : 4);
			ed.setFilters(new InputFilter[] {new InputFilter.LengthFilter(slen)});
			ed.setHint(this.FIELDS[i]);
			ed.setId(1000 + i);
			
			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, lastcontrol);
			params.topMargin = 0;
			lastcontrol = ed.getId();
			ed.setLayoutParams(params);
			
			layout.addView(ed);
			//ed.animate();
		}

		//Comment field
		ed=new EditText(this);
		ed.setHint("Comment");
		ed.setId(1000 + FIELDS.length);
		
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, lastcontrol);
		params.topMargin = 10;
		lastcontrol = ed.getId();
		ed.setLayoutParams(params);
		
		layout.addView(ed);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_day, menu);
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
	public void onClick(View view) 
	{
		switch(view.getId()){
		case R.id.cmdnewdayTanks:
			//TANKS.put(1234, new int[] {10,20});
			
			Intent intent=new Intent(this, NewDayTanksActivity.class);
			Bundle b=new Bundle();
			b.putSerializable("TANKS", this.TANKS);
			//b.putIntArray("TANKS", TANKS.get(1234));
			b.putInt("SITE_ID", this.SITE_ID);
			intent.putExtras(b);
			startActivityForResult(intent, 0);
			break;
		case R.id.cmdnewdaySave:
			//Save data to dayentry table
			Date newdate=null;
			try {
				newdate = Device.sdf.parse( Device.sdf.format(new Date()) );
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Object[] values=new Object[12]; // siteid + fdate + fields(9) + comment
			values[0] = this.SITE_ID;
			values[1] = newdate;
			for(int i=0;i<this.FIELDS.length;i++) {
				Object tobj = ((EditText)findViewById(1000 + i)).getText();
				if (tobj.toString().length()==0)
				{
					Device.ShowMessageDialog(this, "Value not entered for Parameter " +  this.FIELDS[i]);
					return;
				}
				values[2 + i] = tobj;
			}
			values[2 + FIELDS.length]=((EditText)findViewById(1000 + FIELDS.length)).getText();  //10
					
			SQLiteDatabase db=new DbHelper(this).getWritableDatabase();
			SQLiteDatabase dbr=new DbHelper(this).getReadableDatabase();
			
			db.execSQL("DELETE FROM DAYENTRY_TANKS WHERE DAYENTRY_ID IN (SELECT ID FROM DAYENTRY WHERE SITE_ID=? AND FDATE=?)",new Object[] {this.SITE_ID, newdate});
			db.execSQL("DELETE FROM DAYENTRY WHERE SITE_ID=? AND FDATE=?",new Object[] {this.SITE_ID, newdate});
			
			db.execSQL("INSERT INTO DAYENTRY(SITE_ID , FDATE , TP , CP , CHK , FLW , DIFF , LP , TEMP , MCF , TOTAL , COMMENT)" + 
			" VALUES(?,?,?,?,?,?,?,?,?,?,?,?)", values);
			Cursor cur= dbr.rawQuery("SELECT last_insert_rowid();", null);
			cur.moveToFirst();
		 	Integer id= cur.getInt(0);
		 	for (Integer key : TANKS.keySet()){
		 		db.execSQL("INSERT INTO DAYENTRY_TANKS(DAYENTRY_ID,TANK_ID,TOP,BTM) VALUES (?,?,?,?);", 
		 				new String[]{id.toString(), key.toString(), Integer.toString(TANKS.get(key)[0]), Integer.toString(TANKS.get(key)[1])});
		 	}

			
			Toast.makeText(this, "Record saved", Toast.LENGTH_LONG).show();
			this.finish();
			break;
		case R.id.cmdnewdayCancel:
			this.finish();
			break;
		}
	}
}
