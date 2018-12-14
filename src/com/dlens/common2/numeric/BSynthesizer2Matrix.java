
package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.ConvergenceException;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Bill Adams
 */
public class BSynthesizer2Matrix {
    //The columns in the limit matrix that are the goals
    //null means to use all columns
    int goalColumns[];
    //The unscaled supermatrix
    DoubleMatrix unscaledSupermatrix;
    //The scaled supermatrix (which is calculated by us on init)
    public DoubleMatrix scaledSupermatrix;
    //The cluster supermatrix telling local priorities of clusters
    DoubleMatrix clusterMatrix;
    //enumerates nodes in a cluster, so
    //nodesInCluster[clusterNumb][nodeNumb] gives the node in clusterNumb
    //in position (relative to that cluster of nodeNumb
    int nodesInCluster[][];
    //The cluster a node resides in, so
    //clusterOfNode[nodeNumb] would be the cluster that that node resides in
    //calculated at init.
    int clusterOfNode[];
    //The number of nodes, total, calculated at init
    int nnodes;
    //The number of clusters, calculated at init
    int nclusters;
    //The node synthesizer, indexed by
    //nodeSynthesizer[otherClusterNumb][wrtNodeNumb], i.e. by row, column.
    BSynthesizer2 nodeSynthesizer[][];
    //The cluster synthesizer
    BSynthesizer2 clusterSynthesizer[];
    //The error to allow for in v1 limit calculation
    double V1_ERROR=1e-8;
    //The error to allow for in v2 limit calculation
    double V2_ERROR=1e-8;

    public BSynthesizer2Matrix(DoubleMatrix unscaledSupermatrix,
            DoubleMatrix clusterMatrix, int nodesInCluster[][]) {
        goalColumns=null;
        this.unscaledSupermatrix=unscaledSupermatrix;
        this.clusterMatrix=clusterMatrix;
        this.nodesInCluster=nodesInCluster;
        nclusters=nodesInCluster.length;
        nnodes=unscaledSupermatrix.getSize();
        setupScaledSupermatrix();
        setupClusterOfNodeArray();
        setupNodeSynthesizers();
        setupClusterSynthesizers();
    }

    public static BSynthesizer2Matrix factorFromIdeal(DoubleMatrix idealMatrix,
            DoubleMatrix clusterMatrix, int nodesInCluster[][]) {
            return new BSynthesizer2Matrix(unscaledSupermatrix(idealMatrix, nodesInCluster),
                    clusterMatrix, nodesInCluster);
    }

    private static DoubleMatrix unscaledSupermatrix(DoubleMatrix idealMatrix, int nodesInCluster[][]) {
        double sum=0;
        double [][]idealData=idealMatrix.getDataUnsafe();
        int size=idealMatrix.getSize();
        int row;
        int nclusters=nodesInCluster.length;
        DoubleMatrix unscaledSupermatrix=new DoubleMatrix(idealMatrix);
        double [][]normalData=unscaledSupermatrix.getDataUnsafe();
        for(int col=0; col<size; col++) {
            //Work on a column, now we need to go through each cluster
            for(int cluster=0; cluster<nclusters; cluster++) {
                //total up the values in this column, this cluster
                sum=0;
                for(int rowClusterPos=0; rowClusterPos < nodesInCluster[cluster].length; rowClusterPos++) {
                    row=nodesInCluster[cluster][rowClusterPos];
                    sum+=Math.abs(idealData[row][col]);
                }
                if (sum!=0) {
                    for (int rowClusterPos = 0; rowClusterPos < nodesInCluster[cluster].length; rowClusterPos++) {
                        row = nodesInCluster[cluster][rowClusterPos];
                        normalData[row][col]/=sum;
                    }
                }
            }
        }
        return unscaledSupermatrix;
    }


    private void setupClusterSynthesizers() {
        clusterSynthesizer=new BSynthesizer2[nclusters];
        for(int colCluster=0; colCluster < nclusters; colCluster++) {
            clusterSynthesizer[colCluster]=
                    new BSynthesizer2(getClusterCriteriaPriorities(colCluster));
            clusterSynthesizer[colCluster].setAdjust(BSynthesizerStructuralAdjust.BY_CLUSTER);
        }
    }
    private void setupNodeSynthesizers() {
        nodeSynthesizer=new BSynthesizer2[nclusters][nnodes];
        for(int colNode=0; colNode < nnodes; colNode++) {
            for(int rowCluster=0; rowCluster < nclusters; rowCluster++) {
                nodeSynthesizer[rowCluster][colNode]=
                        new BSynthesizer2(getCriteriaPriorities(rowCluster, colNode));
                nodeSynthesizer[rowCluster][colNode].setAdjust(BSynthesizerStructuralAdjust.BY_CLUSTER);
            }
        }
    }

