

package org.xlm.jxlm.d6light.data.conf;

/**
 * Utilities for reg exp parameters
 * @author Loison
 *
 */
public class D6LRegExpParamHelper {

    private D6LRegExpParamHelper() {
        throw new IllegalAccessError( "Utility class" );
    }

	/**
	 * True if given string matches regexp filter
	 * @param regExpParam
	 * @param value
	 * @return
	 */
    public static boolean regExpParamMatch( RegExpType regExpParam, String value ) {
		// check
		if ( value == null ) {
			return false;
		}
		
		String pattern = regExpParam.getValue();
		boolean match = value.matches( pattern );
		
		// negate?
		if ( regExpParam.isNegate() ) {
			match = !match;
		}
		
		return match;
	}

}
