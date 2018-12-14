package com.dlens.common2.numeric.units;


/**
 * For converting time units, base units only.  Prefixes are handled in the UnitConverter.
 * @author Bill Adams
 *
 */
public class ConverterBaseUnitTime implements ConverterBaseUnitInterface<BaseUnitTime> {
	/**
	 * Converts 1 unit to the standard unit
	 * @param unit
	 * @return
	 */
	public double toStandardUnitDouble(double size, BaseUnitTime unit) {
		switch (unit) {
		case Second:
			return size/3600.0;
		case Minute:
			return size/60.0;
		case Hour:
			return size;
		case Day:
			return size * 24.0;
		case Week:
			return size * (24.0*7.0);
		case Month:
			return size * (24.0*30.0);
		case Year:
			return size * (24.0*365.0);
		case Decade:
			return size * (24.0*365.0*10);
		default:
			throw new IllegalArgumentException("Unknown unit " + unit);
		}
	}
	
	/**
	 * Converts one standard unit into toUnit's.
	 * @param toUnit
	 * @return
	 */
	public double fromStandardUnitDouble(double size, BaseUnitTime toUnit) {
		return size/toStandardUnitDouble(1.0, toUnit);
	}
}
