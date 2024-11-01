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

/**
 * Enumeration for lot sub-types
 * @author Loison
 *
 */
public enum D6LPackageSubtypeEnum {

	DEFAULT_LOT( "default" ), 
	COMPONENT_LOT( "component", "Components" ), 
    KIT_LOT( "kit", "Kits" ), 
    EXTRACTED_LOT( "extracted", "extracted" ),
	SINGLE_LOT( "single", "Single" ), 
	
	;
	
    private final String xmlName;
    private final String displayName;
    private final String lotName;

    private D6LPackageSubtypeEnum( String xmlName ) {
        this( xmlName, xmlName, xmlName );
    }
    private D6LPackageSubtypeEnum( String xmlName, String lotName ) {
        this( xmlName, xmlName, lotName );
    }
    
    private D6LPackageSubtypeEnum( String xmlName, String displayName, String lotName ) {
        this.xmlName = xmlName;
        this.displayName = displayName;
        this.lotName = lotName;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getXmlName()
    {
        return xmlName;
    }

    public String getLotName()
    {
        return lotName;
    }

}
