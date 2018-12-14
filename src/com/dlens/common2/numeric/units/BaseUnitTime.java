package com.dlens.common2.numeric.units;

/**
 * The base units available from the time context.
 * @author wjadams
 *
 */
public enum BaseUnitTime implements BaseUnitInterface {
	Second,
	Minute,
	Hour,
	Day,
	Week,
	Month,
	Year,
	Decade;
	final static BaseUnitTime StandardUnit=Hour;
	@Override
	public UnitType getUnitType() {
		return UnitType.Time;
	}

	@Override
	public int toInt() {
		switch (this) 
		{
		case Second:
			return 0;
		case Minute:
			return 1;
		case Hour:
			return 2;
		case Day:
			return 3;
		case Week:
			return 4;
		case Month:
			return 5;
		case Year:
			return 6;
		case Decade:
			return 7;
		default:
			return -1;
		}
	}

	@Override
	public BaseUnitTime fromInt(int val) {
		switch (val) {
		case 0:
			return Second;
		case 1:
			return Minute;
		case 2:
			return Hour;
		case 3:
			return Day;
		case 4:
			return Week;
		case 5:
			return Month;
		case 6:
			return Year;
		case 7:
			return Decade;
		default:
			throw new IllegalArgumentException("Unknown Time base unit "+val);
		}
	}

	@Override
	public BaseUnitInterface getStandardUnit() {
		return StandardUnit;
	}

}
