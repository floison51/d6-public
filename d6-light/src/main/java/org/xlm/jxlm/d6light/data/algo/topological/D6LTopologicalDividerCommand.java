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

package org.xlm.jxlm.d6light.data.algo.topological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xlm.jxlm.d6light.data.algo.D6LAbstractDividerAlgoCommand;
import org.xlm.jxlm.d6light.data.algo.D6LDividerAlgoIF;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier.BomSimplifierKindEnum;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier.MatchResult;
import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.conf.TopologicalDividerType;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.job.D6LJobIF;
import org.xlm.jxlm.d6light.data.measures.D6LEntityDirectedLinkStats;
import org.xlm.jxlm.d6light.data.model.D6LEdge;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;
import org.xlm.jxlm.d6light.data.model.D6LPackage;
import org.xlm.jxlm.d6light.data.model.D6LVertex;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

/**
 * Topological divider command
 * @author Loison
 *
 */
public class D6LTopologicalDividerCommand extends D6LAbstractDividerAlgoCommand {

	/** For debug, activate/desactivate logic controlling parent lots of bom simplifier lots **/
	public static final boolean REWORK_BOM_SIMPLIFIER_LOTS_PARENTS = true;
	
	private static final Logger LOGGER = LogManager.getLogger( D6LTopologicalDividerCommand.class ); 
	
	public D6LTopologicalDividerCommand( D6LightDataConf conf ) throws D6LException {
		super( conf );
	}
	
	@Override
	public void doPrepare( final boolean callAlgo ) throws D6LException {
		
	    LOGGER.info( "Start prepare algo" );

	    // Call ancestor but don't call algo because we do after
		super.doPrepare( false );
		
		D6LAbstractTopologicalDivider topologicalDividerAlgo = 
			(D6LAbstractTopologicalDivider) getAlgo();
		
		// Allocate single and components, if needed
		
		// Ask conf if we need to allocate singles in a special lot
		
		// Use algo value
		boolean isSinglesAllocation = topologicalDividerAlgo.isAllowsSinglesAllocation();
 		
        if ( !topologicalDividerAlgo.isAllowsSinglesAllocation() ) {
            LOGGER.info( "Topological divider '" + topologicalDividerAlgo.getName() + "' doesn't single allocation, any <Single> parameters are ignored." );
        }

		AbstractAlgoType algoConf = topologicalDividerAlgo.getConf();
		if ( topologicalDividerAlgo.isAllowsSinglesAllocation() && ( algoConf instanceof TopologicalDividerType ) ) {
		    
		    TopologicalDividerType topoAlgoConf = ( TopologicalDividerType ) algoConf;
		    
		    // Get conf value
		    if ( topoAlgoConf.getSingles() != null ) {
		        
		        Boolean isSinglesAllocationFromConf = topoAlgoConf.getSingles().isAllocateSingles();
		        
		        // If defined, it overrides general setting
		        if ( isSinglesAllocationFromConf != null ) {
		            isSinglesAllocation = isSinglesAllocationFromConf;
		        }
		        
		    }
		    
		}
		
		if ( isSinglesAllocation || topologicalDividerAlgo.isNeedBomSimplification() ) {
			
            // allocate single and/or simplify bom
            allocateSinglesAndBomSimplification( 
                isSinglesAllocation,
                topologicalDividerAlgo.isNeedBomSimplification()
            );
			
		}

		// Delegate to algo
    	if ( callAlgo ) {
			getAlgo().doPrepare( this );
    	}

        LOGGER.info( "End single allocation and BOM simplification" );
        
	}

/*
	private class PrepareForBenchRunnable implements Runnable {
	    
	    private final Transaction txn;
        private final D6AbstractTopologicalDivider topologicalDividerAlgo;
        private final boolean isSinglesAllocation;
        private final RegExpType allocateSinglesIfParentContainerRegEx; 
        private final D6Bench bench;

        public PrepareForBenchRunnable( 
	        Transaction txn, D6AbstractTopologicalDivider topologicalDividerAlgo,
	        boolean isSinglesAllocation,
	        RegExpType allocateSinglesIfParentContainerRegEx, 
	        D6Bench bench
	    ) {
	        super();
	        this.txn = txn;
	        this.topologicalDividerAlgo = topologicalDividerAlgo;
	        this.isSinglesAllocation = isSinglesAllocation;
	        this.allocateSinglesIfParentContainerRegEx = allocateSinglesIfParentContainerRegEx;
	        this.bench = bench;
	    }

        @Override
        public void run()
        {
            try {
                
                // skip no bench
                if ( bench.getId() == D6Bench.NO_BENCH ) {
                    return;
                }
                
                // allocate single and/or simplify bom
                allocateSinglesAndBomSimplification( 
                    txn, bench, iPass,
                    isSinglesAllocation, allocateSinglesIfParentContainerRegEx,
                    topologicalDividerAlgo.isNeedBomSimplification(),
                    topologicalDividerAlgo.isNeedBomSimplifiedEntitiesRemovedFromBench()
                );
                
                // save bench
                bench.save( db, txn );
                
            } catch ( D6LException e ) {
                throw new X6Error( e );
            }
        }
	}
*/

