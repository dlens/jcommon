package com.dlens.common2.numeric.units;

public enum UnitType {
	Time,
	Length,
	Volume,
	Unitless,
	Angle,
	Currency,
	Temperature,
	Weight;
	
	public int toUniqueInt() {
		switch (this) {
		case Time:
			return 0;
		case Length:
			return 1;
		case Volume:
			return 2;
		case Unitless:
			return 3;
		case Angle:
			return 4;
		case Currency:
			return 5;
		case Temperature:
			return 6;
		case Weight:
			return 7;
		default:
			throw new IllegalArgumentException("Unable to convert to unique int, internal error.");
		}
	}
	
	public static UnitType fromInt(int val) {
		switch (val) {
		case 0:
			return Time;
		case 1:
			return Length;
		case 2:
			return Volume;
		case 3:
			return Unitless;
		case 4:
			return Angle;
		case 5:
			return Currency;
		case 6:
			return Temperature;
		case 7:
			return Weight;
		default:
			throw new IllegalArgumentException("Unknown unit type " + val);
		}
	}
	
	public ConverterBaseUnitInterface baseUnitConverter() {
		switch (this) {
		case Time:
			return new ConverterBaseUnitTime();
		case Temperature:
			return new ConverterBaseUnitTemperature();
		case Length:
			return new ConverterBaseUnitLength();
		case Weight:
			return new ConverterBaseUnitWeight();
		case Volume:
			return new ConverterBaseUnitVolume();
		case Currency:
			return new ConverterBaseUnitCurrency();
		default:
			throw new IllegalArgumentException("I cannot convert " + this
					+ " at this time.");
		}
	}
	
}
