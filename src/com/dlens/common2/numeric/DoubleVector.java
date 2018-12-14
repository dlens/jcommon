package com.dlens.common2.numeric;
import com.dlens.common2.exceptions.ConvergenceException;
import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMable;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.util.*;
import java.io.*;


/**Essentially a wrapper class for the double[] data type.  This class is
 *allowed to grow and shrink.  It is more efficient than trying to use a
 *Vector.
 */
public class DoubleVector implements JDOMable {
    /**The internal data of the vector.*/
    double []data;
    /**
     *Constructs are vector of size <code>size</code>.
     *Please notice that negative sizes just get rounded
     *up to zero, and that a size of zero is perfectly
     *legal.
     *@param size The size of the vector to create.
     */
    public DoubleVector(int size) {
        if (size<0) size=0;
        data=new double[size];
    }
    
    /**
     *Constructs a copy of the vector given as a parameter.
     *@param src The vector to copy from .
     */
    public DoubleVector(DoubleVector src) {
        int size=src.data.length;
        data = new double[size];
        System.arraycopy(src.data, 0, data, 0, size);
    }
    
    /**Returns a copy of the data.*/
    public double[] getData() {
        int size=data.length;
        double rval[] = new double[size];
        System.arraycopy(data, 0, rval, 0, size);
        return rval;
    }
    
    /**Constructs a vector of size zero.*/
    public DoubleVector() {
        this(0);
    }
    
    /**
     *Constructs a DoubleVector from a double[].
     */
    public DoubleVector(double []d)
    {
        data = new double[d.length];
        System.arraycopy(d, 0, data, 0, d.length);
    }
    
    /**
     *Constructs a DoubleVector from a double[], but only from start to end.
     */
    public DoubleVector(double []d, int start, int end)
    {
        int size=end-start+1;
        data = new double[size];
        System.arraycopy(d, start, data, 0, size);
    }
    
    /**
     *Removes a place from our vector.
     *@param place The place to remove.
     *@throws ArrayIndexOutOfBoundsException If <code>place</code> was out of bounds.
     */
    public void rmPlace(int place) 
        throws ArrayIndexOutOfBoundsException
    {
        if ((place < 0) || (place >= data.length)) {
            throw new ArrayIndexOutOfBoundsException(place);
        }
        double newData[] = new double[data.length-1];
        System.arraycopy(data, 0, newData, 0, place);
        System.arraycopy(data, place+1, newData, place, newData.length - place);
        data=null;
        data=newData;
    }
    
    /**
     *Inserts a place at the end of our vector.
     *@return The position of the place added.
     */
    public int addPlace() {
        double newData[] = new double[data.length+1];
        System.arraycopy(data, 0, newData, 0, data.length);
        data=null;
        data=newData;
        return data.length-1;
    }

    /**
     *Inserts a place at the end of our vector, and sets it
     *to a particular value.
     *@param val The value to put in the new place.
     */
    public int addPlace(double val) {
        double newData[] = new double[data.length+1];
        System.arraycopy(data, 0, newData, 0, data.length);
        newData[data.length]=val;
        data=null;
        data=newData;
        return data.length-1;
    }

    /**Sets the value for a place.
     *@param place The place in the matrix to set.
     *@param val The value to put in that place.
     *@throws ArrayIndexOutOfBoundsException If <code>place</code> was out of bounds.
     */
    public void set(int place, double val) 
        throws ArrayIndexOutOfBoundsException
    {
        data[place]=val;
    }
    
    /**Sets the value for a place.
     *@param place The place in the matrix to set.
     *@param val The value to put in that place.
     *@throws ArrayIndexOutOfBoundsException If <code>place</code> was out of bounds.
     */
    public void set(double vals[]) 
        throws ArrayIndexOutOfBoundsException
    {
        if (vals==null)
            throw new ArrayIndexOutOfBoundsException();
        data=new double[vals.length];
        System.arraycopy(vals, 0, data, 0, vals.length);
    }
    
    /**Returns the value for this vector at a place.
     *@param place The place to get the value from.
     *@returns The value at that place.
     *@throws ArrayIndexOutOfBoundsException If <code>place</code> was out of bounds.
     */
    public double get(int place) throws ArrayIndexOutOfBoundsException
    {
        return data[place];
    }
    
    /**A useful debugging routine.  It makes a pretty string that represents this
     *vector, useful for printing to stdout.
     *@return The pretty string.
     */
    public String toString()
    {
        String s = new String();
        for(int i=0;i < data.length; i++) {
            s+=data[i]+"\n";
        }
        return s;
    }
    
    /**
     *Creates a <code>jdom</code> element representing this double vector.
     *This element is useful for saving/restoring vectors.  See {@link fromElement}
     *for restoring from an element.
     *@param name The name to give this <code>jdom</code> element.
     *@return The <code>jdom</code> element representing this vector.
     */
    public Element toElement(String name)
    {
        Element rval=new Element(name);
        rval.setAttribute("size", ""+data.length);
        for (int i=0; i<data.length; i++) {
            rval.addContent("\n"+String.valueOf(data[i]));
        }
        return rval;
    }
    
