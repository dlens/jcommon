
package com.dlens.common2.numeric.sets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wjadams
 */
public class DOneToOnePolyFxTest {

    public DOneToOnePolyFxTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testEval() {
        DOneToOnePolyFx fx=new DOneToOnePolyFx(new double[] {2, 3, -5});
        double x=2.5;
        Double pt[] = {x};
        assertEquals(2+3*x+x*x*(-5), fx.evaluate(pt), 0);
    }

}
