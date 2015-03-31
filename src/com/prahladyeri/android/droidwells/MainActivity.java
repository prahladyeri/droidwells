package com.prahladyeri.android.droidwells;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
		findViewById(R.id.cmdmainEmailCSVData).setOnClickListener(this);
		findViewById(R.id.cmdmainNewDayData).setOnClickListener(this);
		findViewById(R.id.cmdmainExit).setOnClickListener(this);
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
		case R.id.cmdmainExit:
			Device.ShowMessageDialog(this, "Sure you want to exit " + getText(R.string.app_name), MessageBoxType.YesNo, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which==dialog.BUTTON_POSITIVE){
						MainActivity.this.finish();
					}
				}
			});
			break;
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
							,MessageBoxType.Custom3, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (which==DialogInterface.BUTTON_POSITIVE || which==DialogInterface.BUTTON_NEUTRAL)
									{
										//DELETE THE RECORDS FROM DATABASE
										SQLiteDatabase db=new DbHelper(MainActivity.this).getWritableDatabase();
										if (which==DialogInterface.BUTTON_POSITIVE) {
											//delete all
											db.execSQL("DELETE FROM DAYENTRY_TANKS WHERE DAYENTRY_ID IN (SELECT ID FROM DAYENTRY WHERE SITE_ID=?)", new String[]{tsiteid.toString()});
											db.execSQL("DELETE FROM DAYENTRY WHERE SITE_ID=?", new String[]{tsiteid.toString()});
											db.execSQL("DELETE FROM TANKS WHERE SITE_ID=?", new String[]{tsiteid.toString()});
											db.execSQL("DELETE FROM SITES WHERE ID=?", new String[]{tsiteid.toString()});
										}
										else {
											//delete before today
											String sfdate = Device.sdf.format(new Date());
											db.execSQL("DELETE FROM DAYENTRY_TANKS WHERE DAYENTRY_ID IN (SELECT ID FROM DAYENTRY WHERE FDATE<? AND SITE_ID=?)", new String[]{sfdate, tsiteid.toString()});
											db.execSQL("DELETE FROM DAYENTRY WHERE FDATE<? AND SITE_ID=?", new String[]{sfdate, tsiteid.toString()});
										}
										
										Toast.makeText(MainActivity.this, "Record Deleted successfully.", Toast.LENGTH_LONG).show();
									}
									else {
										//Cancelled, so don't do anything.
									}
								}
							}
							, "Entire site", "Cancel", "Before today");
				}
			});
			break;
		case R.id.cmdmainNewDayData:
			dbr=(new DbHelper(this)).getReadableDatabase();
			cur=dbr.rawQuery("SELECT SITE_NAME || '::' || COMPANY_NAME, ID FROM SITES ORDER BY SITE_NAME", null);
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
					b.putString("SITE_NAME", zvalues[which].split("::")[0]);
					b.putInt("SITE_ID", zsiteid[which]);
					intent.putExtras(b);
					startActivityForResult(intent, 0);
				}
			});

			break;
		case R.id.cmdmainEmailCSVData:
		case R.id.cmdmainExportCSVData:
			dbr=(new DbHelper(this)).getReadableDatabase();
			//cur=dbr.rawQuery("SELECT DAYENTRY.ID, SITE_NAME + FDATE FROM DAYENTRY,SITES WHERE DAYENTRY.SITE_ID=SITES.ID ORDER BY FDATE DESC, SITE_NAME ASC", null);
			cur=dbr.rawQuery("SELECT SITE_ID, FDATE || '::' || COMPANY_NAME || '::' || SITE_NAME, COMPANY_NAME, FDATE FROM DAYENTRY A, SITES B WHERE A.SITE_ID=B.ID ORDER BY SITE_NAME", null);
			final Integer[] ykeys=new Integer[cur.getCount()];
			final String[] yvalues= new String[cur.getCount()];
			//final String[] yfdates= new String[cur.getCount()];
			//final String[] ycompanies= new String[cur.getCount()];
			final String[] fullFileName = {"", ""};
			final Integer viewid=view.getId();
			while(cur.moveToNext())
			{
				 ykeys[cur.getPosition()] = cur.getInt(0);
				 yvalues[cur.getPosition()] = cur.getString(1);
				 //ycompanies[cur.getPosition()] = cur.getString(2);
				 //newdate = new Date(cur.getLong(3));
				 //yfdates[cur.getPosition()] = cur.getString(3); //Device.sdf.format(newdate);
			}
			
			Device.ShowListDialog(this, "SELECT SITE", yvalues, true, new DialogInterface.OnClickListener() {
				private String sfdate = "";
				private ArrayList<String> scompany=new ArrayList<String>();
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					//String scompany = "";
					if (which != Dialog.BUTTON_POSITIVE || Device.CheckedItems.size()==0) return;
					final SQLiteDatabase dbr=(new DbHelper(MainActivity.this)).getReadableDatabase();
					final String[] sites=new String[Device.CheckedItems.size()];//ykeys[which].toString();
					for(int i=0;i<Device.CheckedItems.size();i++) {
						sites[i] = ykeys[Device.CheckedItems.get(i)].toString();
						sfdate = yvalues[Device.CheckedItems.get(i)].split("::")[0];
						//scompany = yvalues[Device.CheckedItems.get(i)].split("::")[1];
						if (!scompany.contains(yvalues[Device.CheckedItems.get(i)].split("::")[1])) {
							scompany.add(yvalues[Device.CheckedItems.get(i)].split("::")[1]);
						}
					}
					final int cnt = Device.CheckedItems.size();
					//Cursor cur=dbr.rawQuery("SELECT FDATE,TP, CP, CHK, FLW, LP , TEMP , MCF , TOTAL,COMMENT  FROM DAYENTRY,SITES WHERE DAYENTRY.SITE_ID=SITES.ID AND SITE_ID=? ORDER BY FDATE DESC LIMIT 500", new String[]{s});
					
					//final Cursor cur=dbr.rawQuery("SELECT COMPANY_NAME, SITE_NAME, FDATE,TP, CP, CHK, FLW, LP , TEMP , MCF , TOTAL,COMMENT  FROM DAYENTRY,SITES WHERE DAYENTRY.SITE_ID=SITES.ID AND SITE_ID in (" + Device.makePlaceholders(Device.CheckedItems.size()) +  ") AND FDATE = '"  + sfdate  + "' ORDER BY FDATE DESC LIMIT 500", sites);
					//AND FDATE = '"  + sfdate  + "' 
					//**final Cursor cur=dbr.rawQuery("SELECT COMPANY_NAME, SITE_NAME, FDATE,TP, CP, CHK, FLW, LP , TEMP , MCF , TOTAL,COMMENT, TOPFT,TOPIN,BTMFT,BTMIN,TANK_NUMBER FROM DAYENTRY_TANKS,DAYENTRY,SITES,TANKS WHERE DAYENTRY_TANKS.DAYENTRY_ID=DAYENTRY.ID AND DAYENTRY_TANKS.TANK_ID=TANKS.ID AND DAYENTRY.SITE_ID=SITES.ID AND FDATE = '"  + sfdate  + "' AND DAYENTRY.SITE_ID in (" + Device.makePlaceholders(Device.CheckedItems.size()) +  ") ORDER BY FDATE DESC LIMIT 500", sites);
					//fullFileName[0] = Export.ExportData(MainActivity.this, cur, ycompanies[0] + "-" + yfdates[0].substring(0, 10));
					//**fullFileName[0] = ""; //The complete path
					//**fullFileName[1] = scompany + "-" + sfdate; //.substring(0, 10); //Just the filename w/o extension
					if (viewid == R.id.cmdmainExportCSVData) {
						//final String[] dirs = Device.getStorageDirectories();
						final String[] dirs = StorageUtils.getStorageList();
						Device.ShowListDialog(MainActivity.this, "Select Location", dirs, false, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								for (int i=0;i<scompany.size();i++) {
									fullFileName[1] = scompany.get(i) + "-"  + sfdate;
									fullFileName[0] = dirs[which] + "/" + fullFileName[1] + ".csv";
									String strsql  = "SELECT COMPANY_NAME, SITE_NAME, FDATE,TP, CP, CHK, FLW, LP , TEMP , MCF , TOTAL,COMMENT, TOPFT,TOPIN,BTMFT,BTMIN,TANK_NUMBER FROM DAYENTRY_TANKS,DAYENTRY,SITES,TANKS WHERE DAYENTRY_TANKS.DAYENTRY_ID=DAYENTRY.ID AND DAYENTRY_TANKS.TANK_ID=TANKS.ID AND DAYENTRY.SITE_ID=SITES.ID AND FDATE = '"  + sfdate  + "' AND DAYENTRY.SITE_ID in (" + Device.makePlaceholders(cnt) +  ") AND SITES.COMPANY_NAME='" + scompany.get(i) + "' ORDER BY FDATE DESC LIMIT 500";
									final Cursor cur=dbr.rawQuery(strsql, sites); //AND COMPANY_NAME='" + scompany.get(i) + "' 
									Export.ExportData(MainActivity.this, cur,fullFileName[0]);
									Toast.makeText(MainActivity.this, "CSV saved at " + fullFileName[0], Toast.LENGTH_LONG).show();
								}
							}
						});
					}
					else { //Email csv data
						//fullFileName[0] = Environment.getExternalStorageDirectory() + "/" + fullFileName[1] + ".csv";
						Device.ShowInputDialog(MainActivity.this, "Email id: ", new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (which==dialog.BUTTON_POSITIVE){
									//Device.ShowMessageDialog(MainActivity.this, Device.input.getText().toString());
									for (int i=0;i<scompany.size();i++) {
										fullFileName[1] = scompany.get(i) + "-"  + sfdate;
										fullFileName[0] = Environment.getExternalStorageDirectory() + "/" + fullFileName[1] + ".csv";
										String strsql  = "SELECT COMPANY_NAME, SITE_NAME, FDATE,TP, CP, CHK, FLW, LP , TEMP , MCF , TOTAL,COMMENT, TOPFT,TOPIN,BTMFT,BTMIN,TANK_NUMBER FROM DAYENTRY_TANKS,DAYENTRY,SITES,TANKS WHERE DAYENTRY_TANKS.DAYENTRY_ID=DAYENTRY.ID AND DAYENTRY_TANKS.TANK_ID=TANKS.ID AND DAYENTRY.SITE_ID=SITES.ID AND FDATE = '"  + sfdate  + "' AND DAYENTRY.SITE_ID in (" + Device.makePlaceholders(cnt) +  ") AND SITES.COMPANY_NAME='" + scompany.get(i) + "' ORDER BY FDATE DESC LIMIT 500";
										final Cursor cur=dbr.rawQuery(strsql, sites);
										Export.ExportData(MainActivity.this, cur,fullFileName[0]);

										sendEmail(Device.input.getText().toString(), fullFileName[0]);
									}
								}
							}
						});
					}
				}
			});
			
			break;
		}
	}
	
	private void sendEmail(String emailaddress, String fullFileName) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		
		i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fullFileName));
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailaddress});
		i.putExtra(Intent.EXTRA_SUBJECT, "Automatic CSV Generated by DROIDWELLS app");
		i.putExtra(Intent.EXTRA_TEXT   , "Good Day, \nPFA the automatically generated CSV report.\nCheers,\nDROIDWELLS app.");
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
}
