package com.dlens.common2;

import static org.junit.Assert.*;

import org.junit.Test;

public class Common2VersionTest {

	@Test
	public void test() {
		assertTrue(Common2Version.getVersionMajor() > -1);
		assertNotNull(Common2Version.getVersionPostfix());
		assertNotNull(Common2Version.getVCSStatus());
	}

}
