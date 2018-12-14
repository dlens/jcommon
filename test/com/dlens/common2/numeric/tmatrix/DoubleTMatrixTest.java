package com.dlens.common2.numeric.tmatrix;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class DoubleTMatrixTest {

	@Test
	public void testInit() {
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
		DoubleTMatrix tmat=new DoubleTMatrix(rowTree, colTree);
		tmat.set(root, croot, 1.5);
		assertEquals(1.5, tmat.get(root, croot), 0.0);
		assertEquals(1.5, tmat.get(0, 0), 0.0);
		assertTrue(tmat.hasChanged());
		//System.out.println(tmat.toString());
		tmat.clearChangedStatus();
		assertFalse(tmat.hasChanged());
		//System.out.println("After clearing changed status.");
		//System.out.println(tmat.toStringMatrixOnly());
		
	}
	
	@Test
	public void testSimpleRowCascade() {
		MTree rowTree=new MTree();
		MTreeNode root=rowTree.getRoot();
		MTree colTree=new MTree();
		MTreeNode croot=colTree.getRoot();
		MTreeNode col1=croot.addChild();
		MTreeNode col2=croot.addChild();
		DoubleTMatrix dmat=new DoubleTMatrix(rowTree, colTree);
		dmat.set(root, croot, 9.0);
		dmat.set(root, col1, 6.0);
		dmat.set(root, col2, 3.0);
		dmat.clearChangedStatus();
		dmat.setAndCascadeInRow(root, croot, 18.0);
		Object[][] newVals=dmat.getMatrix();
		Object[][] correct={{18.0, 12.0, 6.0}};
		assertArrayEquals(correct, newVals);
	}

	@Test
	public void testRowCascade() {
		MTree rowTree=new MTree();
		MTreeNode root=rowTree.getRoot();
		MTree colTree=new MTree();
		MTreeNode croot=colTree.getRoot();
		MTreeNode col1=croot.addChild();
		MTreeNode col2=croot.addChild();
		MTreeNode col11=col1.addChild();
		MTreeNode col12=col1.addChild();
		MTreeNode col13=col1.addChild();
		DoubleTMatrix dmat=new DoubleTMatrix(rowTree, colTree);
		dmat.set(root, croot, 9.0);
		dmat.set(root, col1, 6.0);
		dmat.set(root, col11, 1.0);
		dmat.set(root, col12, 3.0);
		dmat.set(root, col13, 2.0);
		dmat.set(root, col2, 3.0);
		dmat.setAndCascadeInRow(root, col1, 12.0);
		Object[][] newVals=dmat.getMatrix();
		Object[][] correct={{15.0, 12.0, 2.0, 6.0, 4.0, 3.0}};
		assertArrayEquals(correct, newVals);
	}

	@Test
	public void testCascade() {
		MTree rowTree=new MTree();
		MTreeNode root=rowTree.getRoot();
		MTreeNode row1=root.addChild();
		MTreeNode row2=root.addChild();
		MTree colTree=new MTree();
		MTreeNode croot=colTree.getRoot();
		MTreeNode col1=croot.addChild();
		MTreeNode col2=croot.addChild();
		MTreeNode col11=col1.addChild();
		MTreeNode col12=col1.addChild();
		MTreeNode col13=col1.addChild();
		DoubleTMatrix dmat=new DoubleTMatrix(rowTree, colTree);
		dmat.set(root, croot, 9.0);
		dmat.set(root, col1, 6.0);
		dmat.set(root, col11, 1.0);
		dmat.set(root, col12, 3.0);
		dmat.set(root, col13, 2.0);
		dmat.set(root, col2, 3.0);
		dmat.set(row1, croot, 5.0);
		dmat.set(row2, croot, 4.0);
		dmat.set(row1, col1, 4.0);
		dmat.set(row2, col1, 2.0);
		dmat.set(row1, col2, 1.0);
		dmat.set(row2, col2, 2.0);
		dmat.set(row1, col11, .5);
		dmat.set(row2, col11, .5);
		dmat.set(row1, col12, 2.0);
		dmat.set(row2, col12, 1.0);
		dmat.set(row1, col13, 1.5);
		dmat.set(row2, col13, 0.5);
		dmat.clearChangedStatus();
		System.out.println(dmat.toStringMatrixOnly());
		dmat.setAndCascade(root, col1, 12.0);
		Object newVals[][]=dmat.getMatrix();
		Object correctVals[][]={
				{15.0, 12.0, 2.0, 6.0, 4.0, 3.0}, 
				{9.0, 8.0, 1.0, 4.0, 3.0, 1.0}, 
				{6.0, 4.0, 1.0, 2.0, 1.0, 2.0} 	
		};
		assertArrayEquals(correctVals, newVals);
	}
	
//	@Test
//	public void testCascadeToInit() {
//		MTree rowTree=new MTree();
//		MTreeNode root=rowTree.getRoot();
//		MTreeNode row1=root.addChild();
//		MTreeNode row2=root.addChild();
//		MTree colTree=new MTree();
//		MTreeNode croot=colTree.getRoot();
//		MTreeNode col1=croot.addChild();
//		MTreeNode col2=croot.addChild();
//		DoubleTMatrix dmat=new DoubleTMatrix(rowTree, colTree);
//		dmat.setAndCascade(root, croot, 60.0);
//		dmat.clearChangedStatus();
//		System.out.println(dmat.toStringMatrixOnly());
//		dmat.setAndCascade(root, col1, 45.0);
//		dmat.clearChangedStatus();
//		System.out.println(dmat.toStringMatrixOnly());
//		dmat.setAndCascade(row1, croot, 50.0);
//		dmat.clearChangedStatus();
//		System.out.println(dmat.toStringMatrixOnly());
//		dmat.setAndCascade(row1, col1, 40.0);
//		dmat.clearChangedStatus();
//		System.out.println(dmat.toStringMatrixOnly());
//	}
	
	@Test
	public void testCascadeToInit2() throws Exception {
		MTree rowTree=new MTree();
		MTreeNode root=rowTree.getRoot();
		MTreeNode row1=root.addChild();
		MTreeNode row2=root.addChild();
		MTree colTree=new MTree();
		MTreeNode croot=colTree.getRoot();
		MTreeNode col1=croot.addChild();
		MTreeNode col2=croot.addChild();
		DoubleTMatrix dmat=new DoubleTMatrix(rowTree, colTree);
		dmat.setAndCascade(root, col1, 45.0);
		dmat.setAndCascade(root, col2, 15.0);
		dmat.clearChangedStatus();
		System.out.println(dmat.toStringMatrixOnly());
		dmat.setAndCascade(row1, croot, 40.0);
		dmat.clearChangedStatus();
		System.out.println(dmat.toStringMatrixOnly());
		dmat.setAndCascade(row2, col1, 15.0);
		dmat.clearChangedStatus();
		System.out.println(dmat.toStringMatrixOnly());
	}
}
