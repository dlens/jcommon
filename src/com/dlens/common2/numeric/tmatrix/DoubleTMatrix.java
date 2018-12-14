package com.dlens.common2.numeric.tmatrix;


public class DoubleTMatrix extends TMatrix<Double> {

	public DoubleTMatrix(MTree rowsTree, MTree columnsTree) {
		super(rowsTree, columnsTree);
	}

	public void setAndCascade(MTreeNode rowNode, MTreeNode colNode, double val) {
		cascadeInRow(rowNode, colNode, val);
		cascadeInColumn(rowNode, colNode, val);
		set(rowNode, colNode, val);
		cascadeToNullRowSiblingIfCan(rowNode, colNode, val);
		cascadeToNullColumnSiblingIfCan(rowNode, colNode, val);
	}
	
	
	private void cascadeToNullRowSiblingIfCan(MTreeNode rowNode, MTreeNode colNode, double val) {
		MTreeNode nulls[]=getNullSiblingsInRow(rowNode, colNode);
		if (nulls.length==1) {
			inferValueFromRow(rowNode, nulls[0]);
		}
	}
	
	private void cascadeToNullColumnSiblingIfCan(MTreeNode rowNode, MTreeNode colNode, double val) {
		MTreeNode nulls[]=getNullSiblingsInColumn(rowNode, colNode);
		if (nulls.length==1) {
			inferValueFromColumn(nulls[0], colNode);
		}
	}
	
	private void inferValueFromRow(MTreeNode rowNode, MTreeNode colNode) {
		MTreeNode parent=colNode.parent;
		if (parent==null) return;
		Double parentValD=get(rowNode, parent);
		if (parentValD==null) return;
		double parentVal=parentValD;
		double sibsSum=0;
		MTreeNode sibs[]=colNode.getSiblingsNotSelf();
		for(int i=0; i<sibs.length; i++) {
			Double valD=get(rowNode, sibs[i]);
			if (valD!=null)
				sibsSum+=valD;
		}
		double val=parentVal-sibsSum;
		setAndCascade(rowNode, colNode, val);
	}
	
	private void inferValueFromColumn(MTreeNode rowNode, MTreeNode colNode) {
		MTreeNode parent=rowNode.parent;
		if (parent==null) return;
		double parentVal=get(parent, colNode);
		double sibsSum=0;
		MTreeNode sibs[]=rowNode.getSiblingsNotSelf();
		for(int i=0; i<sibs.length; i++) {
			Double valD=get(sibs[i], colNode);
			if (valD!=null)
				sibsSum+=valD;
		}
		double val=parentVal-sibsSum;
		setAndCascade(rowNode, colNode, val);
	}
	
	public void cascadeInRow(MTreeNode rowNode, MTreeNode colNode, double val) {
		cascadeDownInRow(rowNode, colNode, val);
		cascadeUpInRow(rowNode, colNode, val);		
	}
	
	public void cascadeInColumn(MTreeNode rowNode, MTreeNode colNode, double val) {
		cascadeDownInColumn(rowNode, colNode, val);
		cascadeUpInColumn(rowNode, colNode, val);
	}
	
	public void setAndCascadeInRow(MTreeNode rowNode, MTreeNode colNode, double val) {
		cascadeDownInRow(rowNode, colNode, val);
		cascadeUpInRow(rowNode, colNode, val);
		set(rowNode, colNode, val);
	}
	
	public Double multiplyEntryBy(MTreeNode rowNode, MTreeNode colNode, double factor) {
		Double val=get(rowNode, colNode);
		if (val==null) return null;
		return val * factor;
	}
	
	public Double addEntryBy(MTreeNode rowNode, MTreeNode colNode, double factor) {
		Double val=get(rowNode, colNode);
		if (val==null) return null;
		return val + factor;
	}
	
