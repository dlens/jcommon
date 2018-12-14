package com.dlens.common2.numeric.tmatrix;

/**
 * The point of this class is to have an incredibly simple tree
 * for use in a matrix whose rows and columns are indexed by trees.
 * @author Bill Adams
 *
 */
public class MTree {
	MTreeNode root;
	
	public MTree() {
		root=new MTreeNode(this);
	}
	
	public int getNodeCount() {
		return root.getNodeCount();
	}
	
	public MTreeNode getRoot() {
		return root;
	}
	
	public int getNodeIndex(MTreeNode node) {
		if (node==null) throw new IllegalArgumentException();
		return root.getNodeIndex(node, 0);
	}
	
	public MTree deepCopy() {
		//FIXME
		return null;
	}
	
	@Override
	public String toString() {
		return root.toString(0);
	}
}
