package com.dlens.common2.numeric.units;

import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;

/**
 * This class stores a lookup table amongst all possible currencies, including
 * our own internal use only currency.  It contains a currency context telling
 * when the currency table was created (not actually created, but the context
 * for which the currency table makes sense, i.e. June 2008).
 * @author Bill Adams
 *
 */
public class CurrencyTable {
	private CurrencyContext context;
	private Object[][] standardValues={
			{BaseUnitCurrency.USCent, new Double(100.0)},
			{BaseUnitCurrency.USDollar, new Double(1.0)},
			{BaseUnitCurrency.Euro, new Double(0.75)},
			{BaseUnitCurrency.PoundSterling, new Double(0.80)},
			{BaseUnitCurrency.InternalUseOnly, new Double(1.0)}
	};
	Hashtable<BaseUnitCurrency,Double> currencyTable=new Hashtable<BaseUnitCurrency, Double>();
	public CurrencyTable() {
		context=new CurrencyContext();
		initTable();
	}
	
	public CurrencyTable(Date date) {
		context=new CurrencyContext(date);
		initTable();
	}
	
	public CurrencyTable(String dateFormatString) throws ParseException {
		context=new CurrencyContext(dateFormatString);
		initTable();
	}
	
	private void initTable() {
		//FIXME
		//This should be grabbing currency tables for the correct context.
		currencyTable=standardTable();
	}
	
	private Hashtable<BaseUnitCurrency,Double> arrayToHash(Object[][] vals) {
		Hashtable<BaseUnitCurrency,Double>rval=new Hashtable<BaseUnitCurrency, Double>();
		for(int i=0; i<vals.length; i++) {
			BaseUnitCurrency key=(BaseUnitCurrency)vals[i][0];
			Double val=(Double)vals[i][1];
			rval.put(key, val);
		}
		return rval;
	}
	
	private Hashtable<BaseUnitCurrency,Double> standardTable() {
		return arrayToHash(standardValues);
	}
	
	public double oneStandardUnitTo(BaseUnitCurrency toCurrency) {
		Double val=currencyTable.get(toCurrency);
		if (val==null)
			throw new IllegalArgumentException("Could not convert to "+toCurrency+" sorry.");
		return val;
	}
}