	private void cascadeDownInRow(MTreeNode rowNode, MTreeNode colNode, double val) {
		MTreeNode []colKids=colNode.getChildren();
		double ratio=0;
		Double oldValD=get(rowNode, colNode);
		if (oldValD==null) return;
		double oldVal=oldValD;
		if (oldVal==0.0) return;
		ratio=val/oldVal;
		for(int kid=0; kid < colKids.length; kid++) {
			Double newVal=multiplyEntryBy(rowNode, colKids[kid], ratio);
			if (newVal!=null) {
				cascadeDownInRow(rowNode, colKids[kid], newVal);
				set(rowNode, colKids[kid], newVal);
			}
		}
	}
	
	private Double sumOfChildrenInRow(MTreeNode rowNode, MTreeNode colNode) {
		MTreeNode kids[]=colNode.getChildren();
		double rval=0;
		for(int i=0; i<kids.length; i++) {
			Double v=get(rowNode, kids[i]);
			if (v==null) return null;
			rval+=v;
		}
		return rval;
	}
	
	private void cascadeUpInRow(MTreeNode rowNode, MTreeNode colNode, double val) {
		Double oldval=get(rowNode, colNode);
		MTreeNode colParent=colNode.parent;
		Double newVal;
		if (colParent==null) return;
		if (oldval != null) {
			double diff = val = oldval;
			if (diff == 0)
				return;
			newVal = addEntryBy(rowNode, colParent, diff);
			if (newVal == null)
				return;
		} else {
			MTreeNode sibs[]=colNode.getSiblingsNotSelf();
			double sum=val;
			Double aval;
			for(int i=0; i<sibs.length; i++) {
				aval=get(rowNode, sibs[i]);
				if (aval==null) return;
				sum+=aval;
			}
			newVal=sum;			
		}
		cascadeUpInRow(rowNode, colParent, newVal);
		set(rowNode, colParent, newVal);
	}
	
	private void cascadeDownInColumn(MTreeNode rowNode, MTreeNode colNode, double val) {
		MTreeNode []rowKids=rowNode.getChildren();
		double ratio=0;
		Double oldValD=get(rowNode, colNode);
		if (oldValD==null) return;
		double oldVal=oldValD;
		if (oldVal==0.0) return;
		ratio=val/oldVal;
		for(int kid=0; kid < rowKids.length; kid++) {
			Double newVal=multiplyEntryBy(rowKids[kid], colNode, ratio);
			if (newVal!=null) {
				cascadeDownInColumn(rowKids[kid], colNode, newVal);
				setAndCascadeInRow(rowKids[kid], colNode, newVal);
			}
		}
		
	}
	
	private void cascadeUpInColumnOld(MTreeNode rowNode, MTreeNode colNode, double val) {
		Double oldval=get(rowNode, colNode);
		if (oldval==null) return;
		double diff=val=oldval;
		if (diff==0) return;
		MTreeNode rowParent=rowNode.parent;
		if (rowParent==null) return;
		Double newVal=addEntryBy(rowParent, colNode, diff);
		cascadeUpInColumn(rowParent, colNode, newVal);
		setAndCascadeInRow(rowParent, colNode, newVal);		
	}

	private void cascadeUpInColumn(MTreeNode rowNode, MTreeNode colNode, double val) {
		Double oldval=get(rowNode, colNode);
		MTreeNode rowParent=rowNode.parent;
		Double newVal;
		if (rowParent==null) return;
		if (oldval != null) {
			double diff = val = oldval;
			if (diff == 0)
				return;
			newVal = addEntryBy(rowParent, colNode, diff);
			if (newVal == null)
				return;
		} else {
			MTreeNode sibs[]=rowNode.getSiblingsNotSelf();
			double sum=val;
			Double aval;
			for(int i=0; i<sibs.length; i++) {
				aval=get(sibs[i], colNode);
				if (aval==null) return;
				sum+=aval;
			}
			newVal=sum;			
		}
		cascadeUpInColumn(rowParent, colNode, newVal);
		set(rowParent, colNode, newVal);
	}
	
}
