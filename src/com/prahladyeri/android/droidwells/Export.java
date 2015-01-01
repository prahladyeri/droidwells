package com.prahladyeri.android.droidwells;

import java.io.PrintWriter;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.widget.Toast;

public class Export 
{
	private static String GenerateCSV(Cursor cursor){
		String header="";
		String body="";
		String[] columns = cursor.getColumnNames();
		
		for(int i=0;i<columns.length;i++)
		{
			 header += "\"" + columns[i].replace("\"", "\"\"") + "\",";
		}

		while(cursor.moveToNext())
		{
			String row="";
			for(int i=0;i<cursor.getColumnCount();i++)
			{
				row += "\"" + cursor.getString(i).replace("\"", "\"\"") + "\",";
			}
			body += row + "\n";
		}
		
		return header + "\n" + body;
	}
	
	public static void ExportData(Context context, Cursor cursor)
	{
		String location= Environment.getExternalStorageDirectory() + "/droidwells.csv";
		PrintWriter writer=null;
		try {
			writer = new PrintWriter(location);
		} catch (Exception ex) {
			Device.ShowMessageDialog(context, "Error occured: " + ex.toString());
		}
		String s=GenerateCSV(cursor);
		if (s!=null)
			writer.write(s);
		else
			Device.ShowMessageDialog(context, "s is null.");
			
		writer.close();
		
		Toast.makeText(context, "CSV saved at " + location, Toast.LENGTH_LONG).show();
		//((Activity)context).finish();
	}

}
