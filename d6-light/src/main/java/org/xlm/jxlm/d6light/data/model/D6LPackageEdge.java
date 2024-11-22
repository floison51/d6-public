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
import org.jgrapht.Graph;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class D6LPackageEdge extends D6LAbstractPackageEntity implements D6LEdgeIF {

	@Enumerated
	@Basic(optional=false)
	private D6LLinkDirectionEnum linkDirection = D6LLinkDirectionEnum.NotDirected;
	
	public D6LPackageEdge( D6LPackageTypeEnum type ) {
		super( type );
	}

	public D6LPackageEdge( D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		super( type, displayType );
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
	public D6LEntityKindEnum getKind() {
		return D6LEntityKindEnum.egde;
	}

	@Override
	public void delete(Session session) {
		
		// Delete from outGraph
		Graph<D6LPackageVertex, D6LPackageEdge> outGraph = D6LDb.getInstance().outGraph;
		outGraph.removeEdge( this );
		
		// Remove from session
		super.delete(session);
		
	}
	
}