    private void setupClusterOfNodeArray() {
        clusterOfNode=new int[nnodes];
        for(int cluster=0; cluster < nclusters; cluster++) {
            for(int nodeP=0; nodeP < nodesInCluster[cluster].length; nodeP++) {
                clusterOfNode[nodesInCluster[cluster][nodeP]]=cluster;
            }
        }
    }
    private void setupScaledSupermatrix() {
        scaledSupermatrix=new DoubleMatrix(unscaledSupermatrix);
        double[][] rvalData=scaledSupermatrix.getDataUnsafe();
        int rowNode;
        int colNode;
        //Loop over the column cluster
        for(int colCluster=0; colCluster < nclusters; colCluster++) {
            //Loop over the column node
            for(int colNodeP=0; colNodeP<nodesInCluster[colCluster].length; colNodeP++) {
                colNode=nodesInCluster[colCluster][colNodeP];
                //Now we have the column we are working on rescaling, now we loop
                //over the rows.
                for(int rowCluster=0; rowCluster < nclusters; rowCluster++) {
                    //Get the multiplier
                    double scale=clusterMatrix.get(rowCluster, colCluster);
                    //Now loop over the nodes in this row cluster
                    for(int rowNodeP=0; rowNodeP < nodesInCluster[rowCluster].length; rowNodeP++) {
                        rowNode=nodesInCluster[rowCluster][rowNodeP];
                        rvalData[rowNode][colNode]*=scale;
                    }
                }
            }
        }
        //Now we need to normalize columns
        scaledSupermatrix.normalizeCols();
    }

    private double[] getCriteriaPriorities(int rowCluster, int colNode) {
        int ncriteria=nodesInCluster[rowCluster].length;
        double[] rval=new double[ncriteria];
        int rowNode;
        for(int crit=0; crit<ncriteria; crit++) {
            rowNode=nodesInCluster[rowCluster][crit];
            rval[crit]=unscaledSupermatrix.get(rowNode, colNode);
        }
        return rval;
    }

    private double[] getClusterCriteriaPriorities(int clusterCol) {
        double rval[]=new double[nclusters];
        for(int rowCluster=0; rowCluster < nclusters; rowCluster++) {
            rval[rowCluster]=clusterMatrix.get(rowCluster, clusterCol);
        }
        return rval;
    }

    public void setInverter(int rowCluster, int colNode, int criteriaPos,
            BSynthesizerCriteriaInverter inverter) {
        nodeSynthesizer[rowCluster][colNode].setInverter(criteriaPos, inverter);
    }

    public BSynthesizerCriteriaInverter getInverter(int rowCluster, int colNode, int criteriaPos) {
        return nodeSynthesizer[rowCluster][colNode].getInverter(criteriaPos);
    }

    public void setAdjust(int rowCluster, int colNode, BSynthesizerStructuralAdjust adjust) {
        nodeSynthesizer[rowCluster][colNode].setAdjust(adjust);
    }
    
    public BSynthesizerStructuralAdjust getAdjust(int rowCluster, int colNode) {
        return nodeSynthesizer[rowCluster][colNode].getAdjust();
    }

    public void setFormula(int rowCluster, int colNode, BSynthesizerFormula formula) {
        nodeSynthesizer[rowCluster][colNode].setFormula(formula);
    }

    public BSynthesizerFormula getFormula(int rowCluster, int colNode) {
        return nodeSynthesizer[rowCluster][colNode].getFormula();
    }

    public void setInverter(int colCluster, int criteriaPos,
            BSynthesizerCriteriaInverter inverter) {
        clusterSynthesizer[colCluster].setInverter(criteriaPos, inverter);
    }

    public BSynthesizerCriteriaInverter getInverter(int colCluster, int criteriaPos) {
        return clusterSynthesizer[colCluster].getInverter(criteriaPos);
    }

