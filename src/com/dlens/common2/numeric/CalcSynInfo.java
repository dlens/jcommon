
package com.dlens.common2.numeric;

/**
 * Encodes the data for synthesis at a single node/cluster place.
 * 
 * @author Bill Adams
 */
public class CalcSynInfo {
    private SynthesizerEnum type;
    public CalcSynAlt alts[];
    public CalcSynInfo(SynthesizerEnum type, int nalts) {
        this.type=type;
        this.alts=new CalcSynAlt[nalts];
        for(int i=0; i<nalts; i++)
            this.alts[i]=new CalcSynAlt(false);
    }
    public void setInverted(int alt, boolean val) {
        alts[alt].setInverted(val);
    }
    
    public void synthesize(boolean useRows, double[][]vals, double scalers[], double rval[],
            NormalizerEnum pType)
    {
        double realVals[][]=getCorrectedMatrix(useRows, vals, pType);
        switch (type) {
            case ADDITIVE:
                synthesizeAdditive(realVals, scalers, rval, pType);
                break;
            case ADDITIVE_SD:
                synthesizeAdditiveSD(realVals, scalers, rval, pType);
                break;
            case MULTIPLICATIVE:
                synthesizeMultiplicative(realVals, scalers, rval, pType);
                break;
            case MULTIPLICATIVE_SD:
                synthesizeMultiplicativeSD(realVals, scalers, rval, pType);
                break;                
        }
    }
    private double invert(double v) {
        switch (type) {
            case ADDITIVE:
                if (v==0) return 0;
                return 1-v;
            case MULTIPLICATIVE:
            case MULTIPLICATIVE_SD:
                if (v==0) return 0;
                return 1.0/v;
            case ADDITIVE_SD:
                if (v==0) return 0;
                return -v;
            default:
                System.out.println("Error in invert.");
                return v;
        }
    }
    private double[][] getCorrectedMatrix(boolean transpose, double vals[][], NormalizerEnum pType) {
        double rval[][];
        if (transpose) {
            rval=new double[vals[0].length][vals.length];
            for(int i=0; i<rval.length; i++)
                for(int j=0; j<rval[i].length; j++)
                    rval[i][j]=vals[j][i];
        } else {
            rval=new double[vals.length][vals[0].length];            
            for(int i=0; i<rval.length; i++)
                for(int j=0; j<rval[i].length; j++)
                    rval[i][j]=vals[i][j];
        }
        for(int j=0; j<rval[0].length; j++) {
            if (alts[j].inverted) {
                for(int i=0; i<rval.length; i++) {
                    rval[i][j]=invert(rval[i][j]);
                }
                //Renormalize if necessary.
                rescaleColIfNeeded(rval, j, pType);
            }
        }
        return rval;
    }
    
    private void synthesizeAdditive(double[][]vals, double scalers[], double rval[],
            NormalizerEnum pType)
    {
        for(int i=0; i<rval.length; i++) {
            rval[i]=0;
            for(int j=0; j<scalers.length; j++) {
                try {
                    rval[i]+=scalers[j]*vals[i][j];
                } catch (Exception e) {
                    System.out.println("Hello.");
                }
            }
        }
    }

    private void synthesizeAdditiveSD(double[][]vals, double scalers[], double rval[],
            NormalizerEnum pType)
    {
        for(int i=0; i<rval.length; i++) {
            rval[i]=0;
            for(int j=0; j<scalers.length; j++) {
                try {
                    rval[i]+=scalers[j]*vals[i][j];
                } catch (Exception e) {
                    System.out.println("Hello.");
                }
            }
        }
    }

    private void synthesizeMultiplicative(double[][] vals, double scalers[], double rval[], NormalizerEnum pType) {
        boolean foundNonzero;
        for (int i = 0; i < rval.length; i++) {
            rval[i] = 1;
            foundNonzero=false;
            for (int j = 0; j < scalers.length; j++) {
                try {
                    if (vals[i][j]!=0) {
                        double base=vals[i][j];
                        double pow=scalers[j];
                        rval[i] *= Math.pow(vals[i][j],scalers[j]);
                        //rval[i]*=vals[i][j];
                        foundNonzero=true;
                    }
                } catch (Exception e) {
                    System.out.println("Hello synthesis problem.");
                    e.printStackTrace();
                }
            }
            if (!foundNonzero) {
                rval[i]=0;
            }
        }
        //Okay, now these values will be way out of line, they need
        rescaleIfNeeded(rval, pType);
    }

    private void synthesizeMultiplicativeSD(double[][] vals, double scalers[], double rval[], NormalizerEnum pType) {
        boolean foundNonzero;
        for (int i = 0; i < rval.length; i++) {
            rval[i] = 1;
            foundNonzero=false;
            for (int j = 0; j < scalers.length; j++) {
                try {
                    if (vals[i][j]!=0) {
                        double base=vals[i][j];
                        double pow=scalers[j];
                        //rval[i] *= Math.pow(vals[i][j],scalers[j]);
                        rval[i]*=vals[i][j];
                        foundNonzero=true;
                    }
                } catch (Exception e) {
                    System.out.println("Hello synthesis problem.");
                    e.printStackTrace();
                }
            }
            if (!foundNonzero) {
                rval[i]=0;
            }
        }
        //Okay, now these values will be way out of line, they need
        rescaleIfNeeded(rval, pType);
    }

    public void rescaleColIfNeeded(double [][]rval, int col, NormalizerEnum pType) {
        switch (pType) {
            case Normalized:
                DoubleMatrix.normalizeCol(rval, col);
                break;
            case Idealized:
                DoubleMatrix.idealizeCol(rval, col);
                break;
        }
        
    }
    
    public void rescaleIfNeeded(double []rval, NormalizerEnum pType) {
        switch (pType) {
            case Idealized:
                GenericMath.idealize(rval);
                break;
            case Normalized:
                GenericMath.normalize(rval);
                break;
        }
    }
    
    public void setType(SynthesizerEnum type) {
        this.type = type;
        switch (type) {
            case MULTIPLICATIVE:
                //setPriorityType(NormalizerEnum.Normalized);
        }
    }

    public SynthesizerEnum getType() {
        return type;
    }
    
    
}
