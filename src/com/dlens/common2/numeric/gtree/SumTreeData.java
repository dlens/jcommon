package com.dlens.common2.numeric.gtree;

public class SumTreeData {
	private double value;
	private boolean cascadesDown=true;
	private boolean cascadesUp=true;
	public SumTreeData(double val) {
		this.value=val;
	}
	
	@Override
	public String toString() {
		return value+"";
	}
	public SumTreeData(double val, boolean cascadesUp) {
		this.value=val;
		this.cascadesUp=cascadesUp;
	}
	
	public SumTreeData(double val, boolean cascadesUp, boolean cascadesDown) {
		this.value=val;
		this.cascadesUp=cascadesUp;
		this.cascadesDown=cascadesDown;		
	}
	
	public SumTreeData(SumTreeData cpy) {
		this.value=cpy.value;
		this.cascadesDown=cpy.cascadesDown;
		this.cascadesUp=cpy.cascadesUp;
	}
	
	public void add(double data) {
		value+=data;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		if (value!=this.value) {
			this.value = value;
		}
	}
	
	public boolean getCascadesUp() {
		return cascadesUp;
	}
	
	public boolean getCascadesDown() {
		return cascadesDown;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (obj.getClass().equals(SumTreeData.class)) {
			SumTreeData other=(SumTreeData)obj;
			if (value==other.value) {
				return true;
			}
		}
		return false;
	}
}
