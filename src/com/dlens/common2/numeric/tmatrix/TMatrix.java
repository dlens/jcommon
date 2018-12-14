package com.dlens.common2.numeric.tmatrix;

import java.util.Vector;

/**
 * A matrix whose rows are indexed by a tree, and whose columns
 * are indexed by another tree.
 * @author Bill Adams
 *
 */
public class TMatrix<EntryType> {
	public MTree rowsTree;
	public MTree columnsTree;
	private TMatrixEntry<EntryType> [][]data;
	
	public TMatrix(MTree rowsTree, MTree columnsTree) {
		if (rowsTree==null) throw new IllegalArgumentException();
		if (columnsTree==null) throw new IllegalArgumentException();
		this.rowsTree=rowsTree;
		this.columnsTree=columnsTree;
		initMatrix();
	}

	private void initMatrix() {
		int nrows=rowsTree.getNodeCount();
		int ncols=columnsTree.getNodeCount();
		this.data=new TMatrixEntry[nrows][ncols];
		for(int row=0; row < nrows; row++) {
			for(int col=0; col<ncols; col++) {
				this.data[row][col]=new TMatrixEntry();
			}
		}
	}
	public EntryType get(int row, int col) {
		return data[row][col].getValue();
	}
	
	public void set(int row, int col, EntryType val) {
		data[row][col].setValue(val);
	}
	
	public void set(MTreeNode rowNode, MTreeNode colNode, EntryType val) {
		int row=rowsTree.getNodeIndex(rowNode);
		int col=columnsTree.getNodeIndex(colNode);
		if (row <0) throw new IllegalArgumentException();
		if (col <0) throw new IllegalArgumentException();
		set(row, col, val);
	}
	
	public EntryType get(MTreeNode rowNode, MTreeNode colNode) {
		int row=rowsTree.getNodeIndex(rowNode);
		int col=columnsTree.getNodeIndex(colNode);
		if (row <0) throw new IllegalArgumentException();
		if (col <0) throw new IllegalArgumentException();
		return get(row, col);
	}
	
	public String toStringMatrixOnly() {
		String rval="";
		for(int row=0; row < data.length; row++) {
			for(int col=0; col < data[row].length; col++) {
				rval+=data[row][col]+" ";
			}
			rval+="\n";
		}
		return rval;
	}
	@Override
	public String toString() {
		String rval="Row Tree:\n"+rowsTree.toString()+"\n"+"Column Tree:\n"+columnsTree.toString();
		rval+=toStringMatrixOnly();
		return rval;
	}
	
	public void clearChangedStatus() {
		for(int row=0; row < data.length; row++) {
			for(int col=0; col < data[row].length; col++) {
				data[row][col].clearChangedStatus();
			}
		}
		
	}
	
	public boolean hasChanged() {
		for(int row=0; row < data.length; row++)
			for(int col=0; col < data[row].length; col++)
				if (data[row][col].getHasChanged())
					return true;
		return false;
	}
	
	public Object[][] getMatrix() {
		Object[][] rval=new Object[data.length][];
		for(int i=0; i<rval.length; i++)
			rval[i]=new Object[data[i].length];
		for(int row=0; row<rval.length; row++) {
			for(int col=0; col < rval[row].length; col++) {
				rval[row][col]=data[row][col].getValue();
			}
		}
		return rval;
	}
	
	public boolean hasNullChildrenInRow(MTreeNode rowNode, MTreeNode colNode) {
		MTreeNode kids[]=colNode.getChildren();
		for(int i=0; i<kids.length; i++) {
			if (get(rowNode, kids[i])==null) {
				return true;
			}
		}
		return false;
	}
	
	public MTreeNode[] getNullSiblingsInRow(MTreeNode rowNode, MTreeNode colNode) {
		Vector<MTreeNode> sibs=colNode.getSiblingsVector();
		for(int i=(sibs.size()-1); i>=0; i--) {
			MTreeNode sib=sibs.get(i);
			if (get(rowNode, sib)!=null) {
				sibs.remove(i);
			}
		}
		return sibs.toArray(new MTreeNode[sibs.size()]);
	}
	public MTreeNode[] getNullSiblingsInColumn(MTreeNode rowNode, MTreeNode colNode) {
		Vector<MTreeNode> sibs=rowNode.getSiblingsVector();
		for(int i=(sibs.size()-1); i>=0; i--) {
			MTreeNode sib=sibs.get(i);
			if (get(sib, colNode)!=null) {
				sibs.remove(i);
			}
		}
		return sibs.toArray(new MTreeNode[sibs.size()]);
	}
}
