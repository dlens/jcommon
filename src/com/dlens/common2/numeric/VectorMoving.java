
package com.dlens.common2.numeric;
import java.util.Vector;

/**
 * Just a simple static class to move things in vectors around.
 * @author  W Adams
 */
public class VectorMoving {
    
    
    public static void vectorMoveAfter(Vector v, int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        Object mover;
        if (start==end)
            return;
        if ((start<0)||(start>=v.size())||(end<-1)||(end>=v.size()))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            mover=v.get(start);
            for(int i=start; i < end; i++) {
                v.set(i, v.get(i+1));
            }
            v.set(end, mover);
        } else {
            mover=v.get(start);
            for(int i=start; i > (end+1); i--) {
                v.set(i,v.get(i-1));
            }
            v.set(end+1, mover);
        }
    }

    /**Returns the start and end indexes, ie the rval is an int[2] first
     value is the start, second is the end.*/
    public static int[] vectorMoveAfter(Vector v, Object startO, Object endO)
        throws ArrayIndexOutOfBoundsException
    {
        int start =v.indexOf(startO);
        int end;
        int rval[]=new int[2];
        if (endO==null) {
            end=-1;
        } else {
            end=v.indexOf(endO);
        }
        rval[0]=start;
        rval[1]=end;
        vectorMoveAfter(v, start, end);
        return rval;
    }
    
    public static Vector vector(Object[] v) {
        Vector rval =new Vector(v.length);
        for(int i=0; i<v.length; i++) {
            rval.add(v[i]);
        }
        return rval;
    }

    public static void vectorMoveAfter(double []v, int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        double mover;
        if (start==end)
            return;
        if ((start<0)||(start>=v.length)||(end<-1)||(end>=v.length))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            mover=v[start];
            for(int i=start; i < end; i++) {
                v[i]=v[i+1];
            }
            v[end]=mover;
        } else {
            mover=v[start];
            for(int i=start; i > (end+1); i--) {
                v[i]=v[i-1];
            }
            v[end+1]=mover;
        }
    }

    public static void vectorMoveAfter(int []v, int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        int mover;
        if (start==end)
            return;
        if ((start<0)||(start>=v.length)||(end<-1)||(end>=v.length))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            mover=v[start];
            for(int i=start; i < end; i++) {
                v[i]=v[i+1];
            }
            v[end]=mover;
        } else {
            mover=v[start];
            for(int i=start; i > (end+1); i--) {
                v[i]=v[i-1];
            }
            v[end+1]=mover;
        }
    }

    public static void vectorMoveAfter(boolean []v, int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        boolean mover;
        if (start==end)
            return;
        if ((start<0)||(start>=v.length)||(end<-1)||(end>=v.length))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            mover=v[start];
            for(int i=start; i < end; i++) {
                v[i]=v[i+1];
            }
            v[end]=mover;
        } else {
            mover=v[start];
            for(int i=start; i > (end+1); i--) {
                v[i]=v[i-1];
            }
            v[end+1]=mover;
        }
    }

    public static void vectorMoveAfter(Object []v, int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        Object mover;
        if (start==end)
            return;
        if ((start<0)||(start>=v.length)||(end<-1)||(end>=v.length))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            mover=v[start];
            for(int i=start; i < end; i++) {
                v[i]=v[i+1];
            }
            v[end]=mover;
        } else {
            mover=v[start];
            for(int i=start; i > (end+1); i--) {
                v[i]=v[i-1];
            }
            v[end+1]=mover;
        }
    }

}
