package com.prahladyeri.android.droidwells;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalInputFilter implements InputFilter 
{
	Pattern mPattern;
	
	public DecimalInputFilter(int digitsBeforeZero,int digitsAfterZero) {
	    mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
	        Matcher matcher=mPattern.matcher(dest);       
	        if(!matcher.matches())
	            return "";
	        return null;
	}
}