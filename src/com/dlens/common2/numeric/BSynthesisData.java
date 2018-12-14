
package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.ConvergenceException;

/**
 * @author Bill Adams
 */
public class BSynthesisData {
    double V2_ERROR=1e-10;
    double V1_ERROR=1e-10;
    //Which columns to use as goals for pulling out global priorities.
    //If null we use all of them.
    int[] goalColumns=null;
    DoubleMatrix idealSupermatrix;
    DoubleMatrix normalSupermatrix;
    DoubleMatrix clusterSupermatrix;
    int nnodes;
    int nclusters;
    int clusterPositions[][];
    int nodeClusterRelationship[];
    //indexed by cluster,node (i.e. row, col)
    BSynthesizer nodeSynthesizers[][];
    BSynthesizer clusterSynthesizers[];

    public BSynthesisData(DoubleMatrix idealSupermatrix,
            DoubleMatrix clusterSupermatrix, int clusterPositions[][]) {
        this.idealSupermatrix=idealSupermatrix;
        this.clusterPositions=clusterPositions;
        this.clusterSupermatrix=clusterSupermatrix;
        nnodes=idealSupermatrix.getSize();
        nclusters=clusterPositions.length;
        setupNormalSupermatrix();
        setupNodeSynthesizers();
        setupClusterSynthesizers();
        setupNodeClusterRelationship();
    }

    private void setupNodeClusterRelationship() {
        int node;
        nodeClusterRelationship=new int[nnodes];
        for(int cluster=0; cluster < nclusters; cluster++) {
            for(int nodeP=0; nodeP < clusterPositions[cluster].length; nodeP++) {
                node=clusterPositions[cluster][nodeP];
                nodeClusterRelationship[node]=cluster;
            }
        }
    }
    private void setupNodeSynthesizers() {
        nodeSynthesizers=new BSynthesizer[nclusters][nnodes];
        for(int cluster=0; cluster < nclusters; cluster++) {
            for(int node=0; node < nnodes; node++) {
                nodeSynthesizers[cluster][node]=
                        new BSynthesizer(clusterPositions, cluster, node,
                        idealSupermatrix, clusterSupermatrix);
            }
        }
    }

