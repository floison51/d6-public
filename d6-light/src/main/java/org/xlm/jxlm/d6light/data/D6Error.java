

package org.xlm.jxlm.d6light.data;

/**
 * D6-light exception
 * @author Loison
 *
 */
public class D6Error extends Error {

	private static final long serialVersionUID = 2296853761438100788L;

    /**
     * Constructor
     */
    public D6Error() {
        super();
    }

	/**
	 * Constructor
	 * @param message message
	 */
	public D6Error( String message ) {
		super( message );
	}

	/**
	 * Constructor
	 * @param message message
	 * @param cause cause
	 */
    public D6Error( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Constructor
     * @param cause cause
     */
    public D6Error( Throwable cause )
    {
        super( cause );
    }

    /**
     * Handle a throwable, throw back a X6Exception
     * 
     * @param e
     * @throws D6Error 
     */
    public static void handleThrowable( Throwable t ) throws D6Error
    {
        if ( t instanceof D6Error ) {
            throw ( D6Error ) t;
        } else {
            throw new D6Error( t );
        }
        
    }

}
