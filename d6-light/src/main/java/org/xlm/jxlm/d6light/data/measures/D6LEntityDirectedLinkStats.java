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

import org.xlm.jxlm.d6light.data.model.D6LVertex;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

/**
 * Directed links per bench stats per entity
 * @author Francois Loison
 *
 */
@Entity
public class D6LEntityDirectedLinkStats {

	@Id
	@OneToOne
    private D6LVertex vertex;
    
    /** Nb directed links from for entity bench **/
    private int nbDirectedLinks = -1;
    
    /** Nb directed links to for entity bench **/
    private int nbDirectedLinksTo = -1;
    
    /** BOM heads have 0 or more than 2 links directed to them **/
    private boolean isBomHead = false;
    
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

	public int getNbDirectedLinksFromForBench() {
		return nbDirectedLinks;
	}

	public void setNbDirectedLinksFromForBench(int nbDirectedLinksFromForBench) {
		this.nbDirectedLinks = nbDirectedLinksFromForBench;
	}

    public void incNbDirectedLinksFromForBench( int incDirectedLinksFromForBench ) {
        
        this.nbDirectedLinks += incDirectedLinksFromForBench;
        if ( this.nbDirectedLinks < 0 ) {
            this.nbDirectedLinks = 0;
        }
    }

	public int getNbDirectedLinksTo() {
		return nbDirectedLinksTo;
	}

	public void setNbDirectedLinksTo( int nbDirectedLinksTo ) {
		this.nbDirectedLinksTo = nbDirectedLinksTo;
		// bom head?
		this.isBomHead = ( nbDirectedLinksTo == 0 ) || ( nbDirectedLinksTo >= 2 );
	}

    public void incNbDirectedLinksTo( int incDirectedLinksTo ) {
        this.nbDirectedLinksTo += incDirectedLinksTo;
        if ( this.nbDirectedLinksTo < 0 ) {
            this.nbDirectedLinksTo = 0;
        }
        // bom head?
        this.isBomHead = ( nbDirectedLinksTo == 0 ) || ( nbDirectedLinksTo >= 2 );
    }

	public boolean isBomHead() {
		return isBomHead;
	}

}
