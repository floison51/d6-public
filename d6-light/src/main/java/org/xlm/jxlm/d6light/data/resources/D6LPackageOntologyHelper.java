/**
 *  Public Data Systemizer, see https://doi.org/10.1016/j.compind.2023.104053
 *  Copyright (C) 2025 Francois LOISON
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see https://www.gnu.org/licenses/gpl-3.0.html
**/

package org.xlm.jxlm.d6light.data.resources;

import java.util.Locale;
import java.util.ResourceBundle;

import org.xlm.jxlm.d6light.data.exception.D6LError;

/**
 * Lots ontology helper
 * @author Loison
 *
 */
public class D6LPackageOntologyHelper {

	private static String fixedOntology = null;
	private static ResourceBundle rbLots = null;
	
	public static void setOntology( String ontology ) {
		
		// check
		if ( D6LPackageOntologyHelper.fixedOntology != null ) {
			throw new D6LError( "Changing ontology is forbidden" );
		}
		
		// Init
        // Load resources
        Locale locale = Locale.forLanguageTag( ontology );
        D6LPackageOntologyHelper.rbLots = 
        	ResourceBundle.getBundle( 
        		"org.xlm.jxlm.d6light.data.packkage.resources.PackageResource", 
        		locale 
        	);

	}
	
	public static String getPackageType() {
		return getLabel( "packageType" );
	}
	
	/**
	 * Get ontologized resource, for example if ontology = legacy, lot name = "lot", ontology = academic, lot name = "package"
	 * @param key
	 * @return
	 */
	public static String getLabel( String key ) {
		
		if ( rbLots == null ) {
			throw new D6LError( "Ontology is not defined" );
		}
		
		String result = rbLots.getString( key );
		return result;
		
	}


}
