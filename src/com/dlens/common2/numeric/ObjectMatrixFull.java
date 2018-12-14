
package com.dlens.common2.numeric;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;

import com.dlens.common2.exceptions.*;
import com.dlens.common2.interfaces.*;

import java.lang.reflect.*;

/**
 *
 * @author  William Adams
 */
public class ObjectMatrixFull implements ObjectMatrixInterface, java.io.Serializable {
    //The number of rows.
    private int rows=0;
    //The number of columns
    private int cols=0;
    //The data in the matrix
    private Object [][]data;
    
    private int count = 0;  //number of objects in the matrix
    
    /**The Constructor to use when creating one of these beasts from a JDOM
     *element.
     */
    private Constructor elementConstructor;
    /**
     * Creates a new instance of ObjectMatrixFull 
     */
    public ObjectMatrixFull() {
    }
    public ObjectMatrixFull(int rows, int cols) {
        this.rows=rows; this.cols=cols;
        data=new Object[rows][cols];
    }

    public ObjectMatrixFull(Object[][] data) {
        this.rows=data.length;
        if (this.rows>0)
            this.cols=data[0].length;
        this.data=data;
        resetCount();
    }
    /**
     *Constructs a copy of the matrix.
     *@param src The matrix to copy from.
     */
    public ObjectMatrixFull(ObjectMatrixFull src)
        throws ArrayIndexOutOfBoundsException
    {
        rows=src.rows; cols=src.cols;
        data = new Object[rows][cols];
        for(int i=0;i<rows;i++)
            for(int j=0;j<cols;j++)
                data[i][j]=src.data[i][j];
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

    /**Sets a place in the matrix.*/
    public void set(int row, int col, Object val)
        throws ArrayIndexOutOfBoundsException
    {
        if( (val==null) && (data[row][col]!=null) ) {   //if current position is already null - we arent losing anything
            count--;    //we removed one
        } else if( (val!=null) && (data[row][col]==null) ) {   //if we are replacing - we dont lose anything
            count++;
        }
        data[row][col]=val;
    }

    /**Gets a value in the matrix.*/
    public Object get(int row, int col)
        throws ArrayIndexOutOfBoundsException
    {
        if (data==null)
            throw new ArrayIndexOutOfBoundsException("Null matrix, no data to get.");
        Object o = null;
        try {
            o =data[row][col]; 
        } catch( Exception e) {
            System.out.println("*matrix has: " + this.rows + ", " + this.cols + " and wants @ "+ row + ", " + col);
            e.printStackTrace();
        }
        return o;
    }

    /**Inserts a row before the given row.*/
    public void addRowBefore(int row)
        throws ArrayIndexOutOfBoundsException
    {
        if ((rows==0)&&(row==0)) {
            /*The empty matrix case.*/
            rows++;
            data=new Object[rows][cols];
            return;
        }
        if ((row < 0) || (row >=this.rows)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Object[][] newData=new Object[rows+1][cols];
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
            data=new Object[rows][cols];
            return;
        }
        if ((row < 0) || (row >=this.rows)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Object[][] newData=new Object[rows+1][cols];
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
            data=new Object[rows][cols];
            return;
        }
        if ((col < 0) || (col >=this.cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Object[][] newData=new Object[rows][cols+1];
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
            data=new Object[rows][cols];
            return;
        }
        if ((col < 0) || (col >=this.cols)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Object[][] newData=new Object[rows][cols+1];
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
    }

    /**
     *Constructs a <code>jdom</code> element representing our Object matrix.
     *<b>Warning</b> this method should only be called if all of your objects
     *implement the JDOMable interface.
     *Useful for storing and reloading a matrix (see {@link fromElement} for reloading).
     *@param name The name to give the element we are returning.
     *@return The <code>jdom</code> element representing this matrix.
     */

    public Element toElement(String name)
    {
        Element rval=new Element(name);
        Element td;
        rval.setAttribute("rows", ""+rows);
        rval.setAttribute("cols", ""+cols);
        String commonClassName = getCommonClassName();
        boolean hasCommonClass = false;
        if (commonClassName != null) {
            rval.setAttribute("_CLASS_", commonClassName);
            hasCommonClass=true;
        }
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                if (data[i][j]!=null) {
                    td=((JDOMable)data[i][j]).toElement("td");
                } else {
                    td=new Element("td");
                }
                rval.addContent(td);
            }
        }
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

    public Element toElement(String name, ObjectToJDOM objectToJDOM)
    {
        Element rval=new Element(name);
        Element td;
        rval.setAttribute("rows", ""+rows);
        rval.setAttribute("cols", ""+cols);
        String commonClassName = getCommonClassName();
        boolean hasCommonClass = false;
        if (commonClassName != null) {
            rval.setAttribute("_CLASS_", commonClassName);
            hasCommonClass=true;
        }
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                if (data[i][j]!=null) {
                    //td=((JDOMable)data[i][j]).toElement("td");
                    td=objectToJDOM.objectToJDOM(data[i][j], "td");
                } else {
                    td=new Element("td");
                }
                rval.addContent(td);
            }
        }
        return rval;
    }

