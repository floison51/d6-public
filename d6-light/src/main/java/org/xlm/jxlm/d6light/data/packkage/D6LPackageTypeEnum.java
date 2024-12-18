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

package org.xlm.jxlm.d6light.data.packkage;

import org.xlm.jxlm.d6light.data.resources.D6LPackageOntologyHelper;

/**
 * Lots enumeration
 * @author Loison
 *
 */
public enum D6LPackageTypeEnum {
	
	TECHNICAL_PKG( "Technical" ),
	TECHNICAL_PKG_DEPENDENCY( "TechnicalDep" ), 
	BUSINESS_PKG( "Business" ), 
	BUSINESS_PKG_DEPENDENCY( "BusinessDep" ),
	;

	private final String kind;
	
	private D6LPackageTypeEnum( String kind ) {
		this.kind = kind;
	}

	public boolean isBusiness() {
		return ( this == BUSINESS_PKG ) ;
	}
	
    public String getDisplayName() {
        return D6LPackageOntologyHelper.getLabel( "pkgType.type." + name() ) ;
    }

    public String getShortName() {
        return D6LPackageOntologyHelper.getLabel( "pkgType.type.short." + name() );
	}

	public String getKind() {
		return kind;
	}

}
