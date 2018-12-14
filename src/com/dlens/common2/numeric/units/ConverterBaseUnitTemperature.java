package com.dlens.common2.numeric.units;

public class ConverterBaseUnitTemperature 
	implements ConverterBaseUnitInterface<BaseUnitTemperature>
{

	@Override
	public double toStandardUnitDouble(double size, BaseUnitTemperature fromBase) {
		//Convert to the standard unit
		switch (fromBase) {
		case Kelvin:
			return size;
		case Celsisus:
			return size+273;
		case Fahrenheit:
			return (size-32)*5/9 + 273;
		default:
			throw new IllegalArgumentException("Unknown base.");
		}
	}

	@Override
	public double fromStandardUnitDouble(double size, BaseUnitTemperature toBase) {
		//Convert from the standard unit
		switch (toBase) {
		case Kelvin:
			return size;
		case Celsisus:
			return size - 273;
		case Fahrenheit:
			return (size - 273)*9/5 +32;
		default:
			throw new IllegalArgumentException("Cannot convert.");
		}
	}

}
