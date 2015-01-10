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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;


enum MessageBoxType
{
	OKOnly,
	OkCancel,
	YesNo
}

class Device 
{
	public static ArrayList<Integer> CheckedItems;
	public static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	public static EditText input=null;
	
	/**
	 * Raturns all available SD-Cards in the system (include emulated)
	 *
	 * Source: Dmitry Lozenko (http://stackoverflow.com/questions/11281010/how-can-i-get-external-sd-card-path-for-android-4-0)
	 * Warning: Hack! Based on Android source code of version 4.3 (API 18)
	 * Because there is no standart way to get it.
	 * TODO: Test on future Android versions 4.4+
	 *
	 * @return paths to all available SD-Cards in the system (include emulated)
	 */
	public static String[] getStorageDirectories()
	{
		final Pattern DIR_SEPORATOR = Pattern.compile("/");
	    // Final set of paths
	    final Set<String> rv = new HashSet<String>();
	    // Primary physical SD-CARD (not emulated)
	    final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
	    // All Secondary SD-CARDs (all exclude primary) separated by ":"
	    final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
	    // Primary emulated SD-CARD
	    final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
	    if(TextUtils.isEmpty(rawEmulatedStorageTarget))
	    {
	        // Device has physical external storage; use plain paths.
	        if(TextUtils.isEmpty(rawExternalStorage))
	        {
	            // EXTERNAL_STORAGE undefined; falling back to default.
	            rv.add("/storage/sdcard0");
	        }
	        else
	        {
	            rv.add(rawExternalStorage);
	        }
	    }
	    else
	    {
	        // Device has emulated storage; external storage paths should have
	        // userId burned into them.
	        final String rawUserId;
	        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
	        {
	            rawUserId = "";
	        }
	        else
	        {
	            final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
	            final String[] folders = DIR_SEPORATOR.split(path);
	            final String lastFolder = folders[folders.length - 1];
	            boolean isDigit = false;
	            try
	            {
	                Integer.valueOf(lastFolder);
	                isDigit = true;
	            }
	            catch(NumberFormatException ignored)
	            {
	            }
	            rawUserId = isDigit ? lastFolder : "";
	        }
	        // /storage/emulated/0[1,2,...]
	        if(TextUtils.isEmpty(rawUserId))
	        {
	            rv.add(rawEmulatedStorageTarget);
	        }
	        else
	        {
	            rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
	        }
	    }
	    // Add all secondary storages
	    if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
	    {
	        // All Secondary SD-CARDs splited into array
	        final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
	        Collections.addAll(rv, rawSecondaryStorages);
	    }
	    return rv.toArray(new String[rv.size()]);
	}
	
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
		ShowDialog(context,message,MessageBoxType.OKOnly,null,false, null,null,false);
	}
	
	public static void ShowInputDialog(Context context, String message, OnClickListener listener)
	{
		ShowDialog(context,message,MessageBoxType.OKOnly, null ,false, listener,null,true);
	}

	public static void ShowMessageDialog(Context context, String message, MessageBoxType type , OnClickListener listener)
	{
		ShowDialog(context,message,type, null ,false, listener,null, false);
	}
	
	public static void ShowListDialog(Context context, String message, String[] listItems, boolean isMultiChoice, OnClickListener listener)
	{
		if (isMultiChoice)
			ShowDialog(context, message, MessageBoxType.OkCancel , listItems, isMultiChoice, listener,null, false);
		else
			ShowDialog(context, message, MessageBoxType.OKOnly , listItems, isMultiChoice, null,listener, false);
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
	
	private static void ShowDialog(Context context, String message, MessageBoxType type , String[] listItems, boolean isMultiChoice, OnClickListener listener,OnClickListener selectedItemListener, boolean inputBox)
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		
		if (listItems != null && isMultiChoice==false)
		{
			CheckedItems=new ArrayList<Integer>();//won't be used in this case.
			if (listItems.length==0) {
				builder.setTitle("No records found.");
				type = MessageBoxType.OKOnly;
			}
			else
				builder.setTitle(message);
			
			builder.setItems(listItems, selectedItemListener);
		}
		else if (listItems != null && isMultiChoice==true)
		{
			CheckedItems=new ArrayList<Integer>();
			if (listItems.length==0) {
				builder.setTitle("No records found.");
				type = MessageBoxType.OKOnly;
			}
			else
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
			builder.setTitle(R.string.app_name); //"DROID WELLS"
			if (!inputBox) {
				builder.setMessage(message);
			}
			else {
				input=new EditText(context);
				//et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				builder.setView(input);
				builder.setMessage(message);
			}
		}
		
		if (listItems ==null || listItems.length==0 || isMultiChoice)
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
	
	public static String makePlaceholders(int len) {
	    if (len < 1) {
	        // It will lead to an invalid query anyway ..
	        throw new RuntimeException("No placeholders");
	    } else {
	        StringBuilder sb = new StringBuilder(len * 2 - 1);
	        sb.append("?");
	        for (int i = 1; i < len; i++) {
	            sb.append(",?");
	        }
	        return sb.toString();
	    }
	}
	
}