    private void setupClusterSynthesizers() {
        clusterSynthesizers=new BSynthesizer[nclusters];
        for(int cluster=0; cluster < nclusters; cluster++) {
            clusterSynthesizers[cluster]=
                    new BSynthesizer(clusterPositions, cluster, idealSupermatrix, clusterSupermatrix);
            clusterSynthesizers[cluster].structAdjust=BSynthesizerStructuralAdjust.BY_CLUSTER;
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

    public double[] synthesisStep(DoubleMatrix lastResult, int column) {
        int wrtCluster=nodeClusterRelationship[column];
        double [][]intermediates=new double[nnodes][nclusters];
        //Get node synthesized values
        for(int cluster=0; cluster < nclusters; cluster++) {
            //Synthesize every cluster in this column
            double [][]alts=lastResult.extractColumns(clusterPositions[cluster]).getDataUnsafe();
            BSynthesizer nodeSyn=nodeSynthesizers[cluster][column];
            double tmpCol[]=nodeSyn.synthesize(alts);
            //Now store
            for(int row=0; row < nnodes; row++) {
                intermediates[row][cluster]=tmpCol[row];
            }
        }
        //Now synthesize by column
        return clusterSynthesizers[wrtCluster].synthesize(intermediates);
    }

    public DoubleMatrix synthesisStep(DoubleMatrix lastResult) {
        DoubleMatrix rval=new DoubleMatrix(lastResult.getSize());
        double rvalData[][]=rval.getDataUnsafe();
        for(int wrtCluster=0; wrtCluster < nclusters; wrtCluster++) {
            for(int colP=0; colP < clusterPositions[wrtCluster].length; colP++) {
                int column=clusterPositions[wrtCluster][colP];
                double []rvalCol=synthesisStep(lastResult, column);
                for(int row=0; row < rvalCol.length; row++) {
                    rvalData[row][column]=rvalCol[row];
                }
            }
        }
        return rval;
    }

    public DoubleMatrix limitV2FirstAttempt(double err, int maxIterations)
        throws ConvergenceException
    {
        DoubleMatrix lastValue=idealSupermatrix;
        DoubleMatrix nextValue=synthesisStep(lastValue);
        int iteration=0;
        while ((iteration <= maxIterations) && (!nextValue.isNear(lastValue, err))) {
            /*System.out.println("iteration: "+iteration);
            System.out.println("Last matrix:");
            System.out.println(lastValue.toString());
            System.out.println();*/
            lastValue=nextValue;
            nextValue=synthesisStep(lastValue);
            iteration++;
        }
        if (iteration > maxIterations)
            throw new ConvergenceException("Did not converge in "+maxIterations+" iterations.");
        return nextValue;
    }

    public void setCriteriaInverter(int clusterRow, int nodeCol, int criteriaNumb,
            BSynthesizerCriteriaInverter inverter) {
        nodeSynthesizers[clusterRow][nodeCol].setCriteriaInvertType(criteriaNumb, inverter);
    }

    /**
     * Calculates the limit matrix using the old algorithm.
     * This is used for global priorities calculation, since
     * the new limit matrix may have differing columns, we
     * take a weighted average of those columns.
     * @param err
     */
    public void calcOldLimitMatrix(double err) {
    }

    public DoubleMatrix clusterScaledNormalSupermatrix() {
        DoubleMatrix scaledNormalSupermatrix=new DoubleMatrix(normalSupermatrix);
        double[][] rvalData=scaledNormalSupermatrix.getDataUnsafe();
        int rowNode;
        int colNode;
        //Loop over the column cluster
        for(int colCluster=0; colCluster < nclusters; colCluster++) {
            //Loop over the column node
            for(int colNodeP=0; colNodeP<clusterPositions[colCluster].length; colNodeP++) {
                colNode=clusterPositions[colCluster][colNodeP];
                //Now we have the column we are working on rescaling, now we loop
                //over the rows.
                for(int rowCluster=0; rowCluster < nclusters; rowCluster++) {
                    //Get the multiplier
                    double scale=clusterSupermatrix.get(rowCluster, colCluster);
                    //Now loop over the nodes in this row cluster
                    for(int rowNodeP=0; rowNodeP < clusterPositions[rowCluster].length; rowNodeP++) {
                        rowNode=clusterPositions[rowCluster][rowNodeP];
                        rvalData[rowNode][colNode]*=scale;
                    }
                }
            }
        }
        //Now we need to normalize columns
        return scaledNormalSupermatrix;
    }

    /**
     * Calculates the limit priorities using the v1 algorithm.
     * That is, it first calculates the cluster scaled supermatrix, then
     * does the traditional limit matrix, and averages the goal columns.
     * @return
     */
    public double[] limitPrioritiesV1() throws ConvergenceException {
        DoubleMatrix clusterScaled=clusterScaledNormalSupermatrix();
        return clusterScaled.limitPriorities(V1_ERROR);
    }

    private double []getLimitColumnPriorities(DoubleMatrix v2Limit) throws ConvergenceException {
        if (!isHierarchy()) {
            double[] tmpColumnPriorities = limitPrioritiesV1();
            double[] columnPriorities;
            int goal;
            //Okay, we only need to do the averaging over goal columns that are not
            //zero.  We means we need to look through all column priorities and
            //zero out ones that are not goals, and also zero out ones that have
            //a zero column in the limit.
            if (goalColumns == null) {
                //we are to use all of them, so nothing to do
                columnPriorities = tmpColumnPriorities;
            } else {
                columnPriorities = new double[tmpColumnPriorities.length];
                for (int goalP = 0; goalP < goalColumns.length; goalP++) {
                    goal = goalColumns[goalP];
                    if (!v2Limit.isColumnZero(goal)) {
                        columnPriorities[goal] = tmpColumnPriorities[goal];
                    }
                }
                //Okay we have copied over the relevant data, now we need to renormalize
                GenericMath.normalize(columnPriorities);
            }
            return columnPriorities;
        } else {
            double[] columnPriorities=new double[nnodes];
            int[] goalNodes=idealSupermatrix.getGoalPlaces();
            int goal;
            double sum=0;
            for(int goalP=0; goalP<goalNodes.length; goalP++) {
                goal=goalNodes[goalP];
                columnPriorities[goal]=1.0/goalNodes.length;
            }
            return columnPriorities;
        }
    }
    /**
     * Calculates the actual limit priorities using the v2 algorithm.
     * That is it takes the v2 limit, and then the v1 global priorities
     * and averages the selected columns with those priorities
     */
    public double[] limitPriorities() throws ConvergenceException {
       DoubleMatrix v2Limit=limitV2();
       double columnPriorities[]=getLimitColumnPriorities(v2Limit);
       double rval[]=v2Limit.sumCols(columnPriorities);
       return rval;
    }

    private boolean isHierarchy() {
        return idealSupermatrix.isHierarchy(nnodes);
    }
    /**
     * This is a wrapper function that figures out the type
     * of supermatrix we have, then calculates
     * @return
     * @throws ConvergenceException
     */
    public DoubleMatrix limitV2() throws ConvergenceException {
        if (isHierarchy()) {
            //Okay, we need to do the hierarchy calculation
            return limitV2Hierarchy();
        } else {
            return limitV2FirstAttempt(V2_ERROR, getMaxIterations());
        }
    }

    private int getMaxIterations() {
        return nnodes*nnodes+nnodes+100;
    }

    public int[] getGoalColumns() {
        return goalColumns;
    }

    public void setGoalColumns(int[] goalColumns) {
        this.goalColumns = goalColumns;
    }

    /**
     * The V2 synthesis algorithm for a hierarchy.
     */
    public DoubleMatrix limitV2Hierarchy() {
        int [][]levels=idealSupermatrix.getLevelsV2();
        DoubleMatrix rval=new DoubleMatrix(idealSupermatrix);
        double[][] rvalData=rval.getDataUnsafe();
        int column;
        //Iterate over levels, notice we forgo the level
        //0 because it has zero columns, which were already
        //copied over from the idealSupermatrix.
        for(int level=1; level<levels.length; level++) {
            //Now synthesize the columns in this level
            for(int columnP=0; columnP < levels[level].length; columnP++) {
                column=levels[level][columnP];
                double synCol[]=synthesisStep(rval, column);
                //Got new values, now aggregate with existing column
                double newCol[]=limitV2HierarchyAggregate(rval, column, synCol);
                //Now have new value, fill in
                for(int row=0; row<nnodes; row++) {
                    rvalData[row][column]=newCol[row];
                }
            }
        }
        return rval;
    }

    private double[] limitV2HierarchyAggregate(DoubleMatrix mat, int column, double []synCol) {
        int rows=mat.getRows();
        double rval[]=new double[rows];
        double matData[][]=mat.getDataUnsafe();
        for(int row=0; row < rows; row++) {
            int count=0;
            if (matData[row][column]!=0)
                count++;
            if (synCol[row]!=0)
                count++;
            rval[row]=matData[row][column]+synCol[row];
            if (count!=0) {
                rval[row]/=count;
            }
        }
        return rval;
    }
}
