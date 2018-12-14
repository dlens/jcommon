package com.dlens.common2.numeric.tmatrix;

import org.apache.log4j.Logger;

import com.dlens.common2.numeric.DoubleMatrix;
import com.dlens.common2.numeric.sets.DoubleIntervalSet;
import com.dlens.common2.numeric.sets.UnionDoubleIntervals;


public class UnionIntervalsTMatrix extends TMatrix<UnionDoubleIntervals> {

	//The logger
	private static Logger logger = Logger.getLogger(DoubleMatrix.class);
	
	public UnionIntervalsTMatrix(MTree rowsTree, MTree columnsTree) {
		super(rowsTree, columnsTree);
	}

	@Override
	public void set(MTreeNode rowNode, MTreeNode colNode,
			UnionDoubleIntervals val) throws IllegalArgumentException {
		if (canSetSafely(rowNode, colNode, val)) {
			super.set(rowNode, colNode, val);
		} else {
			throw new IllegalArgumentException("Could not set place.");
		}
	}
	
	public void set(MTreeNode rowNode, MTreeNode colNode, DoubleIntervalSet di) {
		set(rowNode, colNode, new UnionDoubleIntervals(di));
	}
	
	public boolean canSetSafely(MTreeNode row, MTreeNode col, UnionDoubleIntervals newVal) {
		return canSetSafelyUpRow(row, col, newVal) &&
			canSetSafelyDownRow(row, col, newVal) &&
			canSetSafelyUpColumn(row, col, newVal) &&
			canSetSafelyDownColumn(row, col, newVal);
	}
	
	private boolean canSetSafelyUpRow(MTreeNode row, MTreeNode col, UnionDoubleIntervals newVal) {
		//Get siblings and make sure they, along with the new value adds to something that
		//intersects the parents values.
		MTreeNode colParent=col.parent;
		if (colParent==null) return true;
		UnionDoubleIntervals parentInt=get(row, colParent);
		MTreeNode colOtherSibs[]=col.getSiblingsNotSelf();
		UnionDoubleIntervals colOtherSibsInts[]=new UnionDoubleIntervals[colOtherSibs.length+1];
		for(int i=0; i<colOtherSibs.length; i++) {
			colOtherSibsInts[i]=get(row, colOtherSibs[i]);
			if (colOtherSibsInts[i]==null) {
				//If a sibling is null, I cannot check, so just return
				return true;
			}
		}
		colOtherSibsInts[colOtherSibs.length]=newVal;
		UnionDoubleIntervals sum=UnionDoubleIntervals.sum(colOtherSibsInts);
		return parentInt.intersects(sum);
	}
	
	private boolean canSetSafelyDownRow(MTreeNode row, MTreeNode col, UnionDoubleIntervals newVal) {
		//Get children and make sure they sum to something acceptable here.
		MTreeNode colKids[]=col.getChildren();
		if (colKids.length==0) return true;
		UnionDoubleIntervals colKidsInts[]=new UnionDoubleIntervals[colKids.length];
		for(int i=0; i<colKids.length; i++) {
			colKidsInts[i]=get(row, colKids[i]);
			if (colKidsInts[i]==null) {
				//If a sibling is null, I cannot check, so just return
				return true;
			}
		}
		UnionDoubleIntervals sum=UnionDoubleIntervals.sum(colKidsInts);
		return newVal.intersects(sum);
	}
	
	private boolean canSetSafelyUpColumn(MTreeNode row, MTreeNode col, UnionDoubleIntervals newVal) {
		//Get siblings and make sure they, along with the new value adds to something that
		//intersects the parents values.
		MTreeNode rowParent=row.parent;
		if (rowParent==null) return true;
		UnionDoubleIntervals parentInt=get(row, rowParent);
		MTreeNode rowOtherSibs[]=row.getSiblingsNotSelf();
		UnionDoubleIntervals rowOtherSibsInts[]=new UnionDoubleIntervals[rowOtherSibs.length+1];
		for(int i=0; i<rowOtherSibs.length; i++) {
			rowOtherSibsInts[i]=get(rowOtherSibs[i], col);
			if (rowOtherSibsInts[i]==null) {
				//If a sibling is null, I cannot check, so just return
				return true;
			}
		}
		rowOtherSibsInts[rowOtherSibs.length]=newVal;
		UnionDoubleIntervals sum=UnionDoubleIntervals.sum(rowOtherSibsInts);
		return parentInt.intersects(sum);
	}
	
	private boolean canSetSafelyDownColumn(MTreeNode row, MTreeNode col, UnionDoubleIntervals newVal) {
		//Get children and make sure they sum to something acceptable here.
		MTreeNode rowKids[]=row.getChildren();
		if (rowKids.length==0) return true;
		UnionDoubleIntervals rowKidsInts[]=new UnionDoubleIntervals[rowKids.length];
		for(int i=0; i<rowKids.length; i++) {
			rowKidsInts[i]=get(rowKids[i], col);
			if (rowKidsInts[i]==null) {
				//If a sibling is null, I cannot check, so just return
				return true;
			}
		}
		UnionDoubleIntervals sum=UnionDoubleIntervals.sum(rowKidsInts);
		return newVal.intersects(sum);
	}
}
