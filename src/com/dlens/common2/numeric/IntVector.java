
package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMable;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.util.*;
import java.io.*;

/**
 * A simple class that wraps around an int[], that allows it to grow/shrink, save
 *and restore from <code>jdom</code> elements, etc.
 * @author  dlens
 */
public class IntVector implements JDOMable {
    /**The internal data of the vector.*/
    private int[] data;
    
    /** Creates a new instance of IntVector of the given size. 
     *@param size The size of the vector to create.  If size is less than zero, we
     *reset it to zero.
     */
    public IntVector(int size) {
        if (size < 0) size = 0;
        data = new int [size];
    }
    
    /**
     *Constructs a copy of the vector given as a parameter.
     *@param src The vector to copy from .
     */
    public IntVector(IntVector src) {
        int size=src.data.length;
        data = new int[size];
        System.arraycopy(src.data, 0, data, 0, size);
    }
    
    
    /**
     *Removes a place from our vector.
     *@param place The place to remove.
     *@throws ArrayIndexOutOfBoundsException If the place was out of bounds.
     */
    public void rmPlace(int place) 
        throws ArrayIndexOutOfBoundsException
    {
        if ((place < 0) || (place >= data.length)) {
            throw new ArrayIndexOutOfBoundsException(place);
        }
        int newData[] = new int[data.length-1];
        System.arraycopy(data, 0, newData, 0, place);
        System.arraycopy(data, place+1, newData, place, newData.length - place);
        data=null;
        data=newData;
    }
    
    /**
     *Inserts a place at the end of our vector.
     *@return The index of the place added.
     */
    public int addPlace() {
        int newData[] = new int[data.length+1];
        System.arraycopy(data, 0, newData, 0, data.length);
        data=null;
        data=newData;
        return data.length-1;
    }
    
    /**Adds an integer to the end of our vector.*/
    public void add(int i) {
        addPlace();
        data[data.length-1]=i;
    }
    
    /**Returns a copy of the int[] backing this.*/
    public int[] toArray() {
        int newData[] = new int[data.length];
        System.arraycopy(data, 0, newData, 0, data.length);
        return newData;
    }
    
    /**
     *Sets a place in our vector.
     *@param place The place to set.
     *@param val The value to put in that place.
     *@throws ArrayIndexOutOfBoundsException If the place was out of bounds.
     */
    public void set(int place, int val) 
        throws ArrayIndexOutOfBoundsException
    {
        data[place]=val;
    }
    
    /**Retrieves the data from a place in our vector.
     *@param place The place to retrieve data from.
     *@throws ArrayIndexOutOfBoundsException If the place was out of bounds.
     */
    public int get(int place) throws ArrayIndexOutOfBoundsException
     {
        return data[place];
    }
    
    /**A useful debugging method, which constructs a pretty string representing
     *this vector.  It is useful for printing to stdout.  It is not useful for saving/restoring.
     *For that, please see {@link fromElement} and {@link toElement}.
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
     *Creates a <code>jdom</code> element representing this vector.  Useful for saving/restoring
     *this vector.  Use {@link fromElement} to restoring from a <code>jdom</code> element.
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
     *Creates a node representing this a vector of Integers for a jdom.
     *@param name The name to give the <code>jdom</code> element.
     *@param v The vector, whose objects should be <code>Integer</code>'s.
     *This vector is who we are storing to an element.
     *@return The <code>jdom</code> element representing the integer Vector
     *<code>v</code>.
     */
    public static Element toElement(String name, Vector v)
    {
        Element rval=new Element(name);
        int size = v.size();
        rval.setAttribute("size", ""+size);
        for (int i=0; i < size; i++) {
            try {
                rval.addContent("\n"+String.valueOf((Integer)v.get(i)));
            } catch (Exception e) {}
        }
        return rval;
    }
    
    /**
     *Restructures a vector based on a jdom element.
     *@param element The jdom element.
     *@throws XMLFormatException If the element is not in the correct format.
     */
    public void fromElement(Element element)
        throws XMLFormatException
    {
        String size=element.getAttributeValue("size");
        String text = element.getTextTrim();
        if (text.equals("")) {
            this.data = new int[0];
            return;
        }
        int sizeInt=0;
        if ((size != null) && (!"".equals(size))) {
            try {
                sizeInt = Integer.parseInt(size);
                if (sizeInt == 0) {
                    this.data = new int[0];
                    return;
                }
            } catch (NumberFormatException e) {
                throw new XMLFormatException("While parsing a vector, size attribute ="+size+" was not an integer.");
            }
        }
        String numbers[] = text.split("\\s+");
        //System.out.println("Parsed '"+text+"' to have "+numbers.length+" elements.");
        //if (numbers.length > sizeInt) sizeInt = numbers.length;
        data=new int[sizeInt];
        for(int i = 0; i< sizeInt; i++) {
            try {
                data[i]=Integer.parseInt(numbers[i]);
            } catch (Exception ignored) {}
        }
    }
    
    /**
     *This is useful if you have a <tt>java.util.Vector</tt> of <tt>Integer</tt>'s.  It
     *will read in an xml node similar to the last, and fill in your vector with
     *it's values.
     *@param element The xml element.
     *@param vec The vector to fill in.
     *@throws XMLFormatException If the element is not in the correct format.
     */
    public static void fromElement(Element elt, java.util.Vector vec)
        throws XMLFormatException
    {
        IntVector v = new IntVector(0);
        v.fromElement(elt);
        vec.clear();
        vec.setSize(0);
        for(int i = 0; i < v.data.length; i++) {
            vec.addElement(new Integer(v.data[i]));
        }
        //System.out.println("FromElement for Vector gave.");
        //System.out.println(vec.toString());
    }
    
    /**A useful testing main.*/
    public static void main(String args[]) {
        String fname = "rmme.xml";
        IntVector v = new IntVector(5);
        v.set(0, 3); v.set(1, 6); v.set(2, -12);
        Element elt = v.toElement("vector");
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
            IntVector tv = new IntVector(0);
            tv.fromElement(elt);
            System.out.println(tv.toString());
        } catch (Exception e) {
            System.out.println("A horrible error.");
            System.exit(1);
        }
    }
    
    public int size() {
        return data.length;
    }

    public void moveAfter(int start, int end)
    {
        VectorMoving.vectorMoveAfter(data, start, end);
    }
    
    public void set(int val) {
        for(int i=0; i<data.length; i++)
            data[i]=val;
    }
}
