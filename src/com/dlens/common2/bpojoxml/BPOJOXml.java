package com.dlens.common2.bpojoxml;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.JDOMable;
import com.dlens.common2.interfaces.ObjectToJDOM;

/**
 * My class to handle converting pojo to xml and the other way
 * around.
 * @author Bill Adams
 *
 */
public class BPOJOXml {
	private static final String SINGLE_VALUE_ATT = "value";
	private static final String CLASS_ATT = "class";
	private static final String LIST_ELT_NAME = "li";
	private static final String IS_NULL_ATT = "isNull";
	private static Map<Class<?>, ToFromElement> KNOWN_CLASSES = new HashMap<Class<?>, BPOJOXml.ToFromElement>();
	private static Map<Class<?>, ToFromElement> KNOWN_INTERFACES = new HashMap<Class<?>, ToFromElement>();
	private static Map<ClassPropertyTester, ToFromElement> KNOWN_CLASS_TESTER = new HashMap<BPOJOXml.ClassPropertyTester, BPOJOXml.ToFromElement>();
	static {
		//Initialization code goes here.
		//In particular registering the handling of double, int, float, long, and other things
		//I cannot yet imagine :)
		registerKnownClass(Double.class, doubleToFromElement());
		registerKnownClass(Integer.class, integerToFromElement());
		registerKnownClass(String.class, stringToFromElement());
		registerKnownClass(Boolean.class, booleanToFromElement());
		registerKnownInterface(List.class, listToFromElement());
		registerKnownTester(
				new ClassPropertyTester() {
					public boolean check(Class<?> clazz) {
						return isClassNonPrimitiveArray(clazz);
					}
				}, 
				arrayToFromElement()
		);
		registerKnownTester(
				new ClassPropertyTester() {
					public boolean check(Class<?> clazz) {
						return isClassPrimitiveArray(clazz);
					}
				},
				primitiveArrayToFromElement()
		);
	}
	
	{
	}
	
	public static void registerKnownClass(Class<?> clazz, ToFromElement toFrom) {
		if (KNOWN_CLASSES.containsKey(clazz))
			throw new IllegalArgumentException("Already know about class "+clazz.getName());
		KNOWN_CLASSES.put(clazz, toFrom);
	}
	
	protected static boolean isClassNonPrimitiveArray(Class<?> clazz) {
		if (!clazz.isArray())
			return false;
		return !clazz.getComponentType().isPrimitive();
	}

	protected static boolean isClassPrimitiveArray(Class<?> clazz) {
		if (!clazz.isArray())
			return false;
		return clazz.getComponentType().isPrimitive();
	}

	public static void registerKnownInterface(Class<?> clazz, ToFromElement toFrom) {
		if (KNOWN_INTERFACES.containsKey(clazz))
			throw new IllegalArgumentException("Already know about class "+clazz.getName());
		KNOWN_INTERFACES.put(clazz, toFrom);
	}
	
	public static void registerKnownTester(ClassPropertyTester cpt, ToFromElement toFrom) {
		KNOWN_CLASS_TESTER.put(cpt, toFrom);
	}
	
	private static ToFromElement doubleToFromElement() {
		JDOMToObject j2o = jdomToDouble();
		ObjectToJDOM o2j = primitiveToJDOM();
		return new ToFromElement(o2j, j2o);
	}

	private static ToFromElement integerToFromElement() {
		JDOMToObject j2o = jdomToInteger();
		ObjectToJDOM o2j = primitiveToJDOM();
		return new ToFromElement(o2j, j2o);
	}
	
	private static ToFromElement booleanToFromElement() {
		JDOMToObject j2o = jdomToBoolean();
		ObjectToJDOM o2j = primitiveToJDOM();
		return new ToFromElement(o2j, j2o);
	}
	
	private static ToFromElement stringToFromElement() {
		JDOMToObject j2o = jdomToString();
		ObjectToJDOM o2j = primitiveToJDOM();
		return new ToFromElement(o2j, j2o);
	}
	
	private static ToFromElement listToFromElement() {
		JDOMToObject j2o = jdomToList();
		ObjectToJDOM o2j = listToJDOM();
		return new ToFromElement(o2j, j2o);
	}

	private static ToFromElement arrayToFromElement() {
		JDOMToObject j2o = jdomToArray();
		ObjectToJDOM o2j = arrayToJDOM();
		return new ToFromElement(o2j, j2o);
	}

