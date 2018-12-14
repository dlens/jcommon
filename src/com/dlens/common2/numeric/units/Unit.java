package com.dlens.common2.numeric.units;

import java.util.Vector;

/**
 * Represents a full unit.  It has a base unit, plus zero or more unit prefixes.
 * A unit prefix is something like 1000's or centi.
 * @author Bill Adams
 *
 */
public class Unit {
	Vector<UnitPrefix>prefixes=new Vector<UnitPrefix>();
	BaseUnitInterface baseUnit;
	
	public Unit(BaseUnitInterface base) {
		if (base==null) throw new IllegalArgumentException("Base cannot be null");
		this.baseUnit=base;
	}
	
	public Unit(UnitPrefix prefix, BaseUnitInterface base) {
		this(base);
		if (prefix==null) throw new IllegalArgumentException("Prefix cannot be null.");
		prefixes.add(prefix);
	}
	
	public Unit(UnitPrefix prefix1, UnitPrefix prefix2, BaseUnitInterface base) {
		this(prefix1, base);
		if (prefix2==null) throw new IllegalArgumentException("Prefix2 cannot be null.");
		prefixes.add(prefix2);
	}
	
	public UnitType getUnitType() {
		return baseUnit.getUnitType();
	}
	
	public BaseUnitInterface getBaseUnit() {
		return baseUnit;
	}

	public double prefixesDoubleValue() {
		double rval=1.0;
		for(int i=0; i<prefixes.size(); i++) {
			rval*=prefixes.get(i).doubleValue();
		}
		return rval;
	}
}

