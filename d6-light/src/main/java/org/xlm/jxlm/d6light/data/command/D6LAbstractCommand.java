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

package org.xlm.jxlm.d6light.data.command;

import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.xlm.jxlm.audit.d6.data.command.D6NotAllocatedException;
import org.xlm.jxlm.audit.d6.data.lot.D6LotLink;
import org.xlm.jxlm.audit.x6.common.X6Exception;
import org.xlm.jxlm.audit.x6.common.data.lot.D6LotTypeEnum;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LEdge;
import org.xlm.jxlm.d6light.data.model.D6LEntityRegistry;
import org.xlm.jxlm.d6light.data.model.D6LLinkDirectionEnum;
import org.xlm.jxlm.d6light.data.model.D6LPackage;
import org.xlm.jxlm.d6light.data.model.D6LPackageData;
import org.xlm.jxlm.d6light.data.model.D6LPackageEdge;
import org.xlm.jxlm.d6light.data.model.D6LPackageEntityIF;
import org.xlm.jxlm.d6light.data.model.D6LVertex;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

/**
 * Base abstract class for commands
 * @author Loison
 *
 */
public abstract class D6LAbstractCommand implements D6LCommandIF {

    protected static final Logger LOGGER = LogManager.getLogger( D6LAbstractCommand.class );
	
	/** Configuration **/
	protected D6LightDataConf conf;
	
	protected final D6LDb db = D6LDb.getInstance();
	
    /** cache for single lots **/
    private D6LPackage cacheSinglePackage = null;
	
	/**
	 * Default constructor
	 */
	public D6LAbstractCommand() {
		super();
	}
	
	/**
	 * Constructor
	 * @param conf
	 * @param db
	 * @param listFatalErrors
	 * @throws D6LException 
	 */
	public D6LAbstractCommand( D6LightDataConf conf ) throws D6LException {
		super();
		this.conf= conf;
	}

	@Override
	public final void execute( Session session ) throws D6LException
	{
		
        doPrepare( session, true );
        
        // flush session
        session.flush();
        
        doRun( session, true );
		
        // flush session
        session.flush();
        
	}
	
	protected void finalizeSingleEntities( Session session ) throws D6LException {

		LOGGER.info( "Finalize single entities" );
		
        // clear single lots cache
        cacheSinglePackage = null;
        
		D6LEntityRegistry daoEntities = db.daoEntityRegistry;
		
		// Browse unallocated objects
		try (
			Stream<D6LVertex> unallocatedVertices = daoEntities.getVertices( session, D6LPackage.UNALLOCATED );
		) {
			unallocatedVertices.forEach(
				vertex -> {

					// single objects have not been allocated because they have no
					// links
					// technical lots lotissement is driven by links
					// so single objects have not been lotized

					// count links
					int nbLinksRoleA = db.inGraph.incomingEdgesOf( vertex ).size();
					int nbLinksRoleB = db.inGraph.outgoingEdgesOf( vertex ).size();
					
					finalizeSingleEntity( session, vertex, nbLinksRoleA, nbLinksRoleB );
					
				}
			);
		} catch ( D6LError error ) {
			D6LException.handleThrowable( error );
		}

	}

    private void finalizeSingleEntity( 
        Session session, D6LVertex vertex, 
        int nbLinksRoleA, int nbLinksRoleB 
    )
        throws D6LError
    {
        if ( ( nbLinksRoleA == 0 ) && ( nbLinksRoleB == 0 ) ) {
        	// yes, it's a single object

        	// find single lot for current bench and pass
            
            // Try cache
            if ( cacheSinglePackage == null ) {

            	cacheSinglePackage = new D6LPackage( D6LPackageTypeEnum.BUSINESS_PKG, D6LPackageSubtypeEnum.SINGLE_LOT );
            	
            	// persist it
            	session.persist( cacheSinglePackage );
                
            }
            
        	// assign to single lot
        	vertex.setPackage( cacheSinglePackage );
        	
        	// save entity
        	session.merge( vertex );
        	
        }
    }

	private void allocateLinksAndProcessBusinessLotDependencies( Session session )
			throws D6LNotAllocatedException, D6LException 
		{
			// first build business lot dependencies driven by objects and links
			// belong to lot

			allocateLinksAndProcessLotDependenciesFromLinks( session );
					
			// then build business lot dependencies coming from lots belonging to
			// business lot
			
			boolean finished = false;
			
			while ( !finished ) {
				
				// be optimistic
				finished = true;
				
				// we need lot level to be set 
				setLotLevelAndChildrenNbsAndPassLeaf( txn, false );
		
				// browse lot starting from highest level (leaves)
				finished = buildBusinessLotDependencies( txn, finished );
				
				// Deal with level 1 lots links with different roles levels
				tuneLinksWithDifferentRoleLevels( txn );
				
			}

		}

