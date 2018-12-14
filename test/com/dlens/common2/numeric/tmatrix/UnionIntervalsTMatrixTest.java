package com.dlens.common2.numeric.tmatrix;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dlens.common2.numeric.sets.DoubleIntervalSet;

import static org.junit.Assert.*;

public class UnionIntervalsTMatrixTest {
	
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
		MTreeNode col1=croot.addChild();
		MTreeNode col2=croot.addChild();
		MTreeNode col21=col2.addChild();
		MTreeNode col22=col2.addChild();
		UnionIntervalsTMatrix umat=new UnionIntervalsTMatrix(rowTree, colTree);
		umat.set(root, croot, new DoubleIntervalSet(95, 100, true, true));
		umat.set(root, col1, new DoubleIntervalSet(0, 40, true, true));
		try {
			umat.set(root, col2, new DoubleIntervalSet(0, 50, true, true));
			fail("Setting to [0,50] should have caused an exception.");
		} catch (Exception e) {			
		}
		umat.set(root, col2, new DoubleIntervalSet(0, 60, true, true));
		umat.set(root, col21, new DoubleIntervalSet(0, 25, true, true));
		umat.set(root, col22, new DoubleIntervalSet(0, 30, true, true));
	}
}
