
package com.dlens.common2.numeric;
import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.ObjectToJDOM;

import org.jdom.Element;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *
 * @author Keith Istler
 */
public class ObjectMatrix implements ObjectMatrixInterface {
    
    ObjectMatrixInterface matrix = null;
    
    private boolean alwaysFull = false;
    
    /** Creates a new instance of ObjectMatrix */
    public ObjectMatrix(int rows, int cols) {
        matrix = new SparseObjectMatrix(rows, cols);
        this.setAlwaysFull(alwaysFull);
    }
    
    public ObjectMatrix() {
        matrix = new SparseObjectMatrix();
        this.setAlwaysFull(alwaysFull);
    }
    
    public ObjectMatrix(Object [][]data) {
        matrix=new ObjectMatrixFull(data);
    }
    
    public ObjectMatrix( ObjectMatrixInterface newMat ) {
        this.setAlwaysFull(alwaysFull);
        if( (newMat.getLoad()>=.3) || alwaysFull) {
            this.matrix = new ObjectMatrixFull(newMat.getRows(), newMat.getCols());
        } else {
            this.matrix = new SparseObjectMatrix(newMat.getRows(), newMat.getCols());
        }
        
        SparseMatrixPosition entry = null;
        Object val = null;
        if( newMat instanceof SparseObjectMatrix) {
            Hashtable data = ((SparseObjectMatrix)matrix).getData();
            ///..right?
            Iterator iter = data.keySet().iterator();
            while(iter.hasNext()) {
                entry = (SparseMatrixPosition)data.get(iter.next());
                this.matrix.set(entry.getRow(), entry.getCol(), data.get(entry) );  //copy data
            }
            //
        } else {
            int tCols = newMat.getCols();
            int tRows = newMat.getRows();
            for(int c=0; c<tCols; c++) {
                for(int r=0; r<tRows; r++) {
                    val = newMat.get(r,c);
                    this.matrix.set(r,c,val);
                }
            }
        }
    }
    
    public void setAlwaysFull(boolean bAlways) {
        this.alwaysFull = bAlways;
        if( bAlways && (matrix instanceof SparseObjectMatrix) ) {
            sparseToFull();
        } else if( !bAlways && (matrix instanceof ObjectMatrixFull) ) {
            balance();
        }
    }
    
    public void makeEmptyFullMatrix() {
        this.matrix = new ObjectMatrixFull(this.matrix.getRows(), this.matrix.getCols());
    }
    
    public static void main(String[] args) {
        ObjectMatrixInterface matrix = new ObjectMatrix(3, 4);
        
        matrix.set(0,0, "0,0");
        matrix.set(0,1, "0,1");
        matrix.set(0,2, "0,2");
        matrix.set(0,3, "0,3");
        
        System.out.println("size: " + matrix.getSize());
        
        matrix.set(0,3, null);
        System.out.println("size: " + matrix.getSize());

        System.out.println("size: " + matrix.getSize());
        
        
        //matrix.mvRowAfter(0, 2);
        //matrix.set(3,2, null);
//        int[] order = new int[5];
//        order[0]=0; order[1]=4; order[2]=3; order[3]=2; order[4]=1;        
//        matrix.reorderCols(order);
        
        System.out.println( matrix.toString() );

       System.out.println(matrix.toXMLString());
       
       Element fr = matrix.toElement(matrix.getClass().getName());

       matrix = new SparseObjectMatrix();
//       try {
//           //matrix.fromElementOld(fr);
//       } catch(XMLFormatException ex) {
//           ex.printStackTrace();
//       }
       
       matrix.rmCol(1);
       matrix.rmCol(0);
       
       System.out.println("matr: " + matrix.toString() );
       System.out.println("rows: " + matrix.getRows()  );
       System.out.println("cols: " + matrix.getCols()  );
    }
    
