package com.dlens.common2.numeric.units;

/**
 * 
 * @author Bill Adams
 *
 */
public enum BaseUnitWeight implements BaseUnitInterface {
	Ounce,
	Pound,
	USTon,
	BrittishTon,
	MetricTon,
	Gram;

	@Override
	public BaseUnitInterface fromInt(int val) {
		switch (val) {
		case 0:
			return Ounce;
		case 1:
			return Pound;
		case 2:
			return USTon;
		case 3:
			return BrittishTon;
		case 4:
			return MetricTon;
		case 5:
			return Gram;
		default:
			throw new IllegalArgumentException("Unable to turn integer "+val+" to a Weight unit.");
		}
	}

	@Override
	public BaseUnitInterface getStandardUnit() {
		return Pound;		
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.Weight;
	}

	@Override
	public int toInt() {
		switch (this) {
		case Ounce:
			return 0;
		case Pound:
			return 1;
		case USTon:
			return 2;
		case BrittishTon:
			return 3;
		case MetricTon:
			return 4;
		case Gram:
			return 5;
		default:
			throw new IllegalArgumentException("Cannot turn into a an int.");
		}
	}

}