	/**
	 * Allocate links and process lot dependencies from links
	 * @param txn
	 * @param daoRef
	 * @param iPass
	 * @param iLinkLevel
	 * @throws D6NotAllocatedException
	 * @throws X6Exception
	 */
	public void allocateLinksAndProcessLotDependenciesFromLinks( Session session ) 
		throws D6LNotAllocatedException, D6LException 
	{

        // Link driven lot dependencies
		LOGGER.info( "Process Lot Dependencies from entity links" );
		
		// Special package for new links between existing objects
		D6LPackage newExistingLinkLot = null;
		
		newExistingLinkLot = allocateLinksAndProcessLotDependenciesFromLinks( 
			session, newExistingLinkLot, db.inGraph.edgeSet() 
		);
		
	}

    private D6LPackage allocateLinksAndProcessLotDependenciesFromLinks( 
        Session session, D6LPackage existingLinkLot,
        Set<D6LEdge> entityLinks 
    )
        throws D6LException, D6LNotAllocatedException
    {
        D6LPackage newExistingLinkLot = existingLinkLot;
        
        for ( D6LEdge link : entityLinks ) {
            
        	// get roleA
        	D6LVertex entityA = db.inGraph.getEdgeSource( link );
            // get roleB
        	D6LVertex entityB = db.inGraph.getEdgeTarget( link );
            
            
        	// check if we have a lot dependency
        	long entityA_lotId = entityA.getPackage().getId();
        	long entityB_lotId = entityB.getPackage().getId();
        	
        	// check they are allocated
        	if ( entityA_lotId == D6LPackage.UNALLOCATED.getId() ) {
        		throw new D6LNotAllocatedException( 
        			"Entity " + entityA.getDisplay() + " is not allocated", D6LPackage.UNALLOCATED
        		);
        	}
        	if ( entityB_lotId == D6LPackage.UNALLOCATED.getId() ) {
        		throw new D6LNotAllocatedException( 
        			"Entity " + entityB.getDisplay() + " is not allocated", D6LPackage.UNALLOCATED
        		);
        	}

        	if ( entityA_lotId == entityB_lotId ) {
        		
        		// Assign package A or B to link
       			link.setPackageEntity( entityA.getPackage() );
       			
        	} else {
        		
        		// We have a lot dependency
        		
        		// get packages
        		D6LPackageEdge lotDependency = getOrCreatePackageDependency(
        			session,
        			link, 
        			entityA.getPackage(), entityB.getPackage()
        		);

    			// set link lot to lot dependency
    			link.setPackageEntity( lotDependency );
        			
        	}

        	// save link
        	session.merge( link );
        	
        }
        
        return newExistingLinkLot;
        
    }

    private boolean buildBusinessLotDependencies( Session session, boolean finished )
        throws D6LException
    {
        
        boolean newFinished = finished;
        
    	// browse lot dependencies
    	for ( D6LPackageEdge lotDependency : db.outGraph.edgeSet() ) {
    		
    		/*
    		// it is a dependency implying a Business Lot dependency?
    		D6LPackage lotDependencyA = db.outGraph.getEdgeSource( lotDependency );
    		long idBusinessLotA = lotDependencyA.getId();

    		D6LotIF lotDependencyB = D6AbstractLot.getAbstractLot( db, txn, lotDependency.getIdLotRoleB() );							
    		long idBusinessLotB = lotDependencyB.getIdLot();
    		if ( ( idBusinessLotB == D6LPackage.ID_NO_LOT ) && ( level != 1 ) ) {
    			throw new D6LException(
    				"No business parent lot for lot dependency "
    					+ lotDependencyB.getId() + " ( "
    					+ lotDependencyB.getType( db ) + " )");
    		}

    		if ( ( idBusinessLotA != D6LPackage.ID_NO_LOT ) && ( idBusinessLotB != D6LPackage.ID_NO_LOT ) && ( idBusinessLotA != idBusinessLotB ) ) {
    			// we have a business lot dependency
    		*/	
    			newFinished = processBusinessLotDependency( 
    			    session, newFinished, 
    			    lotDependency, 
    			    lotDependencyA, idBusinessLotA, lotDependencyB, idBusinessLotB 
    			);

    		/*
    		}
    		*/
        }
        return newFinished;
    }

