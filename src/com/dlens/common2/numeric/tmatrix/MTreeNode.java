package com.dlens.common2.numeric.tmatrix;

import java.util.Vector;

/**
 * The nodes in an MTree.
 * @author Bill Adams
 *
 */
public class MTreeNode {
	MTree parentTree;
	MTreeNode parent;
	Vector<MTreeNode> children=new Vector<MTreeNode>();
	public MTreeNode(MTree parentTree, MTreeNode parent) {
		if (parent==null) throw new IllegalArgumentException("Cannot create node without a parent through this public constructor.");
		this.parent=parent;
		this.parentTree=parentTree;
	}
	
	protected MTreeNode(MTree parentTree) {
		this.parent=null;
		this.parentTree=parentTree;
	}
	
	public MTreeNode addChild() {
		MTreeNode kid=new MTreeNode(this.parentTree, this);
		children.add(kid);
		return kid;
	}
	
	public int getChildCount() {
		return children.size();
	}
	
	public MTreeNode[] getChildren() {
		return children.toArray(new MTreeNode[children.size()]);
	}
	
	public int getNodeCount() {
		int rval=1;
		int size=children.size();
		for(int i=0; i<size; i++)
			rval+=children.get(i).getNodeCount();
		return rval;
	}
	
	public int getNodeIndex(MTreeNode node, int offset) {
		if (node==this) {
			return offset;
		}
		int plusOffset=1;
		int size=children.size();
		int tmpRval;
		for(int i=0; i<size; i++) {
			tmpRval=children.get(i).getNodeIndex(node, offset+plusOffset);
			if (tmpRval >= 0) return tmpRval;
			plusOffset+=children.get(i).getNodeCount();
		}
		return -1;
	}
	
	private String indentString(int indentLevel) {
		String rval="";
		for(int i=0; i<indentLevel; i++)
			rval+="\t";
		return rval;
	}
	
	public boolean hasChildren() {
		return (children.size()>0);
	}
	public String toString(int indentLevel) {
		int kids=getChildCount();
		int all=getNodeCount();
		int index=parentTree.getNodeIndex(this);
		String rval=indentString(indentLevel)+"o: nkids="+kids+" nAll="+all+" index="+index+"\n";
		for(int i=0; i<children.size(); i++)
			rval+=children.get(i).toString(indentLevel+1);
		return rval;
	}
	
	public MTreeNode[] getSiblings() {
		if (parent==null) {
			return new MTreeNode[] {this};			
		}
		return parent.getChildren();
	}
	
	public Vector<MTreeNode> getSiblingsVector() {
		MTreeNode[] sibs=getSiblings();
		Vector<MTreeNode>rval=new Vector<MTreeNode>();
		for(int i=0; i<sibs.length; i++)
			rval.add(sibs[i]);
		return rval;
	}
	
	public Vector<MTreeNode> getChildrenVector() {
		MTreeNode[] sibs=getChildren();
		Vector<MTreeNode>rval=new Vector<MTreeNode>();
		for(int i=0; i<sibs.length; i++)
			rval.add(sibs[i]);
		return rval;
	}
	
	
	public MTreeNode[] getSiblingsNotSelf() {
		if (parent==null)
			return new MTreeNode[] {};
		Vector<MTreeNode>rval=getSiblingsVector();
		rval.remove(this);
		return rval.toArray(new MTreeNode[rval.size()]);
	}
	
}