	private void allocateSinglesAndBomSimplification( 
		boolean allocateSingles, 
		boolean isNeedBomSimplification 
	) throws D6LException {
		
	    LOGGER.info( "Allocate singles and do bom simplification" );
	    
	    List<D6LJobIF<D6LEntityIF>> postActions = new ArrayList<>();
	    
        D6LDividerAlgoIF dividerAlgo = (D6LDividerAlgoIF) getAlgo();
        
		// create Bom simplification lots lot for current bench and pass
        Map<BomSimplifierKindEnum,D6LPackage> mapBomSimplificationLots = new HashMap<>();
        
        if ( isNeedBomSimplification && ( dividerAlgo instanceof D6LTopologicalDividerIF )) {
            
            // Get bom simplifications
            List<D6LAbstractBomSimplifier> listBomSimplifiers = ( ( D6LTopologicalDividerIF ) dividerAlgo ).getListBomSimplifiers();
            
            // Create lots: Components, Kits, ...
            for ( D6LAbstractBomSimplifier bomSimplifier : listBomSimplifiers ) {
            
                // Check 
            	D6LPackage bomSimplificationLot = 
            		createBomSimplificationLot( dividerAlgo.getProducesLotType(), bomSimplifier );
        			
        		mapBomSimplificationLots.put( bomSimplifier.getKind(), bomSimplificationLot );

        	}
    		
        }
	
		// find single lot for current bench
        D6LPackage singleLot = null;
		
		if ( allocateSingles ) {		
			
		    // Use a thread safe method to get single lot
		    // Because some threads may creating another single lot in the mean while...
		    singleLot = db.daoEntityRegistry.getOrCreateSingleLot( 
	    		dividerAlgo.getProducesLotType() 
	    	);
		}
		
		// allocate single and component objects
		
		// record objects already processed
		Set<Integer> idEntitiesProcessed = new HashSet<>();
	
		// browse vertices belonging to bench
        LOGGER.info( "  vertices" );
		for ( D6LVertex v : inGraph.vertexSet() ) {
			
			// select only unallocated objects
			if ( v.getIdPackage() != D6LPackage.TECH_ID_UNALLOCATED ) {
				continue;
			}
			
			// Do allocation and get post actions
			List<D6LJobIF<D6LEntityIF>> curPostActions = allocateSingleAndBomSimplificationAndTopOfBom( 
				v, mapBomSimplificationLots, singleLot, idEntitiesProcessed
			);
			
			postActions.addAll( curPostActions );
			
		}
		
		// Execute post actions
        for ( D6LJobIF<D6LEntityIF> postAction : postActions ) {
            postAction.doJob( null );
        }
            
		// allocate links to component lot, if both roles are in component lot
        LOGGER.info( "  edges" );
		for ( D6LEdge link: inGraph.edgeSet() ) {
			
			// select only unallocated links
			if ( link.getIdPackage() != D6LPackage.TECH_ID_UNALLOCATED ) {
				continue;
			}
			
			D6LVertex roleA_entity = inGraph.getEdgeSource( link );
			D6LVertex roleB_entity = inGraph.getEdgeTarget( link );

            // remove components from benches
            for ( D6LPackage bomSimplificationLot : mapBomSimplificationLots.values() ) {
            
                // role B in component lot?
                if ( 
                    ( roleB_entity.getIdPackage() == bomSimplificationLot.getId() ) 
                 ) {
                    
                    // link is in a lot dependency
                    
                    link.setIdPackage( bomSimplificationLot.getId() );
                    
                    // We de-allocated link from bench, that means that link stats for objectA and objectB have changed
                    // Rework stats
                    reworkStatsAndSingleLotForDirectedLinkToComponent( 
                    	singleLot, roleA_entity, roleB_entity 
                    );
                    
                }

            }
            
            // role A entity
            allocateSingleAndBomSimplificationAndTopOfBom(
				roleA_entity, mapBomSimplificationLots, singleLot, idEntitiesProcessed
			);
			
            // role B entity
			allocateSingleAndBomSimplificationAndTopOfBom( 
				roleB_entity, mapBomSimplificationLots, singleLot, idEntitiesProcessed
			);
			
        }

	}

