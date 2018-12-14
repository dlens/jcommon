package com.dlens.common2.numeric;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.dlens.common2.exceptions.ConvergenceException;
import com.dlens.common2.exceptions.NonHierarchyException;
import com.dlens.common2.exceptions.XMLFormatException;

/**
 *This class is a square matrix of doubles.  It is used for most of the ANP calculations
 *in this library.
 */

public class DoubleSquareMatrix implements java.io.Serializable {
    /**This is a nice standard eigen-error to use.  You may find
     *you need to use a different one, though.*/
    public static final double StandardEigenError=1e-14;
    /**The maximum number of iterations we will go through
     *in the eigenvector calculation.
     */
    static final int MaxEigenIterations = 100000;
    /**The maximum number of iterations to go through in computing a limit matrix.  Each
     *iteration is actually an iteration of checking for cycles of order 1,2,...,SizeOfMatrix.
     */
    static final int MaxLimitIterations = 1000;
    /**This flag says that if a place in the matrix is completely uncompared, then its
     *priority should be returned as zero.  This is <b>not followed</b> if every place
     *is not compared.  In that case we return all equal priorities.
     */
    static final boolean ZeroCompletelyUncompared = true;
    /**The size of the matrix.*/
    private int size;
    /**The actual data for the matrix.*/
    private double data[][];
    /**
     *The constructor takes the size of the new
     *square matrix to create.  Zero sizes are
     *allowed, as are negative sizes.  If the size was
     *negative, we simply assume you meant zero.
     */
    public DoubleSquareMatrix(int size) {
        if (size < 0) {
            size=0;
        }
        data=new double[size][size];
        this.size=size;
    }

    /**
     *Simply constructs a matrix of size zero.
     */
    public DoubleSquareMatrix() {
        this(0);
    }
    /**
     *Constructs a copy of the matrix.
     *@param src The matrix to copy from.
     */
    public DoubleSquareMatrix(DoubleSquareMatrix src)
        throws ArrayIndexOutOfBoundsException
    {
        size=src.size;
        data = new double[size][size];
        for(int i=0;i<size;i++)
            for(int j=0;j<size;j++)
                data[i][j]=src.data[i][j];
    }

    public DoubleSquareMatrix(int size, String data, boolean upperTriangle) {
    	this(size);
    	int count=0;
    	String datas[]=data.split("\\s+");
    	if (!upperTriangle) {
    		for(int i=0; i<size; i++) {
    			for(int j=0; j<size; j++) {
    				try {
    					this.data[i][j]=Double.parseDouble(datas[count]);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    				count++;
    			}
    		}
    	} else {    		
    		for(int i=0; i<size; i++) {
    			this.data[i][i]=1;
    			for(int j=i+1; j<size; j++) {
    				try {
    					this.setReflexive(i, j, Double.parseDouble(datas[count]));
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    				count++;    			
    			}
    		}    		
    	}
    }
    
    /**Creates a doublesquarematrix from a simple string that has the values of the matrix.
     */
    public DoubleSquareMatrix(String input, int size) {
        this(size);
        data=this.parseToString(input, size);
    }
    
    /**Resets the data in the matrix to be ratios of the given vector.*/
    public void ratioMatrix(DoubleVector dv) {
        size=dv.getSize();
        data = new double[size][size];
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                if (i==j) {
                    data[i][j]=1;
                } else {
                    if (dv.get(j)!=0.0)
                        data[i][j]=dv.get(i)/dv.get(j);
                }
            }
        }
    }

    /**Resets the data in the matrix to be ratios of the given vector.*/
    public void ratioMatrix(double dv[]) {
        DoubleVector v = new DoubleVector(dv);
        ratioMatrix(v);
    }
    /**
     *Adds a place at the end, and sets the diagonal to 1 there.
     *@return The index of the added place.
     */
    public int addPlaceId() {
        int rval = addPlace();
        data[rval][rval]=1;
        return rval;
    }
    /** Inserts a place at the end of the matrix.
     *@return The index of the added place.
     */
    public int addPlace() {
        double newData[][] = new double[size+1][size+1];
        for(int i=0;i<size;i++) {
            for(int j=0;j<size;j++) {
                newData[i][j]=data[i][j];
            }
        }
        data=null;
        data=newData;
        size++;
        return size - 1;
    }

    /** Removes a place from the matrix.
      * @param place The place to remove.
      * @throws ArrayIndexOutOfBoundsException If <code>place</code> was out of bounds.
     */
    public void rmPlace(int place)
        throws ArrayIndexOutOfBoundsException
    {
        int i,j;
        if ((place < 0) || (place >= size)) {
            throw new ArrayIndexOutOfBoundsException(place);
        }
        /*Okay, if we are here, we are safe.*/
        double newData[][] = new double[size-1][size-1];
        for(i=0;i<place;i++) {
            for(j=0;j<place;j++) {
                newData[i][j]=data[i][j];
            }
            for(j=place;j<(size-1);j++) {
                newData[i][j]=data[i][j+1];
            }
        }
        for(i=place;i<(size-1);i++) {
            for(j=0;j<place;j++) {
                newData[i][j]=data[i+1][j];
            }
            for(j=place;j<(size-1);j++) {
                newData[i][j]=data[i+1][j+1];
            }
        }
        data=null;
        data=newData;
        size--;
    }
    /**
     *Returns the size of the matrix.
     *@return The size of the matrix.
     */
    public int getSize() {
        return size;
    }


    /**
     *Returns the size of the matrix.
     *@return The size of the matrix.
     */
    public int size() {
        return size;
    }

    /**
     *Used to set the value in our matrix.
     *@param row The row where we want to set.
     *@param col The column where we want to set.
     *@param val The value to put in the matrix at place <code>(row, col)</code>.
     *@throws ArrayIndexOutOfBoundsException If <code>row</code> or <code>col</code> was out of bounds.
     */
    public void set(int row, int col, double val)
        throws ArrayIndexOutOfBoundsException
    {
        data[row][col]=val;
    }

    /**
     *Used to reflexively set the value in our matrix.  In other words it sets (row,col) to
     *val and (col, row) to 1/val.
     *@param row The row where we want to set.
     *@param col The column where we want to set.
     *@param val The value to put in the matrix at place <code>(row, col)</code>.
     *@throws ArrayIndexOutOfBoundsException If <code>row</code> or <code>col</code> was out of bounds.
     */
    public void setReflexive(int row, int col, double val)
        throws ArrayIndexOutOfBoundsException
    {
        data[row][col]=val;
        if (val != 0)
            data[col][row]=1/val;
        else
            data[col][row]=0;
    }

    /**
     *Used to access the data in our matrix.
     *@param row The row to access.
     *@param col The column to access.
     *@return The value at place <code>(row, col)</code> in our matrix.
     *@throws ArrayIndexOutOfBoundsException If <code>row</code> or <code>col</code> was out of bounds.
     */
    public double get(int row, int col)
        throws ArrayIndexOutOfBoundsException
    {
        return data[row][col];
    }

    /**A useful function for debugging.  It creates a pretty string representation of
     *the matrix, useful for printing to stdout.  It is not useful for reading back in.
     *For doing that try {@link toElement} and {@link fromElement}.
     *@return The pretty string.
     */
    public String toString() {
        StringBuffer rval;
        rval=new StringBuffer(size*size*16);
        for(int i = 0; i< size; i++) {
            for(int j = 0; j< size; j++) {
                rval=rval.append(data[i][j]+" ");
            }
            rval=rval.append("\n");
        }
        return rval.toString();
    }

    /**
     *A simple addition method noncreating.
     *@param a The matrix to add to <code>this</code>
     *@param result The matrix to store the result in.
     *@throws ArrayIndexOutOfBoundsException If the matrices are not all of the same size.
     */
    public void add(DoubleSquareMatrix a, DoubleSquareMatrix result)
        throws ArrayIndexOutOfBoundsException
    {
        if (size != a.size) {
            throw new ArrayIndexOutOfBoundsException("Tried to add a matrix of size "+size+" to a matrix of size "+a.size);
        }
        if (a.size != result.size) {
            throw new ArrayIndexOutOfBoundsException("Tried to add matrix of size "+a.size+" and store the result in a matrix of size "+result.size);
        }
        for(int i=0; i<size;i++) {
            for(int j=0; j< size;j++) {
                result.data[i][j]=data[i][j]+a.data[i][j];
            }
        }
    }

    /**
     *Adds the matrices, and returns the result.
     *This actually will allocate for a new matrix.
     *@param a The matrix to add to this.
     *@throws ArrayIndexOutOfBoundsException If <code>a</code> and this don't have the same size.
     */
    public DoubleSquareMatrix add(DoubleSquareMatrix a)
        throws ArrayIndexOutOfBoundsException
    {
        if (size != a.size) {
            throw new ArrayIndexOutOfBoundsException("Tried to add a matrix of size "+size+" to a matrix of size "+a.size);
        }
        DoubleSquareMatrix result = new DoubleSquareMatrix(size);
        for(int i=0; i<size;i++) {
            for(int j=0;j<size;j++) {
                result.data[i][j]=data[i][j]+a.data[i][j];
            }
        }
        return result;
    }


    /**
     *Multiplies this matrix to another, and stores the result.
     *It does not return a matrix, but instead stores
     *the result in the second paramater.
     *@param a The matrix to multiply to ours.
     *@param result The matrix to store the result of the multiplication inside.
     *@throws ArrayIndexOutOfBoundsException If all of the matrices don't have the same size.
     */
    public void multiply(DoubleSquareMatrix a, DoubleSquareMatrix result)
        throws ArrayIndexOutOfBoundsException
    {
        if (size != a.size) {
            throw new ArrayIndexOutOfBoundsException("Tried to muliply a matrix of size "+size+" to a matrix of size "+a.size);
        }
        if (a.size != result.size) {
            throw new ArrayIndexOutOfBoundsException("Tried to muliply a matrix of size "+size+" and store in a matrix of size "+result.size);
        }
        for(int i=0; i<size;i++) {
            for(int j=0;j<size;j++) {
                result.data[i][j]=0;
                for(int k=0;k<size;k++) {
                    result.data[i][j]+=data[i][k]*a.data[k][j];
                }
            }
        }
    }

    /**
     *Multiplies <code>this</code> to another matrix, and returns the result.
     *@param a The matrix to multiply to <code>this</code>.
     *@return A <code>DoubleSquareMatrix</code> containing the multiplied data.
     *@throws ArrayIndexOutOfBoundsException If all of the matrices don't have the same size.
     */
    public DoubleSquareMatrix multiply(DoubleSquareMatrix a)
        throws ArrayIndexOutOfBoundsException
    {
        if (size != a.size) {
            throw new ArrayIndexOutOfBoundsException("Tried to muliply a matrix of size "+size+" to a matrix of size "+a.size);
        }
        DoubleSquareMatrix result = new DoubleSquareMatrix(size);
        for(int i=0; i<size;i++) {
            for(int j=0;j<size;j++) {
                result.data[i][j]=0;
                for(int k=0;k<size;k++) {
                    result.data[i][j]+=data[i][k]*a.data[k][j];
                }
            }
        }
        return result;
    }