    public void sparseToFull() {
        ObjectMatrixInterface newMatrix = new ObjectMatrixFull(matrix.getRows(), matrix.getCols());
        Hashtable data = ((SparseObjectMatrix)this.matrix).getData();
        Iterator iter = data.keySet().iterator();
        Object dat = null;
        SparseMatrixPosition entry = null;
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);
            newMatrix.set(entry.getRow(), entry.getCol(), dat);
        }
        this.matrix = newMatrix;
    }
    
    public void fulltoSparse() {
        ObjectMatrixInterface newMatrix = new SparseObjectMatrix(matrix.getRows(), matrix.getCols());
        int rows = newMatrix.getRows();
        int cols = newMatrix.getCols();
        Object val = null;
        //isNull isnt called because it takes just aslong as this does
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                val = matrix.get(r,c);
                if(val!=null) {
                    newMatrix.set(r,c,val);
                }
            }
        }
        this.matrix = newMatrix;
    }
    
    public void balance() {

        if(!this.alwaysFull) {  //should always be a full matrix
            if( (matrix.getLoad() >= .3) && (matrix instanceof SparseObjectMatrix) ) {
                sparseToFull();
            } else if( ( matrix.getLoad() < (0.3)  ) && (matrix instanceof ObjectMatrixFull) ) {
                fulltoSparse();
            }
        }
    }
    
    /**
     * Returns number of columns in instance
     * @return
     */
    public int getCols() {
        return matrix.getCols();
    }

    /**
     * Returns number of rows in instance
     * @return
     */
    public int getRows() {
        return matrix.getRows();
    }

    /**Sets a place in the matrix.*/
    public void set(int row, int col, Object val)
        throws ArrayIndexOutOfBoundsException {
        matrix.set(row, col, val);
        balance();
    }

    /**Gets a value in the matrix.*/
    public Object get(int row, int col)
        throws ArrayIndexOutOfBoundsException {
        return matrix.get(row,col);
    }

    /**Inserts a row before the given row.*/
    public void addRowBefore(int row)
        throws ArrayIndexOutOfBoundsException {
        matrix.addRowBefore(row);
        balance();
    }

    /**Inserts a column before the given column.*/
    public void addRowAfter(int row)
        throws ArrayIndexOutOfBoundsException {
        matrix.addRowAfter(row);
        balance();
    }

    /**Adds a row at the end of the matrix.*/
    public void addRow() {
        matrix.addRow();
        balance();
    }

    /**Inserts a column before the given column.*/
    public void addColBefore(int col)
        throws ArrayIndexOutOfBoundsException {
        matrix.addColBefore(col);
        balance();
    }

    /**Inserts a column before the given column.*/
    public void addColAfter(int col)
        throws ArrayIndexOutOfBoundsException {
        matrix.addColAfter(col);
        balance();
    }

    /**Adds a row at the end of the matrix.*/
    public void addCol() {
        matrix.addCol();
        balance();
    }
    
    /**This function converts the int matrix to a simple
     *string, useful for printing debug info.
     */
    public String toString() {
        return matrix.toString();
    }

    /**
     *Constructs a <code>jdom</code> element representing our Object matrix.
     *<b>Warning</b> this method should only be called if all of your objects
     *implement the JDOMable interface.
     *Useful for storing and reloading a matrix (see {@link fromElement} for reloading).
     *@param name The name to give the element we are returning.
     *@return The <code>jdom</code> element representing this matrix.
     */
    public Element toElement(String name) {
        return matrix.toElement(name);
    }

    /**
     *Constructs a <code>jdom</code> element representing our Object matrix.
     *<b>Warning</b> this method should only be called if all of your objects
     *implement the JDOMable interface.
     *Useful for storing and reloading a matrix (see {@link fromElement} for reloading).
     *@param name The name to give the element we are returning.
     *@return The <code>jdom</code> element representing this matrix.
     */
    public Element toElement(String name, ObjectToJDOM objectToJDOM) {
        return matrix.toElement(name, objectToJDOM);
    }

    /**If every non-null object has the same class, this method returns
     *that class name.  If there was no common class, then null is returned.
     *If there was no non-null, then null is returned.
     */
    public String getCommonClassName() {
        return matrix.getCommonClassName();
    }
    
//    /**
//     *Fills in this String matrix with the data from <code>element</code>.
//     *@throws XMLFormatException If the element does not have the right data.
//     */
//    public void fromElementOld(Element element)
//        throws XMLFormatException {
//        String isSparse = element.getAttributeValue("isSparse");
//        if(isSparse==null) {
//            matrix = new ObjectMatrixFull();
//        }
//        matrix.fromElementOld(element);
//    }

    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element)
        throws XMLFormatException {
        matrix.fromElement(element);
    }
    
    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element, JDOMToObject jdomer)
        throws XMLFormatException {
        String isSparse = element.getAttributeValue("isSparse");
        if(isSparse==null) {
            matrix = new ObjectMatrixFull();
        }
        matrix.fromElement(element, jdomer);
    }

    public void rmCol(int place)
        throws ArrayIndexOutOfBoundsException {
        matrix.rmCol(place);
        balance();
    }

    public void rmRow(int place)
        throws ArrayIndexOutOfBoundsException {
        matrix.rmRow(place);
        balance();
    }

    /**Moves a column after a given column (end).  If end=-1, that
     *means to move the given column to the zeroth position.
     */
    public void mvColAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException {
        matrix.mvColAfter(start, end);
    }

    /**Moves a row after a given row (end).  If end=-1, that
     *means to move the given row to the zeroth position.
     */
    public void mvRowAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException {
        matrix.mvRowAfter(start, end);
    }

    /**This function reorders the columns of the given matrix.
     *@param newPos This is an integer array that has the new positions.
     *It should have the same size as the matrix we are using.
     */
    public void reorderCols(int newPos[]) {
        matrix.reorderCols(newPos);
    }
    
    /**Tells if all the data in the matrix is nulls.*/
    public boolean isNull() {
        return matrix.isNull();
    }
    
    public String toXMLString() {
        return matrix.toXMLString();
    }

    /**Returns a Object[][] of the data in this matrix.*/
    public Object[][] toFlashCommObject() {
        return matrix.toFlashCommObject();
    }
    
    public int getSize() {
        return matrix.getSize();
    }
    
    public double getLoad() {
        return matrix.getLoad();
    }
    
    public void clearColumn(int column)
    {
        matrix.clearColumn(column);
    }
}
