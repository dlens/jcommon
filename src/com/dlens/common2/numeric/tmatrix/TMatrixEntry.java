package com.dlens.common2.numeric.tmatrix;

/**
 * Used to store entries in a TMatrix.
 * @author Bill Adams
 *
 */
public class TMatrixEntry<EntryType> {
	private boolean hasChanged=false;
	private EntryType val;
	public TMatrixEntry(EntryType val) {
		this.val=val;
	}
	
	protected TMatrixEntry() {		
	}
	
	public EntryType getValue() {
		return val;
	}
	
	public void setValue(EntryType val) {
		if (val!=null) {
			if (val.equals(this.val)) {
				return;
			}
			this.val=val;
			this.hasChanged=true;
		} else {
			if (this.val==null) {
				return;
			} else {
				this.val=null;
				this.hasChanged=true;
			}
		}
	}

	private String hasChangedString() {
		if (hasChanged) return "new:";
		else return "";
	}
	
	@Override
	public String toString() {
		return hasChangedString()+val;
	}
	
	protected void clearChangedStatus() {
		hasChanged=false;
	}
	
	public boolean getHasChanged() {
		return hasChanged;
	}
}
