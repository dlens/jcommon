
package com.dlens.common2.numeric;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;

import com.dlens.common2.exceptions.*;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.ObjectToJDOM;

/**
 * This is a general purpose class to handle basic
 * integer needs.
 * @author  dlens
 */
public class IntMatrixFull implements IntMatrixInterface {
    /**Number of rows in the matrix.*/
    private int rows=0;
    /**Number of columns in the matrix.*/
    private int cols=0;
    /**The data of the matrix.*/
    private int[][] data=null;

    int size = 0;
    
    /** Creates a new instance of IntMatrixFull */
    public IntMatrixFull() {
    }
    /**Creates a new instance of IntMatrixFull of size rows x cols*/
    public IntMatrixFull(int rows, int cols) {
        this.rows=rows; this.cols=cols;
        data=new int[rows][cols];
    }

    /**
     *Constructs a copy of the matrix.
     *@param src The matrix to copy from.
     */
    public IntMatrixFull(IntMatrixFull src)
        throws ArrayIndexOutOfBoundsException
    {
        rows=src.rows; cols=src.cols;
        data = new int[rows][cols];
        int tVal = 0;
        for(int i=0;i<rows;i++) {
            for(int j=0;j<cols;j++) {
                tVal = src.data[i][j];
                if(tVal!=0) {
                    this.size++;
                }
                data[i][j]=src.data[i][j];
            }
        }
    }
    
    public int getSize() {
        return this.size;
    }
    
    public double getLoad() {
        double load = 0.0;
        if( (this.getRows()!=0) && (this.getCols()!=0) ) {
            double size = getSize();
            double avail = this.getRows()*this.getCols();
            load = this.getSize()/avail;
        }
        return load;
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
    
    /**Tells if all the data in the matrix is nulls.*/
    public boolean isNull() {
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                if (data[i][j]!=0)
                    return false;
        return true;
    }
    
    /**Sets a place in the matrix.*/
    public void set(int row, int col, int val)
        throws ArrayIndexOutOfBoundsException {
        if( (this.get(row,col)==0) && (val!=0) ) {
            size++;
        } else if( (this.get(row,col)!=0) && (val==0) ) {
            size--;
        }
        
        data[row][col]=val;
    }
    
    /**Gets a value in the matrix.*/
    public int get(int row, int col)
        throws ArrayIndexOutOfBoundsException
    {
        if (data==null)
            throw new ArrayIndexOutOfBoundsException("Null matrix, no data to get.");
        return data[row][col];
    }

    /**Inserts a row before the given row.*/
    public void addRowBefore(int row)
        throws ArrayIndexOutOfBoundsException
    {
        if ((rows==0)&&(row==0)) {
            /*The empty matrix case.*/
            rows++;
            data=new int[rows][cols];
            return;
        }
        if ((row < 0) || (row >=this.rows)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int[][] newData=new int[rows+1][cols];
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
            data=new int[rows][cols];
            return;
        }
        if ((row < 0) || (row >=this.rows)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int[][] newData=new int[rows+1][cols];
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
            data=new int[rows][cols];
            return;
        }
        if ((col < 0) || (col >=this.cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int[][] newData=new int[rows][cols+1];
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
            data=new int[rows][cols];
            return;
        }
        if ((col < 0) || (col >=this.cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int[][] newData=new int[rows][cols+1];
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
    
    public String toXMLString() {
        try {
            java.io.StringWriter swriter = new java.io.StringWriter();
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(org.jdom.output.Format.getPrettyFormat());
            Document doc = new Document(toElement(getClass().getName()));
            outputter.output(doc, swriter);
            return swriter.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
    
    /**This function converts the int matrix to a simple
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
        //return rval;
    }

    public String getCommonClassName() {
        return "Int";
    }
    
    /**
     *Constructs a <code>jdom</code> element representing our int matrix.
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

    /**
     *Constructs a <code>jdom</code> element representing our Object matrix.
     *<b>Warning</b> this method should only be called if all of your objects
     *implement the JDOMable interface.
     *Useful for storing and reloading a matrix (see {@link fromElement} for reloading).
     *@param name The name to give the element we are returning.
     *@return The <code>jdom</code> element representing this matrix.
     */
    public Element toElement(String name, ObjectToJDOM objectToJDOM) {
        return toElement(name);
    }
    
    /**
     *Fills in this int matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element)
        throws XMLFormatException
    {
        if (element == null)
            throw new XMLFormatException("Null XML element sent to IntMatrixFull.");
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
        data=new int[rowsInt][colsInt];
        this.rows=rowsInt; this.cols=colsInt;
        int tVal = 0;
        for(i=0;i<rowsInt;i++) {
            for(j=0;j<colsInt;j++) {
                try {
                    tVal = Integer.parseInt(numbers[count]);
                    if(tVal!=0) {
                        this.size++;
                    }
                    data[i][j]= tVal;
                } catch (Exception e) {}
                count++;
            }
        }
    }
    
    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element, JDOMToObject jdomer)
        throws XMLFormatException {
        //tough cookies - this shouldnt happen
    }

    public void rmCol(int place)
        throws ArrayIndexOutOfBoundsException
    {
        if ((place < 0) || (place >= this.cols))
            throw new ArrayIndexOutOfBoundsException();
        int [][]newData=new int[rows][cols-1];
        int j =0;
        for(int i=0; i<rows; i++) {
            for (j=0; j<place; j++) {
                newData[i][j]=data[i][j];
            }
            for (j=place+1; j<cols;j++) {
                newData[i][j-1]=data[i][j];
            }
            if(data[i][place]!=0) {
                this.size--;
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
        int [][]newData=new int[rows-1][cols];
        int i = 0;
        for(int j=0; j<cols; j++) {
            for(i=0; i<place; i++) {
                newData[i][j]=data[i][j];
            }
            for(i=place+1; i<rows; i++) {
                newData[i-1][j]=data[i][j];
            }
            if(data[place][j]!=0) {
                this.size--;
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
        int last, mover;
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
        int last, mover;
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

    public static void main(String[] argv) {
        testXML();
    }

    /**Tests the reading of a matrix from xml.*/
    public static void testXML() {
        String fname = "/home/dlens/imatrix.xml";
        String data="<IntMatrixFull rows=\"3\" cols=\"4\">1 2 3 4 5 6 7 8 9 10 11 12</IntMatrixFull>";
        IntMatrixFull mat = new IntMatrixFull();
        SAXBuilder parser = new SAXBuilder();
        Document doc;
        try {
            doc = parser.build(new java.io.StringReader(data));
            Element elt = doc.getRootElement();
            mat.fromElement(elt);
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
            mat = new IntMatrixFull();
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
        int [][]newData=new int[this.rows][this.cols];
        int i,j,ii, jj;
        for(j=0; j<cols; j++) {
            jj=newPos[j];
            for(i=0; i<rows; i++) {
                newData[i][j]=data[i][jj];
            }
        }
        data=newData;
    }

    /**Returns a Integer[][] of the data in this matrix.*/
    public Object[][] toFlashCommObject() {
        Integer rval[][]=new Integer[rows][cols];
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                rval[i][j]=new Integer(data[i][j]);
        return rval;
    }

    public void movePlaceAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        mvRowAfter(start, end);
        mvColAfter(start, end);
    }
    
    public void set(int val) {
        int tVal = 0;
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                tVal = data[i][j];
                if(tVal!=0) {
                    this.size++;
                }
                data[i][j]=val;
            }
        }
    }
    
    public void clearColumn(int column)
    {
        for(int i=0; i < this.getRows(); i++) {
            this.set(i,column, 0);
        }
    }
}
