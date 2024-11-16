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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class D6LVertex extends D6LAbstractEntity {

	@Id
	@SequenceGenerator( name="D6LVertexSeq", sequenceName="seq_D6LVertex", initialValue = 0, allocationSize=0)
	private int id;
	
	//@ManyToOne( targetEntity = D6LAbstractPackageEntity.class )
	//protected D6LPackageEntityIF packageEntity = D6LPackageVertex.UNALLOCATED;
	protected int packageEntityId = D6LPackageVertex.UNALLOCATED.getId();
	
	// For persistence
	public D6LVertex() {
		super();
	}
	
	// For graph import
	public D6LVertex( int id ) {
		super();
		this.id = id;
	}

	@Override
	public D6LPackageEntityIF getPackageEntity() {
		return null;
		//return packageEntity;
	}

	@Override
	public D6LAbstractPackageEntity getPackage() {
		return null;
		//return ( D6LAbstractPackageEntity ) packageEntity;
	}

	@Override
	public void setPackageEntity( D6LPackageEntityIF packageEntity ) {
		//this.packageEntity = packageEntity;
	}
	
	@Override
	public void setPackage( D6LAbstractPackageEntity packkage ) {
		//this.packageEntity = packkage;
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
		D6LVertex other = (D6LVertex) obj;
		return id == other.id;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "D6LVertex [id=" + id + ", idPackage=" + packageEntityId + "]";
	}


}