    public void setAdjust(int colCluster, BSynthesizerStructuralAdjust adjust) {
        clusterSynthesizer[colCluster].setAdjust(adjust);
    }

    public BSynthesizerStructuralAdjust getAdjust(int colCluster) {
        return clusterSynthesizer[colCluster].getAdjust();
    }

    public void setFormula(int colCluster, BSynthesizerFormula formula) {
        clusterSynthesizer[colCluster].setFormula(formula);
    }

    public BSynthesizerFormula getFormula(int colCluster) {
        return clusterSynthesizer[colCluster].getFormula();
    }

    public DoubleMatrix eval(DoubleMatrix data, int rowNodesInCluster[][]) {
        BSynthesizer2 clusterSyn;
        int oneCluster[][]=new int[1][nclusters];
        for(int i=0; i<nclusters; i++)
            oneCluster[0][i]=i;
        int colCluster;
        DoubleMatrix rval=new DoubleMatrix(data.getRows(), data.getCols());
        for(int col=0; col < nnodes; col++) {
            colCluster=clusterOfNode[col];
            DoubleMatrix clusterCols=eval(data, rowNodesInCluster, col);
            clusterSyn=clusterSynthesizer[colCluster];
            double result[]=clusterSyn.eval(clusterCols, rowNodesInCluster);
            rval.setColumn(result, col);
        }
        return rval;
    }

    public double[] evalAColumn(DoubleMatrix data, int rowNodesInCluster[][], int colNode) {
        int colCluster = clusterOfNode[colNode];
        DoubleMatrix clusterCols = eval(data, rowNodesInCluster, colNode);
        BSynthesizer2 clusterSyn = clusterSynthesizer[colCluster];
        double result[] = clusterSyn.eval(clusterCols, rowNodesInCluster);
        return result;
    }
    /**
     * Returns all of the evaluations for each row cluster, wrt colNode
     * @param data
     * @param rowNodesInCluster
     * @param col
     * @return
     */
    public DoubleMatrix eval(DoubleMatrix data, int rowNodesInCluster[][], int colNode) {
        int rows=data.getRows();
        DoubleMatrix rval=new DoubleMatrix(rows, nclusters);
        BSynthesizer2 syn;
        for(int cluster=0; cluster<nclusters; cluster++) {
            //First extract the relevent columns
            DoubleMatrix synData=data.extractColumns(nodesInCluster[cluster]);
            //Grab out synthesizer
            syn=nodeSynthesizer[cluster][colNode];
            //Synthesizer
            double result[]=syn.eval(synData, rowNodesInCluster);
            rval.setColumn(result, cluster);
        }
        return rval;
    }

    public DoubleMatrix limitV2ANP(DoubleMatrix idealSupermatrix, int rowNodesInCluster[][],
            double err, int maxIterations)
        throws ConvergenceException
    {
        DoubleMatrix lastValue=idealSupermatrix;
        DoubleMatrix nextValue=eval(lastValue, rowNodesInCluster);
        int iteration=0;
        while ((iteration <= maxIterations) && (!nextValue.isNear(lastValue, err))) {
            /*System.out.println("iteration: "+iteration);
            System.out.println("Last matrix:");
            System.out.println(lastValue.toString());
            System.out.println();*/
            lastValue=nextValue;
            nextValue=eval(lastValue, rowNodesInCluster);
            iteration++;
        }
        if (iteration > maxIterations)
            throw new ConvergenceException("Did not converge in "+maxIterations+" iterations.");
        return nextValue;
    }