    /**
     *Calculates the largest eigenvector for this matrix.
     *<tex txt="\index{Eigen vector} "></tex>
     *The calculation for priorities based on a pairwise comparison matrix are done
     *via finding the largest eigen vector of the comparison matrix.  The algorithm is
      straightforward.  Let <i>A</i> be the comparison matrix, and <i>v</i> be
      the unitary vector (the vector with the same size as <i>A</i> all of whose
      entries are 1).  Define <i>v<sub>1</sub> = Av</i>, and
      <i>v<sub>i+1</sub>=Av<sub>i</sub></i>.  Then, if we know <i>v<sub>i</sub></i>,
      it only requires the calculation of a matrix times a vector (order 2
      calulcation) to find the next one.
      <p>
      Through the miracle of mathematics the largest eigen vector is <tex
      txt="$\lim_{i \to \infty} v_i$">limit as i goes to infinity of
      <i>v<sub>i</sub></i> </tex>.

     *@param error The maximum error to allow in the numerical calculation.
     *@return A vector of size <code>this.size + 1</code>.  The last
     *element of it is the eigenvalue, the rest is the
     *eignvector data.
     *@throws ConvergenceException If we do not converge while calculating this.
     */
    public double[] largestEigenvector(double error)
        throws ConvergenceException {
        double rval[] = new double[size+1];
        double tmp[] = new double[size+1];
        double diff, sum, factor;
        int k;
        for(int i=0;i<size;i++) rval[i]=1.0/size;
        for(int i=0;i<MaxEigenIterations(size);i++) {
            for(int j=0;j<size;j++) {
                tmp[j]=0;
                for(k=0;k<size;k++) {
                    tmp[j]+=data[j][k]*rval[k];
                }
            }
            /*Normalize tmp vector*/
            sum=0;
            for(k=0;k<size;k++) {
                sum+=tmp[k];
            }
            if (sum!=0) {
                factor=1.0/sum;
            } else {
                factor=1;
            }
            for(k=0;k<size;k++) {
                tmp[k]*=factor;
            }
            /*Now get the diff*/
            diff=0;
            for(k=0;k<size;k++) {
                diff+=java.lang.Math.abs(tmp[k]-rval[k]);
                rval[k]=tmp[k];
            }
            rval[size]=sum;
            //System.out.println("diff="+diff+" maxError="+error);
            if (diff < error) {
                return rval;
            }
        }
        throw new ConvergenceException("Calculating largest eigenvector failed to converge.\n"+toString());
    }

    public double[] largestEigenvectorUsing2Powers(double error) throws ConvergenceException {
    	DoubleSquareMatrix tmpPower = new DoubleSquareMatrix(this);
    	DoubleSquareMatrix nextPower = new DoubleSquareMatrix(this);
    	double lastEigen[] = new double[size];
    	double nextEigen[] = new double[size];
    	double oneVector[] = new double[size];
    	double diff;
    	for(int i=0; i < size; i++)
    		oneVector[i]=1;
    	int max2PowerCount = 200 * size;
    	for(int count=0; count < max2PowerCount; count++) {
    		tmpPower.multiply(tmpPower, nextPower);
    		//Normalize by dividing by largest
    		double max = nextPower.maxAbsEntry();
    		if (max!=0)
    			nextPower.scale(1.0/max);
    		//Now multiply by [1.0...1.0]^T and compare to last one
    		nextPower.multiply(oneVector, nextEigen);
    		normalize(nextEigen);
    		diff = distanceEuclidean(nextEigen, lastEigen);
    		if (diff < error) {
    			double rval[] = new double[size+1];
    			System.arraycopy(nextEigen, 0, rval, 0, size);
    			//Now the eigenvalue, need to multiply again and see the diff
    			this.multiply(nextEigen, lastEigen);
    			//The eigenvalue is the ratio of the sums
    			double newSum = sumAbs(lastEigen);
    			normalize(lastEigen);
    			double eigenValue = newSum;
    			rval[size]=eigenValue;
    			return rval;
    		} else {
    			//Got to try again, copy over nextEigen to last
    			System.arraycopy(nextEigen, 0, lastEigen, 0, size);
    		}
    		tmpPower.copy(nextPower);
    	}
    	throw new ConvergenceException("Could not converge on new eigen calculation");
    }
    
    public double[] adamsByrnesPriority(double error) throws ConvergenceException {
    	return adamsByrnesPriority(true, ZeroCompletelyUncompared, error);
    }

    public boolean hasMultipleSpanningSetsOfSize(int minSize) {
    	List<List<Integer>> spanningSets = getSpanningSets();
    	//Go through each spanning set and figure out
    	//if there are two or more sets with at least two items
    	int count=0;
    	for(List<Integer> spanningSet : spanningSets) {
    		if (spanningSet.size() > minSize)
    			count++;
    		if (count > 1)
    			return true;
    	}
    	return false;
    }
    
    public double[] adamsByrnesPriority(boolean useHarker, boolean zeroCompletelyUncompared, double error) throws ConvergenceException {
    	if (isZero() || isId()) {
	        //System.out.println("Found a trivial matrix, we can handle");
	        double rval[] = new double[size+1];
	        for(int i=0; i<size;i++) rval[i]=1.0/size;
	        if (isZero()) {
	            rval[size]=0;
	        } else {
	            rval[size]=size;
	        }
	        return rval;
	    }
    	List<List<Integer>> spanningSets = getSpanningSets();
    	//Need to use new algorithm, allocate rval
    	double[] eigen = new double[size];
    	//Loop over each spanning set and do the calculation
    	double eigenvalue = 0;
    	for(List<Integer> spanningSet : spanningSets) {
    		DoubleSquareMatrix submatrix = submatrix(spanningSet);
    		if (useHarker)
    			submatrix.harkerFix();
    		if (zeroCompletelyUncompared)
    			submatrix.zeroUncompared();
    		double[] subeigen = submatrix.largestEigenvector(error);
    		//Got sub eigen, now copy to rval
    		for(int pos=0; pos < spanningSet.size(); pos++) {
    			eigen[spanningSet.get(pos)] = subeigen[pos];
    			//Need to normalize by doing *= subSize/size
    			eigen[spanningSet.get(pos)] *= (spanningSet.size() + 0.0d)/size;
    		}
    		if (spanningSet.size()==1)
    			eigenvalue+=1.0;
    		else
    			eigenvalue+=subeigen[spanningSet.size()];
    	}
    	//Now I need to calculate the eigenvalue, first normalize
    	normalize(eigen);
    	double rval[] = new double[size+1];
    	System.arraycopy(eigen, 0, rval, 0, size);
    	rval[size]=eigenvalue;
    	return rval;
    }
    
    private DoubleSquareMatrix submatrix(List<Integer> spanningSet) {
    	DoubleSquareMatrix rval = new DoubleSquareMatrix(spanningSet.size());
    	int subSize=spanningSet.size();
    	for(int i=0; i < subSize; i++) {
    		int row = spanningSet.get(i);
    		for(int j=0; j < subSize; j++) {
    			int col = spanningSet.get(j);
    			rval.data[i][j] = data[row][col];
    		}
    	}
    	return rval;
	}

	private List<List<Integer>> getSpanningSets() {
    	DoubleSquareMatrix indicator = indicatorMatrix();
    	DoubleSquareMatrix powerSum = indicator.powerSum(size);
    	//This power sum matrix indicates if a row is connected to another element.  If the
    	//entry is zero, there is no path between, otherwise there is.  We need to find the
    	//largest sub-spanning tree containing each element.
    	
    	//Start off with all indices
    	List<Integer> untestedIndices = new ArrayList<Integer>(size);
    	for(int i=0; i < size; i++) 
    		untestedIndices.add(new Integer(i));
    	List<List<Integer>> spanningSets = new ArrayList<List<Integer>>();
    	while(untestedIndices.size() > 0) {
    		List<Integer> spanningSet = new ArrayList<Integer>();
    		int index = untestedIndices.get(0);
    		spanningSet.add(index);
    		untestedIndices.remove(0);
    		//Check remaining entries to see if we have any connected here
    		for(Integer testIndex : untestedIndices) {
    			if ((powerSum.data[index][testIndex]!=0)
    					|| (powerSum.data[testIndex][index]!=0))
    				//Did have a connection to testIndex, add it
    				spanningSet.add(testIndex);
    		}
    		//Alright, everything in spanningSet has been added to a spanning set, and can be removed
    		untestedIndices.removeAll(spanningSet);
    		spanningSets.add(spanningSet);
    	}
    	return spanningSets;
	}

	/**returns this + this^2 + this^3 + ... + this^m*/
    private DoubleSquareMatrix powerSum(int m) {
    	DoubleSquareMatrix rval = new DoubleSquareMatrix(this);
    	DoubleSquareMatrix power = new DoubleSquareMatrix(this);
    	DoubleSquareMatrix powerTmp = new DoubleSquareMatrix(size);
    	if (m<=1) {
    		return rval;
    	}
    	for(int pow=2; pow <= m; pow++) {
    		power.multiply(this, powerTmp);
    		power.copy(powerTmp);
    		rval.increment(power);
    	}
    	return rval;
	}

    /**
     * increments this by a (i.e. this + a stored in this)
     * @param a
     */
	private void increment(DoubleSquareMatrix a) {
		if (a==null)
			throw new IllegalArgumentException("Cannot increment by null matrix.");
		if (a.size!=size)
			throw new IllegalArgumentException("Cannot increment by matrix not of same size.");
		for(int row=0; row < size; row++)
			for(int col=0; col < size; col++)
				data[row][col]+=a.data[row][col];
	}

	private DoubleSquareMatrix indicatorMatrix() {
    	DoubleSquareMatrix rval = new DoubleSquareMatrix(this);
    	for(int row=0; row < size; row++)
    		for(int col=0; col < size; col++)
    			if (rval.data[row][col]!=0)
    				rval.data[row][col]=1.0;
    	return rval;
	}

	private double distanceEuclidean(double[] a, double[] b) {
    	if ((a==null) && (b==null))
    		return 0;
    	else if (a==null)
    		throw new IllegalArgumentException("One null, one not null.");
    	else if (b==null)
    		throw new IllegalArgumentException("One null, one not null.");
    	else if (a.length!=b.length)
    		throw new IllegalArgumentException("Inequal sizes.");
    	double sum=0;
    	for(int i=0; i < a.length; i++)
    		sum += (a[i]-b[i])*(a[i]-b[i]);
    	return Math.sqrt(sum);
	}

	private void normalize(double[] vec) {
    	double sumAbs = sumAbs(vec);
    	if (sumAbs==0)
    		return;
    	if (vec==null)
    		return;
    	for(int i=0; i < vec.length; i++)
    		vec[i] /= sumAbs;
	}

	private double sumAbs(double[] vec) {
    	double rval = 0;
    	if (vec==null)
    		return rval;
    	for(int i=0; i < vec.length; i++)
    		rval += Math.abs(vec[i]);
    	return rval;
	}

