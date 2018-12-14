package com.dlens.common2.numeric.units;

/**
 * Represents a prefix for a unit.  If the base unit is meters, a prefix might
 * be Centi (for centimeters), etc.
 * @author Bill Adams
 *
 */
public enum UnitPrefix {
	Yocto,
	Zepto,
	Atto,
	Femto,
	Pico,
	Nano,
	Micro,
	Milli,
	Centi,
	Deci,
	Deca,
	Hecto,
	Kilo,
	Mega,
	Giga,
	Tera,
	Peta,
	Exa,
	Zetta,
	Yotta,
	Multiplier10,
	Multiplier100,
	Multiplier1000,
	MultiplierMillion,
	MultiplierBillion,
	MultiplierTrillion,
	MultiplierQuadrillion,
	Divider10,
	Divider100,
	Divider1000,
	DividerMillion,
	DividerBillion,
	DividerTrillion,
	DividerQuadrillion;
	
	public double doubleValue() {
		switch (this) {
		case Yocto:
			return 1e-24;
		case Zepto:
			return 1e-21;
		case Atto:
			return 1e-18;
		case Femto:
			return 1e-15;
		case Pico:
			return 1e-12;
		case Nano:
			return 1e-9;
		case Micro:
			return 1e-6;
		case Milli:
			return 1e-3;
		case Centi:
			return 1e-2;
		case Deci:
			return .1;
		case Deca:
			return 10;
		case Hecto:
			return 100;
		case Kilo:
			return 1000;
		case Mega:
			return 1e6;
		case Giga:
			return 1e9;
		case Tera:
			return 1e12;
		case Peta:
			return 1e15;
		case Exa:
			return 1e18;
		case Zetta:
			return 1e21;
		case Yotta:
			return 1e24;
		case Multiplier10:
			return 10;
		case Multiplier100:
			return 100;
		case Multiplier1000:
			return 1000;
		case MultiplierMillion:
			return 1e6;
		case MultiplierBillion:
			return 1e9;
		case MultiplierTrillion:
			return 1e12;
		case MultiplierQuadrillion:
			return 1e15;
		case Divider10:
			return .1;
		case Divider100:
			return .01;
		case Divider1000:
			return .001;
		case DividerMillion:
			return 1e-6;
		case DividerBillion:
			return 1e-9;
		case DividerTrillion:
			return 1e-12;
		case DividerQuadrillion:
			return 1e-15;
		default:
			throw new IllegalArgumentException();
		}
	}
}
