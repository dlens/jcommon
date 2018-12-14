
package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.NonHierarchyException;
import com.dlens.common2.exceptions.ConvergenceException;
import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMable;

import org.apache.log4j.Logger;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * This class is a generic double matrix class.  It was originally
 * needed only for the ratings section.
 * @author  dlens
 */
public class DoubleMatrix implements java.io.Serializable, JDOMable {
	//The logger
	private static Logger logger = Logger.getLogger(DoubleMatrix.class);
    /**Number of rows in the matrix.*/
    private int rows=0;
    /**Number of columns in the matrix.*/
    private int cols=0;
    /**The data of the matrix.*/
    private double[][] data=null;
    
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
    static final int MaxLimitIterations = 100000;
    /**This flag says that if a place in the matrix is completely uncompared, then its
     *priority should be returned as zero.  This is <b>not followed</b> if every place
     *is not compared.  In that case we return all equal priorities.
     */
    static final boolean ZeroCompletelyUncompared = true;

    /** Creates a new instance of DoubleMatrix of size 0x0 */
    public DoubleMatrix() {
    }
    /**Creates a new instance of DoubleMatrix of size rows x cols*/
    public DoubleMatrix(int rows, int cols) {
        this.rows=rows; this.cols=cols;
        data=new double[rows][cols];
    }

    /**
     *Constructs a copy of the matrix.
     *@param src The matrix to copy from.
     */
    public DoubleMatrix(DoubleMatrix src)
        throws ArrayIndexOutOfBoundsException
    {
        rows=src.rows; cols=src.cols;
        data = new double[rows][cols];
        for(int i=0;i<rows;i++)
            for(int j=0;j<cols;j++)
                data[i][j]=src.data[i][j];
    }

    /**
     * Creates a double matrix with the data given as it's
     * data.  It does not copy.
     */
    public DoubleMatrix(double [][]data)
            throws ArrayIndexOutOfBoundsException
    {
        
        if ((data==null)||(data.length==0)) {
            this.data=new double[0][0];
            rows=0; cols=0;
            return;
        }
        this.data=data;
        this.rows=data.length;
        this.cols=data[0].length;
    }
    
    public void makeRatioFromUpper() {
        for(int i=0; i< data.length; i++) {
            data[i][i]=1.0;
            if (data[i].length!=data.length)
                throw new ArrayIndexOutOfBoundsException();
            for(int j=1; j<data.length; j++) {
                if (data[i][j]!=0) {
                    data[j][i]=1.0/data[i][j];
                } else {
                    data[j][i]=0.0;
                }
            }
        }        
    }
    
    /**Sets a place in the matrix.*/
    public void set(int row, int col, double val)
        throws ArrayIndexOutOfBoundsException
    {
        data[row][col]=val;
    }

    /**Gets a value in the matrix.*/
    public double get(int row, int col)
        throws ArrayIndexOutOfBoundsException
    {
        if (data==null)
            throw new ArrayIndexOutOfBoundsException("Null matrix, no data to get.");
        return data[row][col];
    }

    /**
     * Returns number of columns in instance
     * @return
     */
    public int getCols() {
      return cols;
    }

    /**
     * Returns number of rows in instance
     * @return
     */
    public int getRows() {
      return rows;
    }

