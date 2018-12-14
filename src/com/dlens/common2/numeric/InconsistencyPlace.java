
package com.dlens.common2.numeric;
import com.dlens.common2.interfaces.JDOMable;

/**
 *
 * @author  Administrator
 */
public class InconsistencyPlace implements java.lang.Comparable {
    public int row=0;
    public int col=0;
    /**If the matrix is indexed by another set of integers, you can store
     *the alternate index for the row here.
     */ 
    public int altRowIndex=-1;
    /**If the matrix is indexed by another set of integers, you can store
     *the alternate index for the column here.
     */ 
    public int altColIndex=-1;
    public double improvedInconsistency=0;
    public double originalInconsistency=0;
    public double originalValue=0;
    public double recommendedValue=0;
    public double recValueV1=0;
    public double recValueV2=0;
    public double newInconV1=0;
    public double newInconV2=0;
    /** Creates a new instance of DoubleSquareMatrixPlace */
    public InconsistencyPlace() {
    }
    public InconsistencyPlace(int r, int c, double oldIncon, double origVal, double newIncon, double re) {
        row=r; col=c; improvedInconsistency=newIncon; recommendedValue=re;
        originalInconsistency=oldIncon; originalValue=origVal;
    }
    public InconsistencyPlace(int r, int c, double oldIncon, double origVal, double newInconV1, double recValV1, double newInconV2, double recValV2)
    {
        row=r; col=c; 
        this.newInconV1=newInconV1; 
        this.recValueV1=recValV1;
        this.newInconV2=newInconV2; 
        this.recValueV2=recValV2;
        this.originalInconsistency=oldIncon; 
        this.originalValue=origVal;
        chooseBest();
    }
    
    public void chooseBest() {
        if (newInconV1 < newInconV2) {
            improvedInconsistency=newInconV1;
            recommendedValue=recValueV1;
        } else {
            improvedInconsistency=newInconV2;
            recommendedValue=recValueV2;            
        }
    }
    public String toString() {
        StringBuffer rval = new StringBuffer();
        rval.append("row="+row+" col="+col+"\n");
        rval.append("origIncon="+originalInconsistency+" newIncon="+improvedInconsistency+"\n");
        rval.append("recVal="+recommendedValue);
        return rval.toString();
    }
    public int compareTo(Object o) {
        if (o.getClass() != InconsistencyPlace.class) {
            return -1;
        }
        InconsistencyPlace d = (InconsistencyPlace)o;
        if (improvedInconsistency < d.improvedInconsistency) {
            return -1;
        } else if (improvedInconsistency == d.improvedInconsistency) {
            return 0;
        } else {
            return 1;
        }
    }
    
}
