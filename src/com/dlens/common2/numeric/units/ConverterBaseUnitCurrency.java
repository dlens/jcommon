package com.dlens.common2.numeric.units;

public class ConverterBaseUnitCurrency implements ConverterBaseUnitInterface<BaseUnitCurrency>
{
	CurrencyTable currencyTable=new CurrencyTable();
	@Override
	public double fromStandardUnitDouble(double size, BaseUnitCurrency toBase) {
		// TODO Auto-generated method stub
		return size*currencyTable.oneStandardUnitTo(toBase);
	}

	@Override
	public double toStandardUnitDouble(double size, BaseUnitCurrency fromBase) {
		// TODO Auto-generated method stub
		return size/currencyTable.oneStandardUnitTo(fromBase);
	}

}