	private static ToFromElement primitiveArrayToFromElement() {
		JDOMToObject j2o = jdomToPrimitiveArray();
		ObjectToJDOM o2j = primitiveArrayToJDOM();
		return new ToFromElement(o2j, j2o);
	}

//	public Element toElement(String eltName, double d_val) {
//		Element rval = toElementSimpleValue(eltName, Double.toString(d_val));
//		setType(rval, double.class);
//		return rval;
//	}
//	
//	public static Element toElement(String eltName, int d_val) {
//		Element rval = toElementSimpleValue(eltName, Integer.toString(d_val));
//		setType(rval, int.class);
//		return rval;
//	}
//	
	private static void setType(Element rval, Class<?> class1) {
		String key = class1.getName();
		if (key==null)
			throw new UnsupportedOperationException("Class "+class1.getName()+" unknown");
		rval.setAttribute(CLASS_ATT, key);
	}

	private static String getType(Element elt) {
		String rval = elt.getAttributeValue(CLASS_ATT);
		return rval;
	}
	
	private static Element toElementSimpleValue(String name, String val) {
		Element elt = newElement(name);
		elt.setAttribute(SINGLE_VALUE_ATT, val);
		return elt;
	}
	
	public static Element toElement(String name, Object val) {
		Class<?> knownIterface;
		ToFromElement testerToFrom;
		if (val==null) {
			return toElementNull(name);
		} else if (val instanceof JDOMable){
				Element rval = ((JDOMable)val).toElement(name);
				setType(rval, val.getClass());
				return rval;
		} else if (val.getClass().isPrimitive()) {
			return toElementPrimitive(name, val);
		} else if (isKnownClass(val)) {
			return KNOWN_CLASSES.get(val.getClass()).objectToJDOM.objectToJDOM(val, name);
		} else if ((testerToFrom = getKnownTesterFor(val.getClass()))!=null) {
			return testerToFrom.objectToJDOM.objectToJDOM(val, name);
		} else if ((knownIterface=getKnownInterfaceFor(val.getClass()))!=null) {
			return KNOWN_INTERFACES.get(knownIterface).objectToJDOM.objectToJDOM(val, name);
		} else {
			throw new IllegalArgumentException("Trying to toElement something not jdomable and not of known type");
		}
	}
	
	private static Element toElementPrimitive(String name, Object val) {
		Element rval=toElementSimpleValue(name, val.toString());
		setType(rval, val.getClass());
		return rval;
	}

	private static boolean isKnownClass(Object val) {
		return KNOWN_CLASSES.containsKey(val.getClass());
	}
	
	private static Class<?> getKnownInterfaceFor(Class<?> clazz) {
		Iterator<Class<?>> classIterator = KNOWN_INTERFACES.keySet().iterator();
		while (classIterator.hasNext()) {
			Class<?> next = classIterator.next();
			if (next.isAssignableFrom(clazz)) {
				return next;
			}
		}
		return null;
	}

	private static ToFromElement getKnownTesterFor(Class<?> clazz) {
		Iterator<ClassPropertyTester> classIterator = KNOWN_CLASS_TESTER.keySet().iterator();
		while (classIterator.hasNext()) {
			ClassPropertyTester cpt = classIterator.next();
			if (cpt.check(clazz)) {
				return KNOWN_CLASS_TESTER.get(cpt);
			}
		}
		return null;
	}

	private static Element toElementNull(String name) {
		Element elt = new Element(name);
		elt.setAttribute(IS_NULL_ATT, "true");
		return elt;
	}

	private static boolean isNullElt(Element elt) {
		String bval = elt.getAttributeValue(IS_NULL_ATT);
		if ((bval!=null) && (Boolean.parseBoolean(bval))) {
			return true;
		} else {
			return false;
		}
	}
	private static String getSimpleValue(Element elt) {
		return elt.getAttributeValue(SINGLE_VALUE_ATT);
	}
	
	private static Element newElement(String name) {
		return new Element(name);
	}

