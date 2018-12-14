package com.dlens.common2.numeric.units;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * This is the context of when a currency is supposed to have existed.
 * It is useful for telling how much inflation has changed the value of the
 * money, or for correctly converting between currencies.  For instance $1.00
 * is worth a different amount of Euros today versus six months ago.  Essentially
 * context is a date.  However, at some point in the future we may wish to cache the
 * conversion amounts in here, and do other fancy things, thus I am making it a separate
 * class from a simple java date object.
 * @author Bill Adams
 *
 */
public class CurrencyContext {
	Date date;
	public CurrencyContext() {
		date=new Date();
	}
	
	public CurrencyContext(Date theDate) {
		date=theDate;
	}
	
	public CurrencyContext(String dateFormatString) throws ParseException {
		DateFormat parser=DateFormat.getDateInstance();
		date=parser.parse(dateFormatString);
	}
}
