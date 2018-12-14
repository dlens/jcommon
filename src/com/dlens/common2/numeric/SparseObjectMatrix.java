
package com.dlens.common2.numeric;


import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Set;
import org.jdom.Element;
import org.jdom.Document;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMable;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.ObjectToJDOM;

import org.jdom.input.*;
import org.jdom.output.*;


/**
 *
 * @author Keith Istler
 */
public class SparseObjectMatrix implements ObjectMatrixInterface, java.io.Serializable {
    
    private int rows =0;
    private int cols =0;
    
    //private LinkedList data = null;
    private Hashtable data = null;
    
    /** Creates a new instance of SparseMatrix */
    public SparseObjectMatrix() {
        data = new Hashtable();
    }

    public SparseObjectMatrix(int rows, int cols) {
        this();
        this.rows = rows;
        this.cols = cols;
    }
    
    public static void main(String[] args) {
        SparseObjectMatrix matrix = new SparseObjectMatrix(3, 8);
        
//        com.dlens.common.numeric.DoubleRange dr1 = new DoubleRange(0,1);
//        com.dlens.common.numeric.DoubleRange dr2 = new DoubleRange(0,2);
//        com.dlens.common.numeric.DoubleRange dr3 = new DoubleRange(0,3);
//        com.dlens.common.numeric.DoubleRange dr4 = new DoubleRange(0,4);
//        matrix.set(0,0, dr1);
//        matrix.set(0,1, dr2);
//        matrix.set(0,2, dr3);
//        matrix.set(0,3, dr4);
        
        matrix.set(0,0, "0,0");
        matrix.set(0,1, "0,1");
        matrix.set(0,2, "0,2");
        matrix.set(0,3, "0,3");
        matrix.set(0,4, "0,4");
        matrix.set(0,5, "0,5");
        matrix.set(0,6, "0,6");
        matrix.set(0,7, "0,7");
        
        System.out.println("size: " + matrix.getSize());
        System.out.println( matrix.toString() );
        
//        matrix.rmCol(5);//appear to work
//        System.out.println( matrix.toString() );
//        System.out.println("size: " + matrix.getSize());

        System.out.println();
        
//        matrix.mvColAfter(6, -1);    //appears to work
        int[] order = new int[8];
        order[0]=1; order[1]=2; order[2]=0; order[3]=3;
        order[4]=4; order[5]=5; order[6]=6; order[7]=7;
        matrix.reorderCols(order);    //appears to work

        System.out.println( matrix.toString() );

       //System.out.println(matrix.toXMLString());
       
//       Element fr = matrix.toElement(matrix.getClass().getName());
//       matrix = new SparseObjectMatrix();
//       try {
//           matrix.fromElementOld(fr);
//       } catch(XMLFormatException ex) {
//           ex.printStackTrace();
//       }
//       
//       System.out.println("matr: " + matrix.toString());
//       System.out.println("rows: " + matrix.rows);
//       System.out.println("cols: " + matrix.cols);
    }
    
