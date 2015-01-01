package com.prahladyeri.android.droidwells;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

enum MessageBoxType
{
	OKOnly,
	OkCancel,
	YesNo
}

class Device 
{
	public static ArrayList<Integer> CheckedItems;
	
	public static int getMaxKey(LinkedHashMap<Integer, String> dictionary)
	{
		int maxid=0;
		for(Entry<Integer, String> entry : dictionary.entrySet())
		{
			int t=(Integer)entry.getKey();
			if (t > maxid) maxid=t;
		}
		return maxid;
	}
	
	public static boolean CopyFile(String sourceFile, String destFile)
	{
		try 
		{
	        //File f2 = new File(destFile);
	        InputStream in =  new FileInputStream(sourceFile);
	        OutputStream out = new FileOutputStream(destFile);

	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
	        //System.out.println("File copied.");
	        return true;
	    } 
		catch (Exception e) 
		{
	        //Device.ShowMessageDialog(context, ""));
			return false;
	    }		
	}
	
	public static void ShowMessageDialog(Context context, String message)
	{
		ShowDialog(context,message,MessageBoxType.OKOnly,new String[]{},false, null,null);
	}

	public static void ShowMessageDialog(Context context, String message, MessageBoxType type , OnClickListener listener)
	{
		ShowDialog(context,message,type,new String[]{},false, listener,null);
	}
	
	public static void ShowListDialog(Context context, String message, String[] listItems, boolean isMultiChoice, OnClickListener listener)
	{
		if (isMultiChoice)
			ShowDialog(context, message, MessageBoxType.OkCancel , listItems, isMultiChoice, listener,null);
		else
			ShowDialog(context, message, MessageBoxType.OKOnly , listItems, isMultiChoice, null,listener);
	}
	
	public static void ShowDateDialog(Context context,String message,OnDateSetListener listener)
	{
		Calendar c=Calendar.getInstance();
		int y=c.get(Calendar.YEAR);
		int m=c.get(Calendar.MONTH);
		int d=c.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog dlg=new DatePickerDialog(context, listener, y, m, d);
		//dlg.setOnCancelListener(cancelListener);
		//dlg.setMessage(message);
		dlg.setTitle(message);
		dlg.show();
	}
	
	private static void ShowDialog(Context context, String message, MessageBoxType type , String[] listItems, boolean isMultiChoice, OnClickListener listener,OnClickListener selectedItemListener)
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		
		if (listItems.length>0 && isMultiChoice==false)
		{
			CheckedItems=new ArrayList<Integer>();//won't be used in this case.
			builder.setTitle(message);
			
			builder.setItems(listItems, selectedItemListener);
		}
		else if (listItems.length>0 && isMultiChoice==true)
		{
			CheckedItems=new ArrayList<Integer>();
			builder.setTitle(message);
			
			builder.setMultiChoiceItems(listItems, null, new OnMultiChoiceClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which, boolean checked) 
				{
					if (checked)
						CheckedItems.add(which);
					else
					{
						if (CheckedItems.contains(which))
							CheckedItems.remove(which);
					}
				}
			});
		}
		else
		{
			builder.setTitle("DROID WELLS");
			builder.setMessage(message);
		}
		
		if (listItems.length==0 || isMultiChoice)
		{
			switch(type)
			{
			case OKOnly:
				builder.setPositiveButton("OK",listener);
				break;
			case OkCancel:
				builder.setPositiveButton("OK",listener);
				builder.setNegativeButton("Cancel",listener);
				break;
			case YesNo:
				builder.setPositiveButton("Yes",listener);
				builder.setNegativeButton("No",listener);
				break;
			}			
		}

		builder.create().show();
	}
	
	public static String getDeviceIdTm(Context context)
	{
		TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	
	public static String getDeviceIdAndroid(Context context)
	{
		return Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
	}
	
	public static String getDeviceIdPseudo(Context context)
	{
		String tstr="";
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO)
			tstr+= Build.SERIAL;
		tstr += "::" +
				(Build.PRODUCT.length() % 10) + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) +
				(Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) +
				(Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10);

		return tstr;
	}
	
	public static String getDeviceIdUnique(Context context)
	{
		try{
			String a = getDeviceIdTm(context);
			String b = getDeviceIdAndroid(context);
			String c = getDeviceIdPseudo(context);
			
			if (a!=null && a.length()>0 && a.replace("0", "").length()>0) 
				return a;
			else if (b!=null && b.length()>0 && b.equals("9774d56d682e549c")==false) 
				return b;
			else if (c!=null && c.length()>0) 
				return c;
			else
				return "";
		}
		catch(Exception ex)
		{
			return "";
		}
	}
	
}
