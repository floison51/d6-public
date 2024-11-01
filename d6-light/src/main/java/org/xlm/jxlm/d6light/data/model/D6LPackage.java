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

import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

public class D6LPackage extends D6LAbstractEntity {

	public static final int TECH_ID_UNALLOCATED = -1;

	/** Lot containing single objects **/
    public static final String 	TECH_NAME_SINGLE = "Single";
	
	private D6LPackageTypeEnum packageType;
	private D6LPackageSubtypeEnum packageSubtype;
	private String displayType;
	
	private String name;
	
	private int idPackage;

	D6LPackage( int id ) {
		super( id );
	}

	public D6LPackage( int id, D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		this( id );
		this.packageType = type;
		this.packageSubtype = displayType;
	}

	@Override
	public String toString() {
		return "D6Vertex [id=" + id + ", label=" + label + "]";
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public D6LPackageTypeEnum getPackageType() {
		return packageType;
	}

	public void setPackageType(D6LPackageTypeEnum packageType) {
		this.packageType = packageType;
	}

	public D6LPackageSubtypeEnum getPackageSubtype() {
		return packageSubtype;
	}

	public void setPackageSubtype(D6LPackageSubtypeEnum packageSubType) {
		this.packageSubtype = packageSubType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdPackage() {
		return idPackage;
	}

	public void setIdPackage(int idPackage) {
		this.idPackage = idPackage;
	}

	
}
