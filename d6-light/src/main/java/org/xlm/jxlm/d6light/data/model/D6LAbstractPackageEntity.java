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

import java.util.Objects;

import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public abstract class D6LAbstractPackageEntity extends D6LAbstractEntity implements D6LPackageEntityIF {

	/** Lot containing single objects **/
    public static final String 	TECH_NAME_SINGLE = "Single";

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	@Enumerated
	@Basic(optional=false)
    private D6LPackageTypeEnum packageType;

	@Enumerated
	@Basic(optional=true)
    private D6LPackageSubtypeEnum packageSubtype;
	
    private String displayType;
	
	private String name;
	
    @OneToOne( fetch=FetchType.LAZY )
    private D6LPackageData data;

	public D6LAbstractPackageEntity() {
		super();
		this.data = new D6LPackageData( this );
	}

	public D6LAbstractPackageEntity( int id, D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		this();
		this.id = id;
		this.packageType = type;
		this.packageSubtype = displayType;
	}

	public D6LAbstractPackageEntity( int id, D6LPackageTypeEnum type ) {
		this( id, type, null );
	}

	public D6LAbstractPackageEntity( D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		this();
		this.packageType = type;
		this.packageSubtype = displayType;
	}

	public D6LAbstractPackageEntity( D6LPackageTypeEnum type ) {
		this( type, null );
	}

	@Override
	public String toString() {
		return "D6LPkg [id=" + id + ", label=" + label + "]";
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

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public D6LPackageEntityIF getPackageEntity() {
		throw new D6LError( "Not supported in this flavor" );
	}
	
	@Override
	public void setPackageEntity( D6LPackageEntityIF packageEntity ) {
		throw new D6LError( "Not supported in this flavor" );
	}

	@Override
	public D6LAbstractPackageEntity getPackage() {
		throw new D6LError( "Not supported in this flavor" );
	}

	@Override
	public void setPackage( D6LAbstractPackageEntity packkage ) {
		throw new D6LError( "Not supported in this flavor" );
	}

	@Override
	public D6LPackageData getData() {
		//return data;
		return null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		D6LAbstractPackageEntity other = (D6LAbstractPackageEntity) obj;
		return id == other.id;
	}

}
