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

import org.hibernate.Session;
import org.xlm.jxlm.d6light.data.exception.D6LError;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class D6LEdge extends D6LAbstractEntity implements D6LEdgeIF {

	@Id
	private int id;
	
	@Enumerated
	@Basic(optional=false)
	private D6LLinkDirectionEnum linkDirection = D6LLinkDirectionEnum.NotDirected;
	
	D6LEdge() {
		super();
		// Set to unallocated package
		packageEntity = D6LPackageVertex.UNALLOCATED; 
	}

	// For graph import
	public D6LEdge( int id ) {
		this();
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public D6LLinkDirectionEnum getLinkDirection() {
		return linkDirection;
	}

	@Override
	public void setLinkDirection(D6LLinkDirectionEnum linkDirection) {
		this.linkDirection = linkDirection;
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
		D6LEdge other = (D6LEdge) obj;
		return id == other.id;
	}

	@Override
	public final void delete(Session session) {
		throw new D6LError( "Delete is not supported for " + this.getClass().getName() );
	}

}
