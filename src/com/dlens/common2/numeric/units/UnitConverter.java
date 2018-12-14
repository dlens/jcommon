package com.dlens.common2.numeric.units;

/**
 * Used to convert units from one type to another.
 * @author Bill Adams
 *
 */
public class UnitConverter {
	public static double convertDouble(double size, Unit fromUnit, Unit toUnit) {
		UnitType fromUnitType=fromUnit.getUnitType();
		UnitType toUnitType=toUnit.getUnitType();
		if (!fromUnitType.equals(toUnitType)) {
			throw new IllegalArgumentException();
		}
		ConverterBaseUnitInterface<BaseUnitInterface> fromBaseConverter=fromUnitType.baseUnitConverter();
		ConverterBaseUnitInterface<BaseUnitInterface> toBaseConverter=toUnitType.baseUnitConverter();
		double fromPrefixesMult=fromUnit.prefixesDoubleValue();
		double toPrefixesMult=toUnit.prefixesDoubleValue();
		double inStandardUnits=fromBaseConverter.toStandardUnitDouble(size*fromPrefixesMult, fromUnit.getBaseUnit());
		double rval=toBaseConverter.fromStandardUnitDouble(inStandardUnits, toUnit.getBaseUnit());
		//Still need to convert rval multiplier
		return rval/toPrefixesMult;
	}

}
