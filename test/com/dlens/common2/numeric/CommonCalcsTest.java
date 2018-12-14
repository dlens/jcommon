package com.dlens.common2.numeric;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommonCalcsTest {

    @Test
    public void resize() {
        double v1[] = {100.0, 100.0};
        double v2[] = {48, 36, 12, 24};
        assertArrayEquals(new double[] {200}, CommonCalcs.resize(v1, 1), 1e-15);
        assertArrayEquals(new double[] {100, 100}, CommonCalcs.resize(v1, 2), 1e-15);
        assertArrayEquals(new double[] {200/3., 200/3., 200/3.}, CommonCalcs.resize(v1, 3), 1e-12);
        assertArrayEquals(new double[] {120}, CommonCalcs.resize(v2, 1), 1e-12);
        assertArrayEquals(new double[] {84, 36}, CommonCalcs.resize(v2, 2), 1e-12);
        assertArrayEquals(new double[] {60, 32, 28}, CommonCalcs.resize(v2, 3), 1e-12);
        assertArrayEquals(new double[] {4./5*48., 1./5*48. + 3./5 * 36, 2./5*36. + 2./5 * 12, 3./5*12. + 1./5*24, 4./5*24.}, CommonCalcs.resize(v2, 5), 1e-12);

    }
}
