package com.dlens.common2.numeric.units;

public class ConverterBaseUnitWeight implements ConverterBaseUnitInterface<BaseUnitWeight>
{

	@Override
	public double fromStandardUnitDouble(double size, BaseUnitWeight toBase) {
		//The standard unit is pounds.  For this we convert size lbs to toBase.
		switch (toBase) {
		case Ounce:
			return 16*size;
		case Pound:
			return size;
		case USTon:
			return size/2000.0;
		case BrittishTon:
			return size/2240.0;
		case MetricTon:
			return size/2204.0;
		case Gram:
			return size*453.59237;
		default:
			throw new IllegalArgumentException("Unable to convert to "+toBase);
		}
	}

	@Override
	public double toStandardUnitDouble(double size, BaseUnitWeight fromBase) {
		//Converts size fromBase to the standard base (pounds)
		return size / fromStandardUnitDouble(1.0, fromBase);
	}

}
