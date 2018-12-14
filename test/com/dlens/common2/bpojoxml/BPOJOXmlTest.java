package com.dlens.common2.bpojoxml;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jdom.Element;
import org.junit.Test;

public class BPOJOXmlTest {
	@Test
	public void testDouble() throws Exception {
		Double val = new Double(2.0);
		Element element = BPOJOXml.toElement("doubleValue", val);
//		System.out.println(JDOMit.toXMLStringElt(element));
		Double parsed_val = (Double) BPOJOXml.fromElement(element);
		assertEquals(val, parsed_val);
		Integer ival = new Integer(3);
		element = BPOJOXml.toElement("ival", ival);
//		System.out.println(JDOMit.toXMLStringElt(element));
		Integer parsed_ival = (Integer) BPOJOXml.fromElement(element);
		assertEquals(ival, parsed_ival);
	}

	@Test
	public void testString() throws Exception {
		String val = "Hello \" World";
		Element element = BPOJOXml.toElement("stringValue", val);
		//System.out.println(JDOMit.toXMLStringElt(element));
		String parsed_val = (String) BPOJOXml.fromElement(element);
		assertEquals(val, parsed_val);
	}

	@Test
	public void testListDouble() throws Exception {
		List<Double> dlist = new ArrayList<Double>();
		dlist.add(1.0);
		dlist.add(5.0);
		dlist.add(7.0);
		Element elt;
		elt = BPOJOXml.toElement("dlist", dlist);
//		System.out.println(JDOMit.toXMLStringElt(elt));
		List<Double> parsed_dlist = (List<Double>) BPOJOXml.fromElement(elt);
		assertEquals(dlist, parsed_dlist);
	}


	@Test
	public void testListInteger() throws Exception {
		List<Integer> dlist = new ArrayList<Integer>();
		dlist.add(1);
		dlist.add(5);
		dlist.add(-7);
		Element elt;
		elt = BPOJOXml.toElement("dlist", dlist);
//		System.out.println(JDOMit.toXMLStringElt(elt));
		List<Integer> parsed_dlist = (List<Integer>) BPOJOXml.fromElement(elt);
		assertEquals(dlist, parsed_dlist);
	}
	
	@Test
	public void testArrayDouble() throws Exception {
		Double[] darr = new Double[3];
		darr[1]=3.0;
		darr[2]=-157.5;
		Element elt;
		elt = BPOJOXml.toElement("darr", darr);
		Double[] parsed_darr = (Double[]) BPOJOXml.fromElement(elt);
		assertArrayEquals(darr, parsed_darr);
		//System.out.println(JDOMit.toXMLStringElt(elt));
	}
	
	@Test
	public void testArrayInteger() throws Exception {
		Integer[] iarr = new Integer[3];
		iarr[1]=3;
		iarr[2]=-157;
		Element elt;
		elt = BPOJOXml.toElement("darr", iarr);
		Integer[] parsed_iarr = (Integer[]) BPOJOXml.fromElement(elt);
		assertArrayEquals(iarr, parsed_iarr);
		//System.out.println(JDOMit.toXMLStringElt(elt));
	}
	
	@Test
	public void testArrayString() throws Exception {
		String[] sarr = new String[5];
		sarr[2]="i=2";
		sarr[4]="i=4";
		Element elt = BPOJOXml.toElement("sarr", sarr);
		String[] parsed_sarr = (String[]) BPOJOXml.fromElement(elt);
		assertArrayEquals(sarr, parsed_sarr);
	}

	@Test
	public void testArrayMixedWithArrayElt() throws Exception {
		Object[] sarr = new Object[5];
		sarr[0] = new Integer(2);
		sarr[1] = new Double(1.5);
		sarr[2]="i=2";
		sarr[3]=new String[] {"a", "b", "c", "d", "e"};
		Element elt = BPOJOXml.toElement("sarr", sarr);
		Object[] parsed_sarr = (Object[]) BPOJOXml.fromElement(elt);
		assertArrayEquals(sarr, parsed_sarr);
	}
	
	@Test
	public void testdoubleArray() throws Exception {
		double[] arr = new double[] {-1, 3, 5};
		Element elt = BPOJOXml.toElement("arr", arr);
		//System.out.println(JDOMit.toXMLStringElt(elt));
		double[] parsed_arr = (double[]) BPOJOXml.fromElement(elt);
		assertArrayEquals(arr, parsed_arr, 0);
	}

	@Test
	public void testintArray() throws Exception {
		int[] arr = new int[] {-1, 3, 5};
		Element elt = BPOJOXml.toElement("arr", arr);
		//System.out.println(JDOMit.toXMLStringElt(elt));
		int[] parsed_arr = (int[]) BPOJOXml.fromElement(elt);
		assertArrayEquals(arr, parsed_arr);
	}

	@Test
	public void testboolArray() throws Exception {
		boolean[] arr = new boolean[] {false, true, true, false};
		Element elt = BPOJOXml.toElement("arr", arr);
		//System.out.println(JDOMit.toXMLStringElt(elt));
		boolean[] parsed_arr = (boolean[]) BPOJOXml.fromElement(elt);
		assertTrue(Arrays.equals(arr, parsed_arr));
	}
	
	@Test
	public void testMixedMultiArrayWithPrimitives() throws Exception {
		Object[] arr = new Object[4];
		arr[0] = new double[] {1, 3.5, 4.5};
		arr[1] = new int[] {1,2,5};
		arr[2] = new boolean[] {true, true, false, true, false, false, false};
		Element elt = BPOJOXml.toElement("arr", arr);
		//System.out.println(JDOMit.toXMLStringElt(elt));
		Object[] parsed_arr = (Object[]) BPOJOXml.fromElement(elt);
		assertArrayEquals(arr,  parsed_arr);
	}
	
	@Test
	public void testdouble() throws Exception {
		double val = 1.5;
		Element elt = BPOJOXml.toElement("d", val);
		double parse_val = (Double) BPOJOXml.fromElement(elt);
		assertEquals(val, parse_val, 0);
	}

	@Test
	public void testint() throws Exception {
		int val = 1;
		Element elt = BPOJOXml.toElement("d", val);
		int parse_val = (Integer) BPOJOXml.fromElement(elt);
		assertEquals(val, parse_val);
	}

	@Test
	public void testbooleantrue() throws Exception {
		boolean val = true;
		Element elt = BPOJOXml.toElement("d", val);
		boolean parse_val = (Boolean) BPOJOXml.fromElement(elt);
		assertEquals(val, parse_val);
	}
	
	@Test
	public void testbooleanfalse() throws Exception {
		boolean val = false;
		Element elt = BPOJOXml.toElement("d", val);
		boolean parse_val = (Boolean) BPOJOXml.fromElement(elt);
		assertEquals(val, parse_val);
	}
}