	public static Object fromElement(Element elt) throws XMLFormatException {
		if (isNullElt(elt)) {
			return null;
		}
		Class<?> clazz = getTypeClass(elt);
		Class<?> knowInterface;
		ToFromElement toFrom;
		if (JDOMable.class.isAssignableFrom(clazz)) {
			try {
				JDOMable rval = (JDOMable) clazz.getConstructor().newInstance();
				rval.fromElement(elt);
				return rval;
			} catch (Exception e) {
				throw new XMLFormatException(e);
			}
		} else if (clazz.isPrimitive()) {
			return fromElementPrimitive(elt, clazz);
		} else if (KNOWN_CLASSES.containsKey(clazz)) {
			return KNOWN_CLASSES.get(clazz).jdomToObject.objectFromJDOM(elt);
		} else if ((toFrom = getKnownTesterFor(clazz))!=null) {
			return toFrom.jdomToObject.objectFromJDOM(elt);
		} else if ((knowInterface = getKnownInterfaceFor(clazz))!=null) {
			return KNOWN_INTERFACES.get(knowInterface).jdomToObject.objectFromJDOM(elt);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private static Class<?> getTypeClass(Element elt) throws XMLFormatException {
		String type = getType(elt);
		try {
			return Class.forName(type);
		} catch (ClassNotFoundException e) {
			throw new XMLFormatException("Could not find class "+type);
		}
	}

	private static Object fromElementPrimitive(Element elt, Class<?> clazz) {
		String data = getSimpleValue(elt);
		if (clazz.equals(double.class)) {
			return Double.parseDouble(data);
		} else if (clazz.equals(int.class)) {
			return Integer.parseInt(data);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	

	public static Element toElementList(String eltName, Iterable<?> aList) {
		Element rval = new Element(eltName);
		for(Object el : aList) {
			rval.addContent(toElement(LIST_ELT_NAME, el));
		}
		setType(rval, aList.getClass());
		return rval;
	}

	public static Element toElementArray(String eltName, Object aList) {
		Element rval = new Element(eltName);
		int size = Array.getLength(aList);
		for(int i=0; i < size; i++) {
			rval.addContent(toElement(LIST_ELT_NAME, Array.get(aList, i)));
		}
		setType(rval, aList.getClass());
		return rval;
	}

	public static Element toElementPrimitiveArray(String eltName, Object aList) {
		Element rval;
		Class componentType = aList.getClass().getComponentType();
		if (componentType.equals(double.class)) {
			rval = toElementdoubleArray(eltName, (double[])aList);
		} else if (componentType.equals(int.class)) {
			rval = toElementintArray(eltName, (int[])aList);
		} else if (componentType.equals(boolean.class)) {
			rval = toElementbooleanArray(eltName, (boolean[])aList);
		} else {
			throw new UnsupportedOperationException("Cannot handle primitive array class of type "+componentType.getName());
		}
		return rval;
	}

	private static Element toElementdoubleArray(String eltName, double[] aList) {
		if (aList==null)
			return toElementNull(eltName);
		Element rval = new Element(eltName);
		for(int i=0; i < aList.length; i++) {
			Element elt = toElementSimpleValue(LIST_ELT_NAME, Double.toString(aList[i]));
			setType(elt, double.class);
			rval.addContent(elt);
		}
		setType(rval, aList.getClass());
		return rval;
	}

	private static Element toElementbooleanArray(String eltName, boolean[] aList) {
		if (aList==null)
			return toElementNull(eltName);
		Element rval = new Element(eltName);
		for(int i=0; i < aList.length; i++) {
			Element elt = toElementSimpleValue(LIST_ELT_NAME, Boolean.toString(aList[i]));
			setType(elt, boolean.class);
			rval.addContent(elt);
		}
		setType(rval, aList.getClass());
		return rval;
	}

	private static Element toElementintArray(String eltName, int[] aList) {
		if (aList==null)
			return toElementNull(eltName);
		Element rval = new Element(eltName);
		for(int i=0; i < aList.length; i++) {
			Element elt = toElementSimpleValue(LIST_ELT_NAME, Integer.toString(aList[i]));
			setType(elt, int.class);
			rval.addContent(elt);
		}
		setType(rval, aList.getClass());
		return rval;
	}

	private static ObjectToJDOM listToJDOM() {
		return new ObjectToJDOM() {
			@Override
			public Element objectToJDOM(Object object, String name) {
				return toElementList(name, (Iterable<?>) object);
			}
		};
	}
	private static ObjectToJDOM arrayToJDOM() {
		return new ObjectToJDOM() {
			@Override
			public Element objectToJDOM(Object object, String name) {
				return toElementArray(name, object);
			}
		};
	}
	private static ObjectToJDOM primitiveArrayToJDOM() {
		return new ObjectToJDOM() {
			@Override
			public Element objectToJDOM(Object object, String name) {
				return toElementPrimitiveArray(name, object);
			}
		};
	}
	private static JDOMToObject jdomToDouble() {
		return new JDOMToObject() {
			@Override
			public Object objectFromJDOM(Element elt) throws XMLFormatException {
				String data = getSimpleValue(elt);
				try {
					return Double.parseDouble(data);
				} catch (NumberFormatException e) {
					throw new XMLFormatException(e);
				}
			}
		};
	}
	
	private static JDOMToObject jdomToInteger() {
		return new JDOMToObject() {
			@Override
			public Object objectFromJDOM(Element elt) throws XMLFormatException {
				String data = getSimpleValue(elt);
				try {
					return Integer.parseInt(data);
				} catch (NumberFormatException e) {
					throw new XMLFormatException(e);
				}
			}
		};
	}
	
	private static JDOMToObject jdomToBoolean() {
		return new JDOMToObject() {
			@Override
			public Object objectFromJDOM(Element elt) throws XMLFormatException {
				String data = getSimpleValue(elt);
				return Boolean.parseBoolean(data);
			}
		};
	}
	
	private static JDOMToObject jdomToString() {
		return new JDOMToObject() {
			@Override
			public Object objectFromJDOM(Element elt) throws XMLFormatException {
				return getSimpleValue(elt);
			}
		};
	}
	
	private static JDOMToObject jdomToList() {
		return new JDOMToObject() {
			@Override
			public Object objectFromJDOM(Element elt) throws XMLFormatException {
				Class<?> clazz = getTypeClass(elt);
				if (!List.class.isAssignableFrom(clazz))
					throw new XMLFormatException(clazz.getName()+" is not a List");
				return fromElementList(elt, clazz);
			}
		};
	}

	private static JDOMToObject jdomToArray() {
		return new JDOMToObject() {
			@Override
			public Object objectFromJDOM(Element elt) throws XMLFormatException {
				return fromElementArray(elt);
			}
		};
	}

	private static JDOMToObject jdomToPrimitiveArray() {
		return new JDOMToObject() {
			@Override
			public Object objectFromJDOM(Element elt) throws XMLFormatException {
				return fromElementPrimitiveArray(elt);
			}
		};
	}

	protected static Object fromElementArray(Element elt) throws XMLFormatException {
		Class<?> typeClass = getTypeClass(elt);
		Class<?> componentType = typeClass.getComponentType();
		List<Element> children = elt.getChildren(LIST_ELT_NAME);
		int size=children.size();
		Object rval = Array.newInstance(componentType, size);
		for(int i=0; i < size; i++) {
			Element child = children.get(i);
			Array.set(rval, i, fromElement(child));
		}
		return rval;
	}

	protected static Object fromElementPrimitiveArray(Element elt) throws XMLFormatException {
		Class<?> typeClass = getTypeClass(elt);
		Class<?> componentType = typeClass.getComponentType();
		if (componentType.equals(double.class))
			return fromElementdoubleArray(elt);
		else if (componentType.equals(int.class))
			return fromElementintArray(elt);
		else if (componentType.equals(boolean.class))
			return fromElementbooleanArray(elt);
		else
			throw new UnsupportedOperationException("Cannot handle primitive type "+componentType.getName());
	}

	private static Object fromElementbooleanArray(Element elt) throws XMLFormatException {
		List<Element> children = elt.getChildren(LIST_ELT_NAME);
		int size=children.size();
		Object rval = Array.newInstance(boolean.class, size);
		for(int i=0; i < size; i++) {
			Element child = children.get(i);
			Array.setBoolean(rval, i, Boolean.parseBoolean(getSimpleValue(child)));
		}
		return rval;
	}
	
	private static Object fromElementintArray(Element elt) throws XMLFormatException {
		List<Element> children = elt.getChildren(LIST_ELT_NAME);
		int size=children.size();
		Object rval = Array.newInstance(int.class, size);
		for(int i=0; i < size; i++) {
			Element child = children.get(i);
			Array.setInt(rval, i, Integer.parseInt(getSimpleValue(child)));
		}
		return rval;
	}
	
	private static Object fromElementdoubleArray(Element elt) throws XMLFormatException {
		List<Element> children = elt.getChildren(LIST_ELT_NAME);
		int size=children.size();
		Object rval = Array.newInstance(double.class, size);
		for(int i=0; i < size; i++) {
			Element child = children.get(i);
			Array.setDouble(rval, i, Double.parseDouble(getSimpleValue(child)));
		}
		return rval;
	}
	
	protected static List<?> fromElementList(Element elt, Class<?> clazz) throws XMLFormatException {
		try {
			List rval = (List) clazz.getConstructor().newInstance();
			List<Element> children = elt.getChildren(LIST_ELT_NAME);
			for(Element child : children) {
				rval.add(fromElement(child));
			}
			return rval;
		} catch (Exception e) {
			throw new XMLFormatException(e);
		}
	}

	public static ObjectToJDOM primitiveToJDOM() {
		return new ObjectToJDOM() {
			@Override
			public Element objectToJDOM(Object object, String name) {
				return toElementPrimitive(name, object);
			}
		};
	}
	
	private static class ToFromElement {
		ObjectToJDOM objectToJDOM;
		JDOMToObject jdomToObject;
		private ToFromElement(ObjectToJDOM oTj, JDOMToObject jTo) {
			objectToJDOM=oTj;
			jdomToObject = jTo;
		}
	}
	
	public static interface ClassPropertyTester {
		public boolean check(Class<?> clazz);
	}
}
