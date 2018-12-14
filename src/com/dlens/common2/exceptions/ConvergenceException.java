package com.dlens.common2.exceptions;
/**
 *An exception representing an error in a mathematical calculation converging.
 */
public class ConvergenceException extends Exception {
    /**Creates a new blank convergence exception.*/
    public ConvergenceException() {
        super();
    }
    /**Creates a new convergence exception with message <code>s</code>.
     *@param s The message to give the new exception.
     */
    public ConvergenceException(String s) {
        super(s);
    }
}


