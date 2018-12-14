package com.dlens.common2.numeric.tmatrix;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TMatrixTest {

	@Test
	public void testMTreeSimple() throws Exception {
		MTree rowTree=new MTree();
		MTreeNode root=rowTree.getRoot();
		MTreeNode row1=root.addChild();
		MTreeNode row2=root.addChild();
		MTreeNode row11=row1.addChild();
		MTreeNode row12=row1.addChild();
		MTreeNode row13=row1.addChild();
		MTreeNode row21=row2.addChild();
		MTreeNode row22=row2.addChild();
		MTreeNode row23=row2.addChild();
		MTreeNode row24=row2.addChild();
		System.out.println(rowTree);
	}
	
	@Test
	public void testInit() throws Exception {
		MTree rowTree=new MTree();
		MTreeNode root=rowTree.getRoot();
		MTreeNode row1=root.addChild();
		MTreeNode row2=root.addChild();
		MTreeNode row11=row1.addChild();
		MTreeNode row12=row1.addChild();
		MTree colTree=new MTree();
		MTreeNode croot=colTree.getRoot();
		MTreeNode crow1=croot.addChild();
		MTreeNode crow2=croot.addChild();
		MTreeNode crow21=crow2.addChild();
		MTreeNode crow22=crow2.addChild();
		TMatrix<Double> tmat=new TMatrix<Double>(rowTree, colTree);
		tmat.set(root, croot, 1.5);
		assertEquals(1.5, tmat.get(root, croot), 0.0);
		assertEquals(1.5, tmat.get(0, 0), 0.0);
	}
}
