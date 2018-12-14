
package com.dlens.common2.interfaces;
import java.awt.Color;
import java.awt.Font;
import org.jdom.*;
import org.jdom.output.*;
import java.util.*;

import com.dlens.common2.exceptions.XMLFormatException;

import java.lang.reflect.*;

/**
 *
 * @author wjadams
 */
public class JDOMit {
    public static final String CLASS_ID="_CLASS_";
    /** Creates a new instance of JDOMit */
    public static boolean includeClassID=false;
    public JDOMit() {
    }
    
    public static Element toElement(Object o, String name) {
        try {
            JDOMable j = (JDOMable)o;
            if (o==null) {
                return newNullElement(name);
            }
            try {
                Element rval=j.toElement(name);
                if (includeClassID) setClassID(rval, o);
                return rval;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
                return null;
            }
        } catch (Exception e) {
            /*This means is wasn't JDOMable, let's see if we
              have one of the standard classes
             */
        }
        if (o==null) {
            return newNullElement(name);
        }
        Class cls=o.getClass();
        if (cls.equals(String.class)) {
            return toElement((String)o, name);
        } else if (cls.equals(Boolean.class)) {
            return toElement((Boolean)o, name);
        } else if (cls.equals(Double.class)) {
            return toElement((Double)o, name);
        } else if (cls.equals(Integer.class)) {
            return toElement((Integer)o, name);
        } else if (cls.equals(double[].class)) {
            return toElement((double[])o, name);
        } else if (cls.equals(int[].class)) {
            return toElement((int[])o, name);
        } else if (cls.equals(boolean[].class)) {
            return toElement((boolean[])o, name);
        } else if (cls.equals(Vector.class)) {
            return toElement((Vector)o, name);
        } else if (cls.equals(Hashtable.class)) {
            return toElement((Hashtable)o, name);
        } else if (cls.equals(String[].class)) {
            return toElement((String[])o, name);
        } else if (cls.equals(Color.class)) {
            return toElement((Color)o, name);
        } else if (cls.equals(Font.class)) {
            return toElement((Font)o, name);
        } else {
            System.out.println("Unknown type "+cls.getName());
            System.exit(0);
        }
        return null;
    }
    
    public static Element toElement(Color c, String name) {
        Element rval = new Element(name);
        rval.setAttribute("rgb", c.getRGB()+"");
        return rval;
    }
    
    public static Element toElement(Font f, String name) {
        Element rval = new Element(name);
        rval.setAttribute("name", f.getName());
        rval.setAttribute("size", f.getSize()+"");
        rval.setAttribute("style", f.getStyle()+"");
        return rval;
    }
    
    private static Element newNullElement(String name) {
        Element rval=new Element(name);
        rval.setAttribute("isNullObject", ""+true);
        if (includeClassID) setClassID(rval, null);
        return rval;
    }

    private static void setClassID(Element e, Object o) {
        if (o!=null) {
            e.setAttribute(CLASS_ID, o.getClass().getName());
        } else {
            e.setAttribute(CLASS_ID, "null");
        }
    }
    
    public static Element toElement(String s, String name) {
        Element rval=new Element(name);
        rval.setAttribute("data", s);
        if (includeClassID) setClassID(rval, s);
        return rval;
    }
    public static Element toElement(Boolean d, String name) {
        Element rval=new Element(name);
        rval.setAttribute("data", d.toString());
        if (includeClassID) setClassID(rval, d);
        return rval;
    }
    
    public static Element toElement(Double d, String name) {
        Element rval=new Element(name);
        rval.setAttribute("data", d.toString());
        if (includeClassID) setClassID(rval, d);
        return rval;
    }
    
    public static Element toElement(Integer i, String name) {
        Element rval=new Element(name);
        rval.setAttribute("data", i.toString());
        if (includeClassID) setClassID(rval, i);
        return rval;
    }
    
