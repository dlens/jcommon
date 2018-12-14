
package com.dlens.common2.numeric;
import org.apache.log4j.Logger;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;

import com.dlens.common2.exceptions.*;
import com.dlens.common2.interfaces.JDOMable;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.ObjectToJDOM;

import java.util.Hashtable;

/**
 * This is a general purpose class to handle basic
 * integer needs.
 * @author  Keith Istler
 */
public class IntMatrixSparse implements IntMatrixInterface {
	//The logger
	private static Logger logger = Logger.getLogger(IntMatrixSparse.class);
    ObjectMatrixInterface matrix = null;

    /** Creates a new instance of StringMatrix */
    public IntMatrixSparse() {
        matrix = new SparseObjectMatrix();
    }

    /**Creates a new instance of StringMatrix of size rows x cols*/
    public IntMatrixSparse(int rows, int cols) {
        matrix = new SparseObjectMatrix(rows,cols);
    }

    /**
     *Constructs a copy of the matrix.
     *@param src The matrix to copy from.
     */
    public IntMatrixSparse(IntMatrixSparse newMat)    {
        this.matrix = new SparseObjectMatrix(newMat.getRows(), newMat.getCols());
        
        int val = 0;
//        SparseObjectMatrixEntry entry = null;
//        if( newMat instanceof SparseObjectMatrix) {
//            List data = ((SparseObjectMatrix)matrix).getData();
//
//            for(int i=0; i<data.size(); i++) {
//                entry = (SparseObjectMatrixEntry)data.get(i);
//                this.matrix.set(entry.getRow(), entry.getCol(), entry.getData() );
//            }
//        } else {
        int tCols = newMat.getCols();
        int tRows = newMat.getRows();
        for(int c=0; c<tCols; c++) {
            for(int r=0; r<tRows; r++) {
                val = newMat.get(r,c);
                this.set(r,c,val);
            }
        }
//        }
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
    public void set(int row, int col, int val) throws ArrayIndexOutOfBoundsException {

        if( (this.get(row,col)!=0) && (val==0) ) {
            matrix.set(row,col, null);
        } else if( val!=0 ) {
            matrix.set(row, col, new Integer(val));
        }
    }

    /**Gets a value in the matrix.*/
    public int get(int row, int col)
    throws ArrayIndexOutOfBoundsException {
        Object val = matrix.get(row,col);
        int ret = 0;
        try {
            if(val!=null) {
                if(val instanceof String) {
                    ret = Integer.parseInt(val.toString());
                    matrix.set(row,col, new Integer(ret));
                } else if (val instanceof Integer) {
                    ret = ((Integer)val).intValue();
                } else {
                    throw new Exception("Unknown class type in IntMatrixSparse");
                }
            }
        } catch(Exception e) {
            logger.error(e);
            if(val!=null) {
                logger.error("*class:: " + val.getClass().getName() );
                logger.error("*value: " + val.toString());
            }
        }
        return ret;
    }
    
     public Hashtable getData() {
        return ((SparseObjectMatrix)this.matrix).getData();
    }

    /**Inserts a row before the given row.*/
    public void addRowBefore(int row)
        throws ArrayIndexOutOfBoundsException {
        matrix.addRowBefore(row);
    }

    /**Inserts a column before the given column.*/
    public void addRowAfter(int row)
        throws ArrayIndexOutOfBoundsException {
        matrix.addRowAfter(row);
    }

    /**Adds a row at the end of the matrix.*/
    public void addRow() {
        matrix.addRow();
    }

    /**Inserts a column before the given column.*/
    public void addColBefore(int col)
        throws ArrayIndexOutOfBoundsException {
        matrix.addColBefore(col);
    }

    /**Inserts a column before the given column.*/
    public void addColAfter(int col)
        throws ArrayIndexOutOfBoundsException {
        matrix.addColAfter(col);
    }

    /**Adds a row at the end of the matrix.*/
    public void addCol() {
        matrix.addCol();
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
    }

    public void rmRow(int place)
        throws ArrayIndexOutOfBoundsException {
        matrix.rmRow(place);
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
//        String isSparse = element.getAttributeValue("isSparse");
//        if(isSparse==null) {
//            //matrix = new ObjectMatrixFull();
//            IntMatrixFull matrixFull = new IntMatrixFull();
//            matrixFull.fromElement(element);
//        }
        matrix = new SparseObjectMatrix();
        matrix.fromElement(element);
    }
    
    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element, JDOMToObject jdomer)
        throws XMLFormatException {
        matrix = new SparseObjectMatrix();
        matrix.fromElement(element, jdomer);
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
        testXML();
    }

    /**Tests the reading of a matrix from xml.*/
    public static void testXML() {
        String fname = "/home/dlens/imatrix.xml";
        String data="<IntMatrixSparse rows=\"3\" cols=\"4\">1 2 3 4 5 6 7 8 9 10 11 12</IntMatrixSparse>";
        IntMatrixSparse mat = new IntMatrixSparse();
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
            mat = new IntMatrixSparse();
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
        if(val!=0) {
            for(int i=0; i<this.matrix.getRows(); i++)
                for(int j=0; j<this.matrix.getCols(); j++)
                    this.set(i,j,val);
        }
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
        for(int i=0; i < this.getRows(); i++) {
            this.set(i,column, 0);
        }
    }
}
