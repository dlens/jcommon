
package com.dlens.common2.numeric.sets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wjadams
 */
public class DNToOneLinearFxTest {

    public DNToOneLinearFxTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testInternalEval() {
        double constant=5.2;
        double coeffs[]={2.1, -1.4, 4.5, 3.6};
        DNToOneLinearFx fx = new DNToOneLinearFx(coeffs.length, coeffs, constant);
        Double pt[]={3.5, -1.5, 2.0, 1.5};
        double rval=constant;
        for(int i=0; i<coeffs.length; i++)
            rval+=coeffs[i]*pt[i];
        assertEquals(rval, fx.evaluate(pt),0);
        fx = new DNToOneLinearFx(2, new double[] {-1.0, 2.0}, 0);
        assertEquals(-1.0*5+2*(-6), fx.evaluate(new Double[] {5.0, -6.0}),0);

    }

}