	    private boolean processBusinessLotDependency( 
	        Session session, boolean finished, D6LPackageEntityIF lotDependency, D6LPackageEntityIF lotDependencyA,
	        long idBusinessLotA, D6LPackageEntityIF lotDependencyB, long idBusinessLotB 
	    )
	        throws D6LException
	    {
	        
	        boolean newFinished = finished;
	        
	        // existing?
	        D6LotLink businessLotDependency = getExistingLotDependency( txn, idBusinessLotA, idBusinessLotB, false );
	        if ( businessLotDependency == null ) {
	        	
	        	// not finished
	            newFinished = false;
	        	
	        	// create it
	        	businessLotDependency = new D6LotLink(
	        		D6LotTypeEnum.BUSINESS_LOT_DEPENDENCY, null, iPass,
	        		idBusinessLotA, idBusinessLotB, 
	        		lotDependencyA.getLinkLevel(), lotDependencyB.getLinkLevel()
	        	);
	        	
	        	// parent is business lot common parent
	        	D6LPackage lot_A = db.daoLots.byId.get( txn, idBusinessLotA, null );
	        	D6LPackage lot_B = db.daoLots.byId.get( txn, idBusinessLotA, null );
	        	
	        	if ( 
	        	      ( lot_A != null ) && ( lot_B != null ) 
	        	      &&
	        	      // same parent?
	        	      ( lot_A.getIdLotParent() == lot_B.getIdLotParent() )
	        	) {
	        		businessLotDependency.setIdLotParent( lot_A.getIdLotParent() );
	        	}
	        	
	        	// set direction from link
	        	businessLotDependency.setLinkDirection( lotDependency.getLinkDirection() );

	        	// save it
	        	businessLotDependency.save( db, txn );
	        }
	        
	        // parent is businessLotDependency
	        lotDependency.setIdLotParent( businessLotDependency.getId() );
	        
	        // save it
	        lotDependency.save( db, txn );
	        return newFinished;
	    }
		
		protected void addFatalError( String message ) throws D6LException {
			if ( listFatalErrors != null ) {
				listFatalErrors.add( message );
			} else {
				throw new D6LException( message );
			}
		}

	private D6LPackageEdge getOrCreatePackageDependency(
        Session session,
		D6LEdge link, D6LPackage pck_A, D6LPackage pck_B
	)
		throws D6LException 
	{
	    
		D6LPackageEdge lotDependency = getExistingPackageDependency( session, pck_A, pck_B, true );

		if ( lotDependency == null) {
			
			// create it
			D6LPackageTypeEnum depType = D6LPackageData.getAssociatedLotDependencyType(
					pck_A.getPackageType(), pck_A.getPackageSubtype(),
					pck_B.getPackageType(), pck_B.getPackageSubtype(), 
					false
            );
			
			if ( depType == null ) {
				
				// Error
				throw new D6LException( "Unknown lot dependency between lot '" + pck_A + "' and lot '" + pck_B + "'" );

			}
			
			lotDependency = new D6LPackageEdge( depType );

			// Add to graph
			db.outGraph.addEdge( pck_A, pck_B, lotDependency );
			
			// set direction from link
			lotDependency.setLinkDirection( link.getLinkDirection() );
			
            // Reset directed lot links
            // Count forward link
            lotDependency.getData().setNbDirectedLinks( 1, 0 );
	        
			// save it
			session.merge( lotDependency );

		}

		return lotDependency;
	}

	/**
	 * get existing lot dependency
	 * @param txn
	 * @param pkgFrom
	 * @param pkgTo
	 * @return
	 * @throws X6Exception 
	 */
	public D6LPackageEdge getExistingPackageDependency( 
	    Session session, D6LPackage pkgFrom, D6LPackage pkgTo, 
	    boolean isCountDirectedLotLinks
	) throws D6LException {
		
	    if ( pkgFrom.getId() == pkgTo.getId() ) {
	        throw new D6LException( "Search lots with idFrom == idTo is not permitted" );
	    }
	    
	    // lot dependency exists?
	    D6LPackageEdge forwardLotLink = db.outGraph.getEdge( pkgFrom, pkgTo );
		// try other directions...
	    D6LPackageEdge reverseLotLink = db.outGraph.getEdge( pkgTo, pkgFrom );

		// exists?
	    D6LPackageEdge lotLink = null;
		if ( forwardLotLink != null ) {
			// forward lot link OK
			lotLink = forwardLotLink;
			
            // Directed links
            if ( isCountDirectedLotLinks ) {
                // Count directed link
                lotLink.getData().incNbDirectedLinks( 1, 0 );
                // save it
                session.merge( lotLink );
            }
            
		}

		if ( reverseLotLink != null ) {
			// forward lot link OK
			lotLink = reverseLotLink;
			// but direction is bi-dir
			lotLink.setLinkDirection( D6LLinkDirectionEnum.DirectedBoth );

            // Directed links
            if ( isCountDirectedLotLinks ) {
                // Count directed link
                lotLink.getData().incNbDirectedLinks( 0, 1 );
            }
            
			// save it
            session.merge( lotLink );
			
		}

		return lotLink;
	}

	/**
	 * Prepare before execution
	 * @param session 
	 */
	protected abstract void doPrepare( Session session, final boolean callAlgo ) throws D6LException;

	/**
	 * Actual command execution
	 * @param session 
	 * 
	 * @param txn
	 * @throws D6LException
	 * @throws Exception
	 */
	protected abstract void doRun( Session session, final boolean callAlgo ) throws D6LException;


    /**
     * Algo short name, for threads.
     * @return
     */
    abstract protected String getShortName();
    
}
