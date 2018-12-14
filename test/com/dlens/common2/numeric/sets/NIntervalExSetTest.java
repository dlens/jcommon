
package com.dlens.common2.numeric.sets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wjadams
 */
public class NIntervalExSetTest {

    public NIntervalExSetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testContains() {
        NIntervalExSet<Double> set=new NIntervalExSet<Double>(2, new Double[] {4.0, 3.0}, new Double[] {6.0, 10.0});
        assertTrue(set.contains(new Double[] {4.5, 6.0}));
        assertFalse(set.contains(new Double[] {3.5, 6.0}));
        assertFalse(set.contains(new Double[] {5.0, 11.0}));
    }

}
