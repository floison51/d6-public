

package org.xlm.jxlm.d6light.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * D6-light exception
 * @author Loison
 *
 */
public class D6Exception extends Exception {

	private static final long serialVersionUID = 2296853761438100788L;

    /**
     * Constructor
     */
    public D6Exception() {
        super();
    }

	/**
	 * Constructor
	 * @param message message
	 */
	public D6Exception( String message ) {
		super( message );
	}

	/**
	 * Constructor
	 * @param message message
	 * @param cause cause
	 */
    public D6Exception( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Constructor
     * @param cause cause
     */
    public D6Exception( Throwable cause )
    {
        super( cause );
    }

    /**
     * Handle a exception, throw back a X6Exception
     * 
     * @param e
     * @throws D6Exception 
     */
    public static void handleException( Exception e ) throws D6Exception
    {
        if ( e instanceof D6Exception ) {
            throw ( D6Exception ) e;
        } else {
            throw new D6Exception( e );
        }
        
    }

    /**
     * Handle a throwable, throw back a X6Exception
     * 
     * @param e
     * @throws D6Exception 
     */
    public static void handleThrowable( Throwable t ) throws D6Exception
    {
        if ( t instanceof D6Exception ) {
            throw ( D6Exception ) t;
        } else {
            throw new D6Exception( t );
        }
        
    }

    public static void handleException( List<? extends Exception> exceptions ) throws D6Exception
    {
        if ( ( exceptions == null ) || exceptions.isEmpty() ) {
            return;
        }
        
        List<String> values = new ArrayList<>();
        
        for ( Exception e : exceptions ) {
            values.add( e.getMessage() );
        }
        
        throw new D6Exception( StringUtils.join( exceptions, '\n' ) );
    }

}
