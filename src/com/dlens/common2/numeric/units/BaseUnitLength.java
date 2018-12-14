package com.dlens.common2.numeric.units;

/**
 * The base units for length
 * @author Bill Adams
 *
 */
public enum BaseUnitLength implements BaseUnitInterface {
	Inch,
	Foot,
	Yard,
	Mile,
	Meter;

	@Override
	public UnitType getUnitType() {
		return UnitType.Length;
	}

	@Override
	public int toInt() {
		switch (this) {
		case Inch:
			return 0;
		case Foot:
			return 1;
		case Yard:
			return 2;
		case Mile:
			return 3;
		case Meter:
			return 4;
		default:
			return -1;
		}
	}

	@Override
	public BaseUnitLength fromInt(int val) {
		switch (val) {
		case 0:
			return Inch;
		case 1:
			return Foot;
		case 2:
			return Yard;
		case 3:
			return Mile;
		case 4:
			return Meter;
		default:
			throw new IllegalArgumentException("Unknown length base unit "
					+ val);
		}
	}

	@Override
	public BaseUnitInterface getStandardUnit() {
		return Meter;
	}

}
