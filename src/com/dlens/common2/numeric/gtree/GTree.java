package com.dlens.common2.numeric.gtree;

/**
 * This is a generic-friendly tree.  It is used for internal computational needs
 * by the main library.  For instance, there is a DoubleTree, DoubleIntervalSetTree
 * and a UnionDoubleIntervalsTree.  The NodeType describes what kind of data the nodes
 * store.
 * @author Bill Adams
 *
 */
public class GTree <NodeType> {
	//The root node of the tree.  Every tree has one.
	private GTreeNode<NodeType> root;
	
	public GTree(NodeType dataAttachedToRoot) {
		root=new GTreeNode<NodeType>(this, null, dataAttachedToRoot);
	}
	
	public GTreeNode<NodeType> getRoot() {
		return root;
	}
	
	public void setAttachedDataEvent(GTreeNode<NodeType> node, NodeType oldVal, NodeType newVal) 
	throws GTreeAttachedDataException
	{
		System.out.println("Node "+node+" changed value from '"+oldVal+"' to "+newVal);		
	}
	
	public boolean addedChildEvent(GTreeNode<NodeType> parent, GTreeNode<NodeType> newChild, int index)
	{
		System.out.println("Added child under '"+parent+"' child was '"+newChild+"' at index "+index);
		return true;
	}
	
	public boolean isLegalAttachedData(NodeType attachedData) {
		return true;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return root.toString();
	}
	
}
