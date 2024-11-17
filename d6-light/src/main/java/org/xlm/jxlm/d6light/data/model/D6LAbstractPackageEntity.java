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

import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public abstract class D6LAbstractPackageEntity extends D6LAbstractEntity implements D6LPackageEntityIF {

	/** Lot containing single objects **/
    public static final String 	TECH_NAME_SINGLE = "Single";

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	@Enumerated
	@Basic(optional=false)
    private D6LPackageTypeEnum packageType;

	@Enumerated
	@Basic(optional=true)
    private D6LPackageSubtypeEnum packageSubtype;
	
    private String displayType;
	
	private String name;
	
	public D6LAbstractPackageEntity() {
		super();
	}

	public D6LAbstractPackageEntity( int id, D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		this();
		this.id = id;
		this.packageType = type;
		this.packageSubtype = displayType;
	}

	public D6LAbstractPackageEntity( int id, D6LPackageTypeEnum type ) {
		this( id, type, null );
	}

	public D6LAbstractPackageEntity( D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		this();
		this.packageType = type;
		this.packageSubtype = displayType;
	}

	public D6LAbstractPackageEntity( D6LPackageTypeEnum type ) {
		this( type, null );
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

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public D6LPackageEntityIF getPackageEntity() {
		throw new D6LError( "Not supported in this flavor" );
	}
	
	@Override
	public void setPackageEntity( D6LPackageEntityIF packageEntity ) {
		throw new D6LError( "Not supported in this flavor" );
	}

	@Override
	public D6LAbstractPackageEntity getPackage() {
		throw new D6LError( "Not supported in this flavor" );
	}

	@Override
	public void setPackage( D6LAbstractPackageEntity packkage ) {
		throw new D6LError( "Not supported in this flavor" );
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
		D6LAbstractPackageEntity other = (D6LAbstractPackageEntity) obj;
		return id == other.id;
	}

	/********************************************************
	 * Stats
	 * ******************************************************/
	
    private Integer nbObjects = null;
    private Integer nbLinks = null;
    
    /** For bidirectional links : nb links from->to **/
    private Integer nbLinks_fromTo = null;
    
    /** For bidirectional links : nb links to->from **/
    private Integer nbLinks_toFrom = null;

    /** When true, entities nbs cn't be modified **/
    private boolean isFrozenForNbs = false;
    
    /**
     * Increment nb links<b/>
     * This method doesn't care of link direction, it is overriden by D6LotLink to separate from/to and to/from links
     * @param inc
     */
    public void incNbDirectedLinks( Integer incNbLinks_fromTo, Integer incNbLinks_toFrom ) {
        
        if ( isFrozenForNbs || !( this instanceof D6LPackageEdge ) ) {
            return;
        }
        
        if ( incNbLinks_fromTo != null ) {
            // get rid of not available state
            if ( nbLinks_fromTo == null ) {
                // reset to 0
                nbLinks_fromTo = 0;
            }
            nbLinks_fromTo += incNbLinks_fromTo;
       }
        if ( incNbLinks_toFrom != null ) {
            // get rid of not available state
            if ( nbLinks_toFrom == null ) {
                // reset to 0
                nbLinks_toFrom = 0;
            }
            nbLinks_toFrom += incNbLinks_toFrom;
       }
        
    }
    
	/**
	 * Get nb entities in lot
	 * @return
	 */
	public int getNbEntities() {
		return getNbObjects() + getNbLinks();
	}

	public int getNbObjects() {
		return ( nbObjects != null ) ? nbObjects : 0;
	}

	public void setNbObjects( int nbObjects ) {
		if ( isFrozenForNbs ) {
			return;
		}
		
		this.nbObjects = nbObjects;
	}

	/**
	 * Increment nb objects
	 * @param inc
	 */
	public void incNbObjects( Integer inc ) {
		if ( isFrozenForNbs || ( inc == null ) ) {
			return;
		}
		// get rid of not available state
		if ( nbObjects == null ) {
			// reset to 0
			nbObjects = 0;
		}
		nbObjects += inc;
	}

	public int getNbLinks() {
		return ( nbLinks != null ) ? nbLinks : 0;
	}

    public void incNbLinks( Integer incNbLinks ) {
        
        if ( isFrozenForNbs || ( incNbLinks != null ) ) {
            // get rid of not available state
            if ( nbLinks == null ) {
                // reset to 0
                nbLinks = 0;
            }
            nbLinks += incNbLinks;
        }

    }
    
	/**
	 * Increment nbs<b/>
	 * @param inc
	 */
	public void incNbs( D6LAbstractPackageEntity other ) {

	    if ( isFrozenForNbs || other == null ) {
			return;
		}
	    
	    // Objects
	    incNbObjects( other.nbObjects );

	    // Links
		incNbLinks( other.nbLinks);
		
	}

    /**
     * Increment nbLinks, depending to link direction
     * @param linkDirection
     * @param incNbLinks
     */
    public void incNbLinks( int incNbLinks ) {
        if ( isFrozenForNbs || ( nbLinks == null ) ) {
            nbLinks = 0;
        }
        nbLinks += 1;
    }
    
	/**
	 * Increment nb directed links only, depending to link direction
	 * @param linkDirection
	 * @param incNbLinks
	 */
	public void incNbDirectedLinks( D6LLinkDirectionEnum linkDirection, int incNbLinks ) {
	    
		if ( isFrozenForNbs ) {
			return;
		}
		
        switch ( linkDirection ) {
            
            case DirectedBoth : {
                
                // We don't kown
                nbLinks_fromTo = null;
                nbLinks_toFrom = null;
                
                break;
                
            }
            case DirectedFromTo : {
                if ( nbLinks_fromTo == null ) {
                    nbLinks_fromTo = 0;
                }
                nbLinks_fromTo += 1;
                break;
            }
            default : {
                throw new D6LError( "Link direction '" + linkDirection + "' is not supported"  );
            }
        }
	}
	
    public void setNbLinks( int nbLinks ) {
		if ( isFrozenForNbs ) {
			return;
		}
		
        this.nbLinks = nbLinks;
    }

    public void setNbDirectedLinks( Integer nbLinks_fromTo, Integer nbLinks_toFrom ) {
		if ( isFrozenForNbs ) {
			return;
		}
		
		this.nbLinks_fromTo = nbLinks_fromTo;
        this.nbLinks_toFrom = nbLinks_toFrom;
	}

    /**
     * Set nbs from given lot
     * @param inc
     */
    public void setNbs( D6LAbstractPackageEntity other ) {

		if ( isFrozenForNbs ) {
			return;
		}
		
        if ( other == null ) {
            return;
        }
        
        // Objects
        nbObjects = other.nbObjects;
        // Links
        setNbLinks( other.nbLinks );
        
    }
    
	/**
	 * Get associated LotDependencyType
	 * @param lotType_A
	 * @param lotSubType_A
	 * @param lotType_B
	 * @param lotSubType_B
	 * @return
	 * @throws X6Exception
	 */
	public static D6LPackageTypeEnum getAssociatedLotDependencyType( 
		D6LPackageTypeEnum lotType_A, D6LPackageSubtypeEnum lotSubType_A, 
		D6LPackageTypeEnum lotType_B, D6LPackageSubtypeEnum lotSubType_B,
		boolean sendException
	) throws D6LException {
	    
		// case both business lots
		if  ( ( lotType_A == D6LPackageTypeEnum.BUSINESS_PKG ) && ( lotType_B == D6LPackageTypeEnum.BUSINESS_PKG ) ) {
			return D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY;
		}

		// cases both business lot dependency to business lot
		if  ( ( lotType_A == D6LPackageTypeEnum.BUSINESS_PKG ) && ( lotType_B == D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY ) ) {
			return D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY;
		}
		if  ( ( lotType_B == D6LPackageTypeEnum.BUSINESS_PKG ) && ( lotType_A == D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY ) ) {
			return D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY;
		}

		// case both business lots dependencies 
		if  ( ( lotType_A == D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY ) && ( lotType_B == D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY ) ) {
			return D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY;
		}
		
		// case both technical lots
		if  ( ( lotType_A == D6LPackageTypeEnum.TECHNICAL_PKG ) && ( lotType_B == D6LPackageTypeEnum.TECHNICAL_PKG ) ) {
			return D6LPackageTypeEnum.TECHNICAL_PKG_DEPENDENCY;
		}
		
		// cases both technical lot dependency to technical lot
		if  ( ( lotType_A == D6LPackageTypeEnum.TECHNICAL_PKG ) && ( lotType_B == D6LPackageTypeEnum.TECHNICAL_PKG_DEPENDENCY ) ) {
			return D6LPackageTypeEnum.TECHNICAL_PKG_DEPENDENCY;
		}
		if  ( ( lotType_B == D6LPackageTypeEnum.TECHNICAL_PKG ) && ( lotType_A == D6LPackageTypeEnum.TECHNICAL_PKG_DEPENDENCY ) ) {
			return D6LPackageTypeEnum.TECHNICAL_PKG_DEPENDENCY;
		}
		
		// case both technical lots dependencies 
		if  ( ( lotType_B == D6LPackageTypeEnum.TECHNICAL_PKG_DEPENDENCY ) && ( lotType_A == D6LPackageTypeEnum.TECHNICAL_PKG_DEPENDENCY ) ) {
			return D6LPackageTypeEnum.TECHNICAL_PKG_DEPENDENCY;
		}

		// sorry, unsupported lot dependency
		if ( sendException ) {
			throw new D6LException( "Unknown lot dependency between " + lotType_A + " and " + lotType_B );
		} else {
			
			// No dependency
			return null;
			
		}
		
	}

	public boolean isFrozenForNbs() {
		return isFrozenForNbs;
	}

	public void setFrozenForNbs(boolean isFrozenForNbs) {
		this.isFrozenForNbs = isFrozenForNbs;
	}

}
