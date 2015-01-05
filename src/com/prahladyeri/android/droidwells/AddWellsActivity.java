package com.prahladyeri.android.droidwells;

import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class AddWellsActivity extends ActionBarActivity implements OnClickListener {
	private LinkedHashMap<Integer,String> tanks=new LinkedHashMap<Integer,String>(); //tank ID::number
	private int SITE_ID=-1; //WHETHER WE ARE EDITING AN EXISTING OR ADDING NEW
	private ActionBarActivity self=null;
	//List<String> tanks=new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wells);
		self = this;
		
		((Button)findViewById(R.id.cmdaddwellsAddTank)).setOnClickListener(this);
		((Button)findViewById(R.id.cmdaddwellsSave)).setOnClickListener(this);
		((Button)findViewById(R.id.cmdaddwellsCancel)).setOnClickListener(this);
		tanks.clear();
		
		Bundle b=this.getIntent().getExtras();
		SITE_ID=b.getInt("ID");
		//Device.ShowMessageDialog(this, Integer.toString(b.getInt("ID"))); 
		if (SITE_ID==-1){
			//TODO: PRESENTLY, SITE_ID IS ALWAYS -1 AS EDIT-SITE IS NOT A REQUIREMENT
			
		}
		else {
			//TODO: EDIT-SITE: IMPLEMENT AND TEST THIS CODE IF THIS FEATURE IS REQUIRED
			DbHelper dbHelper=new DbHelper(this);
			SQLiteDatabase db=dbHelper.getWritableDatabase();
			String[] args={Integer.toString(SITE_ID)};
			Cursor cursor= db.rawQuery("SELECT * FROM SITES WHERE ID=", args);
			if (cursor.moveToNext())
			{
				((EditText)findViewById(R.id.txtaddwellsCompanyName)).setText(cursor.getString(1));
				((EditText)findViewById(R.id.txtaddwellsWellSiteName)).setText(cursor.getString(2));
			}
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_wells, menu);
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
	
	private void addTank(String tankNumber){
		View lastcontrol =null;
		Integer maxid=1000;
		if (tanks.size()==0) {
			lastcontrol = findViewById(R.id.cmdaddwellsAddTank);
		}
		else
		{
			maxid=Device.getMaxKey(tanks);
			lastcontrol = findViewById(maxid);
		}
		maxid+=1; //for the new tank;
		tanks.put(maxid, "");
		 RelativeLayout layout=((RelativeLayout)findViewById(R.id.addwells_layout));
		 RelativeLayout.LayoutParams params =null;
		 
		 //create edittext
		EditText ed=new EditText(this);
		 params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		 params.addRule(RelativeLayout.BELOW, lastcontrol.getId());
		 if (maxid==1001) {
			 params.topMargin+=20;
		 }
		 ed.setId(maxid);
		 ed.setLayoutParams(params);
		 //et.setMaxEms(4);
		 //et.setEms(4);
		 //et.setMaxWidth(maxpixels);
		 ed.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
		 ed.setHint("Tank #");//maxid.toString()
		 ed.setText(tankNumber);
		 layout.addView(ed);
		 
		 //create a delete button
		 Button button=new Button(this);
		 params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		 params.addRule(RelativeLayout.RIGHT_OF, maxid);
		 params.addRule(RelativeLayout.ALIGN_TOP, maxid);
		 button.setLayoutParams(params);
		 button.setId(maxid+1000);
		 button.setText("Delete"); //Integer.toString(maxid+1000)
		 button.setOnClickListener(this);
		 layout.addView(button);
	}

	@Override
	public void onClick(View view) {
		int id=view.getId();
		switch(id){
		case R.id.cmdaddwellsAddTank:
			addTank("");
			break;
		case R.id.cmdaddwellsCancel:
			this.finish();
			break;
		case R.id.cmdaddwellsSave:
			//VALIDATION STARTS
			EditText et=null;
			et=(EditText)findViewById(R.id.txtaddwellsCompanyName);
			if (et.getText().toString().length()==0){
				Device.ShowMessageDialog(this, "Company name cannot be blank");
				return;
			}
			
			et=(EditText)findViewById(R.id.txtaddwellsWellSiteName);
			if (et.getText().toString().length()==0){
				Device.ShowMessageDialog(this, "Well site name cannot be blank");
				return;
			}
			
			for (Entry<Integer, String> entry : tanks.entrySet() ){
				et=(EditText)findViewById(entry.getKey());
				String tanknum = et.getText().toString();
				if (tanknum.length()==0) {
					Device.ShowMessageDialog(this, "Tank number not entered for Tank " + Integer.toString(entry.getKey() - 1000));
					return;
				}
				else{
					entry.setValue(et.getText().toString());
					//tanks.put(entry.getKey(), value)
				}
			}
			//VALIDATION ENDS
			
			//ADD SITE TO DATABASE
			DbHelper dbHelper=new DbHelper(this);
			SQLiteDatabase db= dbHelper.getWritableDatabase();
			SQLiteDatabase dbr=dbHelper.getReadableDatabase();
			//db.execSQL("DELETE FROM TANKS WHERE SITE_ID="  );
			EditText et1 = (EditText)findViewById(R.id.txtaddwellsCompanyName);
			EditText et2= (EditText)findViewById(R.id.txtaddwellsWellSiteName);
			final Object[] ovals={
					et1.getText().toString(),
					et2.getText().toString()
			};
			//db.beginTransaction();
			db.execSQL("INSERT INTO SITES(COMPANY_NAME,SITE_NAME) VALUES(?,?); ", ovals);
			Cursor cur= dbr.rawQuery("SELECT last_insert_rowid();", null);
			//Cursor cur= dbr.rawQuery("SELECT max(ID) from SITES;", null);
			//db.endTransaction();
			cur.moveToFirst();
			final Integer siteid= cur.getInt(0);
			Device.ShowMessageDialog(this, siteid.toString());
			//ADD TANKS TO DATABASE
			db.execSQL("DELETE FROM TANKS WHERE SITE_ID="  + siteid );
			
			for (Entry<Integer, String> entry:tanks.entrySet() ){
				String[] tvals={siteid.toString(), entry.getKey().toString(), entry.getValue()};
				db.execSQL("INSERT INTO TANKS(SITE_ID,TANK_VIEW_ID,TANK_NUMBER) VALUES(?,?,?);", tvals);
			}
			//Toast.makeText(this, "Record Saved", Toast.LENGTH_LONG);
			Device.ShowMessageDialog(this, "Site data saved. Would you like to perform the Day Entry for this site?", MessageBoxType.YesNo ,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					self.finish();
					if (which==DialogInterface.BUTTON_POSITIVE)
					{
						final Integer tsiteid=siteid;
						Intent intent=new Intent(AddWellsActivity.this, NewDayActivity.class);
						Bundle b=new Bundle();
						b.putString("SITE_NAME", ovals[1].toString());
						b.putInt("SITE_ID", siteid );
						intent.putExtras(b);
						startActivityForResult(intent, 0);
					}
					else
					{
					}
				}
			});
			break;
		default:
			if (id>=2000)
			{
				//delete button pressed
				//Device.ShowMessageDialog(this, "delete button clicked");
				int tid=id-1000;
				int max= Device.getMaxKey(tanks);
				int min=2000;
				Integer next=0;
				Integer previous=0;
				Integer tcurr=0;
				for(Integer key : tanks.keySet()){
					if (key<min) min=key;
					if (key>tid) {
						next=key; //get the next editText view
						break;
					}
					previous=tcurr;
					tcurr=key;
				}
				//Device.ShowMessageDialog(this, "Previous: " + previous.toString() + "\nNext: " + next.toString());
				//Device.ShowMessageDialog(this, );
				
				tanks.remove(tid);
				 RelativeLayout layout=((RelativeLayout)findViewById(R.id.addwells_layout));
					layout.removeView(findViewById(tid));
					layout.removeView(findViewById(id));
					
					
				//MARK THE BELOW CONTROLS' TOP & ALIGN-TOP POSITIONS.
				if (tid==min){
					if (next==0) break;
					RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					//this was the topmost control, so align with cmdaddwellsAddTank bottom
					 //int topbutton =findViewById(R.id.cmdaddwellsAddTank);
					 params.addRule(RelativeLayout.BELOW, R.id.cmdaddwellsAddTank);
					 findViewById(next).setLayoutParams(params);
				}
				else if (tid==max){
					//this was the bottom most, so no need to do anything
				}
				else{
					RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					//this was the topmost control, so align with cmdaddwellsAddTank bottom
					 //int topbutton =findViewById(R.id.cmdaddwellsAddTank);
					 params.addRule(RelativeLayout.BELOW, previous);
					 findViewById(next).setLayoutParams(params);
				}
			}
			break;
		}
	}
}
