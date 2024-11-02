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

import org.hibernate.annotations.NaturalId;
import org.xlm.jxlm.d6light.data.model.D6LVertex;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

/**
 * Directed links per bench stats per entity
 * @author Francois Loison
 *
 */
@Entity
public class D6LEntityDirectedLinkStats {

	@Id
	@SequenceGenerator( name="D6LMeasureSeq", sequenceName="seq_D6LMeasure", initialValue = 0, allocationSize=0)
	private int id;
	
	@NaturalId
	@OneToOne
    private D6LVertex vertex;
    
    /** Nb directed links from for entity bench **/
    private int nbDirectedLinksFromForBench = -1;
    
    /** Nb links from for entity bench **/
     private int nbLinksFromForBench = -1;
    
    /** Nb directed links to for entity bench **/
    private int nbDirectedLinksToForBench = -1;
    
    /** Nb links to for entity bench **/
    private int nbLinksToForBench = -1;
    
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

	public int getNbDirectedLinksFromForBench() {
		return nbDirectedLinksFromForBench;
	}

	public void setNbDirectedLinksFromForBench(int nbDirectedLinksFromForBench) {
		this.nbDirectedLinksFromForBench = nbDirectedLinksFromForBench;
	}

    public void incNbDirectedLinksFromForBench( int incDirectedLinksFromForBench ) {
        
        this.nbDirectedLinksFromForBench += incDirectedLinksFromForBench;
        if ( this.nbDirectedLinksFromForBench < 0 ) {
            this.nbDirectedLinksFromForBench = 0;
        }
    }

	public int getNbDirectedLinksToForBench() {
		return nbDirectedLinksToForBench;
	}

	public void setNbDirectedLinksToForBench( int nbDirectedLinksToForBench ) {
		this.nbDirectedLinksToForBench = nbDirectedLinksToForBench;
		// bom head?
		this.isBomHeadForBench = ( nbDirectedLinksToForBench == 0 ) || ( nbDirectedLinksToForBench >= 2 );
	}

    public void incNbDirectedLinksToForBench( int incDirectedLinksToForBench ) {
        this.nbDirectedLinksToForBench += incDirectedLinksToForBench;
        if ( this.nbDirectedLinksToForBench < 0 ) {
            this.nbDirectedLinksToForBench = 0;
        }
        // bom head?
        this.isBomHeadForBench = ( nbDirectedLinksToForBench == 0 ) || ( nbDirectedLinksToForBench >= 2 );
    }

	public boolean isBomHeadForBench() {
		return isBomHeadForBench;
	}

	public int getNbLinksFromForBench() {
		return nbLinksFromForBench;
	}

	public void setNbLinksFromForBench(int nbLinksFromForBench) {
		this.nbLinksFromForBench = nbLinksFromForBench;
	}

    public void incNbLinksFromForBench(int incNbLinksFromForBench) {
        this.nbLinksFromForBench += incNbLinksFromForBench;
        if ( this.nbLinksFromForBench < 0 ) {
            this.nbLinksFromForBench = 0;
        }
    }

	public int getNbLinksToForBench() {
		return nbLinksToForBench;
	}

	public void setNbLinksToForBench(int nbLinksToForBench) {
		this.nbLinksToForBench = nbLinksToForBench;
	}
	
    public void incNbLinksToForBench(int incNbLinksToForBench) {
        this.nbLinksToForBench += incNbLinksToForBench;
        if ( this.nbLinksToForBench < 0 ) {
            this.nbLinksToForBench = 0;
        }
    }

	public int getId() {
		return id;
	}

}
