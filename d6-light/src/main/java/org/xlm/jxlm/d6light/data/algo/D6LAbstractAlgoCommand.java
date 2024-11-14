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

package org.xlm.jxlm.d6light.data.algo;

import java.lang.reflect.Constructor;

import org.hibernate.Session;
import org.xlm.jxlm.d6light.data.command.D6LAbstractCommand;
import org.xlm.jxlm.d6light.data.command.D6LNotAllocatedException;
import org.xlm.jxlm.d6light.data.command.Stats;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LPackage;
import org.xlm.jxlm.d6light.data.util.D6LUtil;

/**
 * Abstract algo command
 * @author Francois Loison
 *
 */
public abstract class D6LAbstractAlgoCommand 
	extends D6LAbstractCommand 
	implements D6LAlgoCommandIF 
{

    private D6LAlgoIF algo;
    
	/**
     * Default constructor
     */
    public D6LAbstractAlgoCommand() {
        super();
    }
    
    /**
     * Constructor
     * @param conf D6 global conf
     * @param db D6 DB
     * @param listFatalErrors listFatalErrors
     * @param iPass iPass
     * @throws X6Exception 
     */
    public D6LAbstractAlgoCommand( 
        D6LightDataConf conf
    ) throws D6LException {
        super( conf );
    }

    /**
     * Create instance given algo class
     * @param algoClass
     * @param conf
     * @return
     * @throws D6LException
     */
	@SuppressWarnings( "unchecked" )
	public static D6LAlgoCommandIF newInstance( 
		D6LAlgoIF algo,
		D6LightDataConf conf
	) throws D6LException {
		
	    D6LAbstractAlgoCommand algoCommand = null;
	    
	    try {
    		
	        String algoClassName = algo.getClass().getName();
    		
    		// get mapping
    		String algoCommandClassName = D6LAbstractAlgo.propsFactory.getProperty( "command|" + algoClassName );
    		
    		if ( algoCommandClassName == null ) {
    			// try ancestor class
    			Class<? extends D6LAlgoIF> algoClazz = (Class<? extends D6LAlgoIF>) D6LUtil.loadBaseAndPluginClass( algoClassName );
    			Class<? extends D6LAlgoIF> algoAncestorClazz = (Class<? extends D6LAlgoIF>) algoClazz.getSuperclass();
    			algoCommandClassName = D6LAbstractAlgo.propsFactory.getProperty( "command|" + algoAncestorClazz.getName() );
    			
    			if ( algoCommandClassName == null ) {
    				throw new D6LException( "Can't find java mapping for command " + algoClassName );
    			}
    			
    		}
    		
    		// get class
    		Class<? extends D6LAbstractAlgoCommand> algoCommandClass = 
    			(Class<? extends D6LAbstractAlgoCommand>) D6LUtil.loadBaseAndPluginClass( algoCommandClassName );
    		
    		// create instance
    		Constructor<? extends D6LAbstractAlgoCommand> cAlgoCommand = 
    			algoCommandClass.getConstructor( D6LightDataConf.class );
    		algoCommand = cAlgoCommand.newInstance( conf );
    		
	    } catch ( Exception e ) {
	        
	        D6LException.handleException( e );
	    }
	    
	    // set algo
	    algoCommand.algo = algo; 
	    
		return algoCommand;
		
	}
	
	
	/**
	 * Allocate entities to process to benches, then launch extra command based preparation
	 * @throws D6LException
	 */
	@Override
	protected void doPrepare( Session session, final boolean callAlgo ) throws D6LException {
		
		// Delegate to algo
        getAlgo().doPrepare( session, this );
        
	}

	/**
	 * Command based run
	 * @throws D6LException
	 */
	protected void doRun( Session session, final boolean callAlgo ) 
		throws D6LException, D6LNotAllocatedException 
	{
		
		// delegate to algo
    	if ( callAlgo ) {
    	    
    	    D6LAlgoIF algo = getAlgo();
            
    	    // run
    	    algo.doRun( session, this );
    	    
    	    // Finalize single entities
    	    finalizeSingleEntities( session );
    	    
	    	// Allocation finalisation
            allocateLinksAndProcessBusinessLotDependencies( session );
 			
			// finalize lots: count nb objects and links
			// delete empty lots, check children lots
            finalizePackages( session );

    		Stats allStats = new Stats();
	    	doCheckAfter( session, LOGGER.isDebugEnabled(), allStats );
	    	
    	}
    	
	}
	
	/*
	 * Check after algo execution : check all entities are allocated
	 * 
	 * @param txn
	 * @param stats
	 * @param isDebug
	 * @throws X6Exception 
	 */
	protected void doCheckAfter( Session session, boolean debugOnError, Stats stats ) throws D6LException {
		
		// count lots
		stats.nbObjects = db.inGraph.vertexSet().size();
		stats.nbLinks   = db.inGraph.edgeSet().size();

		// check allocation is OK
		stats.nbEntityErrors        = (int) db.daoEntityRegistry.getVertices( session, D6LPackage.UNALLOCATED ).count();
		stats.nbEntityLinkErrors    = (int) db.daoEntityRegistry.getEdges( session, D6LPackage.UNALLOCATED ).count();
		
		if ( debugOnError && (stats.nbEntityErrors != 0) ) {
			
			// debug unallocated objects
			LOGGER.fatal( "Unallocated entities:" );
			if ( LOGGER.isDebugEnabled() ) {
				
				// objects
				doCheckAfterDebugObjects( session );
				
			}
		}

		// debug unallocated links
		if ( debugOnError && (stats.nbEntityLinkErrors != 0) ) {
			// debug unallocated links
			LOGGER.fatal( "Unallocated entity links:" );
			if ( LOGGER.isDebugEnabled() ) {
				doCheckAfterDebugLinks( session );

			}
		}

		if ( stats.nbEntityErrors != 0 ) {
			String message = stats.nbEntityErrors + " unallocated entities";
			LOGGER.fatal( message );
			addFatalError( message );
		}
		if ( stats.nbEntityLinkErrors != 0 ) {
			String message = stats.nbEntityLinkErrors + " unallocated entity links";
			LOGGER.fatal( message );
			addFatalError( message );
		}
		if ( stats.nbEntityUniLinkErrors != 0 ) {
			String message = stats.nbEntityUniLinkErrors + " unallocated entity uni links";
			LOGGER.fatal( message );
			addFatalError( message );
		}

	}

    private void doCheckAfterDebugLinks( Session session )
    {
    	db.daoEntityRegistry.getEdges( session, D6LPackage.UNALLOCATED )
    		.forEach(
    			link -> {
            		LOGGER.debug( "Link " + link.getId()
            			+ " roleA: "
            			+ db.inGraph.getEdgeSource( link ) + " roleB: "
            			+ db.inGraph.getEdgeTarget( link )
            		);
    			}
    		);
    }

    private void doCheckAfterDebugObjects( Session session )
    {
    	db.daoEntityRegistry.getVertices( session, D6LPackage.UNALLOCATED )
		.forEach(
			lot -> {
        		LOGGER.debug("Lot " + lot.getId()
        				+ " name: "
        				+ lot.getDisplay()
        		);
			}
		);
    }

	@Override
	public D6LAlgoIF getAlgo() {
		return this.algo;
	}

    @Override
    public String getConfId()
    {
        String confId;
        
        confId = getAlgo().getConf().getId();
        
        return confId;
    }

    @Override
    public String getDisplayName()
    {
        // Get name from algo
        return getAlgo().getName();
    }

}
