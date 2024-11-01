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

import org.xlm.jxlm.audit.x6.common.data.lot.resources.D6LotOntologyHelper;

/**
 * Lots enumeration
 * @author Loison
 *
 */
public enum D6LPackageTypeEnum {
	
	TECHNICAL_LOT( "Technical" ),
	TECHNICAL_LOT_DEPENDENCY( "TechnicalDep" ), 
	BUSINESS_LOT( "Business" ),
	BUSINESS_LOT_DEPENDENCY( "BusinessDep" ),
	DEPENDENCY_LOT( "Dependency" ),
	DEPENDENCY_LOT_DEPENDENCY(  "DependencyDep" )
	;

	private final String kind;
	
	private D6LPackageTypeEnum( String kind ) {
		this.kind = kind;
	}

	public boolean isBusiness() {
		return ( this == BUSINESS_LOT ) || ( this == BUSINESS_LOT_DEPENDENCY ) ;
	}
	
    public String getDisplayName() {
        return D6LPackageOntologyHelper.getLabel( "lotType.type." + name() ) ;
    }

    public String getShortName() {
        return D6LPackageOntologyHelper.getLabel( "lotType.type.short." + name() );
	}

	public String getKind() {
		return kind;
	}

}
