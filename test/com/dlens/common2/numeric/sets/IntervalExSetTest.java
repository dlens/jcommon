
package com.dlens.common2.numeric.sets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wjadams
 */
public class IntervalExSetTest {

    public IntervalExSetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testContains() {
        IntervalExSet<Double> strictBothBounds=new IntervalExSet<Double>(1.0, 2.0, true, true);
        IntervalExSet<Integer> nonstrictBothBounds=new IntervalExSet<Integer>(-10, 10, false, false);
        IntervalExSet<Double> strictOnlyUpper=new IntervalExSet<Double>(null, 5.0, true, true);
        IntervalExSet<Integer> strictOnlyLower=new IntervalExSet<Integer>(-2, null, true, true);
        assertTrue(strictBothBounds.contains(new Double[] {1.1}));
        assertFalse(strictBothBounds.contains(new Double[] {1.0}));
        assertFalse(strictBothBounds.contains(new Double[] {2.0}));
        boolean didFail=false;
        try {
            strictBothBounds.contains((Double[])null);
            didFail=false;
        } catch(Exception e) {
            didFail=true;
        } finally {
            if (!didFail) {
                fail("Did not fail while checking null point.");
            }
        }
        Double[] pt;
        pt=new Double[] {};
        try {
            strictBothBounds.contains(pt);
            didFail=false;
        } catch(Exception e) {
            didFail=true;
        } finally {
            if (!didFail) {
                fail("Did not fail while checking point of dim "+pt.length);
            }
        }
        pt=new Double[] {1.0, 2.0};
        try {
            strictBothBounds.contains(pt);
            didFail=false;
        } catch(Exception e) {
            didFail=true;
        } finally {
            if (!didFail) {
                fail("Did not fail while checking point of dim "+pt.length);
            }
        }
        //Check non-strict
        assertTrue(nonstrictBothBounds.contains(new Integer[] {0}));
        assertTrue(nonstrictBothBounds.contains(new Integer[] {1}));
        assertTrue(nonstrictBothBounds.contains(new Integer[] {10}));
        assertTrue(nonstrictBothBounds.contains(new Integer[] {-10}));
        assertFalse(nonstrictBothBounds.contains(new Integer[] {-11}));
        assertFalse(nonstrictBothBounds.contains(new Integer[] {11}));
        //Check no lower bound
        assertTrue(strictOnlyUpper.contains(new Double[] {4.0}));
        assertTrue(strictOnlyUpper.contains(new Double[] {-4000000000000.0}));
        assertFalse(strictOnlyUpper.contains(new Double[] {5.0}));
        assertFalse(strictOnlyUpper.contains(new Double[] {6.0}));
        //Check no upper bound
        assertTrue(strictOnlyLower.contains(new Integer[] {-1}));
        assertTrue(strictOnlyLower.contains(new Integer[] {10000000}));
        assertFalse(strictOnlyLower.contains(new Integer[] {-2}));
        assertFalse(strictOnlyLower.contains(new Integer[] {-3}));
    }

}