    public static Element toElement(double[] d, String name) {
        Element rval=new Element(name);
        StringBuffer buf = new StringBuffer();
        int len=d.length-1;
        for(int i=0; i<len; i++) {
            buf.append(d[i]); buf.append(" ");
        }
        if (len >= 0)
            buf.append(d[len]);
        rval.setAttribute("data", buf.toString());
        if (includeClassID) setClassID(rval, d);
        return rval;
    }
    
    public static Element toElement(Vector<Double> d, String name, Double justPassNullHere) {
        Element rval=new Element(name);
        StringBuffer buf = new StringBuffer();
        int len=d.size()-1;
        for(int i=0; i<len; i++) {
            buf.append(d.get(i)); buf.append(" ");
        }
        if (len >= 0)
            buf.append(d.get(len));
        rval.setAttribute("data", buf.toString());
        if (includeClassID) setClassID(rval, d);
        return rval;        
    }
    
    public static Element toElement(int[] d, String name) {
        Element rval=new Element(name);
        StringBuffer buf = new StringBuffer();
        int len=d.length-1;
        for(int i=0; i<len; i++) {
            buf.append(d[i]); buf.append(" ");
        }
        if (len >=0) 
            buf.append(d[len]);
        rval.setAttribute("data", buf.toString());
        if (includeClassID) setClassID(rval, d);
        return rval;
    }
    
    public static Element toElement(boolean[] d, String name) {
        Element rval=new Element(name);
        StringBuffer buf = new StringBuffer();
        int len=d.length-1;
        for(int i=0; i<len; i++) {
            buf.append(d[i]); buf.append(" ");
        }
        if (len >=0) 
            buf.append(d[len]);
        rval.setAttribute("data", buf.toString());
        if (includeClassID) setClassID(rval, d);
        return rval;
    }
    
    public static Element toElement(String[] d, String name) {
        Element rval=new Element(name);
        for(int i=0; i<d.length; i++) {
            rval.addContent(toElement((Object)d[i], "data"));
        }
        if (includeClassID) setClassID(rval, d);
        return rval;
    }
    
    public static boolean isElementNull(Element e) {
        if (Boolean.valueOf(e.getAttributeValue("isNullObject")).booleanValue()) {
            return true;
        } else {
            return false;
        }
    }
    
    public static Element toElement(List v, String name) {
        Element rval=new Element(name);
        for(int i=0; i<v.size(); i++) {
            Element tmp = toElement(v.get(i), "data");
            rval.addContent(tmp);
        }
        if (includeClassID) setClassID(rval, v);
        return rval;
    }
    
    public static Element toElement(List v, String name, ObjectToJDOM jdomer) {
        Element rval=new Element(name);
        for(int i=0; i<v.size(); i++) {
            rval.addContent(jdomer.objectToJDOM(v.get(i), "data"));
        }
        if (includeClassID) setClassID(rval, v);
        return rval;
    }
    
    private static Element toElement(Hashtable h, String name) {
        Element rval=new Element(name);
        Enumeration keys=h.keys();
        while(keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object val = h.get(key);
            Element tmp = new Element("entry");
            tmp.addContent(toElement(key, "key"));
            tmp.addContent(toElement(val, "val"));
            rval.addContent(tmp);
        }
        if (includeClassID) setClassID(rval, h);
        return rval;
    }
    
