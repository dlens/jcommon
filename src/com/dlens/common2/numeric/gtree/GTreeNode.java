package com.dlens.common2.numeric.gtree;

import java.util.Vector;

/**
 * A node in a GTree.  The NodeType describes what kind of data it will store.
 * @author Bill Adams
 *
 * @param <NodeType>
 */
public class GTreeNode <NodeType> {
	private GTree<NodeType> parentTree;
	private GTreeNode<NodeType> parent;
	private NodeType attachedData;
	private boolean changed=false;
	private Vector<GTreeNode<NodeType>> children;
	private static final String indent="\t";
	private boolean externallySet=false;
	public GTreeNode(GTree<NodeType>parentTree, GTreeNode<NodeType> parent, NodeType attachedData)
	{
		if (!parentTree.isLegalAttachedData(attachedData))
			throw new IllegalArgumentException();
		this.parentTree=parentTree;
		this.parent=parent;
		this.attachedData=attachedData;
		children=new Vector<GTreeNode<NodeType>>();
	}
	
	public GTreeNode<NodeType> getParent() {
		return parent;
	}
	
	public Vector<GTreeNode<NodeType>> getPath() {
		Vector<GTreeNode<NodeType>> rval=new Vector<GTreeNode<NodeType>>();
		getPath(rval);
		return rval;			
	}
	
	private void getPath(Vector<GTreeNode<NodeType>> addedHere) {
		if (parent!=null)
			parent.getPath(addedHere);
		addedHere.add(this);
	}
	
	private boolean differs(NodeType a, NodeType b) {
		if (a==null) {
			return b!=null;
		} else {
			return !a.equals(b);
		}		
	}
	
	public void setAttachedData(NodeType attachedData) throws GTreeAttachedDataException {
		boolean differs=differs(this.attachedData, attachedData);
		parentTree.setAttachedDataEvent(this, this.attachedData, attachedData);
		this.attachedData = attachedData;
		if (differs) {
			this.changed=true;
			this.externallySet=true;
		}
	}
	
	protected void setAttachedDataNoCallback(NodeType attachedData) {
		boolean differs=differs(this.attachedData, attachedData);
		this.attachedData=attachedData;
		if (differs) this.changed=true;
	}
	
	public NodeType getAttachedData() {
		return attachedData;
	}
	
	@SuppressWarnings("unchecked")
	public GTreeNode<NodeType>[] getChildren() {
		return children.toArray((GTreeNode<NodeType>[])new GTreeNode[children.size()]);
	}
	
	public GTreeNode<NodeType> addChild(NodeType attachedData) throws GTreeAttachedDataException {
		GTreeNode<NodeType>rval=new GTreeNode<NodeType>(parentTree, this, attachedData);
		children.add(rval);
		int index=children.size()-1;
		if (!parentTree.addedChildEvent(this, rval, index)) {
			//this means failure, so drop it
			removeChild(index);
			throw new GTreeAttachedDataException();
		}
		return rval;
	}
	
	public void removeChild(int index) {
		children.remove(index);
	}
	
	public String isChangedString() {
		if (changed)
			return "new: ";
		else
			return "";
	}
	
	public String isExternallySetString() {
		if (externallySet)
			return "exSet: ";
		else
			return "";
	}
	
	@Override
	public String toString() {
		String me=indentString()+isExternallySetString()+isChangedString()+attachedData+"\n";
		for(int i=0; i<children.size(); i++)
			me+=children.get(i).toString();
		return me;
	}
	
	private String indentString() {
		String rval="";
		int depth=getDepth();
		for(int i=0; i<depth; i++)
			rval+=indent;
		return rval;
	}
	public int getDepth() {
		if (parent==null) return 0;
		return parent.getDepth()+1;
	}

	public boolean getExternallySet() {
		return externallySet;
	}
}
