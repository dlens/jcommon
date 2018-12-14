
package com.dlens.common2.numeric.sets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wjadams
 */
public class DOneToOneLinearFxTest {

    public DOneToOneLinearFxTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testEval() {
        DOneToOneLinearFx lf=new DOneToOneLinearFx(2, 3);
        assertEquals(-5*2+3+0.0, lf.evaluate(new Double[] {-5.0}),0);
        lf=new DOneToOneLinearFx(2, 3, new IntervalExSet<Double>(-3.0, 3.0), new IntervalExSet<Double>(-30.0, 5.0));
        assertEquals(-2*2+3, lf.evaluate(new Double[] {-2.0}), 0);
        boolean failed=false;
        try {
            lf.evaluate(new Double[] {-4.0});
        } catch (Exception e) {
            failed=true;
        } finally {
            if (!failed)
                fail("Evaluating outside of domain caused no exception.");
        }
        try {
            lf.evaluate(new Double[] {2.0});
        } catch (Exception e) {
            failed=true;
        } finally {
            if (!failed)
                fail("Evaluating inside domain, but with result outside domain didn't cause exception.");
        }
    }

}
