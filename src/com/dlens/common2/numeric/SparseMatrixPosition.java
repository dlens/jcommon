
package com.dlens.common2.numeric;
import org.jdom.Element;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.ObjectToJDOM;


/**
 *
 * @author Keith Istler
 */
public class SparseMatrixPosition {

    private int row = -1;
    private int col = -1;
    
    /** Creates a new instance of SparseMatrixPosition */
    public SparseMatrixPosition(int tRow, int tCol) {
        this.setRow(tRow);
        this.setCol(tCol);
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

    public org.jdom.Element toElement(String name, ObjectToJDOM otj, Object otjData) {
        Element rval=new Element(name);
        Element td;
        rval.setAttribute("row", ""+this.row);
        rval.setAttribute("col", ""+this.col);
        String commonClassName = otjData.getClass().getName();
        boolean hasCommonClass = false;
        if (commonClassName != null) {
            rval.setAttribute("_CLASS_", commonClassName);
            hasCommonClass=true;
        }

        rval.addContent( otj.objectToJDOM(otjData, "entryData") );     //is "entryData" ok?        
        return rval;
    }
    
    public boolean equals(Object ob) {
        boolean ret = false;
        if(ob instanceof SparseMatrixPosition) {
            SparseMatrixPosition pos = (SparseMatrixPosition)ob;
            if( (pos.getRow()== this.getRow()) && (pos.getCol()==this.getCol()) ) {
                ret = true;
            }  
        }
        return ret;
    }
    
//    public void fromElement(Element element, ObjectFromJDOM ofj)
//        throws XMLFormatException
//    {
//        if (element == null)
//            throw new XMLFormatException("Null XML element sent to SparseObjectMatrix.");
//        String row=element.getAttributeValue("row");
//        String col=element.getAttributeValue("col");
//        int i, j, count=0, rowInt=0, colInt=0;
//        if ((row != null) && (!"".equals(row))) {
//            try {
//                rowInt = Integer.parseInt(row);
//            } catch (NumberFormatException e) {
//                throw new XMLFormatException("While parsing a matrix, row attribute ="+row+" was not an integer.");
//            }
//        } else {
//            throw new XMLFormatException("While parsing the matrix, no rows value given.");
//        }
//        if ((col != null) && (!"".equals(col))) {
//            try {
//                colInt = Integer.parseInt(col);
//            } catch (NumberFormatException e) {
//                throw new XMLFormatException("While parsing a matrix, col attribute ="+col+" was not an integer.");
//            }
//        } else {
//            throw new XMLFormatException("While parsing the matrix, no col value given.");
//        }
//        
//        java.util.List datas=element.getChildren("entry");
//        int ndatas = datas.size();
//        this.row=rowInt; this.col=colInt;
//        
////        Element kid = null;
////        SparseObjectMatrixEntry entry = new SparseObjectMatrixEntry();
////        for(i=0; i<datas.size(); i++) {
////            kid = (Element)datas.get(i);
////            if(!isEmpty(kid)) {
////                data = ofj.objectFromJDOM(kid);
////            }
////        }
//    }
    
    private static boolean isEmpty(org.jdom.Element elt) {
        if (!elt.getAttributes().isEmpty()) {
            return false;
        }
        if (!elt.getContent().isEmpty()) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        //StringBuffer buff = new StringBuffer(Integer.toString(row));
        //buff.append(":");
        //buff.append(Integer.toString(col));
        String temp = Integer.toString(row) + ":" + Integer.toString(col);
        return temp.hashCode();
        //return buff.hashCode();
    }
    
    public String toString() {
        return "@("+row + "," + col+")";
    }
    
}
