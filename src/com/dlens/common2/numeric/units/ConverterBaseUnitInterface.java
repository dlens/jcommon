package com.dlens.common2.numeric.units;



/**
 * The common interface all base unit converters need.
 * @author Bill Adams
 *
 */
public interface ConverterBaseUnitInterface <E extends BaseUnitInterface>{
	public double toStandardUnitDouble(double size, E fromBase);
	public double fromStandardUnitDouble(double size, E toBase);
}
