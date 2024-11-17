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

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LAbstractPackageEntity;
import org.xlm.jxlm.d6light.data.model.D6LEdge;
import org.xlm.jxlm.d6light.data.model.D6LEntityRegistry;
import org.xlm.jxlm.d6light.data.model.D6LLinkDirectionEnum;
import org.xlm.jxlm.d6light.data.model.D6LPackageEdge;
import org.xlm.jxlm.d6light.data.model.D6LPackageVertex;
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
    private D6LAbstractPackageEntity cacheSinglePackage = null;
	
	protected List<String> listFatalErrors = null;

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
	public final void execute( Session session ) throws D6LException, D6LNotAllocatedException
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
			Stream<D6LVertex> unallocatedVertices = daoEntities.getVertices( session, D6LPackageVertex.UNALLOCATED );
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

            	cacheSinglePackage = new D6LPackageVertex( 
            		D6LPackageTypeEnum.BUSINESS_PKG, D6LPackageSubtypeEnum.SINGLE_LOT 
            	);
            	
            	// persist it
            	session.persist( cacheSinglePackage );
                
            }
            
        	// assign to single lot
        	vertex.setPackageEntity( cacheSinglePackage );
        	
        	// save entity
        	session.merge( vertex );
        	
        }
    }

	protected void allocateLinksAndProcessBusinessLotDependencies( Session session )
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
				
				// browse lot starting from highest level (leaves)
				finished = buildBusinessLotDependencies( session, finished );
				
			}

		}

	/**
	 * Allocate links and process lot dependencies from links
	 * @param txn
	 * @param daoRef
	 * @param iPass
	 * @param iLinkLevel
	 * @throws D6NotAllocatedException
	 * @throws D6LException
	 */
	public void allocateLinksAndProcessLotDependenciesFromLinks( Session session ) 
		throws D6LNotAllocatedException, D6LException 
	{

        // Link driven lot dependencies
		LOGGER.info( "Process Lot Dependencies from entity links" );
		
		allocateLinksAndProcessLotDependenciesFromLinks( 
			session, db.inGraph.edgeSet() 
		);
		
	}

    private void allocateLinksAndProcessLotDependenciesFromLinks( 
        Session session,
        Set<D6LEdge> entityLinks 
    )
        throws D6LException, D6LNotAllocatedException
    {

    	for ( D6LEdge link : entityLinks ) {
            
        	// get roleA
        	D6LVertex entityA = db.inGraph.getEdgeSource( link );
        	// We need to refresh graph entities
        	entityA = session.get( D6LVertex.class, entityA.getId() );
        	
            // get roleB
        	D6LVertex entityB = db.inGraph.getEdgeTarget( link );
        	// We need to refresh graph entities
        	entityB = session.get( D6LVertex.class, entityB.getId() );
            
        	// check if we have a lot dependency
        	long entityA_lotId = entityA.getPackageEntity().getId();
        	long entityB_lotId = entityB.getPackageEntity().getId();
        	
        	// check they are allocated
        	if ( entityA_lotId == D6LPackageVertex.UNALLOCATED.getId() ) {
        		throw new D6LNotAllocatedException( 
        			"Entity " + entityA.getDisplay() + " is not allocated", D6LPackageVertex.UNALLOCATED
        		);
        	}
        	if ( entityB_lotId == D6LPackageVertex.UNALLOCATED.getId() ) {
        		throw new D6LNotAllocatedException( 
        			"Entity " + entityB.getDisplay() + " is not allocated", D6LPackageVertex.UNALLOCATED
        		);
        	}

        	if ( entityA_lotId == entityB_lotId ) {
        		
        		// Assign package A or B to link
       			link.setPackageEntity( entityA.getPackageEntity() );
       			
        	} else {
        		
        		// We have a lot dependency
        		
        		// get packages
        		D6LPackageEdge lotDependency = getOrCreatePackageDependency(
        			session,
        			link, 
        			( D6LPackageVertex ) entityA.getPackageEntity(), 
        			( D6LPackageVertex ) entityB.getPackageEntity()
        		);

    			// set link lot to lot dependency
    			link.setPackageEntity( lotDependency.getPackageEntity() );
        			
        	}

        	// save link
        	session.merge( link );
        	
        }
        
    }

    private boolean buildBusinessLotDependencies( Session session, boolean finished )
        throws D6LException
    {
        
        boolean newFinished = finished;
        
    	// browse lot dependencies
    	for ( D6LPackageEdge lotDependencyEdge : db.outGraph.edgeSet() ) {
    		
    		// it is a dependency implying a Business Lot dependency?
    		D6LPackageVertex lotDependencyA = db.outGraph.getEdgeSource( lotDependencyEdge );
    		long idBusinessLotA = lotDependencyA.getId();

    		D6LPackageVertex lotDependencyB = db.outGraph.getEdgeTarget( lotDependencyEdge );
    		
    		/*
    		long idBusinessLotB = lotDependencyB.getIdLot();
    		if ( ( idBusinessLotB == D6LPackage.ID_NO_LOT ) && ( level != 1 ) ) {
    			throw new D6LException(
    				"No business parent lot for lot dependency "
    					+ lotDependencyB.getId() + " ( "
    					+ lotDependencyB.getType( db ) + " )");
    		}
			*/
    		/*
    		if ( ( idBusinessLotA != D6LPackage.ID_NO_LOT ) && ( idBusinessLotB != D6LPackage.ID_NO_LOT ) && ( idBusinessLotA != idBusinessLotB ) ) {
    			// we have a business lot dependency
    			newFinished = processBusinessLotDependency( 
    			    session, newFinished, 
    			    lotDependency, 
    			    lotDependencyA, idBusinessLotA, lotDependencyB, idBusinessLotB 
    			);

    		}
			*/
    		
			newFinished = processBusinessPackageDependency( 
			    session, newFinished, 
			    lotDependencyEdge, lotDependencyA, 
			    lotDependencyA, lotDependencyB, lotDependencyB 
			);
			
        }
    	
        return newFinished;
    }

    private boolean processBusinessPackageDependency( 
        Session session, boolean finished, 
        D6LPackageEdge lotDependency, D6LPackageVertex lotDependencyA,
        D6LPackageVertex lot_A, D6LAbstractPackageEntity lotDependencyB, D6LPackageVertex lot_B 
    )
        throws D6LException
    {
        
        boolean newFinished = finished;
        
        // existing?
        D6LPackageEdge businessLotDependency = getExistingPackageDependency( session, lot_A, lot_B, false );
        if ( businessLotDependency == null ) {
        	
        	// not finished
            newFinished = false;
        	
        	// create it
        	businessLotDependency = new D6LPackageEdge(
        		D6LPackageTypeEnum.BUSINESS_PKG_DEPENDENCY
        	);
        	
        	// Add to graph
        	db.outGraph.addEdge( lot_A, lot_B, businessLotDependency );
        	
        	// set direction from link
        	businessLotDependency.setLinkDirection( lotDependency.getLinkDirection() );

        	// save it
        	businessLotDependency.save( session );
        }
        
        // save it
        lotDependency.save( session );
        
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
		D6LEdge link, D6LPackageVertex pck_A, D6LPackageVertex pck_B
	)
		throws D6LException 
	{
	    
		D6LPackageEdge lotDependency = getExistingPackageDependency( session, pck_A, pck_B, true );

		if ( lotDependency == null) {
			
			// create it
			D6LPackageTypeEnum depType = D6LAbstractPackageEntity.getAssociatedLotDependencyType(
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
            lotDependency.setNbDirectedLinks( 1, 0 );
	        
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
	 * @throws D6LException 
	 */
	public D6LPackageEdge getExistingPackageDependency( 
	    Session session, D6LPackageVertex pkgFrom, D6LPackageVertex pkgTo, 
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
                lotLink.incNbDirectedLinks( 1, 0 );
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
                lotLink.incNbDirectedLinks( 0, 1 );
            }
            
			// save it
            session.merge( lotLink );
			
		}

		return lotLink;
	}

	protected void finalizePackages( 
		Session session
	) throws D6LException {
		
        LOGGER.info( "********** Start finalize lots" );
		
		// count objects and links for technical lots

		// only for last finalization because it is very costly
		
	    LOGGER.info( "Cummulative sums - may be long" );
	    /*
		// all lots - non cummulative sums
        LOGGER.info( "Last finalization - sum entities per lots" );
        SumEntitiesRunnable sumEntitiesRunnable_0 = 
            new SumEntitiesRunnable( db, txn, true, cmdRuntimeConfig.isSeparateFromToLotLinks );
        
        startThread( sumEntitiesRunnable_0 );
  		
        LOGGER.info( "Last finalization - sum entities per lot links" );  
        SumEntitiesRunnable sumEntitiesRunnable_1 = 
            new SumEntitiesRunnable( db, txn, false, cmdRuntimeConfig.isSeparateFromToLotLinks );
        
        startThread( sumEntitiesRunnable_1 );
         */
		// all lots - cummulate sums
        LOGGER.info( "Cummulative sums" );
        
        LOGGER.info( "Levels for lots" );
        		
        LOGGER.info( "********** End finalize lots" );
        
	}

	/**
	 * Thread to sum entities per lot and lot link
	 * 
	 * @author Loison
	 */
	class SumEntities
	{
	
	    private Session session;
	
	    private boolean isLot;
	    
	    private boolean isSeparateFromToLinks;
	
	    public SumEntities( 
	    	Session session, boolean isLot, boolean isSeparateFromToLotLinks
	    ) {
	        super();
	        this.session = session;
	        this.isLot = isLot;
	        this.isSeparateFromToLinks = isSeparateFromToLotLinks;
	    }
	
	    public void run() throws D6LException
	    {
            if ( isLot ) {
                sumEntitiesPerLot( session );
            } else {
                sumEntitiesPerLotLinks( session );
            }
	
	    }
	
	    private void sumEntitiesPerLot( Session session )
	        throws D6LException
	    {
            db.daoEntityRegistry.getPackages( session )
            	.forEach(
            		pkg -> {
            			if ( !pkg.isFrozenForNbs() ) {
    	                	
    	                    // Nb objects
    	                    long nbObjects = db.daoEntityRegistry.getVertices( session, pkg ).count();
    	                    long nbLinks   = db.daoEntityRegistry.getEdges( session, pkg ).count();
    	    
    	                    pkg.setNbObjects( (int) nbObjects );
    	                    pkg.setNbLinks( (int) nbLinks );
    	                    
    	                    
    	                    // update lot
    	                    pkg.save( session );
            			}
            		}
            	);
	    }
	    
	    private void sumEntitiesPerLotLinks( Session session ) {

            db.daoEntityRegistry.getPackageEdges( session )
	        	.forEach(
	        		pkgEdge -> {
	        			if ( !pkgEdge.isFrozenForNbs() ) {
		                	
		                    // Nb objects
		                    long nbObjects = db.daoEntityRegistry.getVertices( session, pkgEdge ).count();
		                    long nbLinks   = db.daoEntityRegistry.getEdges( session, pkgEdge ).count();
		    
		                    pkgEdge.setNbObjects( (int) nbObjects );
		                    pkgEdge.setNbLinks( (int) nbLinks );
		                    
		                    
		                    // update lot
		                    pkgEdge.save( session );
	        			}
	        		}
	        	);
	    }
	    
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
	 * @throws D6LNotAllocatedException 
	 * @throws Exception
	 */
	protected abstract void doRun( Session session, final boolean callAlgo ) throws D6LException, D6LNotAllocatedException;


    /**
     * Algo short name, for threads.
     * @return
     */
    abstract protected String getShortName();
    
}