    /**Inserts a row before the given row.*/
    public void addRowBefore(int row)
        throws ArrayIndexOutOfBoundsException
    {
        if ((rows==0)&&(row==0)) {
            /*The empty matrix case.*/
            rows++;
            data=new double[rows][cols];
            return;
        }
        if ((row < 0) || (row >=this.rows)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        double[][] newData=new double[rows+1][cols];
        for(int i=0; i<row; i++)
            for(int j=0; j<cols; j++)
                newData[i][j]=data[i][j];
        for(int i=row; i<rows ;i++)
            for(int j=0; j<cols; j++)
                newData[i+1][j]=data[i][j];
        rows++;
        data=newData;
    }

    /**Inserts a column before the given column.*/
    public void addRowAfter(int row)
        throws ArrayIndexOutOfBoundsException
    {
        if ((rows==0)&&(row==0)) {
            /*The empty matrix case.*/
            rows++;
            data=new double[rows][cols];
            return;
        }
        if ((row < 0) || (row >=this.rows)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        double[][] newData=new double[rows+1][cols];
        for(int i=0; i<=row; i++)
            for(int j=0; j<cols; j++)
                newData[i][j]=data[i][j];
        for(int i=(row+1); i<rows ;i++)
            for(int j=0; j<cols; j++)
                newData[i+1][j]=data[i][j];
        rows++;
        data=newData;
    }

    /**Adds a row at the end of the matrix.*/
    public void addRow() {
        try {
            if (this.rows==0) {
                addRowAfter(0);
            } else {
                addRowAfter(this.rows-1);
            }
        } catch (Exception ignored) {};
    }

    /**Inserts a column before the given column.*/
    public void addColBefore(int col)
        throws ArrayIndexOutOfBoundsException
    {
        if ((cols==0)&&(col==0)) {
            /*The empty matrix case.*/
            cols++;
            data=new double[rows][cols];
            return;
        }
        if ((col < 0) || (col >=this.cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        double[][] newData=new double[rows][cols+1];
        for(int i=0; i<rows; i++)
            for(int j=0; j<col; j++)
                newData[i][j]=data[i][j];
        for(int i=0; i<rows ;i++)
            for(int j=col; j<cols; j++)
                newData[i][j+1]=data[i][j];
        cols++;
        data=newData;
    }

    /**Inserts a column before the given column.*/
    public void addColAfter(int col)
        throws ArrayIndexOutOfBoundsException
    {
        if ((cols==0)&&(col==0)) {
            /*The empty matrix case.*/
            cols++;
            data=new double[rows][cols];
            return;
        }
        if ((col < 0) || (col >=this.cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        double[][] newData=new double[rows][cols+1];
        for(int i=0; i<rows; i++)
            for(int j=0; j<=col; j++)
                newData[i][j]=data[i][j];
        for(int i=0; i<rows ;i++)
            for(int j=(col+1); j<cols; j++)
                newData[i][j+1]=data[i][j];
        cols++;
        data=newData;
    }

    /**Adds a row at the end of the matrix.*/
    public void addCol() {
        try {
            if (this.cols==0) {
                addColAfter(0);
            } else {
                addColAfter(this.cols-1);
            }
        } catch (Exception ignored) {};
    }


    /**Adds two matrices.*/
    public DoubleMatrix addTo(DoubleMatrix m) {
        if ((m.rows != this.rows) || (m.cols != this.cols)) {
            throw new ArrayIndexOutOfBoundsException("Matrices not of same size.");
        }
        DoubleMatrix rval = new DoubleMatrix(this);
        for(int i=0; i<rval.rows; i++)
            for(int j=0; j<rval.cols;j++)
                rval.data[i][j]+=m.data[i][j];
        return rval;
    }

    /**This function sums up the columns in a matrix.*/
    public double[] sumCols() {
        double[] rval=new double[rows];
        for(int i=0; i<rows; i++) {
            rval[i]=0;
            for(int j=0; j<cols; j++) {
                rval[i]+=data[i][j];
            }
        }
        return rval;
    }

    /**This function sums up the columns in a matrix, scaling with the
     priorities given.*/
    public double[] sumCols(double []priorities) {
        /*System.out.println("Summing cols with column priorities:");
            for(int i=0;i<priorities.length; i++)
                System.out.println(priorities[i]+"");
         */
        double[] rval=new double[rows];
        for(int i=0; i<rows; i++) {
            rval[i]=0;
            for(int j=0; j<cols; j++) {
                rval[i]+=priorities[j]*data[i][j];
            }
        }
        return rval;
    }

    /**This function sums up the columns in a matrix, scaling with the
     priorities given, and using the last vector to store the output.
     The priorities are scaled to add to one. 
     */
    public void sumCols(double []old_priorities, double []rval) {
        /*Normalize first*/
        double sum=0;
        double []priorities=new double[old_priorities.length];
        for(int i=0; i<priorities.length; i++)
            sum+=java.lang.Math.abs(old_priorities[i]);
        if (sum > 0)
            for(int i=0; i<priorities.length; i++)
                priorities[i]=old_priorities[i]/sum;
        //System.out.println("Summing cols with column priorities:");
        //for(int i=0;i<priorities.length; i++)
        //    System.out.println(priorities[i]+"");
        for(int i=0; i<rows; i++) {
            rval[i]=0;
            sum=0;
            for(int j=0; j<cols; j++) {
                if (data[i][j]!=0) {
                    rval[i]+=priorities[j]*data[i][j];
                    sum+=priorities[j];
                }
            }
        }
    }

    /**This function converts the double matrix to a simple
     *string, useful for printing debug info.
     */
    public String toString() {
        StringBuffer buff = new StringBuffer(rows*cols*16);
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                buff.append(data[i][j]+" ");
            }
            buff.append("\n");
        }
        return buff.toString();
    }

    /**
     *Constructs a <code>jdom</code> element representing our double matrix.
     *Useful for storing and reloading a matrix (see {@link fromElement} for reloading).
     *@param name The name to give the element we are returning.
     *@return The <code>jdom</code> element representing this matrix.
     */

    public Element toElement(String name)
    {
        Element rval=new Element(name);
        rval.setAttribute("rows", ""+rows);
        rval.setAttribute("cols", ""+cols);
        rval.setText(toString());
        return rval;
    }

    /**The parameter is a vector of matrices each with the same
     *size.  It returns a matrix of that size that is the
     *average of the given matrices.
     *@param matrices The matrices to average.
     *@returns The new matrix.  Returns null if all of the matrices
     *don't have the same size, or if one of the vector elements wasn't
     *a matrix.
     */
    public static DoubleMatrix averageMatrices(Vector matrices)
    {
        DoubleMatrix rval;
        if (matrices.size()==0)
            return null;
        DoubleMatrix dms[] = new DoubleMatrix[matrices.size()];
        dms = (DoubleMatrix[])matrices.toArray(dms);
        int i,j,k, nmats=dms.length, rows = dms[0].rows, cols=dms[0].cols;
        /*First check that all have the same dims.*/
        for(i=0;i<nmats;i++) {
            if ((dms[i].rows != rows)||(dms[i].cols!=cols)) {
                return null;
            }
        }
        /*Okay all have same size.*/
        rval = new DoubleMatrix(rows, cols);
        for(i=0;i<rows;i++) {
            for(j=0;j<cols;j++) {
                rval.data[i][j]=0;
                for(k=0;k<nmats;k++) {
                    rval.data[i][j]+=dms[k].data[i][j];
                }
                rval.data[i][j]/=nmats;
            }
        }
        return rval;
    }

    /**The parameter is a vector of matrices each with the same
     *size.  It returns a matrix of that size that is the
     *average of the given matrices.
     *@param matrices The matrices to average.
     *@returns The new matrix.  Returns null if all of the matrices
     *don't have the same size, or if one of the vector elements wasn't
     *a matrix.
     */
    public static DoubleMatrix averageMatrices(DoubleMatrix dms[])
    {
        DoubleMatrix rval;
        if (dms.length==0)
            return null;
        int i,j,k, nmats=dms.length, rows = dms[0].rows, cols=dms[0].cols;
        /*First check that all have the same dims.*/
        for(i=0;i<nmats;i++) {
            if ((dms[i].rows != rows)||(dms[i].cols!=cols)) {
                return null;
            }
        }
        /*Okay all have same size.*/
        rval = new DoubleMatrix(rows, cols);
        for(i=0;i<rows;i++) {
            for(j=0;j<cols;j++) {
                rval.data[i][j]=0;
                for(k=0;k<nmats;k++) {
                    rval.data[i][j]+=dms[k].data[i][j];
                }
                rval.data[i][j]/=nmats;
            }
        }
        return rval;
    }

    /**The parameter is a vector of matrices each with the same
     *size.  It returns a matrix of that size that is the
     *average of the given matrices, not including the zeroes in the average.
     *@param matrices The matrices to average.
     *@returns The new matrix.  Returns null if all of the matrices
     *don't have the same size, or if one of the vector elements wasn't
     *a matrix.
     */
    public static DoubleMatrix averageMatricesNonZero(Vector matrices)
    {
        double val;
        DoubleMatrix rval;
        int count;
        if (matrices.size()==0)
            return null;
        DoubleMatrix dms[] = new DoubleMatrix[matrices.size()];
        dms = (DoubleMatrix[])matrices.toArray(dms);
        int i,j,k, nmats=dms.length, rows = dms[0].rows, cols=dms[0].cols;
        /*First check that all have the same dims.*/
        for(i=0;i<nmats;i++) {
            if ((dms[i].rows != rows)||(dms[i].cols!=cols)) {
                return null;
            }
        }
        /*Okay all have same size.*/
        rval = new DoubleMatrix(rows, cols);
        for(i=0;i<rows;i++) {
            for(j=0;j<cols;j++) {
                rval.data[i][j]=0;
                count=0;
                for(k=0;k<nmats;k++) {
                    val=dms[k].data[i][j];
                    rval.data[i][j]+=val;
                    if (val!=0)
                        count++;
                }
                if (count>0)
                    rval.data[i][j]/=count;
            }
        }
        return rval;
    }
    /**
     *Fills in this double matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element)
        throws XMLFormatException
    {
        if (element == null)
            throw new XMLFormatException("Null XML element sent to DoubleMatrix.");
        String rows=element.getAttributeValue("rows");
        String cols=element.getAttributeValue("cols");
        String text = element.getTextTrim();
        int i, j, count=0, rowsInt=0, colsInt=0;
        if ((rows != null) && (!"".equals(rows))) {
            try {
                rowsInt = Integer.parseInt(rows);
            } catch (NumberFormatException e) {
                throw new XMLFormatException("While parsing a matrix, rows attribute ="+rows+" was not an integer.");
            }
        } else {
            throw new XMLFormatException("While parsing the matrix, no rows value given.");
        }
        if ((cols != null) && (!"".equals(cols))) {
            try {
                colsInt = Integer.parseInt(cols);
            } catch (NumberFormatException e) {
                throw new XMLFormatException("While parsing a matrix, cols attribute ="+cols+" was not an integer.");
            }
        } else {
            throw new XMLFormatException("While parsing the matrix, no cols value given.");
        }
        //System.out.println("Found matrix with size="+sizeInt);
        String numbers[] = text.split("\\s+");
        if (numbers.length < rowsInt*colsInt) {
            throw new XMLFormatException("Only gave "+numbers.length+
            " data pieces, however we needed "+rowsInt*colsInt+
            " pieces of data for a "+rowsInt+" x " + colsInt +
            " matrix.");
        }
        data=new double[rowsInt][colsInt];
        this.rows=rowsInt; this.cols=colsInt;
        for(i=0;i<rowsInt;i++) {
            for(j=0;j<colsInt;j++) {
                try {
                    data[i][j]=Double.parseDouble(numbers[count]);
                } catch (Exception e) {}
                count++;
            }
        }
    }

    public void rmCol(int place)
        throws ArrayIndexOutOfBoundsException
    {
        if ((place < 0) || (place >= this.cols))
            throw new ArrayIndexOutOfBoundsException();
        double [][]newData=new double[rows][cols-1];
        for(int i=0; i<rows; i++) {
            for (int j=0; j<place; j++) {
                newData[i][j]=data[i][j];
            }
            for (int j=place+1; j<cols;j++) {
                newData[i][j-1]=data[i][j];
            }
        }
        data=newData;
        cols--;
    }

    public void rmRow(int place)
        throws ArrayIndexOutOfBoundsException
    {
        if ((place < 0) || (place >= this.rows))
            throw new ArrayIndexOutOfBoundsException();
        double [][]newData=new double[rows-1][cols];
        for(int j=0; j<cols; j++) {
            for(int i=0; i<place; i++) {
                newData[i][j]=data[i][j];
            }
            for(int i=place+1; i<rows; i++) {
                newData[i-1][j]=data[i][j];
            }
        }
        data=newData;
        rows--;
    }


    /**Moves a column after a given column (end).  If end=-1, that
     *means to move the given column to the zeroth position.
     */
    public void mvColAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        double last, mover;
        if (start==end)
        if ((start < 0)||(start>=this.cols)||(end<-1)||(end>=this.cols))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            for(int i=0; i<rows; i++) {
                mover=data[i][start];
                for(int j=start; j < end; j++) {
                    data[i][j]=data[i][j+1];
                }
                data[i][end]=mover;
            }
        } else {
            for(int i=0; i<rows; i++) {
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
        if ((start < 0)||(start>=this.rows)||(end<-1)||(end>=this.rows))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            for(int j=0; j<cols; j++) {
                mover=data[start][j];
                for(int i=start; i < end; i++) {
                    data[i][j]=data[i+1][j];
                }
                data[end][j]=mover;
            }
        } else {
            for(int j=0; j<cols; j++) {
                mover=data[start][j];
                for(int i=start; i > (end+1); i--) {
                    data[i][j]=data[i-1][j];
                }
                data[end+1][j]=mover;
            }
        }
    }

    public static void main(String []argv) {
        DoubleMatrix m = new DoubleMatrix();
        DoubleMatrix n = new DoubleMatrix(3, 4);
        testXML();
    }
    /**Tests the reading of a matrix from xml.*/
    public static void testXML() {
        String fname = "/home/dlens/dmatrix.xml";
        String data="<doubleMatrix rows=\"3\" cols=\"4\">1 2 3 4 5 6 7 8 9 10 11 12</doubleMatrix>";
        DoubleMatrix mat = new DoubleMatrix();
        SAXBuilder parser = new SAXBuilder();
        Document doc;
        try {
            doc = parser.build(new java.io.StringReader(data));
            Element elt = doc.getRootElement();
            mat.fromElement(elt);
            System.out.println(mat.toString());
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            FileOutputStream fout = new FileOutputStream(fname);
            doc = new Document();
            doc.setRootElement(mat.toElement("double"));
            outputter.output(doc, fout);
            fout.close();
            mat.addRowAfter(0);
            System.out.println(mat.toString());
            mat.addRowBefore(0);
            System.out.println(mat.toString());
            mat.addColBefore(0);
            System.out.println(mat.toString());
            mat.addColAfter(2);
            System.out.println(mat.toString());
            mat = new DoubleMatrix();
            mat.addRow(); mat.addRow();
            mat.addColBefore(0);
            mat.addColBefore(0);
            mat.addColBefore(0);
            System.out.println(mat.toString());
        } catch (Exception e) {
            System.out.println("Unexpected exception "+e.toString());
            System.exit(1);
        }
    }

    /**This function reorders the columns of the given matrix.
     *@param newPos This is an integer array that has the new positions.
     *It should have the same size as the matrix we are using.
     */
    public void reorderCols(int newPos[])
    {
        double [][]newData=new double[this.rows][this.cols];
        int i,j,ii, jj;
        for(j=0; j<cols; j++) {
            jj=newPos[j];
            for(i=0; i<rows; i++) {
                newData[i][j]=data[i][jj];
            }
        }
        data=newData;
    }
    /**The parameter is a vector of matrices each with the same
     *size.  It returns a matrix of that size that is the
     *average of the given matrices, not including the zeroes in the average.
     *This version scales each matrix by the priority given in priorities.
     *It makes sure the scalings add to one in each spot.
     *@param matrices The matrices to average.
     *@param priorities A double[] of the same size as matrices.
     *@returns The new matrix.  Returns null if all of the matrices
     *don't have the same size, or if one of the vector elements wasn't
     *a matrix.
     */
    public static DoubleMatrix averageMatricesNonZero(Vector matrices, double priorities[])
    {
        double val;
        DoubleMatrix rval;
        double sum;
        if (matrices.size()==0)
            return null;
        if (priorities==null) {
            priorities=new double[matrices.size()];
            for(int i=0; i < priorities.length; i++)
                priorities[i]=1.0/priorities.length;
        }
        DoubleMatrix dms[] = new DoubleMatrix[matrices.size()];
        dms = (DoubleMatrix[])matrices.toArray(dms);
        int i,j,k, nmats=dms.length, rows = dms[0].rows, cols=dms[0].cols;
        /*First check that all have the same dims.*/
        for(i=0;i<nmats;i++) {
            if ((dms[i].rows != rows)||(dms[i].cols!=cols)) {
                return null;
            }
        }
        /*Okay all have same size.*/
        rval = new DoubleMatrix(rows, cols);
        for(i=0;i<rows;i++) {
            for(j=0;j<cols;j++) {
                rval.data[i][j]=0;
                sum=0;
                for(k=0;k<nmats;k++) {
                    val=dms[k].data[i][j];
                    rval.data[i][j]+=val*priorities[k];
                    if (val!=0)
                        sum+=priorities[k];
                }
                if (sum!=0)
                    rval.data[i][j]/=sum;
            }
        }
        return rval;
    }
    
    public static DoubleMatrix averageMatrices(Vector matrices, Vector settings, double priorities[])
    {
		return averageMatrices(matrices, settings, priorities, false);
	}
	
    /**The parameter is a vector of matrices each with the same
     *size.  It returns a matrix of that size that is the
     *average of the given matrices, not including the zeroes in the average.
     *This version scales each matrix by the priority given in priorities.
     *It makes sure the scalings add to one in each spot.
     *@param matrices The matrices to average.
     *@param A vector of IntMatrix.  Each int matrix is the same size as the matrices
     *being averaged.  If settings.get(i).get(j,k)==PiecewiseFx.NO_DATA then
     *matrices.get(i).get(j,k) is not averaged into the (j,k) spot.
     *@param priorities A double[] of the same size as matrices.
     *@returns The new matrix.  Returns null if all of the matrices
     *don't have the same size, or if one of the vector elements wasn't
     *a matrix.
     */
    public static DoubleMatrix averageMatrices(Vector matrices, Vector settings, double priorities[], boolean normalizeCols)
    {
        double val;
        DoubleMatrix rval;
        double sum;
        if (matrices.size()==0)
            return null;
        if (priorities==null) {
            priorities=new double[matrices.size()];
            for(int i=0; i < priorities.length; i++)
                priorities[i]=1.0/priorities.length;
        }
        DoubleMatrix dms[] = new DoubleMatrix[matrices.size()];
        IntMatrix ims[] = new IntMatrix[settings.size()];
        dms = (DoubleMatrix[])matrices.toArray(dms);
        ims = (IntMatrix[])settings.toArray(ims);
        int i,j,k, nmats=dms.length, rows = dms[0].rows, cols=dms[0].cols;
        /*First check that all have the same dims.*/
        for(i=0;i<nmats;i++) {
            if ((dms[i].rows != rows)||(dms[i].cols!=cols)) {
                return null;
            }
        }
        /*Okay all have same size.*/
        rval = new DoubleMatrix(rows, cols);
        for(i=0;i<rows;i++) {
            for(j=0;j<cols;j++) {
                rval.data[i][j]=0;
                sum=0;
                for(k=0;k<nmats;k++) {
                    val=dms[k].data[i][j];
                    if (normalizeCols)
                        val/=dms[k].columnSum(j);
                    rval.data[i][j]+=val*priorities[k];
                    if (ims[k].get(i,j)!=IntMatrix.NO_DATA) {
                        sum+=priorities[k];
                    }
                }
                if (sum!=0)
                    rval.data[i][j]/=sum;
            }
        }
        return rval;
    }
    
    /**Returns a Double[][] of the data in this matrix.*/
    public Double[][] toFlashCommObject() {
        Double rval[][]=new Double[rows][cols];
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                rval[i][j]=new Double(data[i][j]);
        return rval;
    }
	
	/**Normalizes all columns.  Returns itself.*/
	public DoubleMatrix normalizeCols()
	{
		double sum;
		for(int i=0; i<cols; i++) {
			sum=0;
			for(int j=0; j<rows; j++)
				sum+=Math.abs(data[j][i]);
			if (sum > 0) {
				for(int j=0; j<rows; j++)
					data[j][i]/=sum;
			}
		}
		return this;
	}
        
	/**Normalizes all columns.  Returns itself.*/
	public static void normalizeCols(double[][] data)
	{
            int cols=data[0].length;
            int rows=data.length;
		double sum;
		for(int i=0; i<cols; i++) {
			sum=0;
			for(int j=0; j<rows; j++)
				sum+=Math.abs(data[j][i]);
			if (sum > 0) {
				for(int j=0; j<rows; j++)
					data[j][i]/=sum;
			}
		}
	}
        
    /**Normalizes a column.  Returns itself.*/
    public static void normalizeCol(double[][] data, int col) {
        int cols = data[0].length;
        int rows = data.length;
        double sum;
        sum = 0;
        for (int j = 0; j < rows; j++) {
            sum += Math.abs(data[j][col]);
        }
        if (sum > 0) {
            for (int j = 0; j < rows; j++) {
                data[j][col] /= sum;
            }
        }
    }
    /**Normalizes a column.  Returns itself.*/
    public static void idealizeCol(double[][] data, int col) {
        if ((data==null)||(data.length==0)) return;
        int cols = data[0].length;
        int rows = data.length;
        double max;
        max = 0;
        for (int j = 0; j < rows; j++) {
            if (Math.abs(data[j][col]) > max) {
                max=Math.abs(data[j][col]);
            }
        }
        if (max > 0) {
            for (int j = 0; j < rows; j++) {
                data[j][col] /= max;
            }
        }
    }
    
    public static void idealizeCols(double data[][]) {
        if ((data==null)||(data.length==0)) return;
        int cols = data[0].length;
        for(int col=0; col<cols; col++)
            idealizeCol(data, col);
    }
    
    public void idealizeCols() {
        idealizeCols(data);
    }
	/**Returns the column sum, or 1 if the columns summed to 0.*/
	public double columnSum(int col)
	{
		double sum=0;
		for(int j=0; j<rows; j++)
                    sum+=Math.abs(data[j][col]);
		if (sum==0)
                    return 1;
		else
                    return sum;
	}
	
	/**Gets a single value, but after normalizing the column*/
	public double getNormalized(int row, int col)
	{
		return data[row][col]/columnSum(col);
	}
	
	public void sumColsNormalized(double[] priorities, double[] result)
	{
            double[] tmpPriorities = new double[priorities.length];
            for(int i=0; i<cols; i++) {
                tmpPriorities[i]/=columnSum(i);
            }
            sumCols(tmpPriorities, result);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////	
	//////BEGIN the stuff imported from DoubleSquareMatrix from the old dlens code///////
	/////////////////////////////////////////////////////////////////////////////////////

    /**Resets the data in the matrix to be ratios of the given vector.*/
    public void ratioMatrix(double[] dv) {
        int size=dv.length;
        this.rows=size; this.cols=size;
        data = new double[size][size];
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                if (i==j) {
                    data[i][j]=1;
                } else {
                    if (dv[j]!=0.0)
                        data[i][j]=dv[i]/dv[j];
                }
            }
        }
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
    	isSquareThrow();
    	int size=rows;
        double newData[][] = new double[size+1][size+1];
        for(int i=0;i<size;i++) {
            for(int j=0;j<size;j++) {
                newData[i][j]=data[i][j];
            }
        }
        data=null;
        data=newData;
        rows++; cols++;
        return rows - 1;
    }

    /** Removes a place from the matrix.
      * @param place The place to remove.
      * @throws ArrayIndexOutOfBoundsException If <code>place</code> was out of bounds.
     */
    public void rmPlace(int place)
        throws ArrayIndexOutOfBoundsException
    {
        int i,j;
        isSquareThrow();
        int size=rows;
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
        rows--;cols--;
    }
    
    public void isSquareThrow() {
    	if (rows!=cols) throw new UnsupportedOperationException();
    }
    
    /**
     *Returns the size of the matrix.
     *@return The size of the matrix.
     */
    public int getSize() {
    	isSquareThrow();
        return rows;
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
    	isSquareThrow();
        data[row][row]=1.0; data[col][col]=1.0;
        data[row][col]=val;
        if (val != 0)
            data[col][row]=1/val;
        else
            data[col][row]=0;
    }

    /**
     *A simple addition method noncreating.
     *@param a The matrix to add to <code>this</code>
     *@param result The matrix to store the result in.
     *@throws ArrayIndexOutOfBoundsException If the matrices are not all of the same size.
     */
    public void add(DoubleMatrix a, DoubleMatrix result)
        throws ArrayIndexOutOfBoundsException
    {
        if ((rows != a.rows)||(cols != a.cols)) {
            throw new ArrayIndexOutOfBoundsException("Cannot add matrices of different dimensions.");
        }
        if ((rows != result.rows)||(cols != result.cols)) {
            throw new ArrayIndexOutOfBoundsException("Tried to add matrix of the correct size and store in one of the incorrect size.");
        }
        for(int i=0; i<rows;i++) {
            for(int j=0; j< cols;j++) {
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
    public DoubleMatrix add(DoubleMatrix a)
        throws ArrayIndexOutOfBoundsException
    {
        if ((rows != a.rows)||(cols!=a.cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        DoubleMatrix result = new DoubleMatrix(rows,cols);
        for(int i=0; i<rows;i++) {
            for(int j=0;j<cols;j++) {
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
    public void multiply(DoubleMatrix a, DoubleMatrix result)
        throws ArrayIndexOutOfBoundsException
    {
        if (cols != a.rows) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if ((rows != result.rows)||(a.cols!=result.cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for(int i=0; i<rows;i++) {
            for(int j=0;j<a.cols;j++) {
                result.data[i][j]=0;
                for(int k=0;k<cols;k++) {
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
    public DoubleMatrix multiply(DoubleMatrix a)
        throws ArrayIndexOutOfBoundsException
    {
        if (cols != a.rows) {
            throw new ArrayIndexOutOfBoundsException();
        }
        DoubleMatrix result = new DoubleMatrix(rows, a.cols);
        for(int i=0; i<rows;i++) {
            for(int j=0;j<a.cols;j++) {
                result.data[i][j]=0;
                for(int k=0;k<cols;k++) {
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
    	isSquareThrow();
    	int size=rows;
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

    /**This is a convenience function to get the eigen error. It just
     *gets the largest eigenvector with fix, and reads the last value.*/
    public double largestEigenvalue(double error) throws ConvergenceException {
    	isSquareThrow();
    	int size=rows;
        if (size==0) {
            return 0;
        }
        double rval[] = largestEigenvectorWithFix(error);
        return rval[rval.length-1];
    }
    /**This is a convenience function to get the eigen error. It just
     *gets the largest eigenvector without fix, and reads the last value.*/
    public double largestEigenvalueNoFix(double error) throws ConvergenceException {
    	isSquareThrow();
    	int size=rows;
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
            throws ConvergenceException
    {
        double rval[]=largestEigenvector(error);
        double max=0;
        if (rval==null) return rval;
        for(int i=0; i<(rval.length-1); i++) {
            if (Math.abs(rval[i])>max)
                max=rval[i];
        }
        if (max==0) return rval;
        for(int i=0; i<(rval.length-1); i++)
            rval[i]/=max;
        return rval;
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
    public double[] largestEigenvectorIdealNoEigenvalueReturned(double error)
        throws ConvergenceException {
    	isSquareThrow();
    	int size=rows;
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
    	isSquareThrow();
    	int size=rows;
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
    	isSquareThrow();
    	int size=rows;
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
    	isSquareThrow();
    	int size=rows;
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
        DoubleMatrix tmp =new DoubleMatrix(this);
        tmp.harkerFix();
        if (ZeroCompletelyUncompared) tmp.zeroUncompared();
        //System.out.println("Fixed matrix is\n"+tmp.toString());
        return tmp.largestEigenvector(error);
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
    	isSquareThrow();
    	int size=rows;
        if (isZero() || isId()) {
            //System.out.println("Found a trivial matrix, we can handle");
            double rval[] = new double[size];
            for(int i=0; i<size;i++) rval[i]=1.0;
            return rval;
        }
        DoubleMatrix tmp =new DoubleMatrix(this);
        tmp.harkerFix();
        if (ZeroCompletelyUncompared) tmp.zeroUncompared();
        //System.out.println("Fixed matrix is\n"+tmp.toString());
        return tmp.largestEigenvectorIdeal(error);
    }
    /**
     *Turns this matrix into the identity.
     */

    public void id() {
    	isSquareThrow();
    	int size=rows;    	
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
    	if (rows!=cols) return false;
    	int size=rows;
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
        for(int i=0;i<rows;i++) {
            for(int j=0;j<cols;j++) {
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
        for (i=0;i<cols;i++) {
            sum=0;
            for(j=0;j<rows;j++) {
                sum+=Math.abs(data[j][i]);
            }
            if (sum!=0)
                for(j=0;j<rows;j++) {
                    data[j][i]/=sum;
                }
        }
    }

    /**Copies one matrix to this matrix.
     *@param src The matrix copy into this matrix.
     *@throws ArrayIndexOutOfBoundsException If <code>src</code> does not have the same size as this.
     */
    public void copy(DoubleMatrix src) throws ArrayIndexOutOfBoundsException
    {
        if (src==null) {
            throw new ArrayIndexOutOfBoundsException("Tried to copy from a null matrix.");
        }
        if ((src.rows != rows) || (src.cols!=cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int i,j;
        for(i=0;i<rows;i++)
            for(j=0;j<cols;j++)
                data[i][j]=src.data[i][j];
    }

    public DoubleMatrix(int size) {
    	this(size,size);
    }
    
    /**Raises this matrix to a power, and stores the result in the
     *given result matrix.  If power is less than zero, we return the identity matrix.
     *@param pow The power to raise this matrix to.
     *@param result The matrix to store the result in.  It must already have the
     *correct size.
     *@throws ArrayIndexOutOfBoundsException If the matrices sizes don't match.
     */
    public void power(int pow, DoubleMatrix result)
        throws ArrayIndexOutOfBoundsException
    {
    	isSquareThrow(); result.isSquareThrow();
    	int size=rows;    	
        if (result==null) {
            throw new ArrayIndexOutOfBoundsException("The result matrix sent to power was null.");
        }
        if (result.rows != size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        /*Now we are safe.*/
        DoubleMatrix twoPower = new DoubleMatrix(this);
        DoubleMatrix tmp=new DoubleMatrix(size);
        DoubleMatrix tmp2=new DoubleMatrix(size);
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
    public double normalizedDiff(DoubleMatrix other)
        throws ArrayIndexOutOfBoundsException
    {
    	isSquareThrow(); other.isSquareThrow();
    	int size=rows;
        if (other==null) {
            throw new ArrayIndexOutOfBoundsException("normalizedDiff sent a null matrix.");
        }
        if (other.getSize()!=size) {
            throw new ArrayIndexOutOfBoundsException();
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
                diff+=Math.abs(factor1*data[i][j]-factor2*other.data[i][j]);
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
    	isSquareThrow();
    	int size=rows;    	
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
    public DoubleMatrix limitPowerNonHierarchy(double err)
        throws ConvergenceException
    {
        /*Need to get a starting power*/
    	isSquareThrow();
    	int size=rows;
        DoubleMatrix start=new DoubleMatrix(size);
        DoubleMatrix currentPower = new DoubleMatrix(size);
        DoubleMatrix next = new DoubleMatrix(size);
        DoubleMatrix powers[]=new DoubleMatrix[size];
        for(int i=0; i<size; i++)
            powers[i]=new DoubleMatrix(size);
        this.power(startPower(), start);
        currentPower.copy(start);
        /*Now go through a failsafe loop*/
        for(int i=0;i<MaxLimitIterations; i++) {
            for(int k=0;k<size;k++) { /*look for convergence*/
                this.multiply(currentPower, powers[k]);
                //this.multiply(currentPower, next);
                if (powers[k].normalizedDiff(start) < err) {
                    DoubleMatrix powersToAvg[]=new DoubleMatrix[k+1];
                    //Get powers to average
                    for(int ps=0; ps<=k; ps++) {
                        powersToAvg[ps]=powers[ps];
                        powersToAvg[ps].makeColsAddToOne();
                    }
                    DoubleMatrix rval=DoubleMatrix.averageMatrices(powersToAvg);
                    return rval;
                    //next.makeColsAddToOne();
                    //return next;
                }
                currentPower.copy(powers[k]);
            }
            start.copy(currentPower);
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
    public Vector<Vector<Integer>> getLevels()
    {
    	isSquareThrow();
    	int size=rows;
        /*First get a copy to work with.*/
        DoubleMatrix tmp = new DoubleMatrix(this);
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
        while (tmp.getSize() > 0) {
            //System.out.println("Working on matrix:\n"+tmp.toString());
            foundALevel=false;
            /*Find sink columns*/
            level=new Vector();
            tmpLevel=new Vector();
            for(i=0;i<tmp.rows;i++) {
                zeroColumn=true;
                for(j=0;j<tmp.cols;j++) {
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
    public DoubleMatrix limitPowerHierarchy(Vector levels)
        throws NonHierarchyException
    {
    	isSquareThrow();
    	int size=rows;
        int i,j, k, l;
        if (levels==null) {
            levels=getLevels();
        }
        if (levels == null) {
            throw new NonHierarchyException();
        }
        //System.out.println("Limit power hierarchy had "+levels.size()+" levels.");
        DoubleMatrix result = new DoubleMatrix(this);
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
    public DoubleMatrix limitPowerIntelligent(double err)
        throws ConvergenceException
    {
    	isSquareThrow();
    	int size=rows;
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



    public static void test3() {
        try {
            double err=1e-16;
            double comperr=1e-8;
            File file=new File("/trunk/src/com/dlens/anp/engine/smatrix.xml");
            File file1_5=new File("/trunk/src/com/dlens/anp/engine/smatrix_incom.xml");
            File file2=new File("/trunk/src/com/dlens/anp/engine/smatrix2.xml");
            DoubleMatrix dsm = new DoubleMatrix(file);
            System.out.println(dsm.toString()+"Had incon "+dsm.getInconsistencyIndex(err));
            System.out.println("It was completed, full? "+dsm.isComplete(0, 0, 0, err));
            System.out.println("It was completed, spanning? "+dsm.isComplete(1, 0, 0, err));
            System.out.println("It was completed, spanning+1? "+dsm.isComplete(2, 1, 1e-16, err));
            System.out.println("Starting a very incomplete matrix.");
            dsm = new DoubleMatrix(file1_5);
            System.out.println("It was completed, full? "+dsm.isComplete(0, 0, 0, err));
            System.out.println("It was completed, spanning? "+dsm.isComplete(1, 0, 0, err));
            System.out.println("It was completed, spanning+1? "+dsm.isComplete(2, 1, 1e-16, err));
            System.out.println("Starting a spanning+1, but not spanning+2 matrix.");
            dsm = new DoubleMatrix(file2);
            System.out.println("It was completed, full? "+dsm.isComplete(0, 0, 0, err));
            System.out.println("It was completed, spanning? "+dsm.isComplete(1, 0, 0, err));
            System.out.println("It was completed, spanning+1? "+dsm.isComplete(2, 1, comperr, err));
            System.out.println("It was completed, spanning+2? "+dsm.isComplete(2, 2, comperr, err));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    /**Simple constructor from a file.*/
    public DoubleMatrix(File fname)
        throws XMLFormatException, JDOMException, IOException
    {
        SAXBuilder parser = new SAXBuilder();
        Document doc;
        doc = parser.build(fname);
        Element elt = doc.getRootElement();
        fromElement(elt);
    }
    /**A test that used to be a main, but was relegated to a possible test that
     *we can try if necessary.
     */
    public static void testBasic(String []argv) {
        int size=50;
        int ntimes=1;
        DoubleMatrix sm = new DoubleMatrix(size);
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
        DoubleMatrix m=new DoubleMatrix(2);
        DoubleMatrix mm=new DoubleMatrix(2);
        m.id(); m.data[0][1]=1;
        m.power(30, mm);
        System.out.println(mm.toString());
        m= new DoubleMatrix(5);
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
        m=new DoubleMatrix(5);
        m.data[1][0]=.3; m.data[2][0]=.7;
        m.data[3][1]=.6;m.data[4][1]=.4;
        m.data[3][2]=.1;m.data[4][2]=.9;
        DoubleMatrix limit;
        try {
            limit=m.limitPowerHierarchy(null);
            System.out.println("Matrix:\n"+m.toString()+"Has limitPowerHierarchy:\n"+limit.toString());
        } catch (Exception ignored) {
        }
    }

    /**Tests the reading of a matrix from xml.*/
    public static void testXMLSquare() {
        String fname = "/trunk/src/com/dlens/anp/engine/smatrix.xml";
        DoubleMatrix mat = new DoubleMatrix();
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
    	isSquareThrow();
    	int size=rows;        
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
                if (data[j][cols[i]]!=0) {
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
                    rval[i]+=data[i][cols[k]];
                }
            }
            if (nValidCols > 0) {
                rval[i]/=nValidCols;
            }
        }
        return rval;
    }

    public void mvPlaceAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        mvRowAfter(start, end);
        mvColAfter(start, end);
    }

    /**Returns the number of zeroes in the matrix.*/
    public int numbZeroes() {
        int i,j,numb=0;
        for(i=0;i<rows;i++) {
            for(j=0;j<cols;j++) {
                if (data[i][j]==0.0) {
                    numb++;
                }
            }
        }
        return numb;
    }

    /**Tells if a spanning set has been done.*/
    public boolean spanningSetDone() {
    	isSquareThrow();
    	int size=rows;    	
        DoubleMatrix tmp = new DoubleMatrix(size);
        DoubleMatrix power = new DoubleMatrix(size);
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
        for(int i=0; i<this.rows; i++)
            for(int j=0; j<this.cols; j++)
                if (this.data[i][j]!=0)
                    numb++;
        return numb;
    }

    /**Counts the number of non-zeroes in this matrix, above the diagonal.
     */
    public int numbNonzeroAboveDiagonal() {
        int numb=0;
    	isSquareThrow();
    	int size=rows;        
        for(int i=0; i<size; i++)
            for(int j=i+1; j<size; j++)
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
    	isSquareThrow();
    	int size=rows;    	
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
                    	logger.warn("DoubleMatrix.isComplete() - Didn't have enough done.");
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
    	isSquareThrow();
    	int size=rows;
        return (largestEigenvalue(error)-size)/getInconsistencyDivisor();
    }

    /**Returns the divisor for the inconsistency index.*/
    public double getInconsistencyDivisor() {
    	isSquareThrow();
    	int size=rows;
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
    	isSquareThrow();
    	int size=rows;
        //double tmpD,rec, eigen[];
        Vector<InconsistencyPlace> inconList = new Vector<InconsistencyPlace>();
        InconsistencyPlace tmpL[];
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
            logger.warn("DoubleMatrix.isComplete() - On step "+steps+" nextVal="+nextVal);
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
    	isSquareThrow();
    	int size=rows;
        DoubleMatrix tmp = new DoubleMatrix(this);
        tmp.data[r][c]=tmp.data[c][r]=0;
        DoubleMatrix tmpp = new DoubleMatrix(size);
        tmp.power(size, tmpp);
        if (tmpp.data[r][c]!=0) {
            return false;
        } else {
            return true;
        }
        
    }
    
    /**Sets all entries in this matrix to val.*/
    public void set(double val) {
        for(int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
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
    	isSquareThrow();
    	int size=rows;
        for(int i=0; i<size; i++) {
            if (report) {
                if (data[i][i]!=1) {
                    logger.info("setdiagonal to 1 needed to fix diagonal "+i);
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
    /**Computes how compatible the given matrix is with this
     * 
     * @param other
     * @param flag  Which kind of calculation should we do?
     * @return
     */
    public double compatIndex(DoubleMatrix other, int flag) {
    	return compatIndex(other, flag, true);
    }
    
    /**Computes how compatible the given matrix is with this
     * 
     * @param other
     * @param flag  Which kind of calculation should we do?
     * @return
     */
    private double compatIndex(DoubleMatrix other, int flag, boolean makePercent) {
    	isSquareThrow();
    	int size=rows;
    	if (other==null)
    		throw new NullPointerException();
    	if (other.getSize()!=getSize())
    		throw new ArrayIndexOutOfBoundsException();
    	double rval;
    	if ((flag==COMPAT_RATIO_SUM) || (flag==COMPAT_RATIO_PRODUCT)) {
    		DoubleMatrix ratio=ratioOfMatrices(other);
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
    			logger.error(e);
    			return 0;
    		}
    	} else {
    		logger.error("Unknown compatibility flag "+flag+", simply returning 0.");
    		return 0;
    	}
    	//Alright, now we have to turn this bloody thing to percentage, bastages.
    	//First we get the maximally different matrix, and use that to compare
    	DoubleMatrix maximallyDiff=new DoubleMatrix(this);
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
    		rval=toPercent(1, maxCompatIndex, rval);
    	}
    	return rval;
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
    public double compatIndex(DoubleMatrix[] othersRaw, int individualCalcFlag, int groupCalcFlag) {
    	Vector<DoubleMatrix> othersVec=new Vector<DoubleMatrix>();
    	for(int i=0; i<othersRaw.length; i++) {
    		if (!othersRaw[i].isId()) {
    			othersVec.add(othersRaw[i]);
    		}
    	}
    	DoubleMatrix others[]=new DoubleMatrix[othersVec.size()];
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
    
    public DoubleMatrix ratioOfMatrices(DoubleMatrix other) {
    	isSquareThrow(); other.isSquareThrow();
    	int size=rows;
    	if (other==null)
    		throw new NullPointerException();
    	if (other.getSize()!=size)
    		throw new ArrayIndexOutOfBoundsException();
    	DoubleMatrix result=new DoubleMatrix(this);
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
    
    public DoubleMatrix(int size, String data, boolean upperTriangle) {
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
    
    /**
     * Returns a copy of the underlying matrix, not the actual
     * underlying matrix.  So this takes a bit of time.
     * @return
     */
    public double[][] toArray() {
        double rval[][]=new double[this.rows][this.cols];
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                rval[i][j]=data[i][j];
            }
        }
        return rval;
    }
    
    /**
     * Creates a new double matrix that is the identity of the appropriate size.
     * 
     */
    public static DoubleMatrix factoryId(int size) {
        DoubleMatrix rval=new DoubleMatrix(size);
        for(int i=0; i<rval.rows; i++)
            rval.data[i][i]=1.0;
        return rval;
    }
    public static void testCompat() {
    	DoubleMatrix mat1=new DoubleMatrix(4, "2 2 2 2 2 2", true);
    	DoubleMatrix mat2=new DoubleMatrix(4, "4 2 2 2 2 2", true);
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
    	
    	DoubleMatrix others[]=new DoubleMatrix[3];
    	others[0]=new DoubleMatrix(4, "4 2 2 2 2 2", true);
    	others[1]=new DoubleMatrix(4, "2 2 2 2 2 2", true);
    	others[2]=new DoubleMatrix(4, "2 2 2 2 2 2", true);
    	
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


    public boolean isNear(DoubleMatrix other, double err) {
        if (other==null) return false;
        if (other.rows!=rows) return false;
        if (other.cols!=cols) return false;
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                if (!GenericMath.isNear(data[i][j], other.data[i][j], err))
                    return false;
        return true;
    }

    public boolean isNear(DoubleMatrix other) {
        if (other==null) return false;
        if (other.rows!=rows) return false;
        if (other.cols!=cols) return false;
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                if (!GenericMath.isNear(data[i][j], other.data[i][j]))
                    return false;
        return true;
    }

    public double ratioDistance(DoubleMatrix other, MatrixMetricEnum metric) {
        switch (metric) {
            case Max: {
                double max=0;
                double tmp;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        tmp=Math.abs(data[i][j] - other.data[i][j]);
                        if (tmp > max) {
                            max=tmp;
                        }
                    }
                }
                return max;
            }
            case Euclidean: {
                double rval=0;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        rval+=Math.hypot(0,data[i][j]-other.data[i][j]);
                    }
                }
                return rval;

            }
            case MaxRatio:
            default: {
                double max=0;
                double tmp;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        tmp=GenericMath.ratioDistance(data[i][j], other.data[i][j]);
                        if (tmp > max) {
                            max=tmp;
                        }
                    }
                }
                return max;
            }
        }
    }


    public void scalarMultiply(double val) {
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                data[i][j]*=val;
    }

    /**Assuming this matrix is a super matrix for an anp system
     * this finds the columns of any goal nodes.  If no goal nodes
     * are found an array of size zero is returned.  A goal node is something that is
     * not connected from anything, to check for that we just need to
     * see if that row is all zeroes, ignoring self connections.
     * @return
     */
    public int[] getGoalPlaces() {
        isSquareThrow();
        Vector<Integer> rval=new Vector<Integer>();
        for(int i=0; i<rows; i++) {
            boolean foundNonZero=false;
            for(int j=0; j<cols; j++) {
                if ((i!=j) && (data[i][j]!=0)) {
                    foundNonZero=true;
                    break;
                }
            }
            if (!foundNonZero) {
                //Make sure this column is not zero
                boolean isZeroCol=true;
                for(int j=0; j<rows; j++) {
                    if ((i!=j)&&(data[j][i])!=0) {
                        isZeroCol=false;
                        break;
                    }
                }
                if (!isZeroCol) {
                    rval.add(i);
                }
            }
        }

        int rv[]=new int[rval.size()];
        for(int i=0; i<rv.length; i++)
            rv[i]=rval.get(i);
        return rv;
    }

    public DoubleMatrix submatrix(int positions[]) {
        if (positions==null) {
            return new DoubleMatrix(this);
        }
        DoubleMatrix rval=new DoubleMatrix(positions.length);
        for(int i=0; i<positions.length; i++) {
            for(int j=0; j<positions.length; j++) {
                rval.data[i][j]=data[positions[i]][positions[j]];
            }
        }
        return rval;
    }
    
    public double[]getCol(int col, int startRow, int endRow) {
        int rsize=endRow-startRow;
        double []rval=new double[rsize];
        for(int i=startRow; i<endRow; i++) {
            rval[i-startRow]=data[i][col];
        }
        return rval;
    }
    
    public double[][]getCols(double data[][], int startCol, int endCol) {
        int ncols=endCol-startCol;
        int nrows=getRows();
        double[][]rval=new double[nrows][ncols];
        for(int col=startCol; col<endCol; col++) {
            for(int row=0; row<nrows; row++) {
                rval[row][col-startCol]=data[row][col];
            }
        }
        return rval;        
    }
    
    public double[][]getCols(int startCol, int endCol) {
        return getCols(data, startCol, endCol);
    }
    
    public double[] getCol(double[][]vals, int col) {
        int nrows=vals.length;
        double[] rval=new double[nrows];
        for(int i=0; i<nrows; i++)
            rval[i]=vals[i][col];
        return rval;
    }
    
    public DoubleMatrix synthesisHierarchy(CalcSynInfo[][] nodeSyns, CalcSynInfo[] clusterSyns,
            int clusterStarts[], double[][] clusterPriorities, NormalizerEnum pType, double err)
            throws ConvergenceException
    {
        int nClusters=clusterStarts.length;
        Vector<DoubleMatrix>steps=new Vector<DoubleMatrix>();
        steps.add(this);
        DoubleMatrix old=this;
        for(int i=0; i<nClusters; i++) {
            DoubleMatrix next=new DoubleMatrix(this);
            synthesisStep(old, next, nodeSyns, clusterSyns, clusterStarts, clusterPriorities,
                    NormalizerEnum.Raw);
            //If we already have the zero matrix, time to stop
            if (next.isZero()) {
                break;
            }
            steps.add(next);
            old=next;
        }
        //Now we need to sum the matrices.
        DoubleMatrix rval=sum(steps);
        //Now we need to rescale if needed
        rval.rescale(pType);
        return rval;
    }
    
    public void rescale(NormalizerEnum pType) {
        switch (pType) {
            case Raw:
                return;
            case Idealized:
                idealizeCols();
                break;
            case Normalized:
                normalizeCols();
        }
    }
    
    public DoubleMatrix sum(Vector<DoubleMatrix> mats) throws ArrayIndexOutOfBoundsException {
        return sum(mats.toArray(new DoubleMatrix[0]));
    }
    
    public DoubleMatrix sum(DoubleMatrix[] mats) throws ArrayIndexOutOfBoundsException {
        if ((mats.equals(null))||(mats.length==0)) return new DoubleMatrix(0);
        int nRows=mats[0].getRows();
        int nCols=mats[0].getCols();
        DoubleMatrix rval=new DoubleMatrix(nRows,nCols);
        for(int i=0; i<mats.length; i++) {
            rval.add(mats[i],rval);            
        }
        return rval;
    }
    public DoubleMatrix synthesisANP(CalcSynInfo[][] nodeSyns, CalcSynInfo[] clusterSyns,
            int clusterStarts[], double[][] clusterPriorities, NormalizerEnum pType, double err)
            throws ConvergenceException
    {
        DoubleMatrix lastStep=new DoubleMatrix(this);
        DoubleMatrix nextStep=new DoubleMatrix(this.getSize());
        synthesisStep(this, nextStep, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, pType);
        int count=0;
        while (count < MaxLimitIterations) {
            if (nextStep.isNear(lastStep, err)) {
                return nextStep;
            }
            lastStep.copy(nextStep);
            synthesisStep(lastStep, nextStep, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, pType);
        }
        throw new ConvergenceException("ANP Synthesis convergence issue.");
    }

    public DoubleMatrix synthesis(CalcSynInfo[][] nodeSyns, CalcSynInfo[] clusterSyns,
            int clusterStarts[], double[][] clusterPriorities, NormalizerEnum pType, double err)
            throws ConvergenceException
    {
        //This is just a wrapper to see if we are a hierarchy or a full network.
        if (isHierarchy(clusterStarts.length)) {
            return synthesisHierarchy(nodeSyns, clusterSyns, clusterStarts, clusterPriorities, pType, err);
        } else {
            return synthesisANP(nodeSyns, clusterSyns, clusterStarts, clusterPriorities, pType, err);
        }
    }
    
    public boolean isHierarchy(int nClusters) {
        isSquareThrow();
        //Create a 1,0 matrix from this first
        DoubleMatrix bin = new DoubleMatrix(this);
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                if (data[i][j]==0) {
                    bin.data[i][j]=0;
                } else {
                    bin.data[i][j]=1;
                }
            }
        }
        //if it is a hierarchy bin^nClusters=0, so let's do that
        DoubleMatrix pow=new DoubleMatrix(getSize());
        bin.power(nClusters, pow);
        if (pow.isZero()) {
            return true;
        } else {
            return false;
        }
    }
    /**
    public void synthesisStep(DoubleMatrix matToSyn, DoubleMatrix rval, CalcSynInfo[][] nodeSyns, CalcSynInfo[] clusterSyns,
            int clusterStarts[], double[][] clusterPriorities) {
        synthesisStep(matToSyn, rval, nodeSyns, clusterSyns, clusterStarts,
                clusterPriorities, NormalizerEnum.Normalized);
    }
    */
    
    public void synthesisStep(DoubleMatrix matToSyn, DoubleMatrix rval, CalcSynInfo[][] nodeSyns, CalcSynInfo[] clusterSyns,
            int clusterStarts[], double[][] clusterPriorities, NormalizerEnum pType) {
        isSquareThrow();
        int fclusterStarts[]=new int[clusterStarts.length+1];
        System.arraycopy(clusterStarts, 0, fclusterStarts, 0, clusterStarts.length);
        fclusterStarts[clusterStarts.length]=nodeSyns[0].length;
        int nnodes=getSize();
        int nclusters=clusterStarts.length;
        //We need to temporarily store our values.
        double[][] nodeClusterSyn=new double[nclusters][nnodes];
        double[]colSyn = new double[nnodes];
        double scalers[];
        CalcSynInfo mySyn;
        //Now we need to loop over the columns
        for(int colCluster=0; colCluster<nclusters; colCluster++) {
            for (int col = clusterStarts[colCluster]; col < fclusterStarts[colCluster+1]; col++) {
                //Next syn nodes in the cluster wrt this one.
                for (int rowCluster = 0; rowCluster < nclusters; rowCluster++) {
                    mySyn = nodeSyns[rowCluster][col];
                    scalers = getCol(col, fclusterStarts[rowCluster], fclusterStarts[rowCluster + 1]);
                    GenericMath.normalize(scalers);
                    try {
                        double[][] toBeSyn = matToSyn.getCols(fclusterStarts[rowCluster], fclusterStarts[rowCluster + 1]);
                    //Now we need to synthesize
                    mySyn.synthesize(false, toBeSyn, scalers, nodeClusterSyn[rowCluster], pType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //Now we need to synthesize the cluster values.
                scalers = getCol(clusterPriorities, colCluster);
                GenericMath.normalize(scalers);
                clusterSyns[colCluster].synthesize(true, nodeClusterSyn, scalers, colSyn, pType);
                //Now copy to the result
                for(int row=0; row<nnodes; row++) {
                    rval.data[row][col]=colSyn[row];
                }
            }
        }
    }
    
    public static DoubleMatrix limitPowerIntelligentIdeal(DoubleMatrix scaledSupermatrix,
    		DoubleMatrix normalLimitmatrix,
    		DoubleMatrix idealizedMatrix,
    		int clusterSizes[],
    		boolean doStructuralAdjust,
    		double err)
    throws ConvergenceException
    {
    	if ((normalLimitmatrix==null)||(idealizedMatrix==null))
    		throw new IllegalArgumentException();
    	normalLimitmatrix.isSquareThrow();
    	idealizedMatrix.isSquareThrow();
    	if (idealizedMatrix.rows!=normalLimitmatrix.rows) {
    		throw new IllegalArgumentException();
    	}
    	DoubleMatrix rval=new DoubleMatrix(normalLimitmatrix);
        Vector levels = idealizedMatrix.getLevels();
        if (levels == null) {
            //System.out.println("We have a matrix without levels (i.e. non-hierarchy).");
        	//which means we just multiply
            return normalLimitmatrix.multiply(idealizedMatrix);
        } else {
            try {
                return limitPowerHierarchyIdeal(scaledSupermatrix, idealizedMatrix, levels, clusterSizes, doStructuralAdjust);
            } catch (Exception WillNotHappen) {
                /*This won't happen but needed to be there*/
                return scaledSupermatrix.limitPowerNonHierarchy(err);
            }
        }
    }

    /**
     *A nifty new limit power calculation for hierarchies, which is essentially as efficient
     *as doing the AHP calculations directly.  Plus the limit matrix has all of the global
     *priorities for each node.
     *@param idealizedMatrix The matrix of idealized priorities.
     *@param levels If not <code>null</code> the levels of our hierarchy (this should have
     *been computed using {@link getLevels}.  If <code>null</code> then we will compute
     *it here.
     *@return The limit matrix.
     *@throws NonHierarchyException If levels was <code>null</code> and the matrix was not
     *the matrix of a hierarchy.
     */
    public static DoubleMatrix limitPowerHierarchyIdeal(
    		DoubleMatrix scaledSupermatrix, DoubleMatrix idealizedMatrix, 
    		Vector levels, int clusterSizes[], boolean doStructuralAdjust)
        throws NonHierarchyException
    {
    	//A boolean should we do Structural adjust calculations?
    	scaledSupermatrix.isSquareThrow();
    	int size=scaledSupermatrix.rows;
        int i,j, k, l;
        if (levels==null) {
            levels=scaledSupermatrix.getLevels();
        }
        if (levels == null) {
            throw new NonHierarchyException();
        }
        //System.out.println("Limit power hierarchy had "+levels.size()+" levels.");
        DoubleMatrix result = new DoubleMatrix(idealizedMatrix);
        DoubleMatrix saMatrix = new DoubleMatrix(idealizedMatrix);
        //Make scaling matrix a 0,1 matrix
        for(int row=0; row<saMatrix.rows; row++)
        	for(int col=0; col<saMatrix.cols; col++)
        		if (saMatrix.data[row][col]!=0)
        			saMatrix.data[row][col]=1.0;
        for(i=1;i<levels.size();i++) {
            Vector level= (Vector)levels.get(i);
            /*We ignore the first level, cause those are the sinks.*/
            for(j=0;j<level.size();j++) {
                int col=((Integer)level.get(j)).intValue();
                for(k=0;k<size;k++) {
                    if (scaledSupermatrix.data[k][col]!=0) {
                        for(l=0;l<size;l++) {
                            result.data[l][col]+=scaledSupermatrix.data[k][col]*result.data[l][k];
                            saMatrix.data[l][col]+=scaledSupermatrix.data[k][col]*saMatrix.data[l][k];
                        }
                    }
                }
                //col has been handled, but needs to be structurally adjusted
                if (doStructuralAdjust) {
                	int rowOffset=0;
                	//loop over each cluster
                	for(int cluster=0; cluster<clusterSizes.length; cluster++) {
                		//Find max in this cluster in this col
                		double max=0;
                		for(int row=rowOffset; row < (rowOffset+clusterSizes[cluster]); row++) {
                			if (saMatrix.data[row][col]>max)
                				max=saMatrix.data[row][col];                			
                		}
                		//Now we have the max, rescale both the saMatrix, and the results matrix by
                		//this scaling factor.
                		if (max>0) {
                    		for(int row=rowOffset; row < (rowOffset+clusterSizes[cluster]); row++) {
                    			saMatrix.data[row][col]/=max;
                    			result.data[row][col]/=max;
                    		}                			
                		}
                		rowOffset+=clusterSizes[cluster];
                	}
                }
            }
        }
        return result;
    }

    /**
     * Gives access to underlying matrix, please only use for speedy access,
     * do not make structural changes to that matrix.  I can only hope you take
     * this warning to heart.
     * @return
     */
    protected double[][] getDataUnsafe() {
        return this.data;
    }

    public DoubleMatrix submatrix(int startRow, int endRow, int startCol, int endCol) {
        int nrows=endRow-startRow+1;
        int ncols=endCol-startCol+1;
        DoubleMatrix rval=new DoubleMatrix(nrows, ncols);
        for(int row=startRow; row<=endRow; row++) {
            for(int col=startCol; col<=endCol; col++) {
                rval.data[row-startRow][col-startCol]=data[row][col];
            }
        }
        return rval;
    }

    public DoubleMatrix extractColumns(int cols[]) {
        DoubleMatrix rval=new DoubleMatrix(rows, cols.length);
        for(int row=0; row < rows; row++) {
            for(int colP=0; colP < cols.length; colP++) {
                int col=cols[colP];
                rval.data[row][colP]=data[row][col];
            }
        }
        return rval;
    }

    /**
     * A simple helper function, the calls the traditional limit
     * matrix algorithms, and then averages the goal nodes.
     * @param err
     * @return
     */
    public double[] limitPriorities(double err) throws ConvergenceException {
        DoubleMatrix limit=limitPowerIntelligent(err);
        int goalIndexes[] = getGoalPlaces();
        double global[] = limit.averageColumns(goalIndexes);
        return global;
    }

    /**
     * Just as the name suggests, tells if the column is zero.
     * @return
     */
    public boolean isColumnZero(int col) {
        if (col < 0)
            throw new IllegalArgumentException("Column cannot be negative.");
        if (col >= cols)
            throw new IllegalArgumentException("Column "+col+" was larger than the number of columns "+cols);
        for(int row=0; row < rows; row++) {
            if (data[row][col]!=0) {
                return false;
            }
        }
        //Made it here, all values were zero, so it is a zero column
        return true;
    }

    /**Gets the levels of a matrix, as needed by v2.  rval[0] is the
     * nodes in level 0 (the bottom level).  rval[1] is the nodes
     * that only connect to level 0 nodes.  rval[2] is the nodes
     * that connect to at least one leavel 1 node, and the rest of
     * its connections are to level 1 or 0.  Et cetera.
     * @return
     */
    public int[][] getLevelsV2() {
        isSquareThrow();
        int size=getSize();
        Vector<Vector<Integer>>rval=new Vector<Vector<Integer>>();
        Vector<Integer> placesLeft=new Vector<Integer>();
        Vector<Integer> handled=new Vector<Integer>();
        //This is where we store the places still to work on
        for(int i=0; i < size; i++)
            placesLeft.add(i);
        //Let's get level zero first
        Vector<Integer> level=new Vector<Integer>();
        for(int col=0; col < size; col++) {
            if (isColumnZero(col)) {
                level.add(new Integer(col));
                placesLeft.remove(new Integer(col));
                handled.add(new Integer(col));
            }
        }
        if (level.size()==0) {
            //No bottom level, this is clearly a mistake, unless we had nothing
            if (size==0) {
                return new int[][] {{}};
            } else {
                throw new IllegalArgumentException("Matrix had no bottom level.");
            }
        }
        rval.add(level);
        //Now we loop though and handle other levels
        boolean foundSomething=false;
        boolean inLevel;
        for(int l=0; l<size; l++) {
            //Working on level l, nodes are in this level if they
            //are not already handled, and they only connect to things
            //that are already handle
            Vector<Integer> handledTmp=new Vector<Integer>();
            foundSomething=false;
            level=new Vector<Integer>();
            for(int placeP=(placesLeft.size()-1); placeP >=0; placeP--) {
                int place=placesLeft.get(placeP);
                inLevel=true;
                //Check if place is in this level
                for(int row=0; row < size; row++) {
                    if (data[row][place]!=0) {
                        //There was a connection, make sure it was in the previous levels
                        if (!handled.contains(row)) {
                            //Connected so something not already handled, break
                            //out to start another place
                            inLevel=false;
                            break;
                        }
                    }
                }
                if (inLevel) {
                    //Made it here, so place is in this level, add it.
                    level.add(new Integer(place));
                    handledTmp.add(new Integer(place));
                    placesLeft.remove(new Integer(place));
                    foundSomething=true;
                }
            }
            if (!foundSomething) {
                //looked for something in this level, and found nothing, so
                //we are done looking
                break;
            }
            rval.add(level);
            handled.addAll(handledTmp);
        }
        //Okay we are done, check that we have handled every position
        if (placesLeft.size()!=0) {
            //Did not handle everything, something weird happened.
            throw new IllegalArgumentException("Something weird either in my algorithm or in your matrix.");
        }
        //Now turn the results into int[][]
        int[][]rvali=new int[rval.size()][];
        for(int l=0; l<rvali.length; l++) {
            level=rval.get(l);
            rvali[l]=new int[level.size()];
            for(int p=0; p<rvali[l].length; p++) {
                rvali[l][p]=level.get(p);
            }
        }
        return rvali;
    }

    public void setColumn(double[] colData, int col) {
        if (colData.length != getRows())
            throw new IllegalArgumentException();
        for(int row=0; row < colData.length; row++)
            data[row][col]=colData[row];
    }

    public int[][] getLevelsStartAtTop() {
        isSquareThrow();
        Vector<Set<Integer>>rval=new Vector<Set<Integer>>();
        int []goals=getGoalPlaces();
        if ((goals==null)||(goals.length==0))
            throw new IllegalArgumentException("Matrix had no goals, cannot get levels.");
        Set<Integer> level=new HashSet<Integer>();
        Vector<Integer> unhandled=new Vector<Integer>();
        int size=getSize();
        for(int i=0; i<getSize(); i++)
            unhandled.add(i);
        for(int i=0; i<goals.length; i++) {
            level.add(goals[i]);
            unhandled.remove(new Integer(goals[i]));
        }
        //Got the goals level, add to result
        int iteration=0;
        rval.add(level);
        Set<Integer> lastLevel=new HashSet<Integer>(level);
        while ((unhandled.size() > 0) && (iteration < size)) {
            //Search through the last level
            //for things connected from it
            Iterator<Integer>lastLevelIt=lastLevel.iterator();
            level=new HashSet<Integer>();
            while(lastLevelIt.hasNext()) {
                int nodeUpLevel=lastLevelIt.next();
                //Look for nodes connected from, i.e. non-zero rows in that column
                for(int row=0; row < size; row++) {
                    if (unhandled.contains(row)) {
                        if (data[row][nodeUpLevel]!=0) {
                            level.add(row);
                            unhandled.remove(new Integer(row));
                        }
                    }
                }
            }
            lastLevel=level;
            rval.add(level);
            iteration++;
        }
        //Now turn into the rval type I want
        int rvalv[][]=new int[rval.size()][];
        for(int l=0; l < rvalv.length; l++) {
            Set<Integer>aLevel=rval.get(l);
            Integer[]abc=aLevel.toArray(new Integer[aLevel.size()]);
            rvalv[l]=new int[aLevel.size()];
            for(int k=0; k< rvalv[l].length; k++) {
                rvalv[l][k]=abc[k];
            }
        }
        return rvalv;
    }

    public boolean equals(Object other) {
        if (other==null) return false;
        try {
            DoubleMatrix om=(DoubleMatrix)other;
            if ((rows==om.rows)&&(cols==om.cols)) {
                for(int row=0; row < rows; row++) {
                    for(int col=0; col < cols; col++) {
                        if (data[row][col]!=om.data[row][col]) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
