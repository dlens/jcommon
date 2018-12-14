package com.dlens.common2.numeric.units;

public enum BaseUnitTemperature implements BaseUnitInterface {
	Fahrenheit,
	Celsisus,
	Kelvin;

	@Override
	public UnitType getUnitType() {
		return UnitType.Temperature;
	}

	@Override
	public int toInt() {
		switch(this) {
		case Fahrenheit:
			return 0;
		case Celsisus:
			return 1;
		case Kelvin:
			return 2;
		default:
			throw new IllegalArgumentException("Do not know how to turn this temp unit into an int.");
		}
	}

	@Override
	public BaseUnitInterface fromInt(int val) {
		switch (val) {
		case 0:
			return Fahrenheit;
		case 1:
			return Celsisus;
		case 2:
			return Kelvin;
		default:
			throw new IllegalArgumentException("Cannot convert int val to temp unit, sorry.");
		}
	}

	@Override
	public BaseUnitInterface getStandardUnit() {
		return Kelvin;
	}

}
