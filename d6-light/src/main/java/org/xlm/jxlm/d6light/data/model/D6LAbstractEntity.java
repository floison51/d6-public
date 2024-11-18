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

import org.hibernate.Session;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class D6LAbstractEntity implements D6LEntityIF {

	protected String label;

	@ManyToOne( targetEntity = D6LAbstractPackageEntity.class )
	protected D6LPackageEntityIF packageEntity = D6LPackageVertex.UNALLOCATED;

	protected D6LAbstractEntity() {
		super();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDisplay() {
		return getLabel();
	}

	@Override
	public D6LPackageEntityIF getPackageEntity() {
		return packageEntity;
	}

	@Override
	public void setPackageEntity( D6LPackageEntityIF packageEntity ) {
		this.packageEntity = packageEntity;
	}

	@Override
	public void create( Session session ) {
		
		// Initial creation
		session.persist( this );
		
	}
	
	@Override
	public void save( Session session ) {
		
		// Save entity
		session.merge( this );
		
	}
	
	@Override
	public void delete( Session session ) {
		
		// Delete entity
		session.remove( this );
		
	}
	
}
