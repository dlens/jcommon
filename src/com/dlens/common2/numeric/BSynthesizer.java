
package com.dlens.common2.numeric;

/**
 * This is the class that does the actual synthesis calculations
 * based upon the formulas.  It has an enum which describes what
 * formula should be used.  In addition it has a list of criteria
 * and for each criteria some flags.  All of the criteria information
 * is stored in a BSynthesizerCriteria object.
 *
 * This class includes the code to actually synthesize as well.
 * @author Bill Adams
 */
public class BSynthesizer {
    /**Represents the position of nodes in clusters.  So clusterPositions[i][*]
     * is the position of the nodes in cluster i, and clusterPositions[i][0] is
     * the position of the first node in cluster i, etc.  This is not the most
     * memory efficient way to store these things, but it is the most algorithmically
     * efficient way to store the information (and make the algorithms read easier).
     */
    int [][]clusterPositions;
    /**The column this synthesizer is for, assuming it is a node synthesizer
     */
    int nodeColumn;
    /**The cluster row this synthesizer is for, assuming it is a node synthesizer*/
    int clusterRow;
    int nCriteria;
    /**The wrtCluster position, assuming it is a cluster synthesizer.*/
    Integer wrtCluster=null;
    /**The ideal supermatrix*/
    DoubleMatrix idealSupermatrix;
    /**The normalized supermatrix, normalized by cluster*/
    DoubleMatrix normalSupermatrix;
    /**The cluster supermatrix*/
    DoubleMatrix clusterSupermatrix;
    /**How do we handle structural adjust here?*/
    BSynthesizerStructuralAdjust structAdjust=BSynthesizerStructuralAdjust.EMPTY;
    /**The formula to use*/
    BSynthesizerFormula formula=BSynthesizerFormula.ADDITIVE_SIMPLE;
    /**The criteria information
     */
    BSynthesizerCriteria []criteria;
    public BSynthesizer(int[][]clusterPositions, int clusterRow, int nodeColumn,
            DoubleMatrix idealSupermatrix, DoubleMatrix clusterSupermatrix) {
        this.clusterPositions=clusterPositions;
        this.nodeColumn=nodeColumn;
        this.clusterRow=clusterRow;
        this.idealSupermatrix=idealSupermatrix;
        this.clusterSupermatrix=clusterSupermatrix;
        setupNormalSupermatrix();
        setupCriteria();
    }

    public BSynthesizer(int[][]clusterPositions, int wrtCluster,
            DoubleMatrix idealSupermatrix, DoubleMatrix clusterSupermatrix) {
        this.clusterPositions=clusterPositions;
        this.wrtCluster=wrtCluster;
        this.idealSupermatrix=idealSupermatrix;
        this.clusterSupermatrix=clusterSupermatrix;
        setupNormalSupermatrix();
        setupCriteria();
    }

    public boolean isCluster() {
        return (wrtCluster!=null);
    }
    private void setupCriteria() {
        if (!isCluster()) {
            nCriteria = clusterPositions[clusterRow].length;
            criteria = new BSynthesizerCriteria[nCriteria];
            for (int i = 0; i < nCriteria; i++) {
                criteria[i] = new BSynthesizerCriteria(clusterPositions[clusterRow][i],
                        normalSupermatrix.get(clusterPositions[clusterRow][i], nodeColumn),
                        BSynthesizerCriteriaInverter.EMPTY);
            }
        } else {
            nCriteria = clusterPositions.length;
            criteria = new BSynthesizerCriteria[nCriteria];
            for (int i = 0; i < nCriteria; i++) {
                criteria[i] = new BSynthesizerCriteria(wrtCluster,
                        clusterSupermatrix.get(i, wrtCluster),
                        BSynthesizerCriteriaInverter.EMPTY);
            }
        }
    }

    private void setupNormalSupermatrix() {
        double sum=0;
        double [][]idealData=idealSupermatrix.getDataUnsafe();
        int size=idealSupermatrix.getSize();
        int row;
        int nclusters=clusterPositions.length;
        normalSupermatrix=new DoubleMatrix(idealSupermatrix);
        double [][]normalData=normalSupermatrix.getDataUnsafe();
        for(int col=0; col<size; col++) {
            //Work on a column, now we need to go through each cluster
            for(int cluster=0; cluster<nclusters; cluster++) {
                //total up the values in this column, this cluster
                sum=0;
                for(int rowClusterPos=0; rowClusterPos < clusterPositions[cluster].length; rowClusterPos++) {
                    row=clusterPositions[cluster][rowClusterPos];
                    sum+=Math.abs(idealData[row][col]);
                }
                if (sum!=0) {
                    for (int rowClusterPos = 0; rowClusterPos < clusterPositions[cluster].length; rowClusterPos++) {
                        row = clusterPositions[cluster][rowClusterPos];
                        normalData[row][col]/=sum;
                    }
                }
            }
        }
    }

