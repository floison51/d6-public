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

package org.xlm.jxlm.d6light.data.measures;

import java.util.Objects;

import org.xlm.jxlm.d6light.data.model.D6LVertex;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Directed links per bench stats per entity
 * @author Francois LoisonTable
 *
 */
@Entity
@Table(
	indexes = { 
		@Index( name = "nbDirectedLinksFromForBench", columnList = "nbDirectedLinksFromForBench" ),
		@Index( name = "nbDirectedLinksToForBench"  , columnList = "nbDirectedLinksToForBench" ),
		@Index( name = "isBomHeadForBench"          , columnList = "isBomHeadForBench" )
	}
)
public class D6LEntityDirectedLinkStats {

	@Id
	@OneToOne
    private D6LVertex vertex;
    
    /** Nb directed links from for entity bench **/
	private long nbDirectedLinksFromForBench = -1;
    
    /** Nb links from for entity bench **/
    private long nbLinksFromForBench = -1;
    
    /** Nb directed links to for entity bench **/
    private long nbDirectedLinksToForBench = -1;
    
    /** Nb links to for entity bench **/
    private long nbLinksToForBench = -1;
    
    /** BOM heads have 0 or more than 2 links directed to them **/
    private boolean isBomHeadForBench = false;
    
    D6LEntityDirectedLinkStats() {
    	super();
    } 

    /**
     * Constructor
     * @param idEntity idEntity
     * @param idBench idBench
     */
	public D6LEntityDirectedLinkStats( D6LVertex vertex ) {
		this();
		this.vertex = vertex;
	}

	public D6LVertex getVertex() {
		return vertex;
	}

	public long getNbDirectedLinksFromForBench() {
		return nbDirectedLinksFromForBench;
	}

	public void setNbDirectedLinksFromForBench(long nbDirectedLinksFromForBench) {
		this.nbDirectedLinksFromForBench = nbDirectedLinksFromForBench;
	}

    public void incNbDirectedLinksFromForBench( long incDirectedLinksFromForBench ) {
        
        this.nbDirectedLinksFromForBench += incDirectedLinksFromForBench;
        if ( this.nbDirectedLinksFromForBench < 0 ) {
            this.nbDirectedLinksFromForBench = 0;
        }
    }

	public long getNbDirectedLinksToForBench() {
		return nbDirectedLinksToForBench;
	}

	public void setNbDirectedLinksToForBench( long nbDirectedLinksToForBench ) {
		
		this.nbDirectedLinksToForBench = nbDirectedLinksToForBench;
		
		// bom head?
		this.isBomHeadForBench = ( nbDirectedLinksToForBench == 0 );
		
	}

    public void incNbDirectedLinksToForBench( long incDirectedLinksToForBench ) {
        this.nbDirectedLinksToForBench += incDirectedLinksToForBench;
        if ( this.nbDirectedLinksToForBench < 0 ) {
            this.nbDirectedLinksToForBench = 0;
        }
        
		// bom head?
		this.isBomHeadForBench = ( nbDirectedLinksToForBench == 0 );
        
    }

	public boolean isBomHeadForBench() {
		return isBomHeadForBench;
	}

	public long getNbLinksFromForBench() {
		return nbLinksFromForBench;
	}

	public void setNbLinksFromForBench(long nbLinksFromForBench) {
		this.nbLinksFromForBench = nbLinksFromForBench;
	}

    public void incNbLinksFromForBench(long incNbLinksFromForBench) {
        this.nbLinksFromForBench += incNbLinksFromForBench;
        if ( this.nbLinksFromForBench < 0 ) {
            this.nbLinksFromForBench = 0;
        }
    }

	public long getNbLinksToForBench() {
		return nbLinksToForBench;
	}

	public void setNbLinksToForBench(long nbLinksToForBench) {
		this.nbLinksToForBench = nbLinksToForBench;
	}
	
    public void incNbLinksToForBench(long incNbLinksToForBench) {
        this.nbLinksToForBench += incNbLinksToForBench;
        if ( this.nbLinksToForBench < 0 ) {
            this.nbLinksToForBench = 0;
        }
    }

	@Override
	public int hashCode() {
		return Objects.hash(vertex);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		D6LEntityDirectedLinkStats other = (D6LEntityDirectedLinkStats) obj;
		return Objects.equals(vertex, other.vertex);
	}

	@Override
	public String toString() {
		return "D6LEntityDirectedLinkStats [vertex=" + vertex + ", nbDirectedLinksFromForBench="
				+ nbDirectedLinksFromForBench + ", nbLinksFromForBench=" + nbLinksFromForBench
				+ ", nbDirectedLinksToForBench=" + nbDirectedLinksToForBench + ", nbLinksToForBench="
				+ nbLinksToForBench + ", isBomHeadForBench=" + isBomHeadForBench + "]";
	}

    
}
