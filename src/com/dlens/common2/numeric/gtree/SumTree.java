package com.dlens.common2.numeric.gtree;

import java.util.Vector;

/**
 * A simple tree with double values at each node.  However
 * @author Bill Adams
 *
 */
public class SumTree extends GTree<SumTreeData> {
	public SumTreeProportionalityEnum propType=SumTreeProportionalityEnum.Default;
	public SumTree(Double dataAttachedToRoot) {
		super(new SumTreeData(dataAttachedToRoot));
		// TODO Auto-generated constructor stub
	}


	public SumTree(double d, boolean b, boolean c) {
		super(new SumTreeData(d, b, c));
	}


	@Override
	public void setAttachedDataEvent(GTreeNode<SumTreeData> node, SumTreeData oldVal,
			SumTreeData newVal) throws GTreeAttachedDataException {
		// TODO Auto-generated method stub
		switch (propType) {
		case Costs:
			setNodeValueCost(node, oldVal, newVal);
			break;
		case GloablPriorities:
			setNodeValuePriorities(node, oldVal, newVal);
			break;
		}
	}
	
	private void setNodeValueCost(GTreeNode<SumTreeData> node, SumTreeData oldVal, SumTreeData newVal) {
		setNodeValueCascadeDown(node, oldVal, newVal);
		//Cascade changes upwards.
		setNodeValueCascadeUp(node, oldVal, newVal);
	}

	private void setNodeValueCascadeUp(GTreeNode<SumTreeData> node, SumTreeData oldVal, SumTreeData newVal) {
		Vector<GTreeNode<SumTreeData>> path=node.getPath();
		double diff=newVal.getValue()-oldVal.getValue();
		node.setAttachedDataNoCallback(newVal);
		for(int i=(path.size()-2); i >=0; i--) {
			GTreeNode<SumTreeData> tnode=path.get(i);
			SumTreeData told=tnode.getAttachedData();
			if (told.getCascadesUp()) {
				SumTreeData other=new SumTreeData(told);
				other.add(diff);
				setNodeValueCascadeUp(tnode, told, other);
			} else {
				break;
			}
		}
	}

	private void setNodeValueCascadeDown(GTreeNode<SumTreeData> node, SumTreeData oldVal, SumTreeData newVal) {
		node.setAttachedDataNoCallback(newVal);
		if (oldVal.getValue()==0) throw new UnsupportedOperationException("Cannot handle oldVal=0 currently.");
		double ratio=newVal.getValue()/oldVal.getValue();
		GTreeNode<SumTreeData>kids[]=node.getChildren();
		for(int i=0; i<kids.length; i++) {
			double val=kids[i].getAttachedData().getValue();
			setNodeValueCascadeDown(kids[i], new SumTreeData(val), new SumTreeData(ratio*val));
		}
	}
	
	private void setNodeValuePriorities(GTreeNode<SumTreeData> node, SumTreeData oldVal, SumTreeData newVal) {
		setNodeValueCascadeDown(node, oldVal, newVal);
		setNodeValueCascadeSideways(node, oldVal, newVal);
	}
	
	private void setNodeValueCascadeSideways(GTreeNode<SumTreeData> node, SumTreeData oldValD, SumTreeData newValD) {
		GTreeNode<SumTreeData> parent=node.getParent();
		if (parent==null) return;
		GTreeNode<SumTreeData>siblings[]=parent.getChildren();
		double oldSum=0;
		double newVal=newValD.getValue();
		double oldVal=oldValD.getValue();
		oldSum+=oldVal;
		for(int i=0; i<siblings.length; i++) {
			if (!siblings[i].equals(node)) {
				oldSum+=siblings[i].getAttachedData().getValue();
			}
		}
		double oldSumOthers=oldSum-oldVal;
		double newSumOthers=oldSum-newVal;
		System.out.println("Cascading sideways from "+oldVal+" to newVal "+newVal+" oldSumOthers="+oldSumOthers+" newSumOthers="+newSumOthers);
		for(int i=0; i<siblings.length; i++) {
			if (!siblings[i].equals(node)) {
				double val=siblings[i].getAttachedData().getValue();
				setNodeValueCascadeDown(siblings[i], new SumTreeData(val), new SumTreeData(newSumOthers/oldSumOthers*val));
			}
		}
		node.setAttachedDataNoCallback(newValD);
	}
	
	@Override
	public boolean addedChildEvent(GTreeNode<SumTreeData> parent,
			GTreeNode<SumTreeData> newChild, int index) {
		//Fill in some logic perhaps
		return true;
	}
}
