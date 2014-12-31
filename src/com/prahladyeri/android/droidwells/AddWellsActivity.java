package com.prahladyeri.android.droidwells;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;

public class AddWellsActivity extends ActionBarActivity implements OnClickListener {
	private HashMap<Integer,String> tanks=new HashMap<Integer,String>(); //tank ID::number
	
	//List<String> tanks=new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wells);
		
		((Button)findViewById(R.id.cmdaddwellsAddTank)).setOnClickListener(this);
		((Button)findViewById(R.id.cmdaddwellsSave)).setOnClickListener(this);
		
		tanks.clear();
		//TODO: Get existing values and tanks from database
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

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.cmdaddwellsAddTank:
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
			EditText et=new EditText(this);
			 params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			 params.addRule(RelativeLayout.BELOW, lastcontrol.getId());
			 if (maxid==1001) {
				 params.topMargin+=20;
			 }
			 et.setId(maxid);
			 et.setLayoutParams(params);
			 et.setHint(maxid.toString());//("Tank #");
			 layout.addView(et);
			 
			 //create a delete button
			 Button button=new Button(this);
			 params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			 params.addRule(RelativeLayout.RIGHT_OF, maxid);
			 params.addRule(RelativeLayout.ALIGN_TOP, maxid);
			 button.setLayoutParams(params);
			 button.setId(maxid+1000);
			 button.setText("Delete");
			 layout.addView(button);
			break;
		case R.id.cmdaddwellsSave:
			Device.ShowMessageDialog(this, "Save button clicked");
			break;
		}
	}
}
