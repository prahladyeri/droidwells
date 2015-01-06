package com.prahladyeri.android.droidwells;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewDayTanksActivity extends ActionBarActivity implements android.view.View.OnClickListener {
	private HashMap<Integer, int[]> TANKS = null;
	private int SITE_ID = 0;
	private String[] FIELDS = new String[]{"TOP", "BTM"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_day_tanks);
		//((Button)findViewById(R.id.cmdnewdaytanksAdd)).setOnClickListener(this);
		
		Bundle b= this.getIntent().getExtras();
		//Object TANKS  =  b.getSerializable("TANKS");
		//HashMap<Integer, Integer[]> TANKS  = (HashMap<Integer, Integer[]>) b.getSerializable("TANK");
		//HashMap<Integer, Integer[]> TANKS  = (HashMap<Integer, Integer[]>) this.getIntent().getSerializableExtra("TANK");
		this.TANKS =   (HashMap<Integer, int[]>) b.getSerializable("TANKS");
		this.SITE_ID  = b.getInt("SITE_ID");
		if (true) { //add placeholders for all tanks //(TANKS.keySet().size()==0)
			SQLiteDatabase db=(new DbHelper(this)).getReadableDatabase();
			Cursor cursor= db.rawQuery("SELECT ID, TANK_NUMBER FROM TANKS WHERE SITE_ID=?",new String[] {Integer.toString(SITE_ID)});
			//final int[] tankids=new int[cursor.getCount()];
			//final String[] tanknos=new String[cursor.getCount()];
			int lastcontrol  = 0;
			RelativeLayout relativelayout =  (RelativeLayout)findViewById(R.id.newdaytanks_layout);
			RelativeLayout.LayoutParams params = null;
			while(cursor.moveToNext())
			{
				//Add a textview
				params =new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				TextView tv=new TextView(this);
				tv.setId(1000 + cursor.getInt(0)); //ID 1001..2..3
				tv.setText(cursor.getString(1)); //TANKNO
				if (lastcontrol!=0)
					params.addRule(RelativeLayout.BELOW, lastcontrol);
				lastcontrol = tv.getId();
				tv.setLayoutParams(params);
				relativelayout.addView(tv);
				Integer tankid = cursor.getInt(0);
				if (!TANKS.containsKey(tankid))
					this.TANKS.put(tankid, new int[FIELDS.length]);
				
				//Add editext for each individual numeric fields
				for (int i=0;i<FIELDS.length;i++) {
					params =new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.RIGHT_OF, lastcontrol);
					params.addRule(RelativeLayout.ALIGN_TOP, tv.getId());
					EditText et=new EditText(this);
					et.setId( ((i+1)*1000) + tv.getId() ); //2001..3001..4001
					et.setInputType(InputType.TYPE_CLASS_NUMBER);
					et.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
				
					//et.setHint(Integer.toString(et.getId()));
					et.setHint(FIELDS[i]);
					if (TANKS.get(tankid)[i] > 0) et.setText(Integer.toString(TANKS.get(tankid)[i])); //et.setText(); //TANKNO
					
					et.setLayoutParams(params);
					
					relativelayout.addView(et);
					lastcontrol = et.getId();
				}
			}
			
			//Finally add a command button
			params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, lastcontrol);
			Button btn=new Button(this);
			btn.setLayoutParams(params);
			btn.setId(1);
			btn.setOnClickListener(this);
			btn.setText("Save and Return");
			relativelayout.addView(btn);
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
		case 1:
			for (Integer key : TANKS.keySet())
			{
				int[] fieldValues=new int[FIELDS.length];
				for (int j=0;j<FIELDS.length;j++) {
					//1001	2001	3001
					Integer viewid = (1000 * (j+1)) + (1000 + key);
					EditText et = (EditText)findViewById(viewid);
					String strval = et.getText().toString();
					fieldValues[j] = (strval.length()==0 ? 0 : Integer.parseInt(strval));
					//Device.ShowMessageDialog(this, viewid.toString() + "::" + strval );
				}
				this.TANKS.put(key, fieldValues);
				
			}
			
			Intent intent=new Intent();
			Bundle b=new Bundle();
			b.putSerializable("TANKS", this.TANKS);
			intent.putExtras(b);
			setResult(Activity.RESULT_OK, intent);
			this.finish();
		}
	}
}
