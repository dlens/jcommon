
package com.dlens.common2.exceptions;

/**
 * An exception that happens when reading a data structure from a <code>jdom</code>
 *xml element.
 * @author  dlens
 */
public class XMLFormatException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>XMLFormatException</code> without detail message.
     */
    public XMLFormatException() {
    }
    
    
    /**
     * Constructs an instance of <code>XMLFormatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public XMLFormatException(String msg) {
        super(msg);
    }
    
    /**Constructs a new exception from an old one.*/
    public XMLFormatException(Exception e) {
        super(e);
    }
    
}