	/**
	 * Create BOM simplification lot, type is divider's type<p/>
	 * Actual type is moved to business depending on BOM Simplifier cont at end of run in D6AbstractBomSimplifier.moveSimplifiedTechnicalLotsToBusinessLot
	 * @see org.xlm.jxlm.D6LAbstractBomSimplifier.d6.data.algo.topological.bomsimplifiers.D6AbstractBomSimplifier.moveSimplifiedTechnicalLotsToBusinessLot
	 * @param txn
	 * @param bench
	 * @param iPass
	 * @param defaultLotType
	 * @param bomSimplifier
	 * @return
	 * @throws D6LException
	 */
	private D6LPackage createBomSimplificationLot(
		D6LPackageTypeEnum defaultLotType, D6LAbstractBomSimplifier bomSimplifier
	)
		throws D6LException 
	{
		
		// Lot type
		D6LPackageTypeEnum lotType = defaultLotType;
		
		D6LPackage bomSimplificationLot = db.daoEntityRegistry.newPackage( lotType, null );
		
		setParameters( bomSimplifier, bomSimplificationLot );
		
		return bomSimplificationLot;
		
	}
	
	private void setParameters(
		D6LAbstractBomSimplifier bomSimplifier, D6LPackage bomSimplificationLot
	)
		throws D6LException 
	{
	    
		switch ( bomSimplifier.getKind() ) {
		    
		    case Components: {
				
		        // sub-type to identify component lot later on
				bomSimplificationLot.setPackageSubtype( D6LPackageSubtypeEnum.COMPONENT_LOT );
				// name
				bomSimplificationLot.setName( D6LPackageSubtypeEnum.COMPONENT_LOT.getLotName() );
				break;
				
		    }

		    case Kits: {
		        
		        // sub-type to identify component lot later on
		        bomSimplificationLot.setPackageSubtype( D6LPackageSubtypeEnum.KIT_LOT );
		        // name
		        bomSimplificationLot.setName( D6LPackageSubtypeEnum.KIT_LOT.getLotName() );
		        break;
		        
		    }

		    default: {
		        throw new D6LException( "Unknown BOM Simplifier kind: " + bomSimplifier.getKind() );
		    }
		    
		}
		
	}