    public DoubleMatrix limitV2ANPa(DoubleMatrix idealSupermatrix, int rowNodesInCluster[][],
            double err, int startPower, int maxIterations) throws ConvergenceException
    {
        DoubleMatrix lastValue=idealSupermatrix;
        DoubleMatrix nextValue=eval(lastValue, rowNodesInCluster);
        //Start doing a bunch of iterations, before looking for convergence
        for(int i=0; i<startPower; i++) {
            nextValue=eval(nextValue, rowNodesInCluster);
        }
        //Now look in earnest
        Vector<DoubleMatrix>powers=new Vector<DoubleMatrix>();
        powers.add(nextValue);
        DoubleMatrix distances=new DoubleMatrix(1);
        distances.set(0, 0, 0);
        int npowers;
        double distance;
        int minConvergentSeqLength=2*(idealSupermatrix.getSize()+2);
        for(int iteration=0; iteration < maxIterations; iteration++) {
            lastValue=nextValue;
            nextValue=eval(lastValue, rowNodesInCluster);
            npowers=powers.size();
            powers.add(nextValue);
            distances.addPlace();
            distances.set(npowers, npowers, 0);
            //Update distances
            for(int i=0; i < npowers; i++) {
                distance=nextValue.ratioDistance(powers.get(i), MatrixMetricEnum.Max);
                distances.set(npowers, i, distance);
                distances.set(i, npowers, distance);
            }
            DoubleMatrix limitOfSeq=limitOfSequence(powers, distances, minConvergentSeqLength, err);
            if (limitOfSeq!=null) {
                //Found limit
                return limitOfSeq;
            }
        }
        throw new ConvergenceException();
    }

    public static int[] getMinMax(Vector<Vector<Integer>>data) {
        int min;
        int max;
        int d;
        int first=data.get(0).get(0);
        min=first;
        max=first;
        for(int i=0; i<data.size(); i++) {
            Vector<Integer>level=data.get(i);
            for(int j=0; j<level.size(); j++) {
                d=level.get(j);
                if (d > max)
                    max=d;
                if (d < min)
                    min=d;
            }
        }
        return new int[] {min, max};
    }

    public static Vector<Vector<Integer>> convergentSubsequences(DoubleMatrix distances, double err) {
        Vector<Integer>positionsLeft=new Vector<Integer>();
        Vector<Vector<Integer>>rval=new Vector<Vector<Integer>>();
        for(int i=0; i<distances.getSize(); i++)
            positionsLeft.add(new Integer(i));
        while(positionsLeft.size() > 0) {
            Vector<Integer>convergentSubseq=extractAConvergentSubsequence(distances, err, positionsLeft);
            if (convergentSubseq.size() > 0) {
                rval.add(convergentSubseq);
            } else {
                break;
            }
        }
        return rval;
    }
    private static int getMin(Vector<Integer> vals) {
        int min=vals.get(0);
        for(int i=1; i<vals.size(); i++) {
            if (vals.get(i) < min) {
                min=vals.get(i);
            }
        }
        return min;
    }
    private static int getMax(Vector<Integer> vals) {
        int max=vals.get(0);
        for(int i=1; i<vals.size(); i++) {
            if (vals.get(i) > max) {
                max=vals.get(i);
            }
        }
        return max;
    }

    private DoubleMatrix limitOfSequence(Vector<DoubleMatrix>sequence, DoubleMatrix distances, int minConvergentSeqLength, double err)
    {
        Vector<Vector<Integer>>subseqs=convergentSubsequences(distances, err);
        if (subseqs.size()==0)
            return null;
        int minMax[]=getMinMax(subseqs);
        if (minMax[1]-minMax[0] < minConvergentSeqLength) {
            //No convergent subseqs yet, or not of sufficient size
            return null;
        }
        //Found convergent subsequence (or subsequences
        //Need to get last element of each convergent subsequence
        //and return their average
        Vector<DoubleMatrix>matrices=new Vector<DoubleMatrix>();
        for(int i=0; i<subseqs.size(); i++) {
            matrices.add(sequence.get(getMax(subseqs.get(i))));
        }
        //Have our matrices to average
        return DoubleMatrix.averageMatrices(matrices);
    }

