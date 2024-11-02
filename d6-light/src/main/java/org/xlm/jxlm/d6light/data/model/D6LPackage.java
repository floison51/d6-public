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

import org.hibernate.SessionFactory;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class D6LPackage extends D6LAbstractEntity {

	public static final int TECH_ID_UNALLOCATED = -1;

	/** Lot containing single objects **/
    public static final String 	TECH_NAME_SINGLE = "Single";

	public static final D6LPackage UNALLOCATED = new D6LPackage( TECH_ID_UNALLOCATED, D6LPackageTypeEnum.TECHNICAL_PKG, null );
	
	@Id
	@SequenceGenerator( name="D6LPackageSeq", sequenceName="seq_D6LPackage", initialValue = 0, allocationSize=0)
	private int id;
	
	@Enumerated
	@Basic(optional=false)
    private D6LPackageTypeEnum packageType;

	@Enumerated
	@Basic(optional=true)
    private D6LPackageSubtypeEnum packageSubtype;
	
    private String displayType;
	
	private String name;
	
    //@OneToOne( fetch=FetchType.LAZY )
    //private D6LVertex primaryTarget;
	
	public D6LPackage() {
		super();
	}

	public D6LPackage( int id, D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		super();
		this.id = id;
		this.packageType = type;
		this.packageSubtype = displayType;
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
/*
	public void setPrimaryTarget( D6LVertex vertex ) {
		this.primaryTarget = vertex;
	}

	public D6LVertex getPrimaryTarget() {
		return primaryTarget;
	}
*/	
	public static void initDb( SessionFactory sessionFactory ) {
		
		// Create persisted objects
		sessionFactory.inSession( 
			session -> {
				session.persist( UNALLOCATED );	
			});
		
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public D6LPackage getPackage() {
		throw new D6LError( "Not supported in this flavor" );
	}
	
	@Override
	public void setPackage( D6LPackage packkage ) {
		throw new D6LError( "Not supported in this flavor" );
	}

}
