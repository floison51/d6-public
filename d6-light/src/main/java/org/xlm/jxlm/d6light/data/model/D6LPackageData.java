package org.xlm.jxlm.d6light.data.model;

import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

public class D6LPackageData {
	
	final D6LEntityIF entity;

    private Integer nbObjects = null;
    private Integer nbLinks = null;
    
    /** For bidirectional links : nb links from->to **/
    private Integer nbLinks_fromTo = null;
    
    /** For bidirectional links : nb links to->from **/
    private Integer nbLinks_toFrom = null;

    /** When true, entities nbs cn't be modified **/
    private boolean isFrozenForNbs = false;
    
   public D6LPackageData( D6LEntityIF entity ) {
		super();
		this.entity = entity;
	}

/**
     * Increment nb links<b/>
     * This method doesn't care of link direction, it is overriden by D6LotLink to separate from/to and to/from links
     * @param inc
     */
    public void incNbDirectedLinks( Integer incNbLinks_fromTo, Integer incNbLinks_toFrom ) {
        
        if ( isFrozenForNbs || !( entity instanceof D6LPackageEdge ) ) {
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
	public void incNbs( D6LPackageData pckData ) {

	    if ( isFrozenForNbs || pckData == null ) {
			return;
		}
	    
	    // Objects
	    incNbObjects( pckData.nbObjects );

	    // Links
		incNbLinks( pckData.nbLinks);
		
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
            case DirectedToFrom : {
                if ( nbLinks_toFrom == null ) {
                    nbLinks_toFrom = 0;
                }
                nbLinks_toFrom += 1;
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
    public void setNbs( D6LPackageData pckData ) {

		if ( isFrozenForNbs ) {
			return;
		}
		
        if ( pckData == null ) {
            return;
        }
        
        // Objects
        nbObjects = pckData.nbObjects;
        // Links
        setNbLinks( pckData.nbLinks );
        
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

}