    /**
     *Restructures a vector based on a jdom element.
     *@param element The jdom element to read data from.
     *@throws XMLFormatException If this xml element is not correctly formatted.
     */
    public void fromElement(Element element)
        throws XMLFormatException
    {
        if (element == null)
            throw new XMLFormatException("Null XML element sent to DoubleVector.");
        String size=element.getAttributeValue("size");
        String text = element.getTextTrim();
        int sizeInt=0;
        if ((size != null) && (!"".equals(size))) {
            try {
                sizeInt = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new XMLFormatException("While parsing a vector, size attribute ="+size+" was not an integer.");
            }
        }
        String numbers[] = text.split("\\s+");
        //if (numbers.length > sizeInt) sizeInt = numbers.length;
        data=new double[sizeInt];
        for(int i = 0; i< sizeInt; i++) {
            try {
                data[i]=Double.parseDouble(numbers[i]);
            } catch (Exception ignored) {}
        }
    }
       
    /**Access the size variable.*/
    public int getSize() {return data.length;}
    
    /**
     *Sums absolute value of the entries.
     */
    public double sum()
    {
        double sum=0;
        int i;
        for(i=0; i<data.length; i++) {
            sum+=Math.abs(data[i]);
        }
        return sum;
    }

    /**
     *Makes this vector's elements add to one in absolute value.
     *This does actually change the values in this vector.
     */
    public void normalize()
    {
        double sum=0;
        int i;
        for(i=0; i<data.length; i++) {
            sum+=Math.abs(data[i]);
        }
        if (sum!=0) {
            for(i=0;i<data.length;i++) {
                data[i]/=sum;
            }
        }
    }
        
    /**Assumes a Vector of DoubleVectors, and averages them, filling in this
     *with their values.*/
    public void average(Vector v)
        throws ArrayIndexOutOfBoundsException
    {
        DoubleVector []dvs = new DoubleVector[v.size()];
        dvs=(DoubleVector[])v.toArray(dvs);
        for(int i=0; i<data.length; i++ ) {
            data[i]=0;
            for(int j=0; j<dvs.length; j++) {
                data[i]+=dvs[j].data[i];
            }
            if (dvs.length>0) data[i]/=dvs.length;
        }
    }   

    /**Assumes a Vector of DoubleVectors, and averages them, filling in this
     *with their values.  It does not count zeroes in the average.*/
    public void averageNonZero(Vector v)
        throws ArrayIndexOutOfBoundsException
    {
        int count;
        DoubleVector []dvs = new DoubleVector[v.size()];
        dvs=(DoubleVector[])v.toArray(dvs);
        for(int i=0; i<data.length; i++ ) {
            data[i]=0;
            count=0;
            for(int j=0; j<dvs.length; j++) {
                data[i]+=dvs[j].data[i];
                if (dvs[j].data[i]!=0)
                    count++;
            }
            if (count>0) data[i]/=count;
        }
    }   
    /**
     *Creates another double[], whose values are the same as these, except
     *they have been rescaled so that their absolute values add to one
     *(the values in this vector are not changed by this method, unlike
     *{@link normalize}.
     *@return The double[] of normalized values.
     */
    public double []normalizeReturnDoubleArray()
    {
        double rval[]=new double[data.length];
        double sum=0;
        int i;
        for(i=0; i<data.length; i++) {
            sum+=Math.abs(data[i]);
        }
        if (sum!=0) {
            for(i=0;i<data.length;i++) {
                rval[i]=data[i]/sum;
            }
        }
        return rval;
    }
    
