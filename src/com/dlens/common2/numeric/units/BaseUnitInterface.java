package com.dlens.common2.numeric.units;

/**
 * The common ancestor of any unit type.
 * @author wjadams
 *
 */
public interface BaseUnitInterface {
	public UnitType getUnitType();
	public int toInt();
	public BaseUnitInterface fromInt(int val);
	public BaseUnitInterface getStandardUnit();
}
