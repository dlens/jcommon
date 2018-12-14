package com.dlens.common2.numeric.units;

/**
 * All volume base units.
 * @author wjadams
 *
 */
public enum BaseUnitVolume implements BaseUnitInterface {
	Ounce,
	Teaspoon,
	Tablespoon,
	Cup,
	//My personal favorite
	Pint,
	Quart,
	Gallon,
	Liter,
	//The cubic units (all are cubic I know)
	CubicCentimeter,
	CubicMeter,
	CubicInch,
	CubicFoot,
	CubicYard;

	@Override
	public UnitType getUnitType() {
		return UnitType.Volume;
	}

	@Override
	public int toInt() {
		switch (this) {
		case Ounce:
			return 0;
		case Teaspoon:
			return 1;
		case Tablespoon:
			return 2;
		case Cup:
			return 3;
		case Pint:
			return 4;
		case Quart:
			return 5;
		case Gallon:
			return 6;
		case Liter:
			return 7;
		case CubicCentimeter:
			return 8;
		case CubicMeter:
			return 9;
		case CubicInch:
			return 10;
		case CubicFoot:
			return 11;
		case CubicYard:
			return 12;
		default:
			return -1;
		}
	}

	@Override
	public BaseUnitVolume fromInt(int val) {
		switch (val) {
		case 0:
			return Ounce;
		case 1:
			return Teaspoon;
		case 2:
			return Tablespoon;
		case 3:
			return Cup;
		case 4:
			return Pint;
		case 5:
			return Quart;
		case 6:
			return Gallon;
		case 7:
			return Liter;
		case 8:
			return CubicCentimeter;
		case 9:
			return CubicMeter;
		case 10:
			return CubicInch;
		case 11:
			return CubicFoot;
		case 12:
			return CubicYard;
		default:
			throw new IllegalArgumentException("Unknown volume base unit "
					+ val);
		}
	}

	@Override
	public BaseUnitInterface getStandardUnit() {
		return Liter;
	}

}