    /**If every non-null object has the same class, this method returns
     *that class name.  If there was no common class, then null is returned.
     *If there was no non-null, then null is returned.
     */
    public String getCommonClassName()
    {
        String name=null;
        for(int i=0; i < rows; i++) {
            for(int j=0; j < cols; j++) {
                if (data[i][j]!=null) {
                    if (name==null) {
                        /*this is the first non-null*/
                        name=data[i][j].getClass().getName();
                    } else if (!name.equals(data[i][j].getClass().getName())) {
                        //whoops, one name was not the same.
                        return null;
                    }
                }
            }
        }
        return name;
    }
    
    /**Tells if an element is empty.*/
    private static boolean isEmpty(Element elt) {
        if (!elt.getAttributes().isEmpty()) {
            return false;
        }
        if (!elt.getContent().isEmpty()) {
            return false;
        }
        return true;
    }
    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElementOld(Element element)
        throws XMLFormatException
    {
        if (element == null)
            throw new XMLFormatException("Null XML element sent to StringMatrix.");
        String rows=element.getAttributeValue("rows");
        String cols=element.getAttributeValue("cols");
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
        String commonClassName=element.getAttributeValue("_CLASS_");
        Class commonClass=null;
        if ((commonClassName==null)||(commonClassName.equals(""))) {
            //nothing to do
        } else {
            try {
                commonClass = Class.forName(commonClassName);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                throw new XMLFormatException("Could not find class "+commonClassName);
            }
        }
        java.util.List datas=element.getChildren("td");
        int ndatas = datas.size();
        //System.out.println("Found matrix with size="+sizeInt);
        if (ndatas < rowsInt*colsInt) {
            throw new XMLFormatException("Only gave "+ndatas+
            " data pieces, however we needed "+rowsInt*colsInt+
            " pieces of data for a "+rowsInt+" x " + colsInt +
            " matrix.");
        }
        data=new Object[rowsInt][colsInt];
        this.rows=rowsInt; this.cols=colsInt;
        if ((this.rows!=0) ||(this.cols!=0)) {
            if (elementConstructor==null) {
                throw new XMLFormatException("No constructor for jdom elements.");
            }
        }
        for(i=0;i<rowsInt;i++) {
            for(j=0;j<colsInt;j++) {
                try {
                    Element kid = (Element)datas.get(count);
                    if (!isEmpty(kid))
                        data[i][j]=objectFromJDOM(kid, elementConstructor);
                } catch (Exception e) {}
                count++;
            }
        }
    }

    public void fromElement(Element element)
        throws XMLFormatException
    {
        //tough cookies
    }
    