	private void multiply(double[] vec, double[] result) {
    	if ((vec==null) || (vec.length!=size))
    		throw new IllegalArgumentException("Vec must have same size as matrix to multiply");
    	if ((result==null) || (result.length!=size))
    		throw new IllegalArgumentException("Result must have same size as matrix.");
    	for(int row=0; row < size; row++) {
    		result[row]=0;
    		for(int j=0; j < size; j++) {
    			result[row]+=data[row][j]*vec[j];
    		}
    	}
	}

	private void scale(double d) {
    	for(int row=0; row < size; row++) {
    		for(int col=0; col < size; col++) {
    			data[row][col]*=d;
    		}
    	}
	}

	private double maxAbsEntry() {
    	double rval = 0;
    	for(int row=0; row < size; row++) {
    		for(int col=0; col < size; col++) {
    			if (Math.abs(data[row][col]) > rval) {
    				rval = Math.abs(data[row][col]);
    			}
    		}
    	}
    	return rval;
	}

	/**This is a convenience function to get the eigen error. It just
     *gets the largest eigenvector with fix, and reads the last value.*/
    public double largestEigenvalue(double error) throws ConvergenceException {
        if (size==0) {
            return 0;
        }
        double rval[] = largestEigenvectorWithFix(error);
        return rval[rval.length-1];
    }
    /**This is a convenience function to get the eigen error. It just
     *gets the largest eigenvector without fix, and reads the last value.*/
    public double largestEigenvalueNoFix(double error) throws ConvergenceException {
        if (size==0) {
            return 0;
        }
        double rval[] = largestEigenvector(error);
        return rval[rval.length-1];
    }
    /**
     *Calculates the largest eigenvector for this matrix (and returns the idealized
     *version, i.e. the largest value is 1).
     *<tex txt="\index{Eigen vector} "></tex>
     *The calculation for priorities based on a pairwise comparison matrix are done
     *via finding the largest eigen vector of the comparison matrix.  The algorithm is
      straightforward.  Let <i>A</i> be the comparison matrix, and <i>v</i> be
      the unitary vector (the vector with the same size as <i>A</i> all of whose
      entries are 1).  Define <i>v<sub>1</sub> = Av</i>, and
      <i>v<sub>i+1</sub>=Av<sub>i</sub></i>.  Then, if we know <i>v<sub>i</sub></i>,
      it only requires the calculation of a matrix times a vector (order 2
      calulcation) to find the next one.
      <p>
      Through the miracle of mathematics the largest eigen vector is <tex
      txt="$\lim_{i \to \infty} v_i$">limit as i goes to infinity of
      <i>v<sub>i</sub></i> </tex>.

     *@param error The maximum error to allow in the numerical calculation.
     *@return A vector of size <code>this.size + 1</code>.  The last
     *element of it is the eigenvalue, the rest is the
     *eignvector data.
     *@throws ConvergenceException If we do not converge while calculating this.
     */
    public double[] largestEigenvectorIdeal(double error)
        throws ConvergenceException {
        double rval[] = new double[size+1];
        double tmp[] = new double[size+1];
        double diff=0, sum, factor;
        int k;
        for(int i=0;i<size;i++) rval[i]=1.0/size;
        for(int i=0;i<MaxEigenIterations(size);i++) {
            for(int j=0;j<size;j++) {
                tmp[j]=0;
                for(k=0;k<size;k++) {
                    tmp[j]+=data[j][k]*rval[k];
                }
            }
            /*Idealize tmp vector*/
            sum=0;
            for(k=0;k<size;k++) {
                if (java.lang.Math.abs(tmp[k]) > sum)
                    sum=java.lang.Math.abs(tmp[k]);
            }
            if (sum!=0) {
                factor=1.0/sum;
            } else {
                factor=1;
            }
            for(k=0;k<size;k++) {
                tmp[k]*=factor;
            }
            /*Now get the diff*/
            diff=0;
            for(k=0;k<size;k++) {
                diff+=java.lang.Math.abs(tmp[k]-rval[k]);
                rval[k]=tmp[k];
            }
            rval[size]=sum;
            //System.out.println("diff="+diff+" maxError="+error);
            if (diff < error) {
                return rval;
            }
        }
        throw new ConvergenceException("Calculating largest eigenvector failed to converge (last error was "+diff+" wanted "+error+").\n"+toString());
    }

    /**
     *Implements Harker's fix on the matrix.  This consists of incrementing the diagonal
     *by one for each zero entry in it's row or column.
     */
    void harkerFix() {
        for(int i=0;i<size;i++) {
            for(int j=0;j<size; j++) {
                if (data[i][j] == 0) {
                    if (i!=j) {
                        data[i][i]+=1;
                    } else {
                        data[i][i]=1;
                    }
                }
            }
        }
    }
    /**
     *If a row and column is zero, except possibly the diagonal
     *we make the diagonal zero too.
     */
    void zeroUncompared() {
        boolean zero=true;
        for(int i=0; i<size;i++) {
            zero=true;
            for(int j=0;j<size;j++) {
                if ((i!=j) &&(data[i][j]!=0)) {
                    zero=false;
                    break;
                }
            }
            if (zero) data[i][i]=0;
        }
    }

    /**
     *Calculates the largest eigenvector, using both Harkers fixing.
     *It may zero out the completely uncompared, if the global
     *static variable <code>ZeroCompletelyUncompared</code>
     *is set to true.
     *@param error The error to allow in the comparisons.
     *@return The eigenvector, plus the eigenvalue tacked on at the
     *end.
     */
    public double[] largestEigenvectorWithFix(double error)
        throws ConvergenceException
    {
        //System.out.println("Calculating largest eigenvector from:");
        //System.out.println(toString());
        if (isZero() || isId()) {
            //System.out.println("Found a trivial matrix, we can handle");
            double rval[] = new double[size+1];
            for(int i=0; i<size;i++) rval[i]=1.0/size;
            if (isZero()) {
                rval[size]=0;
            } else {
                rval[size]=size;
            }
            return rval;
        }
        DoubleSquareMatrix tmp =new DoubleSquareMatrix(this);
        tmp.harkerFix();
        if (ZeroCompletelyUncompared) tmp.zeroUncompared();
        //System.out.println("Fixed matrix is\n"+tmp.toString());
        return tmp.largestEigenvector(error);
    }

    /**
     *Calculates the largest eigenvector, using both Harkers fixing.
     *It may zero out the completely uncompared, if the global
     *static variable <code>ZeroCompletelyUncompared</code>
     *is set to true.
     *@param error The error to allow in the comparisons.
     *@return The eigenvector, plus the eigenvalue tacked on at the
     *end.
     */
    public double[] largestEigenvectorWithFixUsing2Powers(double error)
        throws ConvergenceException
    {
        //System.out.println("Calculating largest eigenvector from:");
        //System.out.println(toString());
        if (isZero() || isId()) {
            //System.out.println("Found a trivial matrix, we can handle");
            double rval[] = new double[size+1];
            for(int i=0; i<size;i++) rval[i]=1.0/size;
            if (isZero()) {
                rval[size]=0;
            } else {
                rval[size]=size;
            }
            return rval;
        }
        DoubleSquareMatrix tmp =new DoubleSquareMatrix(this);
        tmp.harkerFix();
        if (ZeroCompletelyUncompared) tmp.zeroUncompared();
        //System.out.println("Fixed matrix is\n"+tmp.toString());
        return tmp.largestEigenvectorUsing2Powers(error);
    }

    /**
     *Calculates the largest eigenvector (scaling to ideal), using both Harkers fixing.
     *It may zero out the completely uncompared, if the global
     *static variable <code>ZeroCompletelyUncompared</code>
     *is set to true.
     *@param error The error to allow in the comparisons.
     *@return The eigenvector, plus the eigenvalue tacked on at the
     *end.
     */
    public double[] largestEigenvectorIdealWithFix(double error)
        throws ConvergenceException
    {
        //System.out.println("Calculating largest eigenvector from:");
        //System.out.println(toString());
        if (isZero() || isId()) {
            //System.out.println("Found a trivial matrix, we can handle");
            double rval[] = new double[size];
            for(int i=0; i<size;i++) rval[i]=1.0;
            return rval;
        }
        DoubleSquareMatrix tmp =new DoubleSquareMatrix(this);
        tmp.harkerFix();
        if (ZeroCompletelyUncompared) tmp.zeroUncompared();
        //System.out.println("Fixed matrix is\n"+tmp.toString());
        return tmp.largestEigenvectorIdeal(error);
    }
    /**
     *Turns this matrix into the identity.
     */

    public void id() {
        for(int i=0; i<size;i++) {
            for(int j=0;j<size;j++) {
                data[i][j]=0;
            }
            data[i][i]=1;
        }
    }

