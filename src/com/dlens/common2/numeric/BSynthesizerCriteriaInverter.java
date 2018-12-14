
package com.dlens.common2.numeric;

import com.dlens.common2.interfaces.JDOMable;
import org.jdom.Element;

/**
 *
 * @author Handles inverting criteria, using whatever
 * formulas we wish.
 */
public enum BSynthesizerCriteriaInverter {
    /**Does no inverting at all*/
    EMPTY,
    /**Does 1-value*/
    PROBABILISTIC,
    /**Does 1/value then renormalizes*/
    RECIPROCAL_NORMAL,
    /**Does 1/value then reidealizes*/
    RECIPROCAL_IDEAL,
    /**Does -value*/
    NEGATIVE;
    public double[] eval(double []alts) {
        switch (this) {
            case EMPTY: {
                return alts;
            }
            case PROBABILISTIC: {
                return evalProb(alts);
            }
            case NEGATIVE: {
                return evalNeg(alts);
            }
            case RECIPROCAL_NORMAL: {
                return evalRecipNormal(alts);
            }
            case RECIPROCAL_IDEAL: {
                return evalRecipIdeal(alts);
            }
            default: {
                return alts;
            }
        }
    }

    public int toUniqueInt() {
        switch (this) {
            case EMPTY:
                return 0;
            case PROBABILISTIC:
                return 1;
            case NEGATIVE:
                return 2;
            case RECIPROCAL_NORMAL:
                return 3;
            case RECIPROCAL_IDEAL:
                return 4;
            default:
                return 0;
        }
    }

    public static BSynthesizerCriteriaInverter fromUniqueInt(int val) {
        switch (val) {
            case 1: return PROBABILISTIC;
            case 2: return NEGATIVE;
            case 3: return RECIPROCAL_NORMAL;
            case 4: return RECIPROCAL_IDEAL;
            case 0:
            default:
                return EMPTY;

        }
    }
    public void toAttribute(Element elt, String att) {
        elt.setAttribute(att, toUniqueInt()+"");
    }

    public static BSynthesizerCriteriaInverter fromAttribute(Element elt, String att) {
        try {
            String val=elt.getAttributeValue(att);
            int ival=Integer.parseInt(val);
            return fromUniqueInt(ival);
        } catch (Exception e) {
            //We are not picky here, just return empty if set incorrectly
            return EMPTY;
        }
    }
    
    public void eval(DoubleMatrix in, DoubleMatrix out, int col) {
        eval(in.getDataUnsafe(), out.getDataUnsafe(), col);
    }

    public void eval(double [][]alts, double[][] results, int col) {
        switch (this) {
            case PROBABILISTIC: {
                evalProb(alts, results, col);
                break;
            }
            case NEGATIVE: {
                evalNeg(alts, results, col);
                break;
            }
            case RECIPROCAL_NORMAL: {
                evalRecipNormal(alts, results, col);
                break;
            }
            case RECIPROCAL_IDEAL: {
                evalRecipIdeal(alts, results, col);
                break;
            }
            case EMPTY:
            default: {
                for(int row=0; row < alts.length; row++)
                    results[row][col]=alts[row][col];
            }
        }
    }

    private double[] evalProb(double alts[]) {
        double rval[]=new double[alts.length];
        for(int i=0; i<rval.length; i++)
            rval[i]=1-alts[i];
        return rval;
    }

    private double[] evalNeg(double alts[]) {
        double rval[]=new double[alts.length];
        for(int i=0; i<rval.length; i++)
            rval[i]=-alts[i];
        return rval;
    }

    private double[] evalRecipNormal(double alts[]) {
        double rval[]=new double[alts.length];
        double sum=0;
        for(int i=0; i<rval.length; i++) {
            if (alts[i]!=0) {
                rval[i]=1/alts[i];
            } else {
                rval[i]=0;
            }
            sum+=Math.abs(rval[i]);
        }
        if (sum!=0)
            for(int i=0; i<rval.length; i++)
                rval[i]/=sum;
        return rval;
    }

    private double[] evalRecipIdeal(double alts[]) {
        double rval[]=new double[alts.length];
        double max=0;
        double tmp=0;
        for(int i=0; i<rval.length; i++) {
            if (alts[i]!=0) {
                rval[i]=1/alts[i];
            } else {
                rval[i]=0;
            }
            tmp=Math.abs(rval[i]);
            if (tmp > max)
                max=tmp;
        }
        if (max!=0)
            for(int i=0; i<rval.length; i++)
                rval[i]/=max;
        return rval;
    }


    private void evalProb(double alts[][], double results[][], int col) {
        for(int i=0; i<alts.length; i++)
            results[i][col]=1-alts[i][col];
    }

    private void evalNeg(double alts[][], double results[][], int col) {
        for(int i=0; i<alts.length; i++)
            results[i][col]=-alts[i][col];
    }

    private void evalRecipNormal(double alts[][], double results[][], int col) {
        double sum=0;
        for(int i=0; i<alts.length; i++) {
            if (alts[i][col]!=0) {
                results[i][col]=1/alts[i][col];
            } else {
                results[i][col]=0;
            }
            sum+=Math.abs(results[i][col]);
        }
        if (sum!=0)
            for(int i=0; i<alts.length; i++)
                results[i][col]/=sum;
    }

    private void evalRecipIdeal(double alts[][], double results[][], int col) {
        double max=0;
        double tmp=0;
        for(int i=0; i<alts.length; i++) {
            if (alts[i][col]!=0) {
                results[i][col]=1/alts[i][col];
            } else {
                results[i][col]=0;
            }
            tmp=Math.abs(results[i][col]);
            if (tmp > max)
                max=tmp;
        }
        if (max!=0)
            for(int i=0; i<alts.length; i++)
                results[i][col]/=max;
    }
}

