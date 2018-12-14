
package com.dlens.common2.numeric;

/**
 * This is the second attempt at the new synthesis algorithm.  It is
 * more general, and at the same time simpler, one hopes.  This is just
 * a single column->cluster synthesizer, or cluster synthesizer.  For
 * a full matrix one see BSynthesizer2Matrix
 * @author Bill Adams
 */
public class BSynthesizer2 {
    BSynthesizerCriteriaInverter inverter[];
    int ncriteria;
    double criteriaPriorities[];
    BSynthesizerStructuralAdjust adjust=BSynthesizerStructuralAdjust.EMPTY;
    BSynthesizerFormula formula=BSynthesizerFormula.ADDITIVE_SIMPLE;
    public BSynthesizer2(double criteriaPriorities[]) {
        this.criteriaPriorities=criteriaPriorities;
        this.ncriteria=criteriaPriorities.length;
        setupInverters();
    }

    private void setupInverters() {
        inverter=new BSynthesizerCriteriaInverter[ncriteria];
        for(int i=0; i<ncriteria; i++) {
            inverter[i]=BSynthesizerCriteriaInverter.EMPTY;
        }
    }
    public void setAdjust(BSynthesizerStructuralAdjust adjust) {
        this.adjust = adjust;
    }

    public BSynthesizerStructuralAdjust getAdjust() {
        return adjust;
    }

    public void setFormula(BSynthesizerFormula formula) {
        this.formula = formula;
    }

    public BSynthesizerFormula getFormula() {
        return formula;
    }

    public void setInverter(int criteriaPos, BSynthesizerCriteriaInverter inv) {
        inverter[criteriaPos]=inv;
    }

    public BSynthesizerCriteriaInverter getInverter(int criteriaPos) {
        return inverter[criteriaPos];
    }

    public DoubleMatrix invertCols(DoubleMatrix input) {
        if (input.getCols()!=ncriteria)
            throw new IllegalArgumentException("Inverter has ncrit="+ncriteria+" matrix has ncols="+input.getCols());
        DoubleMatrix rval=new DoubleMatrix(input);
        for(int col=0; col<ncriteria; col++) {
            inverter[col].eval(input, rval, col);
        }
        return rval;
    }

    public double[] eval(DoubleMatrix data, int rowNodesInCluster[][]) {
        DoubleMatrix inverted=invertCols(data);
        switch(formula) {
            case MULTIPLICATION_SIMPLE: return evalMultiplicationSimple(inverted, rowNodesInCluster);
            case ADDITIVE_SIMPLE:
            default:
                return evalAdditiveSimple(inverted, rowNodesInCluster);
        }
    }

    public double[]evalAdditiveSimple(DoubleMatrix data, int rowNodesInCluster[][]) {
        //init rval
        double []rval=new double[data.getRows()];
        double[][] dataData=data.getDataUnsafe();
        int rowNode;
        double value;
        //Eval for each cluster
        for(int rowCluster=0; rowCluster < rowNodesInCluster.length; rowCluster++) {
            double structuralAdjustFactor=getStructuralAdjustFactor(data, rowNodesInCluster, rowCluster);
            if (structuralAdjustFactor==0)
                structuralAdjustFactor=1;
            double realPriorities[]=multiply(1/structuralAdjustFactor, criteriaPriorities);
            for(int rowNodeP=0; rowNodeP < rowNodesInCluster[rowCluster].length; rowNodeP++) {
                rowNode=rowNodesInCluster[rowCluster][rowNodeP];
                value=0;
                for(int col=0; col < ncriteria; col++) {
                    value+=realPriorities[col]*dataData[rowNode][col];
                }
                rval[rowNode]=value;
            }
        }
        return rval;
    }

    public double[] multiply(double factor, double vector[]) {
        double rval[]=new double[vector.length];
        for(int i=0; i<rval.length; i++)
            rval[i]=factor*vector[i];
        return rval;
    }

    public double getStructuralAdjustFactor(DoubleMatrix data, int rowNodesInCluster[][], int rowCluster) {
        switch (adjust) {
            case BY_CLUSTER: {
                double factor=0;
                int rowNode;
                double tmpFactor;
                for(int rowNodeP=0; rowNodeP < rowNodesInCluster[rowCluster].length; rowNodeP++) {
                    tmpFactor=0;
                    rowNode=rowNodesInCluster[rowCluster][rowNodeP];
                    for(int col=0; col<data.getCols(); col++) {
                        if (data.get(rowNode, col)!=0) {
                            tmpFactor+=criteriaPriorities[col];
                        }
                    }
                    if (tmpFactor > factor) {
                        factor=tmpFactor;
                    }
                }
                if (factor > 0) {
                    return factor;
                } else {
                    return 1;
                }
            }
            case BY_COLUMN: {
                double factor=0;
                double tmpFactor;
                for(int rowNode=0; rowNode < data.getRows(); rowNode++) {
                    tmpFactor=0;
                    for(int col=0; col<data.getCols(); col++) {
                        if (data.get(rowNode, col)!=0) {
                            tmpFactor+=criteriaPriorities[col];
                        }
                    }
                    if (tmpFactor > factor) {
                        factor=tmpFactor;
                    }
                }
                if (factor > 0) {
                    return factor;
                } else {
                    return 1;
                }
            }
            default:
            case EMPTY:
                return 1;
        }
    }

    public double[]evalMultiplicationSimpleOld(DoubleMatrix data, int rowsInCluster[][]) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public double[]evalMultiplicationSimple(DoubleMatrix data, int rowNodesInCluster[][]) {
        //init rval
        double []rval=new double[data.getRows()];
        double[][] dataData=data.getDataUnsafe();
        int rowNode;
        double value;
        double tmpVal;
        //Eval for each cluster
        for(int rowCluster=0; rowCluster < rowNodesInCluster.length; rowCluster++) {
            double structuralAdjustFactor=getStructuralAdjustFactor(data, rowNodesInCluster, rowCluster);
            if (structuralAdjustFactor==0)
                structuralAdjustFactor=1;
            double realPriorities[]=multiply(1/structuralAdjustFactor, criteriaPriorities);
            for(int rowNodeP=0; rowNodeP < rowNodesInCluster[rowCluster].length; rowNodeP++) {
                rowNode=rowNodesInCluster[rowCluster][rowNodeP];
                value=1;
                for(int col=0; col < ncriteria; col++) {
                    tmpVal=Math.abs(dataData[rowNode][col]);
                    if (tmpVal!=0)
                        value*=Math.pow(tmpVal, realPriorities[col]);
                    if (dataData[rowNode][col]<0)
                        value*=-1;
                }
                rval[rowNode]=value;
            }
        }
        return rval;
    }

}
