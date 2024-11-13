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

import org.jgrapht.graph.DefaultEdge;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class D6LPackageEdge extends DefaultEdge implements D6LPackageEntityIF, D6EdgeIF {

	/** Serial ID **/
	private static final long serialVersionUID = -4376861182806887574L;
	
	@Id
	@SequenceGenerator( name="D6LPackageEdgeSeq", sequenceName="seq_D6LPackageEdge", initialValue = 0, allocationSize=0)
	private int id;
	
	private String label;
	
	@Enumerated
	@Basic(optional=false)
    private D6LPackageTypeEnum packageType;

	private D6LLinkDirectionEnum linkDirection;
	
    private D6LPackageData data = new D6LPackageData( this );
    
	D6LPackageEdge() {
		super();
	}

    /**
     * Constructor for lots
     * @param type
     * @param displayType
     */
	public D6LPackageEdge( D6LPackageTypeEnum packageType ) {
		super();
		this.packageType = packageType;
		
		// if lot dependency, set default link direction
		switch ( this.packageType ) {
			case BUSINESS_PKG_DEPENDENCY:
			case TECHNICAL_PKG_DEPENDENCY: {
				// default to not directed link
				linkDirection = D6LLinkDirectionEnum.NotDirected;
				break;
			}
			default: {
				// null direction
				linkDirection = null;
			}
		}
		
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
		throw new D6LError( "Not supported in this flavor" );
	}
	
	@Override
	public void setPackageEntity( D6LPackageEntityIF packageEntity ) {
		throw new D6LError( "Not supported in this flavor" );
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getDisplay() {
		return getLabel();
	}

	@Override
	public D6LPackageData getData() {
		return data;
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
		return "D6LPackageEdge [getSource()=" + getSource() + ", getTarget()=" + getTarget() + "]";
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
		D6LPackageEdge other = (D6LPackageEdge) obj;
		return id == other.id;
	}

}
