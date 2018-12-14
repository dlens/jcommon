
package com.dlens.common2.exceptions;

/**
 *An exception that only occurs what using the {@link ANPEngineImplV1.limitPowerHierarchy}
 *method on a matrix that did not represent a hierarchy.
 * @author  dlens
 */
public class NonHierarchyException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>NotHierarchyException</code> without detail message.
     */
    public NonHierarchyException() {
    }
    
    
    /**
     * Constructs an instance of <code>NotHierarchyException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NonHierarchyException(String msg) {
        super(msg);
    }
}
