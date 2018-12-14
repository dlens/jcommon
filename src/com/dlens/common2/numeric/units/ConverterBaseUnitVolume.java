package com.dlens.common2.numeric.units;

public class ConverterBaseUnitVolume implements ConverterBaseUnitInterface<BaseUnitVolume>
{
	final static double oneLiterInQuarts=1.05668821;
	@Override
	public double fromStandardUnitDouble(double size, BaseUnitVolume toBase) {
		//This converts size Liters (the standard unit) to the units of toBase
		switch (toBase) {
		case CubicCentimeter:
			return  size*1000;
		case CubicFoot:
			return  size*57.75/(12*12*12)*oneLiterInQuarts;
		case CubicInch:
			return size*57.75*oneLiterInQuarts;
		case CubicMeter:
			return size*1e-3;
		case CubicYard:
			return size*57.75/(36*36*36)*oneLiterInQuarts;
		case Cup:
			return size*4*oneLiterInQuarts;
		case Gallon:
			return size*oneLiterInQuarts/4;
		case Liter:
			return size;
		case Ounce:
			return size*32*oneLiterInQuarts;
		case Pint:
			return size*2*oneLiterInQuarts;
		case Quart:
			return size*oneLiterInQuarts;
		case Tablespoon:
			return size*64*oneLiterInQuarts;
		case Teaspoon:
			return size*192*oneLiterInQuarts;
		default:
			throw new IllegalArgumentException("Unable to convert to "+toBase);
		}
	}

	@Override
	public double toStandardUnitDouble(double size, BaseUnitVolume fromBase) {
		return size/fromStandardUnitDouble(1.0, fromBase);
	}

}
