
package com.dlens.common2.numeric;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;

import com.dlens.common2.exceptions.*;
import com.dlens.common2.interfaces.JDOMable;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.ObjectToJDOM;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * This is a general purpose class to handle basic
 * integer needs.
 * @author  dlens
 */
public class IntMatrix {
	//Signifies no data
	public static final int NO_DATA=0;
    IntMatrixInterface matrix = null;
    private boolean alwaysFull = false;
    
    /** Creates a new instance of StringMatrix */
    public IntMatrix() {
        matrix = new IntMatrixSparse();
        this.setAlwaysFull(alwaysFull);
    }

    /**Creates a new instance of StringMatrix of size rows x cols*/
    public IntMatrix(int rows, int cols) {
        matrix = new IntMatrixSparse(rows,cols);
        this.setAlwaysFull(alwaysFull);
    }

    /**
     *Constructs a copy of the matrix.
     *@param src The matrix to copy from.
     */
    public IntMatrix(IntMatrix newMat)    {
        this.setAlwaysFull(alwaysFull);
        if( (newMat.getLoad()>=.3) || alwaysFull) {
            this.matrix = new IntMatrixFull(newMat.getRows(), newMat.getCols());
        } else {
            this.matrix = new IntMatrixSparse(newMat.getRows(), newMat.getCols());
        }
        
        int val = 0;
      /*  SparseObjectMatrixEntry entry = null;
        if( newMat.getTypeOf().equals(SparseObjectMatrix.class) ) {
            Hashtable data = ((SparseObjectMatrix)matrix).getData();
            Iterator iter = data.keySet().iterator();
            
            while(iter.hasNext()) {
                entry = (SparseObjectMatrixEntry)iter.next();
                this.matrix.set(entry.getRow(), entry.getCol(), ((Integer)entry.getData()).intValue() );
            }
        } else {   */
            int tCols = newMat.getCols();
            int tRows = newMat.getRows();
            for(int c=0; c<tCols; c++) {
                for(int r=0; r<tRows; r++) {
                    val = newMat.get(r,c);
                    this.matrix.set(r,c,val);
                }
            }
     //   }
    }

    public Class getTypeOf() {
        return this.matrix.getClass();
    }
    
    public void setAlwaysFull(boolean bAlways) {
        this.alwaysFull = bAlways;
        if( bAlways && (matrix instanceof IntMatrixSparse) ) {
            sparseToFull();
        } else if( !bAlways && (matrix instanceof IntMatrixFull) ) {
            this.balance();
        }
    }
    