    /**
     *Tells us if the matrix is an identity matrix.
     *@return A boolean indicating if we have an identity matrix.
     */
    public boolean isId() {
        for(int i=0;i<size;i++) {
            for(int j=0;j<size;j++) {
                if (((i==j) && (data[i][j]!=1)) || ((i!=j)&&(data[i][j]!=0))) {
                    //System.out.println("Found nonId b/c at ("+i+","+j+")="+data[i][j]);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *Tells us if the matrix is a zero matrix.
     *@return A boolean indicating if we have a zero matrix.
     */
    public boolean isZero() {
        for(int i=0;i<size;i++) {
            for(int j=0;j<size;j++) {
                if (data[i][j]!=0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**Adjusts the given matrix to make the absolute value sum of the matrix
     *columns be 1.
     */
    public void makeColsAddToOne() {
        double sum;
        int i,j;
        for (i=0;i<size;i++) {
            sum=0;
            for(j=0;j<size;j++) {
                sum+=Math.abs(data[j][i]);
            }
            if (sum!=0)
                for(j=0;j<size;j++) {
                    data[j][i]/=sum;
                }
        }
    }

    /**Copies one matrix to this matrix.
     *@param src The matrix copy into this matrix.
     *@throws ArrayIndexOutOfBoundsException If <code>src</code> does not have the same size as this.
     */
    public void copy(DoubleSquareMatrix src) throws ArrayIndexOutOfBoundsException
    {
        if (src==null) {
            throw new ArrayIndexOutOfBoundsException("Tried to copy from a null matrix.");
        }
        if (src.size != size) {
            throw new ArrayIndexOutOfBoundsException("Tried to copy from a matrix of size "+src.size+ " to a matrix of size "+size);
        }
        int i,j;
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                data[i][j]=src.data[i][j];
    }

    /**Raises this matrix to a power, and stores the result in the
     *given result matrix.  If power is less than zero, we return the identity matrix.
     *@param pow The power to raise this matrix to.
     *@param result The matrix to store the result in.  It must already have the
     *correct size.
     *@throws ArrayIndexOutOfBoundsException If the matrices sizes don't match.
     */
    public void power(int pow, DoubleSquareMatrix result)
        throws ArrayIndexOutOfBoundsException
    {
        if (result==null) {
            throw new ArrayIndexOutOfBoundsException("The result matrix sent to power was null.");
        }
        if (result.size != this.size) {
            throw new ArrayIndexOutOfBoundsException("The result matrix sent to power had size "+size+" and it needed size "+result.size);
        }
        /*Now we are safe.*/
        DoubleSquareMatrix twoPower = new DoubleSquareMatrix(this);
        DoubleSquareMatrix tmp=new DoubleSquareMatrix(this.size);
        DoubleSquareMatrix tmp2=new DoubleSquareMatrix(this.size);
        tmp.id();
        int twopower;
        result.id();
        twopower=1;
        while (twopower <= pow) {
            if ((twopower & pow) != 0) {
                tmp.multiply(twoPower, result);
                tmp.copy(result);
            }
            twoPower.multiply(twoPower, tmp2);
            twoPower.copy(tmp2);
            twopower*=2;
        }
    }

    /**
     *Finds the sum of the differences between this matrix and another, after normalizing each (making their columns add to 1).
     *It does not actually normalize each matrix
     *(so the matrices are not changed by calling this function).
     *@param other The other matrix to compare to this.
     *@return The sum of the absolute value of the differences of each entry (after normalizing
     *both matrices).
     *@throws ArrayIndexOutOfBoundsException If the matrices don't have the same size.
     */
    public double normalizedDiff(DoubleSquareMatrix other)
        throws ArrayIndexOutOfBoundsException
    {
        if (other==null) {
            throw new ArrayIndexOutOfBoundsException("normalizedDiff sent a null matrix.");
        }
        if (other.size!=size) {
            throw new ArrayIndexOutOfBoundsException("normalizedDiff on matrix of size "+size+" sent matrix of size "+other.size);
        }
        int i,j;
        double diff=0, factor1, factor2, sum1, sum2;
        for(j=0;j<size;j++) {
            sum1=0;
            sum2=0;
            for(i=0;i<size;i++) {
                sum1+=Math.abs(data[i][j]);
                sum2+=Math.abs(other.data[i][j]);
            }
            factor1=1; factor2=1;
            if (sum1!=0)
                factor1=1/sum1;
            if (sum2!=0)
                factor2=1/sum2;
            for(i=0;i<size;i++)
                diff+=Math.abs(factor1*data[i][j]-factor2*data[i][j]);
        }
        return diff;
    }

    /**
     *Gives us a good starting power for doing limit matrix calculations on this
     *matrix.
     *@return A power that is good for starting limit matrix calculations for this
     *particular matrix.
     */
    public int startPower()
    {
        return 2^((int)Math.round(Math.ceil(Math.log(size))));
    }

    /**
     *Uses William Adams calculus type limit matrix calculation to find
     *the limit matrix.  It assumes this thing is non-hierarchical.  If you
     *do not know, use {@link limitPowerIntelligent} instead.
     *@param err The maximum error to allow in computing the limit.
     *@throws ConvergenceException If this calculation does not converge after
     *trying look for cycles <code>MaxLimitIterations</code> times.
     */
    public DoubleSquareMatrix limitPowerNonHierarchy(double err)
        throws ConvergenceException
    {
        /*Need to get a starting power*/
        DoubleSquareMatrix start=new DoubleSquareMatrix(size);
        DoubleSquareMatrix currentPower = new DoubleSquareMatrix(size);
        DoubleSquareMatrix next = new DoubleSquareMatrix(size);
        this.power(startPower(), start);
        currentPower.copy(start);
        /*Now go through a failsafe loop*/
        for(int i=0;i<MaxLimitIterations; i++) {
            for(int k=0;k<size;k++) { /*look for convergence*/
                this.multiply(currentPower, next);
                if (next.normalizedDiff(start) < err) {
                    next.makeColsAddToOne();
                    return next;
                }
                currentPower.copy(next);
            }
            start.copy(next);
        }
        /*If we make it here we never converged*/
        throw new ConvergenceException("LimitPower non hierarchy did not converge.");
    }

    /**This function returns the levels of a matrix.  If the matrix
     *was a non-hierarchy, then it returns null.  Else, it returns a
     *vector of vectors.  Each subvector is a list of nodes in that level.
     *The first element of the returned vector is the sinks, and so on.
     *@return See the main body of documentation.
     */
    public Vector getLevels()
    {
        /*First get a copy to work with.*/
        DoubleSquareMatrix tmp = new DoubleSquareMatrix(this);
        /*Now start getting levels*/
        Vector result = new Vector();
        Vector realPlaces = new Vector();
        Vector level, tmpLevel;
        int i,j;
        boolean zeroColumn, foundALevel;
        realPlaces.setSize(size);
        for(i=0;i<size;i++) {
            realPlaces.set(i, new Integer(i));
        }
        while (tmp.size > 0) {
            //System.out.println("Working on matrix:\n"+tmp.toString());
            foundALevel=false;
            /*Find sink columns*/
            level=new Vector();
            tmpLevel=new Vector();
            for(i=0;i<tmp.size;i++) {
                zeroColumn=true;
                for(j=0;j<tmp.size;j++) {
                    if (tmp.data[j][i] != 0) {
                        zeroColumn=false;
                        break;
                    }
                }
                if (zeroColumn) {
                    foundALevel=true;
                    level.add(realPlaces.get(i));
                    tmpLevel.add(new Integer(i));
                    //System.out.println("Found a zero column at fake="+i+" real="+((Integer)realPlaces.get(i)).intValue());
                }
            }
            for(i=0;i<tmpLevel.size();i++) {
                Integer ii = (Integer)tmpLevel.get(i);
                //System.out.println("Before removing, the matrix is:\n"+tmp.toString());
                //System.out.println("We are removing place "+ii.intValue()+ " out of "+realPlaces.size());
                realPlaces.remove(ii.intValue());
                tmp.rmPlace(ii.intValue());
                //System.out.println("After removing, the matrix is:\n"+tmp.toString());
                for(j=0;j<tmpLevel.size();j++) {
                    Integer tmpInt=(Integer)tmpLevel.get(j);
                    if (tmpInt.compareTo(ii)> 0)
                        tmpLevel.set(j, new Integer(tmpInt.intValue()-1));
                }
            }
            if (foundALevel) {
                result.add(level);
            } else {
                //System.out.println("Left with "+tmp.size+" nodes.");
                return null;
            }
        }
        return result;
    }

    /**
     *A nifty new limit power calculation for hierarchies, which is essentially as efficient
     *as doing the AHP calculations directly.  Plus the limit matrix has all of the global
     *priorities for each node.
     *@param levels If not <code>null</code> the levels of our hierarchy (this should have
     *been computed using {@link getLevels}.  If <code>null</code> then we will compute
     *it here.
     *@return The limit matrix.
     *@throws NonHierarchyException If levels was <code>null</code> and the matrix was not
     *the matrix of a hierarchy.
     */
    public DoubleSquareMatrix limitPowerHierarchy(Vector levels)
        throws NonHierarchyException
    {
        int i,j, k, l;
        if (levels==null) {
            levels=getLevels();
        }
        if (levels == null) {
            throw new NonHierarchyException();
        }
        //System.out.println("Limit power hierarchy had "+levels.size()+" levels.");
        DoubleSquareMatrix result = new DoubleSquareMatrix(this);
        for(i=1;i<levels.size();i++) {
            Vector level= (Vector)levels.get(i);
            /*We ignore the first level, cause those are the sinks.*/
            for(j=0;j<level.size();j++) {
                int col=((Integer)level.get(j)).intValue();
                for(k=0;k<size;k++) {
                    if (data[k][col]!=0) {
                        for(l=0;l<size;l++) {
                            result.data[l][col]+=result.data[k][col]*result.data[l][k];
                        }
                    }
                }
            }
        }
        result.makeColsAddToOne();
        return result;
    }

    /**
     *Checks to see if the matrix is for a hierarchy.  If so, it calls
     *{@link limitPowerHierarchy}.  If not, it calls {@link limitPowerNonHierarchy}.
     *@param err The maximum error to allow for, if we have to do a non-hierarchy
     *limit matrix calculation.
     *@return The limit matrix calculated.
     *@throws ConvergenceException If the limit matrix command failed to converge
     *(this can only happen in non-hierarchy case).
     */
    public DoubleSquareMatrix limitPowerIntelligent(double err)
        throws ConvergenceException
    {
        Vector levels = getLevels();
        if (levels == null) {
            //System.out.println("We have a matrix without levels (i.e. non-hierarchy).");
            return limitPowerNonHierarchy(err);
        } else {
            try {
                return limitPowerHierarchy(levels);
            } catch (Exception WillNotHappen) {
                /*This won't happen but needed to be there*/
                return limitPowerNonHierarchy(err);
            }
        }
    }

    /**
     *Checks to see if the matrix is for a hierarchy.  If so, it calls
     *{@link limitPowerHierarchy}.  If not, it calls {@link limitPowerNonHierarchy}.
     *@param err The maximum error to allow for, if we have to do a non-hierarchy
     *limit matrix calculation.
     *@return The limit matrix calculated.
     *@throws ConvergenceException If the limit matrix command failed to converge
     *(this can only happen in non-hierarchy case).
     */
    public DoubleSquareMatrix limitPowerIntelligentSA(int clusterStarts[], double err)
        throws ConvergenceException
    {
        Vector levels = getLevels();
        if (levels == null) {
            //System.out.println("We have a matrix without levels (i.e. non-hierarchy).");
            return limitPowerNonHierarchySA(clusterStarts, err);
        } else {
            try {
                return limitPowerHierarchySA(clusterStarts, levels);
            } catch (Exception WillNotHappen) {
                /*This won't happen but needed to be there*/
                return limitPowerNonHierarchySA(clusterStarts, err);
            }
        }
    }

    /**
     * 
     * @param clusterStarts  Where the clusters start at, including the first one starting at 0. 
     *So the first element of the array is always 0.
     * @param doStructuralAdjust If true, we do a structural adjust calculation.  If not
     * we do a regular calculation.
     * @param err 
     * @return 
     */
    public DoubleSquareMatrix limitPowerIntelligent(int []clusterStarts, boolean doStructuralAdjust, double err)
        throws ConvergenceException
    {
        if (!doStructuralAdjust) {
            return limitPowerIntelligent(err);
        } else {
            return limitPowerIntelligentSA(clusterStarts, err);
        }
    }

    /**
     * Returns the structurally adjusted version of this matrix based on the cluster
     * start positions.
     * @param clusterStarts Where the clusters start it.  This includes the starting of the first
     *cluster at 0.  So the first element of the array is always 0. 
     */
    public DoubleSquareMatrix structuralAdjustTree(int []clusterStarts)
    {
        DoubleSquareMatrix binary=new DoubleSquareMatrix(size);
        //Setup 0/1 matrix, 0 if the value of this matrix in that spot is zero
        //1 otherwise.
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                if (data[i][j]==0) {
                    binary.data[i][j]=0;
                } else {
                    binary.data[i][j]=1;
                }
            }
        }
        //Now do the multiplication
        DoubleSquareMatrix mult=binary.multiply(this);
        //Now read off values in here to setup the compoenent-wise multiplication matrix.
        DoubleSquareMatrix rval=new DoubleSquareMatrix(size);
        for(int col=0; col<size; col++) {//loop over columns first, since data breaks down better this way.
            double sum=0;
            double maxAlt=0;
            for(int row=0; row < size; row++) {
                sum+=this.data[row][col];
                maxAlt=Math.max(maxAlt, mult.data[row][col]);
            }
            double factor=1;
            if ((sum!=0)&&(maxAlt!=0)) {
                factor=sum/maxAlt;
            }
            for(int row=0; row<size; row++)
                rval.data[row][col]=factor;
        }
        return rval;
    }
    /**
     *Constructs a <code>jdom</code> element representing our double square matrix.
     *Useful for storing and reloading a matrix (see {@link fromElement} for reloading).
     *@param name The name to give the element we are returning.
     *@return The <code>jdom</code> element representing this matrix.
     */

    public Element toElement(String name)
    {
        Element rval=new Element(name);
        rval.setAttribute("size", ""+size);
        rval.setText(toString());
        return rval;
    }
    private double[][] parseToStringOld(String val, int mySize) {
    	double[][] rval=new double[mySize][mySize];
    	int i,j;
    	int offset=0;
    	int newOffset;
    	String numb;
    	for(i=0; i<mySize; i++) {
    		for(j=0; j<mySize; j++) {
    			newOffset=val.indexOf(' ', offset);
    			if (newOffset==-1) {
    				if ((i==(mySize-1))&&(j==(mySize-1))) {
    					newOffset=val.length();
    				}
    			}
    			numb=val.substring(offset, newOffset);
    			offset=newOffset+1;
    			try {
    				rval[i][j]=Double.parseDouble(numb);
    			} catch (Exception e) {
    				logEvent(e);
    			}
    		}
    		offset=val.indexOf("\n", offset)+1;
    	}
    	
    	return rval;
    }

    private void logEvent(Exception e) {
    	e.printStackTrace();
	}

	private double[][] parseToString(String val, int mySize) {
    	double[][] rval=new double[mySize][mySize];
    	String[]vals=val.split("[ \n]+");
    	int count=0;
    	for(int i=0; i<mySize; i++) {
    		for(int j=0; j<mySize; j++) {
    			if (vals.length > count) {
	    			try {
	    				rval[i][j]=Double.parseDouble(vals[count]);
	    			} catch (Exception e) {
	    				logEvent(e);
	    			}
    			} else {
    				logEvent("Parsing matrix not enough size");
    			}
    			count++;
    		}    		
    	}
    	return rval;
    }
    
    /**
     *Fills in this double square matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element)
        throws XMLFormatException
    {
        if (element == null)
            throw new XMLFormatException("Null XML element sent to DoubleSquareMatrix.");
        String size=element.getAttributeValue("size");
        String text = element.getTextTrim();
        int i, j, count=0, sizeInt=0;
        if ((size != null) && (!"".equals(size))) {
            try {
                sizeInt = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new XMLFormatException("While parsing a matrix, size attribute ="+size+" was not an integer.");
            }
        }
        text=text.trim();
        if (true) {
			data = parseToString(text, sizeInt);
			this.size = sizeInt;
		} else {
			String numbers[] = text.split("\\s+");
			int nEntries;
			nEntries = (int) Math.ceil(Math.sqrt(numbers.length + 0.0));
			// if (nEntries > sizeInt) sizeInt = nEntries;
			data = new double[sizeInt][sizeInt];
			this.size = sizeInt;
			// System.out.println("Read a matrix with size="+this.size+"
			// sizeInt="+sizeInt+" numbers.length="+numbers.length);
			for (i = 0; i < sizeInt; i++) {
				for (j = 0; j < sizeInt; j++) {
					try {
						data[i][j] = Double.parseDouble(numbers[count]);
					} catch (Exception e) {
					}
					count++;
				}
			}
		}
    }

    /** A simple testing main. */
    public static void mainTest(String []argv) {
    	testCompat();
        //test3();
        //testXML();
        //testBasic(argv);
    }

    public static void test3() {
        try {
            double err=1e-16;
            double comperr=1e-8;
            String fname="/trunk/src/com/dlens/anp/engine/smatrix.xml";
            String fname1_5="/trunk/src/com/dlens/anp/engine/smatrix_incom.xml";
            String fname2="/trunk/src/com/dlens/anp/engine/smatrix2.xml";
            DoubleSquareMatrix dsm = new DoubleSquareMatrix(fname);
            System.out.println(dsm.toString()+"Had incon "+dsm.getInconsistencyIndex(err));
            System.out.println("It was completed, full? "+dsm.isComplete(0, 0, 0, err));
            System.out.println("It was completed, spanning? "+dsm.isComplete(1, 0, 0, err));
            System.out.println("It was completed, spanning+1? "+dsm.isComplete(2, 1, 1e-16, err));
            System.out.println("Starting a very incomplete matrix.");
            dsm = new DoubleSquareMatrix(fname1_5);
            System.out.println("It was completed, full? "+dsm.isComplete(0, 0, 0, err));
            System.out.println("It was completed, spanning? "+dsm.isComplete(1, 0, 0, err));
            System.out.println("It was completed, spanning+1? "+dsm.isComplete(2, 1, 1e-16, err));
            System.out.println("Starting a spanning+1, but not spanning+2 matrix.");
            dsm = new DoubleSquareMatrix(fname2);
            System.out.println("It was completed, full? "+dsm.isComplete(0, 0, 0, err));
            System.out.println("It was completed, spanning? "+dsm.isComplete(1, 0, 0, err));
            System.out.println("It was completed, spanning+1? "+dsm.isComplete(2, 1, comperr, err));
            System.out.println("It was completed, spanning+2? "+dsm.isComplete(2, 2, comperr, err));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    /**Simple constructor from a file.*/
    public DoubleSquareMatrix(String fname)
        throws XMLFormatException, JDOMException, IOException
    {
        SAXBuilder parser = new SAXBuilder();
        Document doc;
        doc = parser.build(fname);
        Element elt = doc.getRootElement();
        fromElement(elt);
    }
    public DoubleSquareMatrix(double[][] ds) {
    	if (ds==null) {
    		throw new IllegalArgumentException();
    	}
    	size=ds.length;
    	data = new double[size][];
    	for(int row=0; row < ds.length; row++) {
    		if ((ds[row]==null) || (ds[row].length!=size))
    			throw new IllegalArgumentException("All rows must be size "+size);
    		data[row] = ds[row].clone();
    	}
	}

	/**A test that used to be a main, but was relegated to a possible test that
     *we can try if necessary.
     */
    public static void testBasic(String []argv) {
        int size=50;
        int ntimes=1;
        DoubleSquareMatrix sm = new DoubleSquareMatrix(size);
        for(int i=0;i<size;i++) {
            for(int j=0;j<size;j++) {
                try {sm.set(i,j,i+j+1);} catch (Exception e) {}
            }
            try {sm.set(i, i, 1);} catch(Exception e) {}
        }
         try {
            for(int i=0;i<ntimes;i++) {
                double vector[]=sm.largestEigenvectorWithFix(1e-8);
                sm.set(0,0,1);
            }
        } catch (ConvergenceException e) {
            System.out.println(e.toString());
        }
        DoubleSquareMatrix m=new DoubleSquareMatrix(2);
        DoubleSquareMatrix mm=new DoubleSquareMatrix(2);
        m.id(); m.data[0][1]=1;
        m.power(30, mm);
        System.out.println(mm.toString());
        m= new DoubleSquareMatrix(5);
        m.data[3][0]=1; m.data[0][2]=1; m.data[4][2]=1;
        m.data[1][4]=1;
        Vector v = m.getLevels();
        if (v==null) {
            System.out.println("Error in getLevels()\n");
        } else {
            for(int i=0;i<v.size();i++) {
                System.out.println("Level "+i+" has nodes:");
                Vector vv=(Vector)v.get(i);
                for(int j=0;j<vv.size();j++) {
                    System.out.print(((Integer)vv.get(j)).intValue()+" ");
                }
                System.out.println("");
            }
        }
        m=new DoubleSquareMatrix(5);
        m.data[1][0]=.3; m.data[2][0]=.7;
        m.data[3][1]=.6;m.data[4][1]=.4;
        m.data[3][2]=.1;m.data[4][2]=.9;
        DoubleSquareMatrix limit;
        try {
            limit=m.limitPowerHierarchy(null);
            System.out.println("Matrix:\n"+m.toString()+"Has limitPowerHierarchy:\n"+limit.toString());
        } catch (Exception ignored) {
        }
    }

    /**Tests the reading of a matrix from xml.*/
    public static void testXML() {
        String fname = "/trunk/src/com/dlens/anp/engine/smatrix.xml";
        DoubleSquareMatrix mat = new DoubleSquareMatrix();
        SAXBuilder parser = new SAXBuilder();
        Document doc;
        try {
            doc = parser.build(fname);
            Element elt = doc.getRootElement();
            mat.fromElement(elt);
            System.out.println(mat.toString());
            XMLOutputter outputter = new XMLOutputter();
            FileOutputStream fout = new FileOutputStream(fname);
            outputter.output(doc, fout);
            fout.close();
        } catch (Exception e) {
            System.out.println("Unexpected exception "+e.toString());
            System.exit(1);
        }
    }

    /**
     *Averages the given columns, and returns that double vector.
     *If the column was entirely zero, we ignore it (so it doesn't
     *count against us in the averaging).  If cols is <code>null</code>
     *then we assume you wanted to average everything.  Note that
     *if any of the columns you give are out of bounds, we simply ignore them,
     *and do not raise an exception.
     *@param cols The columns to average.
     *@return A double array of the same size as this matrix, whose values
     *are the numerical averages of the given columns.
     */
    public double[] averageColumns(int []cols) {
        int i,j,k;
        boolean zero;
        double rval[] = new double[size];
        int nValidCols=0;
        if ((cols == null) || (cols.length==0)) {
            cols = new int[size];
            for(i=0;i<size;i++) cols[i]=i;
        }
        /*Get rid of any zero columns*/
        int columns[] = new int[cols.length];
        int count=0;
        for(i = 0; i< cols.length; i++) {
            zero = true;
            columns[i]=-1;
            for(j=0;j<size;j++) {
                if (data[j][i]!=0) {
                    zero=false;
                    break;
                }
            }
            if (!zero) {
                count++;
                columns[i]=cols[i];
            }
        }
        int columns2[] = new int[count];
        count=0;
        for(i=0;i<columns.length;i++) {
            if (columns[i]>=0) {
                columns2[count]=columns[i];
                count++;
            }
        }
        cols=columns2;
        /*Done removing zero columns, finally*/
        //System.out.println("Averaging "+cols.length+ " columns.");
        for(i=0; i<size ; i++) {
            rval[i]=0;
            for(k=0; k < cols.length; k++) {
                if ((cols[k] >= 0) && (cols[k]<size)) {
                    if (i==0) nValidCols++;
                    rval[i]+=data[i][k];
                }
            }
            if (nValidCols > 0) {
                rval[i]/=nValidCols;
            }
        }
        return rval;
    }

    /**Moves a column after a given column (end).  If end=-1, that
     *means to move the given column to the zeroth position.
     */
    public void mvColAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        double last, mover;
        if (start==end)
        if ((start < 0)||(start>=this.size)||(end<-1)||(end>=this.size))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            for(int i=0; i<size; i++) {
                mover=data[i][start];
                for(int j=start; j < end; j++) {
                    data[i][j]=data[i][j+1];
                }
                data[i][end]=mover;
            }
        } else {
            for(int i=0; i<size; i++) {
                mover=data[i][start];
                for(int j=start; j > (end+1); j--) {
                    data[i][j]=data[i][j-1];
                }
                data[i][end+1]=mover;
            }
        }
    }

    /**Moves a row after a given row (end).  If end=-1, that
     *means to move the given row to the zeroth position.
     */
    public void mvRowAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        double last, mover;
        if (start==end)
        if ((start < 0)||(start>=this.size)||(end<-1)||(end>=this.size))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            for(int j=0; j<size; j++) {
                mover=data[start][j];
                for(int i=start; i < end; i++) {
                    data[i][j]=data[i+1][j];
                }
                data[end][j]=mover;
            }
        } else {
            for(int j=0; j<size; j++) {
                mover=data[start][j];
                for(int i=start; i > (end+1); i--) {
                    data[i][j]=data[i-1][j];
                }
                data[end+1][j]=mover;
            }
        }
    }

    public void movePlaceAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        mvRowAfter(start, end);
        mvColAfter(start, end);
    }

    /**Returns the number of zeroes in the matrix.*/
    public int numbZeroes() {
        int i,j,numb=0;
        for(i=0;i<size;i++) {
            for(j=0;j<size;j++) {
                if (data[i][j]==0.0) {
                    numb++;
                }
            }
        }
        return numb;
    }

    /**Tells if a spanning set has been done.*/
    public boolean spanningSetDone() {
        DoubleSquareMatrix tmp = new DoubleSquareMatrix(this.size);
        DoubleSquareMatrix power = new DoubleSquareMatrix(this.size);
        int i,j;
        for(i=0;i<size;i++) {
            for(j=0;j<size;j++) {
                if (data[i][j]!=0) {
                    tmp.data[i][j]=1;
                } else {
                    tmp.data[i][j]=0;
                }
            }
        }
        /*Okay we have to binary matrix, now raise to the size-1 power.
         *If this matrix has all non-zero entries then we had a spanning set.*/
        tmp.power(size-1, power);
        if (power.numbZeroes()==0) {
            return true;
        } else {
            return false;
        }
    }

    /**Counts the number of non-zeroes in this matrix.
     */
    public int numbNonzero() {
        int numb=0;
        for(int i=0; i<this.size; i++)
            for(int j=0; j<this.size; j++)
                if (this.data[i][j]!=0)
                    numb++;
        return numb;
    }

    /**Counts the number of non-zeroes in this matrix, above the diagonal.
     */
    public int numbNonzeroAboveDiagonal() {
        int numb=0;
        for(int i=0; i<this.size; i++)
            for(int j=i+1; j<this.size; j++)
                if (this.data[i][j]!=0)
                    numb++;
        return numb;
    }

    /**Tells if these comparisons are complete. The are several types of completeness.
     *Completely done, a spanning set done, or a spanning set plus a minimum of a few
     *extra comparisons with a small enough inconsistency index.
     *@param fullType  If 0, then check for completely done.  If 1, then check for at
     *least a spanning set.  If 2, then check that spanning set plus minExtraComps
     *are done, and that the inconsistency is less than maxInconsistency.
     *@param minExtraComps The minimum number of comparisons above a spanning set
     *done if fullType is 2.
     *@param maxInconsistency The maximum inconsistency to allow if fullType is 2.
     *@param error The eigenvalue computation error to allow for.
     */
    public boolean isComplete(int fullType, int minExtraComps, double maxInconsistency, double error)
        throws ConvergenceException
    {
        int numbZeroes=0, numbDone, numbExtras;
        numbZeroes=numbZeroes();
        /*Numb done has to subtract diagonal, since it doesn't count, and divide
         by two cause each side of diagonal has it.*/
        numbDone=(size*size - numbZeroes -size)/2;
        switch (fullType) {
            case 0: /*Checking if all comparisons are done.*/
                if (numbZeroes==0) {
                    return true;
                } else {
                    return false;
                }
            case 1: /*Checking a spanning set.*/
                if (spanningSetDone()) {
                    return true;
                } else {
                    return false;
                }
            case 2: /*Checking spanning plus.*/
                /*This is easy, if we don't have spanning, we don't have
                 *spanning plus.*/
                if (!spanningSetDone()) {
                    return false;
                } else {
                    //System.out.println("We have a spanning done numbDone="+numbDone);
                    /*We have a spanning set (which always takes size-1
                     *elements).  See if we have minExtraComps done too.*/
                    numbExtras=numbDone-(size-1);
                    if (numbExtras < minExtraComps) {
                        /*We didn't have enough done.*/
//                        System.out.println("Didn't have enough done.");
                        logEvent("DoubleSquareMatrix.isComplete() - Didn't have enough done.");
                        return false;
                    } else {
                        /*We had enough done, but we need to see if they are
                         *consistent enough.*/
                        if (getInconsistencyIndex(error) > maxInconsistency) {
                            /*Had enough compares, but not consistent enough.*/
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
        }
        /*We should never get here.*/
        return false;
    }
    /**Returns the inconsistency index for these comparisons.*/
    public double getInconsistencyIndex(double error) throws ConvergenceException {
        return (largestEigenvalue(error)-size)/getInconsistencyDivisor();
    }

    /**Returns the divisor for the inconsistency index.*/
    public double getInconsistencyDivisor() {
        double t=size-1;
        switch (size) {
            case 0: return 1;
            case 1: return 1;
            case 2: return 1;
            case 3: return .52*t;
            case 4: return .89*t;
            case 5: return 1.12*t;
            case 6: return 1.25*t;
            case 7: return 1.35*t;
            case 8: return 1.40*t;
            case 9: return 1.45*t;
            case 10: return 1.49*t;
            case 11: return 1.51*t;
            case 12: return 1.54*t;
            case 13: return 1.56*t;
            case 14: return 1.57*t;
            case 15: return 1.58*t;
        }
        return 1.98*(1-(size-1)/(size*(size-1)/2));
    }

    /**Tells the absolute position in a matrix, either counting left
     *to right, or down diagonals.
     *@param row The row position.
     *@param col The column position
     *@param size The size of the matrix
     *@param type If 0, then move diagonally, and 0 means (0,1) counting
     *down the diagonal.  You can have a negative position this way.  So
     *(0,0) would have position -size.
     */
    public static int absolutePosition(int row, int col, int size, int type) {
       int diag, part;
       switch (type) {
           case 1:
               return row*size+col;
           case 0:
               diag = col -row;
               if (diag > 0) {
                   part=(size*(size-1))/2-(size-diag)*(size-diag+1)/2;
                   return part+row;
               }
       }
       return 0;
    }
    /**Gets the inconsistency of a single entry, and fills in the oldIncon for you.*/
    public InconsistencyPlace inconsistency(int row, int col, double error)
        throws ConvergenceException
    {
        return inconsistency(row, col, this.getInconsistencyIndex(error), error);
    }

    /**Gets the inconsistencies of a single entry.*/
    public InconsistencyPlace inconsistency(int row, int col, double oldIncon, double error)
        throws ConvergenceException
    {
        double b,a, recV1, recV2, newInconV1, newInconV2, eigen[];
        InconsistencyPlace tmp;
        a=data[row][col];
        b=data[col][row];
        data[row][col]=0;
        data[col][row]=0;
        eigen=this.largestEigenvectorWithFix(error);
        if (eigen[col]!=0) {
            recV1=eigen[row]/eigen[col];
        } else {
            recV1=0;
        }
        newInconV1= this.getInconsistencyIndex(error);
        //Now get V2, i.e. gradient
        data[row][col]=a;
        data[col][row]=b;
        /*Commented out because the gradient method is slow as a dog!
         *
        InconsistencyPlace v2Icon=inconsistencyGradient(row, col, error, .05, .001, 100);
        newInconV2=v2Icon.improvedInconsistency;
        recV2=v2Icon.recommendedValue;
        tmp=new InconsistencyPlace(row, col, oldIncon, a, newInconV1, recV1, newInconV2, recV2);
         *
         */
        if ((oldIncon > newInconV1) && (!isComparisonOnlyDataForPosition(row, col)))
            tmp=new InconsistencyPlace(row, col, oldIncon, a, newInconV1, recV1);
        else
            tmp=new InconsistencyPlace(row, col, oldIncon, a, oldIncon, a);
        data[row][col]=a;
        data[col][row]=b;
        return tmp;
    }
    /**Gets the inconsistencies of all the entries, and returns them
     *sorted.*/
    public InconsistencyPlace[] getMostInconsistents(double error)
        throws ConvergenceException
    {
        double tmpD,rec, eigen[];
        java.util.ArrayList inconList = new java.util.ArrayList();
        InconsistencyPlace tmp, tmpL[];
        double oldIncon=this.getInconsistencyIndex(error);
        /*Loop through upper part, and find incons if I set that value to 0*/
        for(int i=0; i<size; i++) {
            for(int j=i+1; j<size; j++) {
                inconList.add(this.inconsistency(i, j, oldIncon, error));
            }
        }
        /*Now sort.*/
        java.util.Collections.sort(inconList);
        tmpL=new InconsistencyPlace[inconList.size()];
        /*Now let's return the data.*/
        return (InconsistencyPlace[])inconList.toArray(tmpL);
    }

    /**Well, there appears to be a problem calculating the most consistent value
     *of a given entry in the matrix, i.e. the standard calculating methods given
     *values that are actually less consistent than the starting ones sometimes
     *(in some cases all calculating methods do this.
     *To remedy this situation, we have a new most consistent calculation method.
     *It is a simple gradient heuristic.  It allows for variable steps, amongst
     *other tricks.  It will always work on the entry bigger than one (just to
     *help with round off errors).
     *The algorithm goes something like this:
     *<ul>
     *<li>If the entry is zero, set both itself, and the other entry to 1 (and backup
     *the original values.
     *<li>Find the direction of greatest decrease in the inconsistency, and go
     *a Delta_X in that direction.
     *<li>Find the direction of greatest decrease and go in that direction again.
     *However, if it is in the opposite direction as before, go only half the distance.
     *<li>Continue the process.
     *</ul>
     */
    public InconsistencyPlace inconsistencyGradient(int row, int col, double eigenError, double startingStep, double minStep, double maxSteps)
        throws ConvergenceException
    {
        double origValue, origInvert;
        double currentValue;
        origValue=get(row, col);
        origInvert=get(col,row);
        if (origValue == 0) {
            /**If the original value is zero, reset it to 1.*/
            origInvert=0;
            setReflexive(row, col, 1);
        }
        double nextVal;
        double origIncon=getInconsistencyIndex(eigenError);
        int steps=0;
        double best=origValue, nextStep;
        int lastDir=0;
        nextVal=data[row][col];
        while (((nextStep=gradientNextVal(row, col, eigenError, minStep, startingStep, origIncon, lastDir )) != 0)
            && (steps < maxSteps))
        {
            startingStep=Math.abs(nextStep);
            nextVal*=(1+nextStep);
            if (nextStep<0) {
                lastDir=-1;
            } else {
                lastDir=1;
            }
            best=nextVal;
            setReflexive(row, col, nextVal);
//            System.out.println("On step "+steps+" nextVal="+nextVal);
            logEvent("DoubleSquareMatrix.isComplete() - On step "+steps+" nextVal="+nextVal);
            steps++;
        }
        double newIncon=getInconsistencyIndex(eigenError);
        data[row][col]=origValue;
        data[col][row]=origInvert;
        return new InconsistencyPlace(row, col, origIncon, origValue, newIncon, best);
    }


    /**Decides, if we move should move positive or negative in the gradient.*/
    private double gradientNextVal(int row, int col, double error, double minStep, double step, double oldIncon, int lastDir)
        throws ConvergenceException
    {
        if (oldIncon < 0) {
            oldIncon=getInconsistencyIndex(error);
        }
        double oldVal, oldInvert, posIncon, negIncon;
        oldVal=data[row][col]; oldInvert=data[col][row];
        double highVal=(1+step)*data[row][col];
        setReflexive(row, col, highVal);
        posIncon=getInconsistencyIndex(error);
        double lowVal=(1-step)*oldVal;
        setReflexive(row, col, lowVal);
        negIncon=getInconsistencyIndex(error);
        if ((negIncon > oldIncon)&&(posIncon > oldIncon)) {
            step/=2;
            if (step < minStep) {
                return 0;
            } else {
                return gradientNextVal(row, col, error, minStep, step, oldIncon, lastDir);
            }
        }
        if (negIncon < posIncon) {
            if (lastDir > 0) {
                step/=2;
                //System.out.println("Halving step, new is "+step);
                if (Math.abs(step) < minStep) {
                    return 0;
                }
                return gradientNextVal(row, col, error, minStep, step, oldIncon, -1);
            } else {
                return -step;
            }
        } else {
            if (lastDir < 0) {
                step/=2;
                //System.out.println("Halving step, new is "+step);
                if (Math.abs(step) < minStep) {
                    return 0;
                }
                return gradientNextVal(row, col, error, minStep, step, oldIncon, 1);
            } else {
                return step;
            }
        }
    }
    
    /**Asks if the data in row r column c is the only comparison data
     *that we can use to fill in that spot (or if we could use other data
     *to get that one by multiplying various numbers in the matrix.*/
    public boolean isComparisonOnlyDataForPosition(int r, int c)
    {
        DoubleSquareMatrix tmp = new DoubleSquareMatrix(this);
        tmp.data[r][c]=tmp.data[c][r]=0;
        DoubleSquareMatrix tmpp = new DoubleSquareMatrix(this.size);
        tmp.power(size, tmpp);
        if (tmpp.data[r][c]!=0) {
            return false;
        } else {
            return true;
        }
        
    }
    
    /**Sets all entries in this matrix to val.*/
    public void set(double val) {
        for(int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                data[i][j]=val;
            }
        }
    }
    
    /**Gets most calc attempts on eigen vectors.*/
    private int MaxEigenIterations(int size) {
        return MaxEigenIterations*size*size;
    }
    
    /**Sets the diagonal to 1, and if report is true, will tell we had non-1's
     *and had to fix them.
     */
    public void setDiagoanlTo1(boolean report) {
        for(int i=0; i<size; i++) {
            if (report) {
                if (data[i][i]!=1) {
                    logEvent("setdiagonal to 1 needed to fix diagonal "+i);
                }
            }
            data[i][i]=1;
        }
    }
    
    /*Begin stuff for computing compatibility indices*/
    //We do the compatibility index by taking the ratio matrix and summing
    public static final int COMPAT_RATIO_SUM=0;
    //Do the compatibility index by taking the ratio matrix, multiplying upper
    //diagonal, and taking the appropriate root
    public static final int COMPAT_RATIO_PRODUCT=1;
    //Do the compatibility based upon eigenvectors and take ratios, sum
    public static final int COMPAT_EIGEN_SUM=2;
    //Do the compatibility based on the eigenvectors and take ratios, then product those numbers.
    public static final int COMPAT_EIGEN_PRODUCT=3;
    //Do the compatibility based on Alignment2013 with comparison matrix
    public static final int COMPAT_2013_MATRIX=4;
    //Do the compatibility based on Alignment2013 with resulting eigen vectors
    public static final int COMPAT_2013_EIGEN=5;
    /**Computes how compatible the given matrix is with this
     * 
     * @param other
     * @param flag  Which kind of calculation should we do?
     * @return
     */
    public double compatIndex(DoubleSquareMatrix other, int flag) {
    	if (isPost2013Flag(flag)) {
    		return compatIndexPost2013(other, flag);
    	} else {
    		return compatIndex(other, flag, true);
    	}
    }
    
    private double compatIndexPost2013(DoubleSquareMatrix other, int flag) {
    	if (other==null) {
    		throw new IllegalArgumentException("Other cannot be null.");
    	} else if (other.size() != size()) {
    		throw new IllegalArgumentException("Other size "+other.size()+" not equal to this size "+size());
    	}
    	if (flag == COMPAT_2013_MATRIX) {
    		return compatIndexPost2013Matrix(other);
    	} else if (flag == COMPAT_2013_EIGEN) {
    		return compatIndexPost2013Eigen(other);
    	} else {
    		throw new IllegalArgumentException("Cannot compute compatIndexPost2013 with flag "+flag);
    	}
	}

	private double compatIndexPost2013Eigen(DoubleSquareMatrix other) {
		try {
			if (size==0)
				return 1.0;
			double[] eigen1 = this.largestEigenvectorIdealWithFix(DEFAULT_EIGEN_ERROR);
			double[] eigen2 = other.largestEigenvectorIdealWithFix(DEFAULT_EIGEN_ERROR);
			double ratios[] = new double[size];
			double sum = 0;
			for(int i=0; i < size; i++) {				
				if (eigen1[i]==0) {
					ratios[i]=eigen2[i];
				} else if (eigen2[i]==0) {
					ratios[i]=eigen1[i];
				} else if (eigen1[i] < eigen2[i]) {
					ratios[i]=eigen1[i] / eigen2[i];
				} else {
					ratios[i] = eigen2[i] / eigen1[i];
				}
				sum+=ratios[i];
			}
			return sum / size;
		} catch (ConvergenceException e) {
			logEvent(e);
			return 0;
		}
	}

	private double compatIndexPost2013Matrix(DoubleSquareMatrix other) {
		double sum=0;
		double ratio=0;
		int count=0;
		for(int row=0; row < size; row++) {
			for(int col=row+1; col < size; col++) {
				if ((data[row][col]==0) || (other.data[row][col]==0)) {
					//No comparison data here, nothing to compare to, ignore
				} else {
					ratio = data[row][col] / other.data[row][col];
					if (ratio > 1)
						ratio = 1/ratio;
					count++;
					sum+=ratio;
				}
			}
		}
		if (count > 0)
			return sum / count;
		else
			return 1.0;
	}

	private boolean isPost2013Flag(int flag) {
    	return (flag == COMPAT_2013_EIGEN) || (flag == COMPAT_2013_MATRIX);
	}

	/**Computes how compatible the given matrix is with this
     * 
     * @param other
     * @param flag  Which kind of calculation should we do?
     * @return
     */
    private double compatIndex(DoubleSquareMatrix other, int flag, boolean makePercent) {
    	if (other==null)
    		throw new NullPointerException();
    	if (other.getSize()!=getSize())
    		throw new ArrayIndexOutOfBoundsException();
    	double rval;
    	if ((flag==COMPAT_RATIO_SUM) || (flag==COMPAT_RATIO_PRODUCT)) {
    		DoubleSquareMatrix ratio=ratioOfMatrices(other);
    		if (flag==COMPAT_RATIO_SUM) {
    			rval=0;
    			int count=0;
    			for(int i=0; i<size; i++) {
    				for(int j=0; j<size; j++) {
    					rval+=ratio.data[i][j];
    					count++;
    				}
    			}
    			if (count > 0) rval/=count;
    		} else {
    			rval=1;
    			for(int i=0; i<size;i++)
    				for(int j=i+1;j<size; j++)
    					rval*=ratio.data[i][j];
    			rval=Math.pow(rval, 1.0/((size-1)*size/2.0));
    		}
    	} else if ((flag==COMPAT_EIGEN_PRODUCT)||(flag==COMPAT_EIGEN_SUM)){
    		try {
    			if (size==0) return 0;
    			double[] eigenMine=largestEigenvector();
    			double[] eigenOther=other.largestEigenvector();
    			double[] ratios=new double[eigenMine.length-1];
    			for(int i=0;i<ratios.length;i++) {
    				if (eigenMine[i]!=0) {
    					ratios[i]=Math.abs(eigenOther[i]/eigenMine[i]);
    				} else {
    					ratios[i]=1;
    				}
    				if (ratios[i]==0) ratios[i]=1;
    				if (ratios[i]<1) {
    					ratios[i]=1/ratios[i];
    				}
    			}
    			if (flag==COMPAT_EIGEN_PRODUCT) {
    				rval=1;
    				for(int i=0; i<ratios.length; i++) {
    					rval*=ratios[i];
    				}
    				rval=Math.pow(rval, 1.0/ratios.length);
    			} else {
    				rval=0;
    				for(int i=0; i<ratios.length; i++) {
    					rval+=ratios[i];
    				}
    				if (ratios.length>0) rval/=ratios.length;
    			}
    		} catch (ConvergenceException e) {
    			logEvent(e);
    			return 0;
    		}
    	} else {
    		logEvent("Unknown compatibility flag "+flag+", simply returning 0.");
    		return 0;
    	}
    	//Alright, now we have to turn this bloody thing to percentage, bastages.
    	//First we get the maximally different matrix, and use that to compare
    	DoubleSquareMatrix maximallyDiff=new DoubleSquareMatrix(this);
    	double maxVote=10;
    	for(int i=0; i<size; i++) {
    		for(int j=i+1; j<size; j++) {
    			if (maximallyDiff.data[i][j]>1) {
    				maximallyDiff.data[i][j]=1/maxVote;
    				maximallyDiff.data[j][i]=maxVote;
    			} else if ((maximallyDiff.data[i][j]>0) && (maximallyDiff.data[i][j]<1)) {
    				maximallyDiff.data[i][j]=maxVote;
    				maximallyDiff.data[j][i]=1/maxVote;    				
    			}
    		}
    	}
    	if (makePercent) {
    		//Have maximally diff, get it's compatIndex
    		double maxCompatIndex=compatIndex(maximallyDiff, flag, false);
    		//Now call the magic function that takes this info and turns it into a percentage
    		//System.out.println("Raw is "+rval+" max is "+maxCompatIndex);
    		rval=toPercent(1, .82*maxCompatIndex, rval);
    	}
    	return rval;
    }
    
    private void logEvent(String string) {
    	System.out.println(string);
	}

	public double toPercentOld(double lowx, double hix, double valx) {
    	double midPercentX=.1;
    	double lowy=1; double hiy=0; double midx=lowx*midPercentX+hix*(1-midPercentX);
    	double midy=.3;
    	double diffx;
    	if (valx<lowx) {
    		return lowy;
    	} else if (valx > hix) {
    		return hiy;
    	} else if (valx <= midx) {
    		diffx=(valx-lowx)/(midx-lowx);
    		return diffx*midy+(1-diffx)*lowy;
    	} else {
    		diffx=(valx-midx)/(hix-midx);
    		return diffx*hiy+(1-diffx)*midy;
    	}
    }
    
    public static double toPercent(double lowx, double hix, double valx) {
    	if (valx<1) {
    		return 1;
    	} else if (valx<1.1) {
    		return linear(1,1,1.1,.9580961, valx);
    	} else if (valx<2.7) {
    		double m1=-.4816133758;
    		double b1=1.496749561;
    		return m1*valx+b1;
    	} else if (valx<=hix){
    		double x1=2.7;
    		double y1=.1963934466;
    		double x2=hix;
    		double y2=0;
    		return linear(x1,y1,x2,y2,valx);
    	} else {
    		return 0;
    	}
    }
    
    public static double linear(double x1, double y1, double x2, double y2, double x) {
    	double slope=(y2-y1)/(x2-x1);
    	return slope*(x-x1)+y1;
    }
    //Calc group compat using average of their vals
    public static final int COMPAT_GROUP_AVERAGE=0;
    //Calc group compat using geometric average
    public static final int COMPAT_GROUP_GEOMETRIC_AVERAGE=1;
    
    /**
     * Group compatibility.
     * @param others
     * @param individualCalcFlag
     * @param groupCalcFlag
     * @return
     */
    public double compatIndex(DoubleSquareMatrix[] othersRaw, int individualCalcFlag, int groupCalcFlag) {
    	Vector<DoubleSquareMatrix> othersVec=new Vector<DoubleSquareMatrix>();
    	for(int i=0; i<othersRaw.length; i++) {
    		if (!othersRaw[i].isId()) {
    			othersVec.add(othersRaw[i]);
    		}
    	}
    	DoubleSquareMatrix others[]=new DoubleSquareMatrix[othersVec.size()];
    	others=othersVec.toArray(others);
    	if (others.length<=0) return 1;
    	double []individualCompats=new double[others.length];
    	for(int i=0; i<others.length; i++) {
    		individualCompats[i]=compatIndex(others[i], individualCalcFlag);
    	}
    	double rval;
    	if (groupCalcFlag==COMPAT_GROUP_AVERAGE) {
    		rval=0;
    		for(int i=0; i<individualCompats.length; i++) {
    			rval+=individualCompats[i];
    		}
    		if (individualCompats.length > 0) {
    			return rval/individualCompats.length;
    		} else {
    			return 0;
    		}
    	} else {
    		rval=1.0;
    		for(int i=0; i<individualCompats.length; i++) {
    			rval*=individualCompats[i];
    		}
    		if (individualCompats.length > 0) {
    			return Math.pow(rval, 1.0/individualCompats.length);
    		} else {
    			return 0;
    		}
    	}
    }
    //The default error to use for largestEigenvector
    public static final double DEFAULT_EIGEN_ERROR=1e-9;
    /**
     * Simply calls largestEigenvector(DEFAULT_EIGEN_ERROR).
     * @return
     * @throws ConvergenceException
     */
    public double[] largestEigenvector() throws ConvergenceException {
    	return largestEigenvector(DEFAULT_EIGEN_ERROR);
    }
    
    public DoubleSquareMatrix ratioOfMatrices(DoubleSquareMatrix other) {
    	if (other==null)
    		throw new NullPointerException();
    	if (other.size!=size)
    		throw new ArrayIndexOutOfBoundsException();
    	DoubleSquareMatrix result=new DoubleSquareMatrix(this);
    	double val;
    	for(int i=0; i<size; i++) {
    		result.data[i][i]=1.0;
    		for(int j=i+1; j<size; j++) {
    			if (data[i][j]!=0) {
    				val=other.data[i][j]/data[i][j];    				
    			} else {
    				val=1;
    			}
    			val=Math.abs(val);
    			if (val==0) val=1;
    			if (val<1) val=1.0/val;
    			result.data[i][j]=val;
    			result.data[j][i]=val;
    		}
    	}
    	return result;
    }
    
    public static void testCompat() {
    	DoubleSquareMatrix mat1=new DoubleSquareMatrix(4, "2 2 2 2 2 2", true);
    	DoubleSquareMatrix mat2=new DoubleSquareMatrix(4, "4 2 2 2 2 2", true);
    	double rawCompat, compat;
    	rawCompat=mat1.compatIndex(mat2, COMPAT_RATIO_SUM, false);
    	compat=mat1.compatIndex(mat2, COMPAT_RATIO_SUM, true);
    	System.out.println("RATIO_SUM RawCompat="+(rawCompat));
    	System.out.println("RATIO_SUM Compat="+(compat*100)+"%\n");

    	rawCompat=mat1.compatIndex(mat2, COMPAT_RATIO_PRODUCT, false);
    	compat=mat1.compatIndex(mat2, COMPAT_RATIO_PRODUCT, true);
    	System.out.println("RATIO_PRODUCT RawCompat="+(rawCompat));
    	System.out.println("RATIO_PRODUCT Compat="+(compat*100)+"%\n");

    	rawCompat=mat1.compatIndex(mat2, COMPAT_EIGEN_SUM, false);
    	compat=mat1.compatIndex(mat2, COMPAT_EIGEN_SUM, true);
    	System.out.println("EIGEN_SUM RawCompat="+(rawCompat));
    	System.out.println("EIGEN_SUM Compat="+(compat*100)+"%\n");
    	
    	rawCompat=mat1.compatIndex(mat2, COMPAT_EIGEN_PRODUCT, false);
    	compat=mat1.compatIndex(mat2, COMPAT_EIGEN_PRODUCT, true);
    	System.out.println("EIGEN_PRODUCT RawCompat="+(rawCompat));
    	System.out.println("EIGEN_PRODUCT Compat="+(compat*100)+"%\n");
    	
    	DoubleSquareMatrix others[]=new DoubleSquareMatrix[3];
    	others[0]=new DoubleSquareMatrix(4, "4 2 2 2 2 2", true);
    	others[1]=new DoubleSquareMatrix(4, "2 2 2 2 2 2", true);
    	others[2]=new DoubleSquareMatrix(4, "2 2 2 2 2 2", true);
    	
    	double avg;
    	double gavg;
    	avg=mat1.compatIndex(others, COMPAT_RATIO_SUM, COMPAT_GROUP_AVERAGE);
    	gavg=mat1.compatIndex(others, COMPAT_RATIO_SUM, COMPAT_GROUP_GEOMETRIC_AVERAGE);
    	System.out.println("RATIO_SUM/AVG = "+(avg*100)+"%");
    	System.out.println("RATIO_SUM/GeomAVG = "+(avg*100)+"%\n");
    	
    	avg=mat1.compatIndex(others, COMPAT_RATIO_PRODUCT, COMPAT_GROUP_AVERAGE);
    	gavg=mat1.compatIndex(others, COMPAT_RATIO_PRODUCT, COMPAT_GROUP_GEOMETRIC_AVERAGE);
    	System.out.println("RATIO_PRODUCT/AVG = "+(avg*100)+"%");
    	System.out.println("RATIO_PRODUCT/GeomAVG = "+(avg*100)+"%\n");
    	
    	avg=mat1.compatIndex(others, COMPAT_EIGEN_SUM, COMPAT_GROUP_AVERAGE);
    	gavg=mat1.compatIndex(others, COMPAT_EIGEN_SUM, COMPAT_GROUP_GEOMETRIC_AVERAGE);
    	System.out.println("EIGEN_SUM/AVG = "+(avg*100)+"%");
    	System.out.println("EIGEN_SUM/GeomAVG = "+(avg*100)+"%\n");
    	
    	avg=mat1.compatIndex(others, COMPAT_EIGEN_PRODUCT, COMPAT_GROUP_AVERAGE);
    	gavg=mat1.compatIndex(others, COMPAT_EIGEN_PRODUCT, COMPAT_GROUP_GEOMETRIC_AVERAGE);
    	System.out.println("EIGEN_PRODUCT/AVG = "+(avg*100)+"%");
    	System.out.println("EIGEN_PRODUCT/GeomAVG = "+(avg*100)+"%\n");
    	
    }
    
    /**Does component-wise multiplication*/
    public DoubleSquareMatrix componentMultiply(DoubleSquareMatrix rhs)
        throws ArrayIndexOutOfBoundsException
    {
        if (size != rhs.size) {
            throw new ArrayIndexOutOfBoundsException("Tried to muliply a matrix of size "+size+" to a matrix of size "+rhs.size);
        }
        DoubleSquareMatrix result=new DoubleSquareMatrix(size);
        for(int i=0; i<size; i++) {
            for(int j=0; j<size;j++) {
                result.data[i][j]=data[i][j]*rhs.data[i][j];                
            }
        }
        return result;
    }
    /**
     * Uses old method, and is slow, should be fixed.
     *@param levels If not <code>null</code> the levels of our hierarchy (this should have
     *been computed using {@link getLevels}.  If <code>null</code> then we will compute
     *it here.
     *@return The limit matrix.
     *@throws NonHierarchyException If levels was <code>null</code> and the matrix was not
     *the matrix of a hierarchy.
     */
    public DoubleSquareMatrix limitPowerHierarchySA(int clusterStarts[], Vector levels)
        throws NonHierarchyException
    {
        int i,j, k, l;
        if (levels==null) {
            levels=getLevels();
        }
        if (levels == null) {
            throw new NonHierarchyException();
        }
        DoubleSquareMatrix structural=structuralAdjustTree(clusterStarts);
        //System.out.println("Limit power hierarchy had "+levels.size()+" levels.");
        DoubleSquareMatrix tmp = this.componentMultiply(structural);
        return tmp.limitPowerHierarchy(null);
    }
    /**A non-hierarchical limit power that uses structural adjust.
     */    
    public DoubleSquareMatrix limitPowerNonHierarchySA(int clusterStarts[], double err)
        throws ConvergenceException
    {
        /*Need to get a starting power*/
        DoubleSquareMatrix start=new DoubleSquareMatrix(size);
        DoubleSquareMatrix currentPower = new DoubleSquareMatrix(this);
        DoubleSquareMatrix next = new DoubleSquareMatrix(size);
        DoubleSquareMatrix structural=structuralAdjustTree(clusterStarts);
        /*Now go through a failsafe loop*/
        for(int i=0;i<MaxLimitIterations; i++) {
            for(int k=0;k<size;k++) { /*look for convergence*/
                this.multiply(currentPower, next);
                next.componentMultiply(structural);
                if (next.normalizedDiff(start) < err) {
                    next.makeColsAddToOne();
                    return next;
                }
                currentPower.copy(next);
            }
            start.copy(next);
        }
        /*If we make it here we never converged*/
        throw new ConvergenceException("LimitPower non hierarchy did not converge.");
    }
}