    public static Element toElement(Hashtable h, String name, ObjectToJDOM keyJDOMer, ObjectToJDOM valueJDOMer) {
        Element rval=new Element(name);
        Enumeration keys=h.keys();
        while(keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object val = h.get(key);
            Element tmp = new Element("entry");
            tmp.addContent(keyJDOMer.objectToJDOM(key, "key"));
            tmp.addContent(valueJDOMer.objectToJDOM(val, "val"));
            rval.addContent(tmp);
        }
        if (includeClassID) setClassID(rval, h);
        return rval;
    }
    
    
    public static String toXMLString(Object o) {
        try {
            java.io.StringWriter swriter = new java.io.StringWriter();
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(org.jdom.output.Format.getPrettyFormat());
            Document doc = new Document(toElement(o, o.getClass().getName()));
            outputter.output(doc, swriter);
            return swriter.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
    
    public static String toXMLStringElt(Element elt) {
        try {
            java.io.StringWriter swriter = new java.io.StringWriter();
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(org.jdom.output.Format.getPrettyFormat());
            Document doc = new Document(elt);
            outputter.output(doc, swriter);
            return swriter.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
    
    private static Class getClassFromElement(Element elt) throws XMLFormatException {
        String className = elt.getAttributeValue(CLASS_ID);
        if ((className==null)||className.equals(""))
            throw new XMLFormatException("No class given.");
        try {
            if (className.equals("null")) {
                return null;
            } else {
                return JDOMit.class.forName(className);
            }
        } catch (Exception e) {
            throw new XMLFormatException("No such class "+className);
        }
    }
    
    private static Object fromElement(Element elt) throws XMLFormatException {
        Class myClass = getClassFromElement(elt);
        if (myClass==null) {
            return null;
        }
        Class emptyParams[]=null, paramTypes[]={Element.class};
        Object params[]={elt};
        Constructor cn;
        try {
            cn=myClass.getConstructor(paramTypes);
            return cn.newInstance(params);
        } catch (Exception e) {
            //Can't create a new one from params
        }
        try {
            cn=myClass.getConstructor((Class[])null);
            Object rval = cn.newInstance((Object[])null);
            JDOMable j = (JDOMable)rval;
            j.fromElement(elt);
            return j;
        } catch (Exception e) {
            /*Either no empty constructor, or not JDOMable*/
        }
        if (myClass.equals(String.class)) {
            return fromElement((String)null, elt);
        } else if (myClass.equals(Boolean.class)) {
            return fromElement((Boolean)null, elt);
        } else if (myClass.equals(Double.class)) {
            return fromElement((Double)null, elt);
        } else if (myClass.equals(Integer.class)) {
            return fromElement((Integer)null, elt);
        } else if (myClass.equals(double[].class)) {
            return fromElement((double[])null, elt);
        } else if (myClass.equals(int[].class)) {
            return fromElement((int[])null, elt);
        } else if (myClass.equals(Vector.class)) {
            return fromElement((Vector)null, elt);
        } else if (myClass.equals(Hashtable.class)) {
            return fromElement((Hashtable)null, elt);
        }
        throw new XMLFormatException("Cannot handle class "+myClass.getName());
    }
    
    public static String fromElement(String s, Element elt) throws XMLFormatException {
        String rval = elt.getAttributeValue("data");
        return rval;
    }
    
    public static Boolean fromElement(Boolean s, Element elt) throws XMLFormatException {
        String rval = elt.getAttributeValue("data");
        try {
            return Boolean.valueOf(rval);
        } catch (Exception e) {
            throw new XMLFormatException("Illegal boolean value "+rval);
        }
    }
    
    public static Double fromElement(Double s, Element elt) throws XMLFormatException {
        String rval = elt.getAttributeValue("data");
        try {
            return new Double(Double.parseDouble(rval));
        } catch (Exception e) {
            throw new XMLFormatException("Illegal double value "+rval);
        }
    }
    
    public static Integer fromElement(Integer i, Element elt) throws XMLFormatException {
        String rval = elt.getAttributeValue("data");
        try {
            return new Integer(Integer.parseInt(rval));
        } catch (Exception e) {
            throw new XMLFormatException("Illegal integer value "+rval);
        }
    }
    
    public static double[] fromElement(double[]d, Element elt) throws XMLFormatException {
        String data = elt.getAttributeValue("data");
        data=data.trim();
        String datas[]=data.split(" ");
        if (data.length()==0)
            datas=new String[0];
        double []rval = new double[datas.length];
        for(int i=0; i<datas.length; i++) {
            try {
                rval[i]=Double.parseDouble(datas[i]);
            } catch (Exception e) {
                throw new XMLFormatException("Illegal double[] value "+datas[i]);
            }
        }
        return rval;
    }
    
    public static Vector<Double> fromElement(Vector<Double>d, Double passNullHere, Element elt) throws XMLFormatException {
        String data = elt.getAttributeValue("data");
        data=data.trim();
        String datas[]=data.split(" ");
        if (data.length()==0)
            datas=new String[0];
        Vector<Double> rval=new Vector<Double>(datas.length);
        for(int i=0; i<datas.length; i++) {
            try {
                rval.add(Double.parseDouble(datas[i]));
            } catch (Exception e) {
                throw new XMLFormatException("Illegal double value "+datas[i]);
            }
        }
        return rval;
    }
    
    public static int[] fromElement(int[]d, Element elt) throws XMLFormatException {
        String data = elt.getAttributeValue("data");
        data=data.trim();
        String datas[]=data.split(" ");
        if (data.length()==0)
            datas=new String[0];
        int []rval = new int[datas.length];
        for(int i=0; i<datas.length; i++) {
            try {
                rval[i]=Integer.parseInt(datas[i]);
            } catch (Exception e) {
                throw new XMLFormatException("Illegal int[] value "+datas[i]);
            }
        }
        return rval;
    }
    
    public static boolean[] fromElement(boolean[]d, Element elt) throws XMLFormatException {
        String data = elt.getAttributeValue("data");
        data=data.trim();
        String datas[]=data.split(" ");
        if (data.length()==0)
            datas=new String[0];
        boolean []rval = new boolean[datas.length];
        for(int i=0; i<datas.length; i++) {
            rval[i]=Boolean.valueOf(datas[i]).booleanValue();
        }
        return rval;
    }
    
    private static Vector fromElement(Vector v, Element elt) throws XMLFormatException {
        Vector rval = new Vector();
        Iterator it = elt.getChildren("data").iterator();
        while(it.hasNext()) {
            rval.add(fromElement((Element)it.next()));
        }
        return rval;
    }
    
    public static Vector fromElement(Vector v, Element elt, Class dataClass) throws XMLFormatException {
        Vector rval = new Vector();
        Iterator it = elt.getChildren("data").iterator();
        try {
            Constructor con=dataClass.getConstructor((Class[])null);
            while(it.hasNext()) {
                Element a = (Element)it.next();
                if (isElementNull(a)) {
                    rval.add(null);
                } else {
                    JDOMable tmp = (JDOMable)con.newInstance((Object[])null);
                    tmp.fromElement(a);
                    rval.add(tmp);
                }
            }
        } catch (Exception e) {
            throw new XMLFormatException(e);
        }
        return rval;
    }
    
    private static Hashtable fromElement(Hashtable v, Element elt) throws XMLFormatException {
        Hashtable rval = new Hashtable();
        Iterator it = elt.getChildren("entry").iterator();
        while(it.hasNext()) {
            Element entry = (Element)it.next();
            Element key = entry.getChild("key");
            Element val = entry.getChild("val");
            if (key==null)
                throw new XMLFormatException("No key given for a Hashtable entry.");
            if (val==null)
                throw new XMLFormatException("No value given for a Hashtable entry.");
            rval.put(fromElement(key), fromElement(val));
        }
        return rval;
    }
    
    private static Hashtable fromElement(Hashtable v, Element elt, Class keyClass, Class valClass)
        throws XMLFormatException 
    {
        Hashtable rval = new Hashtable();
        Iterator it = elt.getChildren("entry").iterator();
        Constructor keyCon, valCon;
        try {
            keyCon=keyClass.getConstructor((Class[])null);
            valCon=valClass.getConstructor((Class[])null);
        } catch (Exception e) {
            throw new XMLFormatException(e);
        }
        while(it.hasNext()) {
            Element entry = (Element)it.next();
            Element key = entry.getChild("key");
            Element val = entry.getChild("val");
            if (key==null)
                throw new XMLFormatException("No key given for a Hashtable entry.");
            if (val==null)
                throw new XMLFormatException("No value given for a Hashtable entry.");
            try {
                JDOMable keyO = (JDOMable)keyCon.newInstance((Object[])null);
                JDOMable valO = (JDOMable)valCon.newInstance((Object[])null);
                keyO.fromElement(key);
                valO.fromElement(val);
                rval.put(keyO, valO);
            } catch (Exception e) {
                throw new XMLFormatException(e);
            }
        }
        return rval;
    }
    
    public static Hashtable fromElement(Hashtable v, Element elt, Class valClass)
        throws XMLFormatException 
    {
        Hashtable rval = new Hashtable();
        Iterator it = elt.getChildren("entry").iterator();
        Constructor valCon;
        try {
            valCon=valClass.getConstructor((Class[])null);
        } catch (Exception e) {
            throw new XMLFormatException(e);
        }
        while(it.hasNext()) {
            Element entry = (Element)it.next();
            Element key = entry.getChild("key");
            Element val = entry.getChild("val");
            if (key==null)
                throw new XMLFormatException("No key given for a Hashtable entry.");
            if (val==null)
                throw new XMLFormatException("No value given for a Hashtable entry.");
            try {
                String keyO = fromElement((String)null, key);
                JDOMable valO = (JDOMable)valCon.newInstance((Object[])null);
                valO.fromElement(val);
                rval.put(keyO, valO);
            } catch (Exception e) {
                e.printStackTrace();
                throw new XMLFormatException(e);
            }
        }
        return rval;
    }
    
    public static double getDouble(Element elt, String attributeName)
        throws XMLFormatException
    {
        String data = elt.getAttributeValue(attributeName);
        if ((data==null)||(data.equals("")))
            throw new XMLFormatException("No value given to mandatory double "+attributeName);
        try {
            return Double.parseDouble(data);
        } catch (Exception e) {
            throw new XMLFormatException("Illegal double value "+data+" given to mandatory double "+attributeName);
        }
    }
    
    public static void putDouble(Element elt, String attributeName, Double val)
    {
        if (val==null) {
            elt.setAttribute(attributeName, "");
        } else {
            elt.setAttribute(attributeName, val.toString());
        }
    }
    
    public static Double getDDouble(Element elt, String attributeName)
        throws XMLFormatException
    {
        String data = elt.getAttributeValue(attributeName);
        if ((data==null)|| (data.equals(""))) {
            return null;
        } else {
            try {
                return Double.valueOf(data);
            } catch (NumberFormatException e) {
                throw new XMLFormatException("Double value was illegal "+data);
            }
        }
    }
    
    public static int getInt(Element elt, String attributeName)
    throws XMLFormatException
{
    String data = elt.getAttributeValue(attributeName);
    if ((data==null)||(data.equals("")))
        throw new XMLFormatException("No value given to mandatory int "+attributeName);
    try {
        return Integer.parseInt(data);
    } catch (Exception e) {
        throw new XMLFormatException("Illegal int value "+data+" given to mandatory int "+attributeName);
    }
}


    public static void main(String args[]) {
        try {
            Vector v = new Vector();
            v.add(new Double(1)); v.add(new Double(2)); v.add(new Integer(35)); v.add(new Boolean("false")); v.add(new Boolean("true"));
            double d[]={5,4,3,2,-.2343439};
            v.add(d);
            int i[]={-10, 0, 25, 10005};
            v.add(i);
            v.add(null);
            System.out.println(toXMLString(v));
            Element e = toElement(v, "myVec");
            Vector v1 = (Vector)fromElement(e);
            System.out.println(toXMLString(v1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Vector fromElement(Vector v, Element elt, JDOMToObject jdomer) throws XMLFormatException 
    {
        Vector rval = new Vector();
        Iterator it = elt.getChildren("data").iterator();
        while(it.hasNext()) {
            Element a = (Element)it.next();
            if (isElementNull(a)) {
                rval.add(null);
            } else {
                Object tmp = jdomer.objectFromJDOM(a);
                rval.add(tmp);
            }
        }
        return rval;
        
    }

    public static Hashtable fromElement(Hashtable v, Element elt, JDOMToObject jdomer)
        throws XMLFormatException 
    {
        Hashtable rval = new Hashtable();
        Iterator it = elt.getChildren("entry").iterator();
        while(it.hasNext()) {
            Element entry = (Element)it.next();
            Element key = entry.getChild("key");
            Element val = entry.getChild("val");
            if (key==null)
                throw new XMLFormatException("No key given for a Hashtable entry.");
            if (val==null)
                throw new XMLFormatException("No value given for a Hashtable entry.");
            try {
                String keyO = fromElement((String)null, key);
                JDOMable valO=null;
                if (!isElementNull(val)) {
                    valO = (JDOMable)jdomer.objectFromJDOM(val);
                }
                rval.put(keyO, valO);
            } catch (Exception e) {
                e.printStackTrace();
                throw new XMLFormatException(e);
            }
        }
        return rval;
    }
    
    public static Hashtable fromElement(Hashtable v, Element elt, JDOMToObject keyJDOMer, JDOMToObject valueJDOMer)
        throws XMLFormatException
    {
        Hashtable rval = new Hashtable();
        Iterator it = elt.getChildren("entry").iterator();
        while(it.hasNext()) {
            Element entry = (Element)it.next();
            Element key = entry.getChild("key");
            Element val = entry.getChild("val");
            if (key==null)
                throw new XMLFormatException("No key given for a Hashtable entry.");
            if (val==null)
                throw new XMLFormatException("No value given for a Hashtable entry.");
            try {
                Object keyO = null;
                if (!isElementNull(key))
                    keyO= keyJDOMer.objectFromJDOM(key);
                Object valO=null;
                if (!isElementNull(val)) {
                    valO = valueJDOMer.objectFromJDOM(val);
                }
                rval.put(keyO, valO);
            } catch (XMLFormatException xe) {
                throw xe;
            } catch (Exception e) {
                e.printStackTrace();
                throw new XMLFormatException(e);
            }
        }
        return rval;
    }
    
    public static boolean getBoolean(Element elt, String attributeName)
        throws XMLFormatException
    {
        String data = elt.getAttributeValue(attributeName);
        return Boolean.valueOf(data).booleanValue();
    }
    
    public static String toString(Boolean b) {
        if (b==null) {
            return "";
        } else {
            return ""+b;
        }
    }
    
    public static long getLong(Element elt, String att) 
    	throws XMLFormatException
    {
    	String data=elt.getAttributeValue(att);
    	if ((data==null)||(data.equals("")))
    		throw new XMLFormatException("No value given to attribute "+att);
    	try {
    		return Long.parseLong(data);
    	} catch (Exception e) {
    		throw new XMLFormatException("Illegal long value "+data);
    	}
    }
    
    public static Long getLLong(Element elt, String attributeName)
			throws XMLFormatException {
		String data = elt.getAttributeValue(attributeName);
		if ((data == null) || (data.equals("")))
			throw new XMLFormatException("No value given to mandatory int "
					+ attributeName);
		try {
			return Long.parseLong(data);
		} catch (Exception e) {
			throw new XMLFormatException("Illegal int value " + data
					+ " given to mandatory int " + attributeName);
		}
	}
    
    public static Boolean getBBoolean(Element elt, String att)
    {
        String data=elt.getAttributeValue(att);
        if (data==null) return null;
        if (data.equals("")) return null;
        return Boolean.valueOf(data);
    }

    public static String[] fromElement(String[] d, Element elt) throws XMLFormatException
    {
        List list = elt.getChildren("data");
        Iterator it = list.iterator();
        String rval[] = new String[list.size()];
        try {
            int i=0;
            while(it.hasNext()) {
                Element a = (Element)it.next();
                if (isElementNull(a)) {
                    rval[i]=null;
                } else {
                    rval[i]=fromElement((String)null, a);
                }
                i++;
            }
        } catch (Exception e) {
            throw new XMLFormatException(e);
        }
        return rval;
        
    }
    
    public static Color fromElement(Color c, Element elt) throws XMLFormatException
    {
        int rgb=getInt(elt, "rgb");        
        return new Color(rgb, true);
    }
    
    public static Font fromElement(Font f, Element elt) throws XMLFormatException
    {
        int size=getInt(elt, "size");
        int style=getInt(elt, "style");
        String name=elt.getAttributeValue("name");
        if ((name==null)||(name.equals("")))
            throw new XMLFormatException("No name given to font.");
        return new Font(name, style, size);
    }
}
