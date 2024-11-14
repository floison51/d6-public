

package org.xlm.jxlm.d6light.data.command;

/**
 * Command statistics
 * @author Francois Loison
 *
 */
public class Stats {
    
	public int nbObjects = 0;
	public int nbLinks = 0;
	public int nbEntityErrors = 0;
	public int nbEntityLinkErrors = 0;
	public int nbEntityUniLinkErrors = 0;
	
	/**
	 * Default constructor
	 */
	public Stats() {
		super();
	}

	/**
	 * Add stats to me
	 * @param stats other stats
	 */
	public void accumulate( Stats stats ) {
		if ( stats == null ) {
			return;
		}
		
		nbObjects 				+= stats.nbObjects;
		nbLinks 				+= stats.nbLinks;
		nbEntityErrors 			+= stats.nbEntityErrors;
		nbEntityLinkErrors		+= stats.nbEntityLinkErrors;
		nbEntityUniLinkErrors 	+= stats.nbEntityUniLinkErrors;
	}
	
	public long getAllNbs() {
	    
	    long result = 0;
	    
	    result += nbObjects;
	    result += nbLinks;
	    result += nbEntityErrors;
	    result += nbEntityLinkErrors;
	    result += nbEntityUniLinkErrors;
	    
	    return result;
	    
	}
}