    /**
     * Looks through the distance matrix, and finds a sequence converging from it.
     * It finds all elements that are in that subsequence (but there may be other subsequences
     * left to find).
     * @param distanceMatrix
     * @param err
     * @param positionsLeft
     * @return
     */
    public static Vector<Integer> extractAConvergentSubsequence(DoubleMatrix distanceMatrix,
            double err, Vector<Integer>positionsLeft)
    {
        //Find first pair of close things
        int row,col;
        if (positionsLeft==null) {
            positionsLeft=new Vector<Integer>();
            for(int i=0; i< distanceMatrix.getSize(); i++) {
                positionsLeft.add(new Integer(i));
            }
        }
        double[][] distanceData=distanceMatrix.getDataUnsafe();
        Iterator<Integer> rowIterator=positionsLeft.iterator();
        Iterator<Integer> colIterator=positionsLeft.iterator();
        Vector<Integer> rval=new Vector<Integer>();
        boolean found=false;
        while(colIterator.hasNext()) {
            col=colIterator.next();
            rowIterator=positionsLeft.iterator();
            while(rowIterator.hasNext()) {
                row=rowIterator.next();
                if ((row!=col) &&(distanceData[row][col] < err)) {
                    rval.add(new Integer(row));
                    positionsLeft.remove(new Integer(row));
                    rval.add(new Integer(col));
                    positionsLeft.remove(new Integer(col));
                    found=true;
                    break;
                }
            }
            if (found)
                break;
        }
        if (!found) {
            //No convergent subsequence in the positions left
            return new Vector<Integer>();
        }
        //Okay found a convergent pair, look for more
        //We want a transitive list, that is a and b are close, and
        //c is close to b, but not a (c will be sort of close to a, but
        //may not be within our error), we want to add c to the list.
        //So we need to loop through looking for things close to the things
        //in our list.
        while(found) {
            found=false;
            //Okay now look for new close things
            int rvalSizeAtStart=rval.size();
            for(int i=0; i<rvalSizeAtStart; i++) {
                int convergentElement=rval.get(i);
                //Look for all things in the positions left close
                //to convergentElement
                for(int j=(positionsLeft.size()-1); j>=0; j--) {
                    int testElement=positionsLeft.get(j);
                    if (distanceData[convergentElement][testElement] < err) {
                        //Was close enough add testElement to rval
                        rval.add(new Integer(testElement));
                        //Remove from positions left to check
                        positionsLeft.remove(new Integer(testElement));
                        //note we found one
                        found=true;
                    }
                }
            }
        }
        return rval;
    }

    public DoubleMatrix scaledSupermatrix() {
        DoubleMatrix scaledNormalSupermatrix=new DoubleMatrix(unscaledSupermatrix);
        double[][] rvalData=scaledNormalSupermatrix.getDataUnsafe();
        int rowNode;
        int colNode;
        //Loop over the column cluster
        for(int colCluster=0; colCluster < nclusters; colCluster++) {
            //Loop over the column node
            for(int colNodeP=0; colNodeP<nodesInCluster[colCluster].length; colNodeP++) {
                colNode=nodesInCluster[colCluster][colNodeP];
                //Now we have the column we are working on rescaling, now we loop
                //over the rows.
                for(int rowCluster=0; rowCluster < nclusters; rowCluster++) {
                    //Get the multiplier
                    double scale=clusterMatrix.get(rowCluster, colCluster);
                    //Now loop over the nodes in this row cluster
                    for(int rowNodeP=0; rowNodeP < nodesInCluster[rowCluster].length; rowNodeP++) {
                        rowNode=nodesInCluster[rowCluster][rowNodeP];
                        rvalData[rowNode][colNode]*=scale;
                    }
                }
            }
        }
        //Now we need to normalize columns
        scaledNormalSupermatrix.normalizeCols();
        return scaledNormalSupermatrix;
    }

    /**
     * Calculates the limit priorities using the v1 algorithm.
     * That is, it first calculates the cluster scaled supermatrix, then
     * does the traditional limit matrix, and averages the goal columns.
     * @return
     */
    public double[] limitPrioritiesV1() throws ConvergenceException {
        DoubleMatrix scaled=scaledSupermatrix();
        return scaled.limitPriorities(V1_ERROR);
    }

    /**
     * Calculates the actual limit priorities using the v2 algorithm.
     * That is it takes the v2 limit, and then the v1 global priorities
     * and averages the selected columns with those priorities
     */
    public double[] limitPriorities(DoubleMatrix input, int rowNodesInCluster[][]) throws ConvergenceException {
       DoubleMatrix v2Limit=limitV2(input, rowNodesInCluster);
       double columnPriorities[]=getLimitColumnPriorities(v2Limit);
       double rval[]=v2Limit.sumCols(columnPriorities);
       return rval;
    }

    private boolean isHierarchy() {
        return unscaledSupermatrix.isHierarchy(nnodes);
    }

