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

package org.xlm.jxlm.d6light.data.model;

public interface D6LEntityIF extends D6LSaveableIF {
	
	int getId();
	
	String getLabel();
	
	String getDisplay();
	
	D6LPackageEntityIF getPackageEntity();

	void setPackageEntity( D6LPackageEntityIF packkageEntity );

	void setLabel( String label );

	/** Only for vertices **/
	D6LAbstractPackageEntity getPackage();

	/** Only for vertices **/
	void setPackage( D6LAbstractPackageEntity packkage );

}
