package com.dlens.common2.numeric.gtree;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SumTreeTest {

	@Test
	public void test1() throws Exception {
		System.out.println("Testing costs methodology.");
		SumTree tree=new SumTree(1.0);
		GTreeNode<SumTreeData>root=tree.getRoot();
		GTreeNode<SumTreeData> kid1=root.addChild(new SumTreeData(0.3));
		root.addChild(new SumTreeData(.5));
		root.addChild(new SumTreeData(.2));
		kid1.addChild(new SumTreeData(.1));
		kid1.addChild(new SumTreeData(.2));
		System.out.println(tree.toString());
		kid1.setAttachedData(new SumTreeData(0.5));
		System.out.println(tree);
		
	}


	@Test
	public void test2() throws Exception {
		System.out.println("Testing global priorities.");
		SumTree tree=new SumTree(1.0);
		tree.propType=SumTreeProportionalityEnum.GloablPriorities;
		GTreeNode<SumTreeData>root=tree.getRoot();
		GTreeNode<SumTreeData> kid1=root.addChild(new SumTreeData(0.3));
		root.addChild(new SumTreeData(.5));
		root.addChild(new SumTreeData(.2));
		kid1.addChild(new SumTreeData(.1));
		kid1.addChild(new SumTreeData(.2));
		System.out.println(tree.toString());
		kid1.setAttachedData(new SumTreeData(0.5));
		System.out.println(tree);
		
	}
	
	@Test
	public void testNulls() throws Exception {
		SumTree tree=new SumTree(50.0, false, false);
		tree.propType=SumTreeProportionalityEnum.Costs;
		GTreeNode<SumTreeData>root=tree.getRoot();
		GTreeNode<SumTreeData> kid1=root.addChild(new SumTreeData(30.0));
		GTreeNode<SumTreeData> kid2=root.addChild(new SumTreeData(20.0));
		GTreeNode<SumTreeData> kid11=kid1.addChild(new SumTreeData(10.0));
		GTreeNode<SumTreeData> kid12=kid1.addChild(new SumTreeData(15.0));
		GTreeNode<SumTreeData> kid13=kid1.addChild(new SumTreeData(5.0));
		GTreeNode<SumTreeData> kid111=kid11.addChild(new SumTreeData(2.0));
		GTreeNode<SumTreeData> kid112=kid11.addChild(new SumTreeData(5.0));
		GTreeNode<SumTreeData> kid113=kid11.addChild(new SumTreeData(3.0));
		kid1.setAttachedData(new SumTreeData(25.0));
		System.out.println(tree);
		
	}
}
