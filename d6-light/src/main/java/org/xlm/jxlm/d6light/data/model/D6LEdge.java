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
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Session;
import org.jgrapht.graph.DefaultEdge;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class D6LEdge extends DefaultEdge implements D6LEntityIF, D6LEdgeIF {

	private static AtomicInteger seqIdEdge = new AtomicInteger();
	
	/** Serial ID **/
	private static final long serialVersionUID = -4376861182806887574L;
	
	@Id
	@SequenceGenerator( name="D6LEdgeSeq", sequenceName="seq_D6LEdge", initialValue = 0, allocationSize=0)
	private int id;
	
	private String label;
	
	@Enumerated
	@Basic(optional=false)
	private D6LLinkDirectionEnum linkDirection;
	
	@ManyToOne( targetEntity = D6LAbstractPackageEntity.class )
	protected D6LPackageEntityIF packageEntity = D6LPackageVertex.UNALLOCATED;

	D6LEdge() {

		super();
		// Make sure id is unique, unless Set<D6LEdge> will not work
		this.id = seqIdEdge.getAndIncrement();
		// Set to unallocated package
		packageEntity = D6LPackageVertex.UNALLOCATED; 

	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public D6LPackageEntityIF getPackageEntity() {
		return packageEntity;
	}

	@Override
	public void setPackageEntity( D6LPackageEntityIF packageEntity ) {
		this.packageEntity = packageEntity;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getDisplay() {
		return getLabel();
	}

	@Override
	public D6LLinkDirectionEnum getLinkDirection() {
		return linkDirection;
	}

	public void setLinkDirection(D6LLinkDirectionEnum linkDirection) {
		this.linkDirection = linkDirection;
	}

	@Override
	public String toString() {
		return "D6LEdge [getSource()=" + getSource() + ", getTarget()=" + getTarget() + "]";
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
	public void save( Session session ) {
		
		// Save entity
		session.merge( this );
		
	}

}
