package com.dlens.common2.numeric.units;

public class ConverterBaseUnitLength implements ConverterBaseUnitInterface<BaseUnitLength>
{

	@Override
	public double toStandardUnitDouble(double size, BaseUnitLength fromBase) {
		switch (fromBase) {
		case Foot:
			return size / 3.2808399;
		case Inch:
			return size / (3.2808399 * 12);
		case Yard:
			return size / 3.2808399 * 3;
		case Mile:
			return size / 3.2808399 * 5280;
		case Meter:
			return size;
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public double fromStandardUnitDouble(double size, BaseUnitLength toBase) {
		return size / toStandardUnitDouble(1.0, toBase);
	}

}