    /**Performs the actual synthesis steps, not on any given matrix, but
     * for these particular inputs.  These inputs need to have the same number
     * of rows as there are nodes in the idealSupermatrix (so that structural
     * adjust can work).
     * @param alts
     * @return
     */
    public double[] synthesize(double[][] alts) {
        switch (this.formula) {
            case ADDITIVE_SIMPLE: return synthesizeAddSimple(alts);
            case MULTIPLICATION_SIMPLE: return synthesizeMultSimple(alts);
            default: return synthesizeAddSimple(alts);
        }
    }

    private double[][] invertColumns(double alts[][]) {
        int nrows=alts.length;
        int ncols=alts[0].length;
        double [][]rval=new double[nrows][ncols];
        BSynthesizerCriteriaInverter inverter;
        for(int col=0; col < ncols; col++) {
            inverter=criteria[col].inverter;
            inverter.eval(alts, rval, col);
        }
        return rval;
    }
    private double[] synthesizeAddSimple(double originalAlts[][]) {
        double alts[][]=invertColumns(originalAlts);
        int nAlts=alts.length;
        if (alts.length!=idealSupermatrix.getRows()) {
            throw new IllegalArgumentException("alts[][] should have same number of rows as idealSupermatrix.");
        }
        double[] rval=new double[alts.length];
        int nCriteria=criteria.length;
        int critCol;
        double []rawCriteriaPriorities=criteriaPriorities();
        double []criteriaPriorities=new double[rawCriteriaPriorities.length];
        for(int cluster=0; cluster < clusterPositions.length; cluster++) {
            //Loop over clusters, hand each cluster separately
            //First get structurally adjusted priorities
            structuralAdjust(rawCriteriaPriorities, criteriaPriorities, clusterPositions[cluster], alts);
            //Now, let's calculate each alts value in the cluster.
            for(int rowInfo=0; rowInfo<clusterPositions[cluster].length; rowInfo++) {
                //This is the alternative row
                int altRow=clusterPositions[cluster][rowInfo];
                rval[altRow]=0;
                //Now we need to add up across the criteria
                for(int criteriaNumb=0; criteriaNumb < nCriteria; criteriaNumb++) {
                    critCol=criteria[criteriaNumb].column;
                    rval[altRow]+=criteriaPriorities[criteriaNumb]*alts[altRow][criteriaNumb];
                }
            }
        }
        return rval;
    }

    private void structuralAdjust(double raw[], double real[], int clusterPositions[], double [][]alts) {
        int ncols=alts[0].length;
        int nrows=alts.length;
        switch (this.structAdjust) {
            case EMPTY: {
                for(int i=0; i<real.length; i++)
                    real[i]=raw[i];
                break;
            }
            case BY_CLUSTER: {
                for(int col=0; col < ncols; col++) {
                    boolean foundNonzero=false;
                    for(int row=0; row < nrows; row++) {
                        if (alts[row][col]!=0) {
                            foundNonzero=true;
                            break;
                        }
                    }
                    if (foundNonzero) {
                        real[col]=raw[col];
                    } else {
                        real[col]=0;
                    }
                }
                double sum=0;
                for(int i=0; i<real.length; i++) {
                    sum+=Math.abs(real[i]);
                }
                if (sum!=0)
                    for(int i=0; i<real.length; i++)
                        real[i]/=sum;
            }
        }
    }

    private double[] criteriaPriorities() {
        int nCriteria=criteria.length;
        double criteriaPriorities[]=new double[nCriteria];
        for(int i=0; i<nCriteria; i++) {
            if (isCluster()) {
                criteriaPriorities[i]=clusterSupermatrix.get(i, wrtCluster);
            } else {
                criteriaPriorities[i]=normalSupermatrix.get(clusterPositions[clusterRow][i], nodeColumn);
            }
        }
        return criteriaPriorities;
    }

    private double[] synthesizeMultSimple(double alts[][]) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setCriteriaInvertType(int criteriaPos, BSynthesizerCriteriaInverter inverter) {
        criteria[criteriaPos].inverter=inverter;
    }
}
