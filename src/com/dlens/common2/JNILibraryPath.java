package com.dlens.common2;

/**
 * A simple program to print out the library path that java searches for jni's.
 * @author wjadams
 *
 */
public class JNILibraryPath {
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));
	}
}