    public void addRowAfter(int place)  //##############
        throws ArrayIndexOutOfBoundsException {
        
        if(place>this.rows) {
            throw new ArrayIndexOutOfBoundsException("row outside of matrix");
        }
        
        Hashtable newTable = new Hashtable();
        this.rows++;
        SparseMatrixPosition entry = null;
        Iterator iter = data.keySet().iterator();   //iterator of all keys
        Object dat = null;
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);            
            if( entry.getRow() > place ) {
                entry.setRow( entry.getRow()+1 );
            }
            newTable.put(entry, dat);
        }
        this.data = newTable;
        
    }
    
    public void addRowBefore(int place)
        throws ArrayIndexOutOfBoundsException {
        
        if(place>this.cols) {
            throw new ArrayIndexOutOfBoundsException("row outside of matrix");
        }
        
        Hashtable newTable = new Hashtable();
        SparseMatrixPosition entry = null;
        Object dat = null;
        this.rows++;
        Iterator iter = data.keySet().iterator();   //iterator of all keys
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);            
            if( entry.getRow() >= place ) {
                entry.setRow( entry.getRow()+1 );
            }
            newTable.put(entry, dat);
        }
        this.data = newTable;
    }
    
    public void addColAfter(int place)
        throws ArrayIndexOutOfBoundsException {
        
        if(place>this.cols) {
            throw new ArrayIndexOutOfBoundsException("column outside of matrix");
        }
        
        Hashtable newTable = new Hashtable();
        this.cols++;
        SparseMatrixPosition entry = null;
        Object dat = null;
        Iterator iter = data.keySet().iterator();   //iterator of all keys
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);
            if( entry.getCol() > place ) {
                entry.setCol( entry.getCol()+1 );
            }
            newTable.put(entry, dat);
        }
        this.data = newTable;
    }

    public void addColBefore(int place) //##############
        throws ArrayIndexOutOfBoundsException {
        
        if(place>this.cols) {
            throw new ArrayIndexOutOfBoundsException("column outside of matrix");
        }
        
        this.cols++;
        SparseMatrixPosition entry = null;
        Hashtable newTable = new Hashtable();
        Object dat = null;
        Iterator iter = data.keySet().iterator();   //iterator of all keys
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);
            if( entry.getCol() >= place ) {
                entry.setCol( entry.getCol()+1 );
            }
            newTable.put(entry, dat);
        }
        this.data = newTable;
    }
    
    public void addCol() {
        this.cols++;
    }
    public void addRow() {
        this.rows++;
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }

    /**Moves a column after a given column (end).  If end=-1, that
     *means to move the given column to the zeroth position.
     */
    public void mvColAfter(int start, int end) throws ArrayIndexOutOfBoundsException {
        
        if( (start>=this.cols) || (end>=this.cols) ) {
            throw new ArrayIndexOutOfBoundsException("column outside of matrix");
        }
        
        Hashtable newTable = new Hashtable();
        SparseMatrixPosition entry = null;
        Iterator iter = data.keySet().iterator();   //iterator of all keys
        Object dat = null;
        int tCol;
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);
            tCol = entry.getCol();
            
            if(end==-1) {  //move a node to the 0th position
                if(tCol==start) {
                    entry.setCol(0);
                } else if(tCol < start){
                    entry.setCol(entry.getCol()+1);
                }
            } else if( start<end ) { //moving a node to a higher position
                if(tCol==start) {
                    entry.setCol(end);
                } else if( (tCol<=end) && (tCol>start) ){    //shift to lower #
                    entry.setCol(entry.getCol()-1);
                }
            } else if( start>end ) {  //moving a node to a lower position
                if(tCol==start) {
                    entry.setCol(end+1);
                } else if( (tCol<start) && (tCol>end) ){  //shift to higher #
                    entry.setCol(entry.getCol()+1);
                }
            }
            
            newTable.put(entry, dat);
        }
        this.data = newTable;
    }
    
    /**Moves a row after a given row (end).  If end=-1, that
     *means to move the given row to the zeroth position.
     */
    public void mvRowAfter(int start, int end) throws ArrayIndexOutOfBoundsException {
        
        if( (start>=this.rows) || (end>=this.rows) ) {
            throw new ArrayIndexOutOfBoundsException("row outside of matrix");
        }
        
        Hashtable newTable = new Hashtable();
        SparseMatrixPosition entry = null;
        Iterator iter = data.keySet().iterator();   //iterator of all keys
        Object dat = null;
        int tRow;
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);
            tRow = entry.getRow();
            
            if(end==-1) {  //move a node to the 0th position
                if(tRow==start) {
                    entry.setRow(0);
                } else if(tRow < start){
                    entry.setRow(entry.getRow()+1);
                }
            } else if( start<end ) { //moving a node to a higher position
                if(tRow==start) {
                    entry.setRow(end);
                } else if( (tRow<=end) && (tRow>start) ){    //shift to lower #
                    entry.setRow(entry.getRow()-1);
                }
            } else if( start>end ) {  //moving a node to a lower position
                if(tRow==start) {
                    entry.setRow(end+1);
                } else if( (tRow<start) && (tRow>end) ){  //shift to higher #
                    entry.setRow(entry.getRow()+1);
                }
            }
            
            newTable.put(entry, dat);
        }
        this.data = newTable;
    }
    
    /**This function reorders the columns of the given matrix.
     *@param newPos This is an integer array that has the new positions.
     *It should have the same size as the matrix we are using.
     *
     */
    public void reorderCols(int newPos[])
    {
        Hashtable newTable = new Hashtable();
        SparseMatrixPosition entry = null;
        Iterator iter = data.keySet().iterator();   //iterator of all keys
        Object dat = null;

        //newPos[newPos] = old
        Hashtable translation = new Hashtable();
        for(int i=0; i<newPos.length; i++) {
            translation.put( new Integer(newPos[i]), new Integer(i) );
        }
    
        int currCol = 0;
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            dat = data.get(entry);
            currCol = entry.getCol();
            entry.setCol( ((Integer)translation.get(new Integer(currCol)) ).intValue() );
            newTable.put(entry, dat);
        }
        this.data = newTable;
    }
    
    /**Tells if all the data in the matrix is nulls.*/
    public boolean isNull() {
        boolean ret = false;
        if(data.size()==0) {
            ret = true;
        }
        return ret;
    }
    
    public void rmRow(int place)
        throws ArrayIndexOutOfBoundsException
    {
        if( (place<0) || (place>this.rows) ) {
            throw new ArrayIndexOutOfBoundsException("row outside of matrix");
        }
        
        SparseMatrixPosition entry = null;
        Hashtable newTable = new Hashtable();
        int i = 0;
        SparseMatrixPosition[] keys = (SparseMatrixPosition[])data.keySet().toArray(new SparseMatrixPosition[0]);   //iterator of all keys
        Object dat = null;
        this.rows--;
        for(int ii=(keys.length-1); ii>=0; ii--) {
            entry = keys[ii];
            if( entry.getRow() > place ) {
                dat = data.get(entry);
                entry.setRow( entry.getRow()-1 );
                newTable.put(entry, dat);
            } else if( entry.getRow() < place ) {
                newTable.put(entry, data.get(entry));
            }
        }
        this.data = newTable;
    }
    
    public void rmCol(int place)
        throws ArrayIndexOutOfBoundsException
    {
        if( (place<0) || (place>this.cols) ) {
            throw new ArrayIndexOutOfBoundsException("column outside of matrix");
        }
        SparseMatrixPosition entry = null;
        int i = 0;
        Hashtable newTable = new Hashtable();
        SparseMatrixPosition[] keys=(SparseMatrixPosition[])data.keySet().toArray(new SparseMatrixPosition[0]);
        Iterator iter = data.keySet().iterator();   //iterator of all keys
        Object dat = null;
        this.cols--;
        for(int ii=(keys.length-1); ii >=0; ii--) {
            entry = keys[ii];
            if(entry.getCol() == place) {
                //this.data.remove(entry);
            } else if( entry.getCol() != place ) {
                dat = data.get(entry);
                if( entry.getCol() > place ) {
                    entry.setCol( entry.getCol()-1 );
                    newTable.put(entry, dat);
                } else if( entry.getCol() < place ) {
                    newTable.put(entry, data.get(entry));
                }
            }
        }
        this.data = newTable;
    }
    
    /**Sets a place in the matrix.*/
    public void set(int row, int col, Object val)
        throws ArrayIndexOutOfBoundsException
    {
        if( (row>=this.rows) || (col>=this.cols) ) {
            throw new ArrayIndexOutOfBoundsException("row or column outside of matrix");
        }
        SparseMatrixPosition entry = new SparseMatrixPosition(row,col);
        //System.out.println("Setting at: " + entry.toString() + " with val: " + val.toString());
        
        if(val!=null) { //setting a value
            data.put(entry, val);
        } else {
            data.remove(entry);
        }
    }

    /**Gets a value in the matrix.*/
    public Object get(int row, int col)
        throws ArrayIndexOutOfBoundsException {
        SparseMatrixPosition curr = new SparseMatrixPosition(row, col);
        Object val = data.get(curr);
        
/*        if(val!=null) {
            System.out.println("get'ing at: " + curr.toString() + " and will return: " + val.toString());
        } else {
            System.out.println("get'ing at: " + curr.toString() + " and will return: " + null );
        }*/
        return val;
    }
    
    public Object[][] toFlashCommObject() {
        Object[][] data = new Object[this.rows][this.cols];
        //######finish me!!
        return data;
    }
    
    public int getSize() {
        return data.size();
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
    
    /**If every non-null object has the same class, this method returns
     *that class name.  If there was no common class, then null is returned.
     *If there was no non-null, then null is returned.
     */
    public String getCommonClassName()
    {
        String name = null;
        String tmp = null;
        Object obj = null;
        boolean done = false;
        
        if(data.size()>0) {
            Iterator values = data.values().iterator();
            while(values.hasNext() && !done) {
                obj = values.next();
                tmp = obj.getClass().getName();

                if(name == null) {
                    name = tmp;
                } else if(!name.equals(tmp)) {
                    name = null;
                    done = true;
                }
            }
        }
        
        return name;
    }
    
    public String toString() {
        StringBuffer bRet = new StringBuffer();
        SparseMatrixPosition entry = null;
        Object obj = null;
        String temp = null;
        Iterator iter = this.data.keySet().iterator();
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            obj = data.get(entry);
            temp = entry.toString();
            bRet.append( temp );
            bRet.append( "=>" );
            bRet.append(obj.toString());
            if(iter.hasNext())
                bRet.append(" ");
        }
        return bRet.toString();
    }
    
    public Hashtable getData() {
        return this.data;
    }
    
    public void fromElementOld(Element element)
        throws XMLFormatException
    { }
    
    public void fromElement(Element element)
        throws XMLFormatException
    {
        fromElement(element, getObjFromJdomData() );    //this is just for testing 
                              //or if the ObjectFromData object is set to whats bein used
    }
    
   /**
     *Fills in this String matrix with the data from <code>element</code>.
     *@throws XMLFormatException If the element does not have the right data.
     */
    public void fromElement(Element element, JDOMToObject dataJdomer)
        throws XMLFormatException
    {
         if (element == null)
            throw new XMLFormatException("Null XML element sent to SparseObjectMatrix.");
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
        this.rows = rowsInt;
        this.cols = colsInt;        
        
        java.util.List datas=element.getChildren();
        Element kid = null;
        this.data = new Hashtable();
        SparseMatrixPosition entry = null;
        int tRow = -1;
        int tCol = -1;
        for(i=0; i<datas.size(); i++) {
            kid = (Element)datas.get(i);
            if(!isEmpty(kid)&&kid.getName().equals("entry")) {
                
                tRow = Integer.parseInt( kid.getAttributeValue("row") );
                tCol = Integer.parseInt( kid.getAttributeValue("col") );
                Element child = kid.getChild("entryData");

                Object tdata = dataJdomer.objectFromJDOM(child);
                entry = new SparseMatrixPosition(tRow, tCol);
                
                this.data.put( entry, tdata );
            }
        }
    }
    
    private static boolean isEmpty(org.jdom.Element elt) {
        if (!elt.getAttributes().isEmpty()) {
            return false;
        }
        if (!elt.getContent().isEmpty()) {
            return false;
        }
        return true;
    }
    
    public org.jdom.Element toElement(String name) {
        Element el = toElement(name, obToJdom);
        return el;
    }
    
    public org.jdom.Element toElement(String name, ObjectToJDOM otjEntryElement) {
        Element rval=new Element(name);
        Element td;
        rval.setAttribute("rows", ""+this.rows);
        rval.setAttribute("cols", ""+this.cols);
        
        rval.setAttribute("isSparse","true");
        
        String commonClassName = getCommonClassName();
        if (commonClassName != null) {
            rval.setAttribute("_CLASS_", commonClassName);
        }
        
        SparseMatrixPosition entry = null;
        Iterator iter = this.data.keySet().iterator();
        Object obj = null;
        while(iter.hasNext()) {
            entry = (SparseMatrixPosition)iter.next();
            obj = this.data.get(entry); //entry.toElement needs to know about this
            rval.addContent(entry.toElement("entry", otjEntryElement, obj));
        }
        
        return rval;
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
    
    ObjectToJDOM obToJdom = new ObjToJdom();
    JDOMToObject obFromJdomData = new ObjFromJdomData();  //obFromJdom for the entries
    
    public JDOMToObject getObjFromJdomData() {
        return obFromJdomData;
    }
    
    public ObjectToJDOM getObjectToJDOM() {
        return obToJdom;
    }
    public void setObjectToJDOM(ObjectToJDOM otj) {
        obToJdom = otj;
    }
    
    //For converting the data of a xml sparseObjectMatrixEntry to a string - for testing mostly
    class ObjFromJdomData extends JDOMToObject {
        public Object objectFromJDOM(Element elt) throws XMLFormatException {
            return elt.getTextTrim();
        }
    }
    
    //this is the objectToJdom object that converts the data to xml 
    //  -for now we assume a string
    private class ObjToJdom extends ObjectToJDOM {
    //expects a string as the object  - this is expected to be passed in
        public Element objectToJDOM(Object object, String name) {
            Element el = new Element(name);
            el.addContent((String)object);
            return el;
        }
    }
    public void clearColumn(int column)
    {
        for(int i=0; i < this.getRows(); i++) {
            this.set(i,column, null);
        }
    }
}
