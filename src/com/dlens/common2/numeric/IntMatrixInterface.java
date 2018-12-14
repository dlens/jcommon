
package com.dlens.common2.numeric;
import org.jdom.*;

import com.dlens.common2.exceptions.*;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.ObjectToJDOM;

/**
 * This is a general purpose class to handle basic
 * integer needs.
 * @author  dlens
 */
public interface IntMatrixInterface {

   /**
     * Returns number of columns in instance
     * @return
     */
    public int getCols();
    /**
     * Returns number of rows in instance
     * @return
     */
    public int getRows();

    /**Sets a place in the matrix.*/
    public void set(int row, int col, int val)
        throws ArrayIndexOutOfBoundsException;

    /**Gets a value in the matrix.*/
    public int get(int row, int col)
    throws ArrayIndexOutOfBoundsException;

    /**Inserts a row before the given row.*/
    public void addRowBefore(int row)
        throws ArrayIndexOutOfBoundsException;

    /**Inserts a column before the given column.*/
    public void addRowAfter(int row)
        throws ArrayIndexOutOfBoundsException;

    /**Adds a row at the end of the matrix.*/
    public void addRow();

    /**Inserts a column before the given column.*/
    public void addColBefore(int col)
        throws ArrayIndexOutOfBoundsException;

    /**Inserts a column before the given column.*/
    public void addColAfter(int col)
        throws ArrayIndexOutOfBoundsException;

    /**Adds a row at the end of the matrix.*/
    public void addCol();
    
    /**This function converts the int matrix to a simple
     *string, useful for printing debug info.
     */
    public String toString();

    /**
     *Constructs a <code>jdom</code> element representing our Object matrix.
     *<b>Warning</b> this method should only be called if all of your objects
     *implement the JDOMable interface.
     *Useful for storing and reloading a matrix (see {@link fromElement} for reloading).
     *@param name The name to give the element we are returning.
     *@return The <code>jdom</code> element representing this matrix.
     */
    public Element toElement(String name);
    /**
     *Constructs a <code>jdom</code> element representing our Object matrix.
     *<b>Warning</b> this method should only be called if all of your objects
     *implement the JDOMable interface.
     *Useful for storing and reloading a matrix (see {@link fromElement} for reloading).
     *@param name The name to give the element we are returning.
     *@return The <code>jdom</code> element representing this matrix.
     */
    public Element toElement(String name, ObjectToJDOM objectToJDOM);

    /**If every non-null object has the same class, this method returns
     *that class name.  If there was no common class, then null is returned.
     *If there was no non-null, then null is returned.
     */
    public String getCommonClassName();
    
    public void rmCol(int place)
        throws ArrayIndexOutOfBoundsException;

    public void rmRow(int place)
        throws ArrayIndexOutOfBoundsException;

//    /**
//     *Fills in this String matrix with the data from <code>element</code>.
//     *@throws XMLFormatException If the element does not have the right data.
//     */
//    public void fromElementOld(Element element)
//        throws XMLFormatException;

    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element)
        throws XMLFormatException;
    
    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element, JDOMToObject jdomer)
        throws XMLFormatException;
    
    /**Moves a column after a given column (end).  If end=-1, that
     *means to move the given column to the zeroth position.
     */
    public void mvColAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException;

    /**Moves a row after a given row (end).  If end=-1, that
     *means to move the given row to the zeroth position.
     */
    public void mvRowAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException;

    /**This function reorders the columns of the given matrix.
     *@param newPos This is an integer array that has the new positions.
     *It should have the same size as the matrix we are using.
     */
    public void reorderCols(int newPos[]);

    /**Returns a Object[][] of the data in this matrix.*/
    public Object[][] toFlashCommObject();

    public int getSize();
    
    public double getLoad();
    
    public void movePlaceAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException;
    
    public void set(int val);

    public void clearColumn(int column);
}
