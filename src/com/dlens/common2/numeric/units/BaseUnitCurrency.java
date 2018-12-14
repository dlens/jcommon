package com.dlens.common2.numeric.units;

/**
 * The units for currency.
 * @author Bill Adams
 *
 */
public enum BaseUnitCurrency implements BaseUnitInterface {
	InternalUseOnly,
	USCent,
	USDollar,
	PoundSterling,
	//Many more moneys here.
	Euro;

	@Override
	public UnitType getUnitType() {
		return UnitType.Currency;
	}

	@Override
	public int toInt() {
		switch (this) {
		case USCent:
			return 0;
		case USDollar:
			return 1;
		case PoundSterling:
			return 2;
		case Euro:
			return 3;
		default:
			return -1;
		}
	}

	@Override
	public BaseUnitCurrency fromInt(int val) {
		switch (val) {
		case 0:
			return USCent;
		case 1:
			return USDollar;
		case 2:
			return PoundSterling;
		case 3:
			return Euro;
		default:
			throw new IllegalArgumentException("Unknown currency type " + val);
		}
	}

	@Override
	public BaseUnitInterface getStandardUnit() {
		return InternalUseOnly;
	}

	
}
