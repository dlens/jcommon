package com.dlens.common2.numeric;

public class CommonCalcs {

    public static double[] resize(double vec[], int new_size) {
        if (new_size < 1) {
            throw new IllegalArgumentException("New size must be >= 1");
        } else if (vec == null) {
            throw new IllegalArgumentException("Vector cannot be null");
        } else if (vec.length == new_size) {
            //Nothing to do, except return a clone, because this assumes a different object
            return vec.clone();
        }
        //If we make it here, we have to calcualte
        double rval[] = new double[new_size];
        double currentPosition, nextPosition;
        double ratio = vec.length / (new_size+0.0);
        int j, k, p;
        for(int i=0; i < new_size; i++) {
            currentPosition = i * ratio;
            nextPosition = (i + 1) * ratio;
            j = (int) Math.floor(currentPosition);
            k = (int) Math.ceil(nextPosition)-1;
            if (j == k) {
                //We are on one place, we just do diff
                rval[i] = (nextPosition - currentPosition) * vec[j];
            } else {
                rval[i] = (j + 1 - currentPosition) * vec[j];
                rval[i] += (nextPosition-k) * vec[k];
                for (p = j + 1; p < k; p++) {
                    rval[i] += vec[p];
                }
            }
        }
        return rval;
    }

    /**
     * A standard percent difference calculation where the first
     * parameter is assumed to be known.  If that parameter is
     * zero, then no division by zero occurs.
     * @param oldV The "correct value", as used by percent difference calculations.
     * @param newV The "incorrect value", as used by percent difference calculations.
     * @return absolute value of (newV - oldV)/oldV assuming oldV!=0, or just newV otherwise.
     */
    public static double percentDiff(double oldV, double newV) {
        double diff=Math.abs(newV-oldV);
        if (oldV==0)
            return diff;
        return (diff/Math.abs(oldV));
    }

}