    public void sparseToFull() {
        IntMatrixFull newMatrix = new IntMatrixFull(matrix.getRows(), matrix.getCols());
        IntMatrixSparse sparse = ((IntMatrixSparse)this.matrix);
        Hashtable data = sparse.getData();
        Iterator iter = data.keySet().iterator();
        Object dat = null;
        SparseMatrixPosition entry = null;
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);
            try {
                if(dat instanceof String) {
                    newMatrix.set(entry.getRow(), entry.getCol(), (new Integer((String)dat)).intValue());
                } else {
                    newMatrix.set(entry.getRow(), entry.getCol(), ((Integer)dat).intValue());
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        this.matrix = newMatrix;
    }
    
    public void fulltoSparse() {
        IntMatrixSparse newMatrix = new IntMatrixSparse(matrix.getRows(), matrix.getCols());
        int rows = newMatrix.getRows();
        int cols = newMatrix.getCols();
        int val;
        //isNull isnt called because it takes just aslong as this does
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                val = matrix.get(r,c);
                if(val!=0) {
                    newMatrix.set(r,c, val);
                }
            }
        }
        this.matrix = newMatrix;
    }
    
    public void balance() {

        if(!this.alwaysFull) {  //should always be a full matrix
            if( (matrix.getLoad() >= .3) && (matrix instanceof IntMatrixSparse) ) {
                sparseToFull();
            } else if( ( matrix.getLoad() < (0.3)  ) && (matrix instanceof IntMatrixFull) ) {
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
    public void set(int row, int col, int val)
        throws ArrayIndexOutOfBoundsException {
        matrix.set(row, col, val);
        balance();
    }
    
    /**Gets a value in the matrix.*/
    public int get(int row, int col)
    throws ArrayIndexOutOfBoundsException {
        int val = matrix.get(row,col);
        return val;
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
        return matrix.toElement(name, new ObjToJdom());
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

//    /**
//     *Fills in this String matrix with the data from <code>element</code>.
//     *@throws XMLFormatException If the element does not have the right data.
//     */
//    public void fromElementOld(Element element)
//        throws XMLFormatException {
//        matrix.fromElementOld(element);
//    }

    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element)
        throws XMLFormatException {
        String isSparse = element.getAttributeValue("isSparse");
        IntMatrixInterface matrixNew = null;
        if(isSparse==null) {
            matrixNew = new IntMatrixFull();
            matrixNew.fromElement(element);
        } else {
            matrixNew = new IntMatrixSparse();
            matrixNew.fromElement(element);
        }
        //matrix.fromElement(element);
        matrix = matrixNew;
        balance();
    }
    
    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element, JDOMToObject jdomer)
        throws XMLFormatException {
        matrix.fromElement(element, jdomer);
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

    public static void main(String[] argv) {
        
        IntMatrix matrix = new IntMatrix(5,5);
        for(int x = 0; x<5; x++) {
            for(int y = 0; y<4; y++) {
                matrix.set(x,y,y);
            }
        }
        
        for(int x = 0; x<4; x++) {
            for(int y = 0; y<4; y++) {
                matrix.set(x,y,0);
            }
        }
        
        System.out.println("  DONE   ");
        for(int x = 0; x<5; x++) {
            for(int y=0; y<5; y++) {
                System.out.print( matrix.get(x,y) + " ");
            }
            System.out.println();
        }
        
        System.out.println("size: " + matrix.getSize());
        System.out.println("load: " + matrix.getLoad());
        
        //testXML();
    }

    /**Tests the reading of a matrix from xml.*/
    public static void testXML() {
        String fname = "/home/dlens/imatrix.xml";
        String data="<intMatrix rows=\"3\" cols=\"4\">1 2 3 4 5 6 7 8 9 10 11 12</intMatrix>";
        IntMatrix mat = new IntMatrix();
        SAXBuilder parser = new SAXBuilder();
        Document doc;
        try {
            doc = parser.build(new java.io.StringReader(data));
            Element elt = doc.getRootElement();
            //mat.fromElementOld(elt);
            System.out.println(mat.toString());
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(org.jdom.output.Format.getPrettyFormat());
            FileOutputStream fout = new FileOutputStream(fname);
            doc = new Document();
            doc.setRootElement(mat.toElement("Int"));
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
            mat = new IntMatrix();
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
    public void reorderCols(int newPos[]) {
        matrix.reorderCols(newPos);
    }

    /**Returns a Object[][] of the data in this matrix.*/
    public Object[][] toFlashCommObject() {
        return matrix.toFlashCommObject();    //should be a Integer[][]
    }

    public int getSize() {
        return matrix.getSize();
    }
    
    public double getLoad() {
        return matrix.getLoad();
    }
    
    public void movePlaceAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        mvRowAfter(start, end);
        mvColAfter(start, end);
    }
    
    public void set(int val) {
        for(int i=0; i<this.matrix.getRows(); i++)
            for(int j=0; j<this.matrix.getCols(); j++)
                this.set(i,j,val);
    }
    
    private class ObjToJdom extends ObjectToJDOM {
    //expects a string as the object  - this is expected to be passed in
        public Element objectToJDOM(Object object, String name) {
            Element el = new Element(name);
            el.addContent(  ((Integer)object).toString()  );
            return el;
        }
    }
    public void clearColumn(int column)
    {
        matrix.clearColumn(column);
    }
}