	/**
     * We change stats for role A and role B, when applicable we move roleA and role B to single lot
     * @param link
	 * @throws D6LException 
     */
    private void reworkStatsAndSingleLotForDirectedLinkToComponent( 
        D6LPackage singleLot, D6LEntityIF roleA, D6LEntityIF roleB 
    ) throws D6LException
    {

        // Get existing stats
        
        // role A
        D6LEntityDirectedLinkStats roleA_stats = 
        	db.daoEntityStats.getByEntityId( roleA.getId() );
        
        if ( roleA_stats != null ) {
            // we removed a link from role A
            roleA_stats.incNbDirectedLinksFromForBench( -1 );
            roleA_stats.incNbLinksFromForBench( -1 );
            
            // role A is a single?
            long roleA_nbLinksFromEntity = roleA_stats.getNbLinksFromForBench();
            long roleA_nbLinksToEntity   = roleA_stats.getNbLinksToForBench();
            
            if ( 
                // single lot is not null
                ( singleLot != null ) &&
                // bench.getIdLot() = current business lot being divided
                // singleLot.getIdLotParent() parent lot of single lot
                // if single lot parent is current business lot, we can not move entity to single lot
                // this would create a single to component lot dependency into businesslot
                // No links from to entity
                ( roleA_nbLinksFromEntity == 0 ) && ( roleA_nbLinksToEntity == 0 ) 
            ) {
                
                // role A is single, move it to single lot
                roleA.setIdPackage( singleLot.getId() );

            }
        }
        
        // role B
        D6LEntityDirectedLinkStats roleB_stats = 
        	db.daoEntityStats.getByEntityId( roleB.getId() );
        
        if ( roleB_stats != null ) {
            // we removed a link to role B
            roleB_stats.incNbDirectedLinksToForBench( -1 );
            roleB_stats.incNbLinksToForBench( -1 );
            
            // role B is a single?
            long roleB_nbLinksFromEntity = roleB_stats.getNbLinksFromForBench();
            long roleB_nbLinksToEntity   = roleB_stats.getNbLinksToForBench();
            
            if ( 
                // single lot is not null
                ( singleLot != null ) &&
                // bench.getIdLot() = current business lot being divided
                // singleLot.getIdLotParent() parent lot of single lot
                // if single lot parent is current business lot, we can not move entity to single lot
                // this would create a single to component lot dependency into businesslot
                 // No links from to entity
                ( roleB_nbLinksFromEntity == 0 ) && ( roleB_nbLinksToEntity == 0 ) 
            ) {
    
                // role B is single, move it to single lot
                roleB.setIdPackage( singleLot.getId() );
                
            }
        }
        
    }