    /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element, JDOMToObject jdomer)
        throws XMLFormatException
    {
        if (element == null)
            throw new XMLFormatException("Null XML element sent to StringMatrix.");
        String rows=element.getAttributeValue("rows");
        String cols=element.getAttributeValue("cols");
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
        java.util.List datas=element.getChildren();
        int ndatas = datas.size();
        //System.out.println("Found matrix with size="+sizeInt);
        if (ndatas < rowsInt*colsInt) {
            throw new XMLFormatException("Only gave "+ndatas+
            " data pieces, however we needed "+rowsInt*colsInt+
            " pieces of data for a "+rowsInt+" x " + colsInt +
            " matrix.");
        }
        data=new Object[rowsInt][colsInt];
        this.rows=rowsInt; this.cols=colsInt;
        for(i=0;i<rowsInt;i++) {
            for(j=0;j<colsInt;j++) {
                try {
                    data[i][j]=jdomer.objectFromJDOM((Element)datas.get(count));
                } catch (Exception e) {}
                count++;
            }
        }
    }

    public static Object objectFromJDOM(Element elt, Constructor eltConst)
        throws XMLFormatException
    {
        try {
            Object rval;
            rval=eltConst.newInstance((Object[])null);
            ((JDOMable)rval).fromElement(elt);
            return rval;
        } catch (InstantiationException ie) {
            ie.printStackTrace();
            throw new XMLFormatException("Unexpected instantiation error.");
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            throw new XMLFormatException("Unexpected IllegalAccessException.");
        } catch (java.lang.reflect.InvocationTargetException ite) {
            ite.printStackTrace();
            throw new XMLFormatException("Unexpected InvocationTargetException.");
        }
    }

    /*
    public static Object objectFromJDOM(Element elt, Class eltClass)
        throws XMLFormatException
    {
        if (elt==null) return null;
        String className = elt.getAttributeValue("_CLASS_");
        if ((className==null)||(className.equals(""))) {
            return null;
        } else {
            try {
                if (eltClass==null) {
                    eltClass = Class.forName(className);
                }
                java.lang.reflect.Constructor eltConst = eltClass.getConstructor((Class[])null);
                Object rval;
                rval=eltConst.newInstance((Object[])null);
                ((JDOMable)rval).fromElement(elt);
                return rval;
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                throw new XMLFormatException("Could not find class "+className);
            } catch (NoSuchMethodException nsme) {
                nsme.printStackTrace();
                throw new XMLFormatException("Class "+className+" did not have a constructor with no params.");
            } catch (InstantiationException ie) {
                ie.printStackTrace();
                throw new XMLFormatException("Unexpected instantiation error.");
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
                throw new XMLFormatException("Unexpected IllegalAccessException.");
            } catch (java.lang.reflect.InvocationTargetException ite) {
                ite.printStackTrace();
                throw new XMLFormatException("Unexpected InvocationTargetException.");
            }
        }
    }
     */
    public void rmCol(int place)
        throws ArrayIndexOutOfBoundsException
    {
        if ((place < 0) || (place >= this.cols))
            throw new ArrayIndexOutOfBoundsException();
        Object[][] newData=new Object[rows][cols-1];
        for(int i=0; i<rows; i++) {
            if(data[i][place]!=null)
                count--;
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
        Object[][] newData=new Object[rows-1][cols];
        for(int j=0; j<cols; j++) {
            if(data[place][j]!=null)
                this.count--;
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
        int last;
        Object mover;
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
        int last;
        Object mover;
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
        //testXML();
        testFromJDom();
    }
    
    public static Object objectFromJDOM(Element elt) 
        throws XMLFormatException
    {
        return objectFromJDOM(elt, null);
    }
    
    public static void testFromJDom() {
        Element elt = new Element("test");
        elt.setAttribute("_CLASS_", "com.dlens.anp.engine.DoubleRange");
        elt.setAttribute("start", "0"); elt.setAttribute("end", "1");
        try {
            System.out.println(objectFromJDOM(elt).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**Tests the reading of a matrix from xml.*/
    public static void testXML() {
        String fname = "/home/dlens/imatrix.xml";
        String data="<ObjectMatrix rows=\"3\" cols=\"4\">1 2 3 4 5 6 7 8 9 10 11 12</ObjectMatrix>";
        ObjectMatrixFull mat = new ObjectMatrixFull();
        SAXBuilder parser = new SAXBuilder();
        Document doc;
        try {
            doc = parser.build(new java.io.StringReader(data));
            Element elt = doc.getRootElement();
            mat.fromElementOld(elt);
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
            mat = new ObjectMatrixFull();
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
        Object[][] newData=new Object[this.rows][this.cols];
        int i,j,ii, jj;
        for(j=0; j<cols; j++) {
            jj=newPos[j];
            for(i=0; i<rows; i++) {
                newData[i][j]=data[i][jj];
            }
        }
        data=newData;
    }
    
    /**Tells if all the data in the matrix is nulls.*/
    public boolean isNull() {
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                if (data[i][j]!=null)
                    return false;
        return true;
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

    /**Returns a Object[][] of the data in this matrix.*/
    public Object[][] toFlashCommObject() {
        Object rval[][]=new Object[rows][cols];
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                rval[i][j]=data[i][j];
        return rval;
    }
    
    public int getSize() {
        return this.count;
    }
    
    public double getLoad() {
        double load = 0.0;
        if( (this.getRows()!=0) && (this.getCols()!=0) ) {
            double size = getSize();
            double avail = this.getRows()*this.getCols();
            load = size/avail;
        }
        return load;
    }
    public void clearColumn(int column)
    {
        for(int i=0; i < this.getRows(); i++) {
            this.set(i,column, null);
        }
    }
    public void resetCount() {
        count=0;
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                if (data[i][j]!=null) {
                    count++;                    
                }
            }
        }
    }
}