    /**Moves a place just after the destination.*/
    public void movePlaceAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException
    {
        double mover;
        if (start==end)
            return;
        
        if ((start<0)||(start>=this.data.length)||(end<-1)||(end>=this.data.length))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            mover=data[start];
            for(int i=start; i < end; i++) {
                data[i]=data[i+1];
            }
            data[end]=mover;
        } else {
            mover=data[start];
            for(int i=start; i > (end+1); i--) {
                data[i]=data[i-1];
            }
            data[end+1]=mover;
        }
    }

    /**Returns a copy of the data in this double vector.*/
    public double []toArray() {
        double []rval = new double[data.length];
        System.arraycopy(data, 0, rval, 0, data.length);
        return rval;
    }
    
    public int size() {return data.length;}
    
    /**A testing main.*/
    public static void main(String args[]) {
        String fname = "rmme.xml";
        DoubleVector v = new DoubleVector(5);
        v.set(0, 3); v.set(1, 6.6); v.set(2, -12.4);
        Element elt = v.toElement("rvector");
        Document doc = new Document(elt);
        XMLOutputter outputter = new XMLOutputter();
        FileOutputStream fstream=null;
        try {
            fstream = new FileOutputStream(fname);
        } catch (Exception e) {
            System.out.println("Could not open "+fname+ " for output.");
            System.exit(1);
        }
        try {
            outputter.output(doc, System.out);
            outputter.output(doc, fstream);
            fstream.close();
        } catch (Exception e) {}
        SAXBuilder parser = new SAXBuilder();
        try {
            doc = parser.build(fname);
            elt = doc.getRootElement();
            DoubleVector tv = new DoubleVector(0);
            tv.fromElement(elt);
            System.out.println(tv.toString());
        } catch (Exception e) {
            System.out.println("A horrible error.");
            System.exit(1);
        }
    }
    
    public void moveAfter(int start, int end)
        throws ArrayIndexOutOfBoundsException 
    {
        double mover;
        if (start==end)
            return;
        if ((start<0)||(start>=data.length)||(end<-1)||(end>=data.length))
            throw new ArrayIndexOutOfBoundsException();
        if (end > start) {
            mover=data[start];
            for(int i=start; i < end; i++) {
                data[i]=data[i+1];
            }
            data[end]=mover;
        } else {
            mover=data[start];
            for(int i=start; i > (end+1); i--) {
                data[i]=data[i-1];
            }
            data[end+1]=mover;
        }
    }
    
    /**Assumes a Vector of DoubleVectors, and averages them, filling in this
     *with their values.  It does not count zeroes in the average.
     *This version scales v.get(i)'s values by priorities[i] first
     *(and then makes sure the scaling's add to one in each place).
     */
    public void averageNonZero(Vector v, double priorities[])
        throws ArrayIndexOutOfBoundsException
    {
        int count;
        double sum;
        DoubleVector []dvs = new DoubleVector[v.size()];
        if (priorities==null) {
            priorities=new double[v.size()];
            for(int i=0; i < priorities.length; i++)
                priorities[i]=1.0/priorities.length;
        }
        dvs=(DoubleVector[])v.toArray(dvs);
        for(int i=0; i<data.length; i++ ) {
            data[i]=0;
            sum=0;
            for(int j=0; j<dvs.length; j++) {
                data[i]+=dvs[j].data[i]*priorities[j];
                if (dvs[j].data[i]!=0)
                    sum+=priorities[j];
            }
            if (sum!=0) data[i]/=sum;
        }
    }
    
    /**Fills in the data with the eigenvector of the given matrix, however
     *the new sum of this vector is the same as the old (unless the old
     *sum was 0, in which case we make it 1).
     */
    public void fillInEigenScale(DoubleMatrix dsm, double error) throws ConvergenceException
    {
        if (dsm.getSize()<1) {
            this.data=new double[0];
            return;
        }
        if (dsm.isZero()||dsm.isId()) {
            this.data = new double[dsm.getSize()];
            return;
        }
        double data[]=dsm.largestEigenvectorWithFix(error);
        double sum = this.sum();
        this.data = new double[data.length -1];
        if (sum==0) sum=1;
        int size=data.length-1;
        for(int i=0; i<size; i++) {
            this.data[i]=data[i]/sum;
        }
    }
    
    /**Fills in the data with the eigenvector of the given matrix, idealized.
     */
    public void fillInEigenIdeal(DoubleMatrix dsm, double error) throws ConvergenceException
    {
        if (dsm.getSize()<1) {
            this.data=new double[0];
            return;
        }
        if (dsm.isZero()||dsm.isId()) {
            this.data = new double[dsm.getSize()];
            return;
        }
        double data[]=dsm.largestEigenvectorIdealWithFix(error);
        this.data = new double[data.length -1];
        System.arraycopy(data, 0, this.data, 0, this.data.length);
    }
    
    /**
     * Renormalizes a vector after changing one value.  Actually, if you send a scaleFactor
     * of 0, it will renormalize to whatever the last sum was, else it will make the new
     * vector sum to that value.  So, this method changes one place in an array of double
     * and makes the new array sum to scaleFactor (probably you'll want to use 1) or
     * to the previous sum if scaleFactor=0.
     * @param vec The vector whose position <b>place</b> you want to change to <b>newValue</b> and
     *then rescale.
     * @param place The place in the vector to change 
     * @param newValue The new value to put in that place
     * @param scaleFactor The value to make the vector sum to, or if you send 0, it will make the
     *new vector sum to the same as the old.
     */
    public static void changeAndRescale(double []vec, int place, double newValue, double scaleFactor)
    {
        if ((vec==null)||(vec.length==0)) return;
        if (scaleFactor==0) {
            double oldSum=0;
            for(int i=0; i<vec.length; i++)
                oldSum+=vec[i];
            scaleFactor=oldSum;
        }
        vec[place]=newValue;
        double partialSum=0;
        for(int i=0; i<vec.length; i++)
            if (i!=place)
                partialSum+=vec[i];
        double scale=(scaleFactor - newValue)/partialSum;
        if (partialSum==0) scale=0;
        for(int i=0; i<vec.length; i++)
            if (i!=place)
                vec[i]*=scale;
    }
    
    public static double geometricAverage(double []d) {
        int count=0;
        double rval=1;
        if (d==null) return 1;
        for(int i=0; i<d.length; i++) {
            if (d[i]!=0) {
                rval*=d[i];
                count++;
            }
        }
        if (count==0) return 1;
        return Math.pow(Math.abs(rval), 1.0/count);
    }
    
    public static double geometricAverage(DoubleVector dv) {
        if (dv==null) return 1;
        return geometricAverage(dv.data);
    }
}