    private List<D6LJobIF<D6LEntityIF>> allocateSingleAndBomSimplificationAndTopOfBom( 
		D6LVertex entity, Map<BomSimplifierKindEnum,D6LPackage> mapBomSimplifierLots, 
		D6LPackage singleLot, 
		Set<Integer> idObjectsProcessed
	) throws D6LException {
		
    	// Init bom simplifiers
		D6LAbstractTopologicalDivider topologicalDividerAlgo = (D6LAbstractTopologicalDivider) getAlgo();

        List<D6LJobIF<D6LEntityIF>> postActions = new ArrayList<>();
        
        // check only unallocated objects
		if ( entity.getIdPackage() != D6LPackage.TECH_ID_UNALLOCATED ) {
			// already processed
			return postActions;
		}
		
		// already processed?
		if ( idObjectsProcessed.contains( entity.getId() ) ) {
			// already processed
			return postActions;
		}
		
		// find links from this entity
    	Set<D6LEdge> directedLinksFromEntity = inGraph.incomingEdgesOf( entity );
    	// count directed links from entity
    	int nbDirectedLinksFromEntity = directedLinksFromEntity.size();
    	// Graph is directed by constraint
    	int nbLinksFromEntity = nbDirectedLinksFromEntity;
    	
    	// count directed links to entity
		// find links from this entity
    	Set<D6LEdge> directedLinksToEntity = inGraph.outgoingEdgesOf( entity );
    	int nbDirectedLinksToEntity = directedLinksToEntity.size();

    	// count links to entity
    	// Graph is directed by constraint
    	int nbLinksToEntity = nbDirectedLinksToEntity;

    	// Save numbers is a stat object
    	D6LEntityDirectedLinkStats stat = new D6LEntityDirectedLinkStats( entity.getId() );
    	stat.setNbDirectedLinksFromForBench( nbDirectedLinksFromEntity );
    	stat.setNbLinksFromForBench( nbLinksFromEntity );
    	stat.setNbDirectedLinksToForBench( nbDirectedLinksToEntity );
    	stat.setNbLinksToForBench( nbLinksToEntity );
    	
		// If it's a required parent sub-type enum, allocation is done further on
		// if no links, entity is single
		if ( 
			( singleLot != null ) && ( nbLinksFromEntity == 0 ) && ( nbLinksToEntity == 0 ) 
		) {

		    entity.setIdPackage( singleLot.getId() );
			
		} else {
			
			// Get Bom simplifiers
			for ( D6LAbstractBomSimplifier bomSimplifier : topologicalDividerAlgo.getListBomSimplifiers() ) {
			    
			    // Get bom simplifier lot
			    D6LPackage bomSimplifierLot = mapBomSimplifierLots.get( bomSimplifier.getKind() );
			    
    			if ( bomSimplifierLot != null ) {

    				boolean matchWithoutNumbersResult = bomSimplifier.matchWithoutNumbers( 
    					this,
    					entity, stat 
    				);
    				
    				if ( matchWithoutNumbersResult ) {
	    	            // Create histogram entry
	    	            bomSimplifier.createAndSaveHistogramEntry( entity, nbDirectedLinksFromEntity, nbDirectedLinksToEntity );
    				}
    				
    				// Is it a lot requiring to be put in simplifier lot?
    				boolean matchByLotSubType = false;
    				
   			        MatchResult matchResult = null;
   			        
   			        // No need to match if matched by lot sub type
   			        if ( !matchByLotSubType ) {
   			        	/*
   public abstract MatchResult match( 
    	D6LAlgoCommandIF algoCommand, 
    	D6LEntityIF entity, boolean matchWithoutNumbersResult, 
    	D6LPackage singlePackage, List<D6LJobIF<D6LEntityIF>> postActions
    ) throws D6LException;

   			        	 */
   			        	matchResult = bomSimplifier.match( 
   			        		this, 
   			        		entity, 
   			        		matchWithoutNumbersResult, stat, singleLot, postActions 
   			        	);
   			        }
   			        
   			        if ( 
   			        	matchByLotSubType || 
   			        	( ( matchResult != null ) && matchResult.match ) 
   			        ) {
   			        	
   			        	/*
	    				// Modify lot according to entity
   			        	if ( matchResult != null ) {
   			        		bomSimplifier.tuneLot( bomSimplifierLot, matchResult.lotTuningInfo );
   			        	}
   			        	*/
   			        	
	    			    // put to component lot
	    				entity.setIdPackage( bomSimplifierLot.getId() );
	    				
	    	            // Do we need a new simplifier lot?
	    	            if ( !bomSimplifier.isSingleExtractorLot() ) {
	    	            	
	    	            	// Create new lot
	    	            	D6LPackage newLot = 
	    	            		createBomSimplificationLot( 
	    	            			topologicalDividerAlgo.getProducesLotType(), bomSimplifier 
	    	            		);
	    	            	// Record it
	    	            	mapBomSimplifierLots.put( bomSimplifier.getKind(), newLot );
	    	            }
	    	            
   			        }
    				
    			}
    		}
		}
		
		
		// mark object as processed
		idObjectsProcessed.add( entity.getId() );
		
		return postActions;
		
	}
    
    
	@Override
    protected String getShortName()
    {
        return "cmd-topdiv";
    }

}
