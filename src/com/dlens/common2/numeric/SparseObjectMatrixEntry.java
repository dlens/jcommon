
package com.dlens.common2.numeric;
import org.jdom.Element;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMable;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.ObjectToJDOM;

/**
 *
 * @author Keith Istler
 */
public class SparseObjectMatrixEntry implements Comparable, JDOMable {
    
    private int row = -1;
    private int col = -1;
    private Object data = null;

    protected SparseObjectMatrixEntry() {
    }
    
    /** Creates a new instance of SparseObjectMatrixEntry */
    public SparseObjectMatrixEntry(int row, int col, Object data) {
        this.setRow(row);
        this.setCol(col);
        this.setData(data);
    }
    
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public int compareTo(Object val) {
        int ret = 0;
        SparseObjectMatrixEntry eVal = (SparseObjectMatrixEntry)val;
        if( (this.row < eVal.getRow()) || ( (this.row== eVal.getRow()) && (this.col < eVal.getCol()) ) ) {
            ret = -1;
        } else if(  (this.row > eVal.getRow()) || ( (this.row== eVal.getRow()) && (this.col > eVal.getCol()) )  ) {
            ret = 1;
        }
        
        return ret;
    }
    public String toString() {
        String dat = "";
        if(data!=null) {
            dat = data.toString();
        }
        return "[("+this.row +"," + this.col+ ") " + dat + "]";
    }

    public boolean samePlace(SparseObjectMatrixEntry val) {
        SparseObjectMatrixEntry entry = (SparseObjectMatrixEntry)val;
        boolean ret = true;
        
        if( (this.row != entry.getRow()) || (this.col != entry.getCol()) ) {
            ret = false;
        }
        return ret;
    }
    
    public boolean equals(Object val) {
        SparseObjectMatrixEntry entry = (SparseObjectMatrixEntry)val;
        boolean ret = true;
        
        if( (this.row != entry.getRow()) || (this.col != entry.getCol()) 
                || (!this.data.equals(entry.getData())) ) {
            ret = false;
        }
        return ret;
    }
    
    public void fromElement(Element element) throws XMLFormatException {
        // .. need the objectfromjdom object
    }
    
    public void fromElement(Element element, JDOMToObject ofj)
        throws XMLFormatException
    {
        if (element == null)
            throw new XMLFormatException("Null XML element sent to SparseObjectMatrix.");
        String row=element.getAttributeValue("row");
        String col=element.getAttributeValue("col");
        int i, j, count=0, rowInt=0, colInt=0;
        if ((row != null) && (!"".equals(row))) {
            try {
                rowInt = Integer.parseInt(row);
            } catch (NumberFormatException e) {
                throw new XMLFormatException("While parsing a matrix, row attribute ="+row+" was not an integer.");
            }
        } else {
            throw new XMLFormatException("While parsing the matrix, no rows value given.");
        }
        if ((col != null) && (!"".equals(col))) {
            try {
                colInt = Integer.parseInt(col);
            } catch (NumberFormatException e) {
                throw new XMLFormatException("While parsing a matrix, col attribute ="+col+" was not an integer.");
            }
        } else {
            throw new XMLFormatException("While parsing the matrix, no col value given.");
        }
        
        java.util.List datas=element.getChildren("entry");
        int ndatas = datas.size();
        this.row=rowInt; this.col=colInt;
        
        Element kid = null;
        SparseObjectMatrixEntry entry = new SparseObjectMatrixEntry();
        for(i=0; i<datas.size(); i++) {
            kid = (Element)datas.get(i);
            if(!isEmpty(kid)) {
                data = ofj.objectFromJDOM(kid);
            }
        }
    }
    
    public org.jdom.Element toElement(String name) {
        Element ret = new Element(name);
        return ret;
    }
    
    public org.jdom.Element toElement(String name, ObjectToJDOM otj) {
        Element rval=new Element(name);
        Element td;
        rval.setAttribute("row", ""+this.row);
        rval.setAttribute("col", ""+this.col);
        String commonClassName = getData().getClass().getName();
        boolean hasCommonClass = false;
        if (commonClassName != null) {
            rval.setAttribute("_CLASS_", commonClassName);
            hasCommonClass=true;
        }

        rval.addContent( otj.objectToJDOM(data, "entryData") );     //is "entryData" ok?
        //rval.addContent( ((JDOMable)data).toElement("entryData") );     //is "entryData" ok?
        
        return rval;
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
}