    /**
     * This is a wrapper function that figures out the type
     * of supermatrix we have, then calculates
     * @return
     * @throws ConvergenceException
     */
    public DoubleMatrix limitV2(DoubleMatrix input, int rowNodesInCluster[][]) throws ConvergenceException {
        if (rowNodesInCluster==null)
            rowNodesInCluster=nodesInCluster;
        if (input.getSize()==0)
        	return new DoubleMatrix(input);
        if (isHierarchy()) {
            //Okay, we need to do the hierarchy calculation
            //However, for the default limit we should use different type of row clusters
            return limitV2Hierarchy(input, unscaledSupermatrix.getLevelsStartAtTop());
        } else {
            //return limitV2ANP(input, rowNodesInCluster, V2_ERROR, getMaxIterations());
            return limitV2ANPa(input, rowNodesInCluster, V2_ERROR, getStartPower(), getMaxIterations());
        }
    }

    private int getStartPower() {
        return nnodes*nnodes;
    }
    private int getMaxIterations() {
        return nnodes*nnodes+nnodes+100;
    }


    /**
     * The V2 synthesis algorithm for a hierarchy.
     */
    public DoubleMatrix limitV2Hierarchy(DoubleMatrix input, int rowNodesInCluster[][]) {
        int [][]levels=unscaledSupermatrix.getLevelsV2();
        DoubleMatrix rval=new DoubleMatrix(input);
        double[][] rvalData=rval.getDataUnsafe();
        int column;
        //Iterate over levels, notice we forgo the level
        //0 because it has zero columns, which were already
        //copied over from the idealSupermatrix.
        for(int level=1; level<levels.length; level++) {
            //Now synthesize the columns in this level
            for(int columnP=0; columnP < levels[level].length; columnP++) {
                column=levels[level][columnP];
                double synCol[]=evalAColumn(rval, rowNodesInCluster, column);
                //Got new values, now aggregate with existing column
                double newCol[]=limitV2HierarchyAggregator(rval, rowNodesInCluster, column, synCol);
                //Now have new value, fill in
                for(int row=0; row<nnodes; row++) {
                    rvalData[row][column]=newCol[row];
                }
            }
        }
        return rval;
    }

    private double[] limitV2HierarchyAggregator(DoubleMatrix rval, int rowNodesInCluster[][],
            int column, double []newVals)
    {
        DoubleMatrix aggregatorInput=new DoubleMatrix(rval.getRows(), 2);
        BSynthesizer2 aggregator=new BSynthesizer2(new double[] {.5, .5});
        aggregator.setAdjust(BSynthesizerStructuralAdjust.BY_CLUSTER);
        double adata[][]=aggregatorInput.getDataUnsafe();
        for(int row=0; row <adata.length; row++)
            adata[row][0]=rval.get(row, column);
        aggregatorInput.setColumn(newVals, 1);
        return aggregator.eval(aggregatorInput, rowNodesInCluster);
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
            int[] goalNodes=unscaledSupermatrix.getGoalPlaces();
            int goal;
            double sum=0;
            for(int goalP=0; goalP<goalNodes.length; goalP++) {
                goal=goalNodes[goalP];
                columnPriorities[goal]=1.0/goalNodes.length;
            }
            return columnPriorities;
        }
    }

    public int[] getGoalColumns() {
        return goalColumns;
    }

    public void setGoalColumns(int[] goalColumns) {
        this.goalColumns = goalColumns;
    }

    public void setAllAdjust(BSynthesizerStructuralAdjust adjust) {
        for(int i=0; i<nodeSynthesizer.length; i++) {
            for(int j=0; j<nodeSynthesizer[i].length; j++) {
                nodeSynthesizer[i][j].adjust=adjust;
            }
        }
        for(int i=0; i<clusterSynthesizer.length; i++) {
            clusterSynthesizer[i].adjust=adjust;
        }
    }

    public void setAllNodeAdjust(BSynthesizerStructuralAdjust adjust) {
        for(int i=0; i<nodeSynthesizer.length; i++) {
            for(int j=0; j<nodeSynthesizer[i].length; j++) {
                nodeSynthesizer[i][j].adjust=adjust;
            }
        }
    }

    public void setAllClusterAdjust(BSynthesizerStructuralAdjust adjust) {
        for(int i=0; i<clusterSynthesizer.length; i++) {
            clusterSynthesizer[i].adjust=adjust;
        }
    }

    public DoubleMatrix getUnscaledSupermatrix() {
        return new DoubleMatrix(unscaledSupermatrix);
    }

    public DoubleMatrix getScaledSupermatrix() {
        return new DoubleMatrix(scaledSupermatrix);
    }


}
