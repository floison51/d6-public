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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.taskdefs.SQLExec.Transaction;
import org.xlm.jxlm.d6light.data.algo.D6LAbstractDividerAlgoCommand;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier;
import org.xlm.jxlm.d6light.data.conf.RegExpType;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;
import org.xlm.jxlm.d6light.data.model.D6LPackageVertex;

/**
 * Topological divider command
 * @author Loison
 *
 */
public class D6LTopologicalDividerCommand extends D6LAbstractDividerAlgoCommand {

	/** For debug, activate/desactivate logic controlling parent lots of bom simplifier lots **/
	public static final boolean REWORK_BOM_SIMPLIFIER_LOTS_PARENTS = true;
	
	private static final Logger LOGGER = LogManager.getLogger( D6LTopologicalDividerCommand.class ); 
	
	@Override
	public void doPrepare( final boolean callAlgo ) throws D6LException {
		
	    LOGGER.info( "Start Prepare benches" );

	    throw new D6LError( "TODO" );
	    /*
	    // Ancestor creates benches
	    // don't call algo because we do after
		super.doPrepare( false );
		
		D6LAbstractTopologicalDivider topologicalDividerAlgo = (D6LAbstractTopologicalDivider) getAlgo();
		
		// Allocate single and components, if needed
		
		// Ask conf if we need to allocate singles in a special lot
		
		// Use algo value
		boolean isSinglesAllocation = topologicalDividerAlgo.isAllowsSinglesAllocation();
        // Regexp can also override allocation dependencing on parent lot name
		RegExpType allocateSinglesIfParentContainerRegEx = null;
		
        if ( !topologicalDividerAlgo.isAllowsSinglesAllocation() ) {
            LOGGER.info( "Topological divider '" + topologicalDividerAlgo.getName() + "' doesn't single allocation, any <Single> parameters are ignored." );
            // No regexp override : algo doesn't support it
            allocateSinglesIfParentContainerRegEx = null;
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
		        
		        // Regexp can also override allocation dependencing on parent lot name
		        allocateSinglesIfParentContainerRegEx = topoAlgoConf.getSingles().getAllocateSinglesIfParentContainerRegEx();
		        
		    }
		    
		}
		
		if ( isSinglesAllocation || topologicalDividerAlgo.isNeedBomSimplification() ) {
			
		    // browse benches
			EntityCursor<D6Bench> benches = db.daoBenches.byId.entities( txn, null );
			try {
			    
				for ( D6Bench bench: benches ) {
				    
				    // launch a thread per bench
				    Runnable prepareForBenchRunnable = 
				       new PrepareForBenchRunnable( 
				           txn, topologicalDividerAlgo, 
				           isSinglesAllocation, allocateSinglesIfParentContainerRegEx,
				           bench 
				    );
				    
				    prepareForBenchRunnable.run();
				    
				}
				
			} catch ( Throwable t ) {
			    
			    throw new D6LException( t );
			    
			} finally {
				benches.close();
			}
			
		}

		// Delegate to algo
    	if ( callAlgo ) {
			getAlgo().doPrepare( this );
    	}

        LOGGER.info( "End Prepare benches" );
        */
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
		boolean allocateSingles, RegExpType allocateSinglesIfParentContainerRegEx, 
		boolean isNeedBomSimplification, boolean isNeedBomSimplifiedEntitiesRemovedFromBench 
	) throws D6LException {
		
	    throw new D6LError( "TODO" );
	    /*
	    LOGGER.info( "Allocate singles and do bom simplification for bench '" + bench.getId() + "'" );
	    
	    List<X6JobIF<D6LEntityIF>> postActions = new ArrayList<>();
	    
        D6DividerAlgoIF dividerAlgo = (D6DividerAlgoIF) getAlgo();
        
        // Get lot assigned to bench
        D6AbstractLot absLot = D6AbstractLot.getAbstractLot( db, txn, bench.getIdLot(), null );
        
		// create Bom simplification lots lot for current bench and pass
        Map<BomSimplifierKindEnum,D6Lot> mapBomSimplificationLots = new HashMap<>();
        
        if ( isNeedBomSimplification && ( dividerAlgo instanceof D6LTopologicalDividerIF )) {
            
            // Get bom simplifications
            List<D6LAbstractBomSimplifier> listBomSimplifiers = ( ( D6LTopologicalDividerIF ) dividerAlgo ).getListBomSimplifiers();
            
            // Create lots: Components, Kits, ...
            for ( D6LAbstractBomSimplifier bomSimplifier : listBomSimplifiers ) {
            
                // Check 
            	D6Lot bomSimplificationLot = 
            		createBomSimplificationLot(txn, bench, iPass, dividerAlgo.getProducesLotType(), bomSimplifier );
        			
        		mapBomSimplificationLots.put( bomSimplifier.getKind(), bomSimplificationLot );

        	}
    		
        }
	
		// find single lot for current bench
		D6Lot singleLot = null;
		
		// Calculate regexp match
		boolean allocateSinglesOverride = true;
		if ( allocateSinglesIfParentContainerRegEx != null ) {
		    allocateSinglesOverride = D6RegExpParamHelper.regExpParamMatch( allocateSinglesIfParentContainerRegEx, absLot.getName() );
		}
		
		if ( allocateSingles && allocateSinglesOverride ) {		
			
		    // Use a thread safe method to get single lot
		    // Because some threads may creating another single lot in the mean while...
		    singleLot = 
		        db.daoLots.getOrCreateSingleLotForBenchAndPass( 
		             txn, CursorConfig.READ_UNCOMMITTED, bench, iPass, dividerAlgo.getProducesLotType() 
		        );
		}
		
		// allocate single and component objects
		
		// record objects already processed
		Set<Long> idEntitiesProcessed = new HashSet<>();
	
		// browse objects belonging to bench
        LOGGER.info( "  objects" );
		try (
		    EntityCursor<? extends D6LEntityIF> entities = daoEntities.getByBench( bench ).entities( txn, CursorConfig.READ_UNCOMMITTED );
		    D6Progress progress = new D6Progress( db, -1, "    ", " bench entities" );
		) 
		{	
			
			for ( D6LEntityIF entity: entities ) {
				
			    // Tick
				if ( ( progress.iItem % NB_BIG_PROCESSED_TICK ) == 1 ) {
					progress.show();
				}
				
				// select only unallocated objects
				if ( entity.getIdLot() != D6Lot.TECH_ID_UNALLOCATED ) {
				    progress.iItem++;
					continue;
				}
				
				// Do allocation and get post actions
				List<X6JobIF<D6LEntityIF>> curPostActions = allocateSingleAndBomSimplificationAndTopOfBom( 
					txn,
					entity, mapBomSimplificationLots, singleLot, bench, idEntitiesProcessed,
					isNeedBomSimplifiedEntitiesRemovedFromBench
				);
				
				postActions.addAll( curPostActions );
				
				progress.iItem++;

			}
			
		} finally {
		    
		    // Flush queues
		    flushQueues();
		    
		}
		
        try {

            for ( X6JobIF<D6LEntityIF> postAction : postActions ) {
                postAction.doJob( null );
            }
            
        } catch ( Exception e ) {
            D6LException.handleException( e );
        } finally {
            flushQueues();
        }
        
		// allocate links to component lot, if both roles are in component lot
        LOGGER.info( "  links" );
		try (
		    EntityCursor<? extends D6LEntityIF> links = daoEntityLinks.getByBench( bench ).entities( txn, CursorConfig.READ_UNCOMMITTED );
		    D6Progress progress = new D6Progress( db, -1, "  ", " bench entities" );
		) {
			
			for ( D6LEntityIF linkEntity: links ) {
				
			    D6LinkIF link = (D6LinkIF) linkEntity;
				
				// Tick
				if ( ( progress.iItem % NB_BIG_PROCESSED_TICK ) == 0 ) {
                    progress.show();
				}
				
				// select only unallocated links
				if ( link.getIdLot() != D6Lot.TECH_ID_UNALLOCATED ) {
				    progress.iItem++;
					continue;
				}
				
				D6LEntityIF roleA_entity = db.daoMetaEntities.byIdGet( txn, iPass, iPassTechLot, link.getIdRoleA(), null );
                D6LEntityIF roleB_entity = db.daoMetaEntities.byIdGet( txn, iPass, iPassTechLot, link.getIdRoleB(), null );

                // remove components from benches
                for ( D6Lot bomSimplificationLot : mapBomSimplificationLots.values() ) {
                
                    // role B in component lot?
                    if ( 
                        isNeedBomSimplifiedEntitiesRemovedFromBench && 
                        ( roleB_entity.getIdLot() == bomSimplificationLot.getId() ) 
                     ) {
                        
                        // check
                        if ( ! ( link.getLinkDirection() == DependencyBeanDirectionEnum.DirectedFromTo ) ) {
                            throw new D6LException( "Expected a directed link when removing link from bench, idLink = " + link.getId() );
                        }
                        // link is in a lot dependency
                        
                        // remove it from bench
                        link.setIdBench( D6Bench.NO_BENCH );
                        
                        // Quick bench support
                        // Quick bench don't allocate entities, quick bench allocation is when entity belongs to lot UNALLOCATED
                        // Move links to component lot, it will be belong to more to 'quick bench'
                        // further on, link will be move to a lot dependency
                        link.setIdLot( bomSimplificationLot.getId() );
                        
                        // Use queue to save
                        pushSaveEntity( link );
                        //link.save( db, txn );
                        
                        // We de-allocated link from bench, that means that link stats for objectA and objectB have changed
                        // Rework stats
                        reworkStatsAndSingleLotForDirectedLinkToComponent( txn, singleLot, bench, roleA_entity, roleB_entity );
                        
                    }

                }
                
                // role A entity
                allocateSingleAndBomSimplificationAndTopOfBom(
					txn, 
					roleA_entity, mapBomSimplificationLots, singleLot, bench, idEntitiesProcessed,
					isNeedBomSimplifiedEntitiesRemovedFromBench
				);
				
                // role B entity
				allocateSingleAndBomSimplificationAndTopOfBom( 
					txn, 
					roleB_entity, mapBomSimplificationLots, singleLot, bench, idEntitiesProcessed, 
					isNeedBomSimplifiedEntitiesRemovedFromBench
				);
    			
				progress.iItem++;

	        }

        } finally {
            
            // Flush queues
            flushQueues();
            
		}
		*/
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
	/*
	private D6Lot createBomSimplificationLot(
		Transaction txn, D6Bench bench, int iPass,
		D6LPackageTypeEnum defaultLotType, D6LAbstractBomSimplifier bomSimplifier
	)
		throws D6LException 
	{
		
		// Lot type
		D6LPackageTypeEnum lotType = defaultLotType;
		
		D6Lot bomSimplificationLot = new D6Lot( lotType, null, iPass );		    
		setParameters( bomSimplifier, bomSimplificationLot );
		
		bomSimplificationLot.setIdLotParent( bench.getIdLot() );
		
		// No bench to avoid algo to change its parent
		bomSimplificationLot.setIdBench( D6Bench.NO_BENCH );
		
		// save it
		bomSimplificationLot.save( db, txn );
		
		return bomSimplificationLot;
		
	}
	*/
	
	private void setParameters(D6LAbstractBomSimplifier bomSimplifier, D6LPackageVertex bomSimplificationLot)
			throws D6LException {
	    throw new D6LError( "TODO" );
	    /*
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
		        bomSimplificationLot.setLotSubtype( D6LPackageSubtypeEnum.KIT_LOT );
		        // name
		        bomSimplificationLot.setName( D6LPackageSubtypeEnum.KIT_LOT.getLotName() );
		        break;
		        
		    }

		    case LotExtractor: {
		        
		        // sub-type to identify component lot later on
		        bomSimplificationLot.setLotSubtype( D6LPackageSubtypeEnum.EXTRACTED_LOT );

		        // No name
		        bomSimplificationLot.setName( "" );
		        
		        break;
		        
		    }

		    default: {
		        throw new D6LException( "Unknown BOM Simplifier kind: " + bomSimplifier.getKind() );
		    }
		    
		}
		*/
	}

	/**
     * We change stats for role A and role B, when applicable we move roleA and role B to single lot
     * @param link
	 * @throws D6LException 
     */
    private void reworkStatsAndSingleLotForDirectedLinkToComponent( 
        Transaction txn, D6LPackageVertex singleLot, D6LEntityIF roleA, D6LEntityIF roleB 
    ) throws D6LException
    {

	    throw new D6LError( "TODO" );
	    /*
        // Get existing stats
        
        // role A
        D6LEntityDirectedLinkStats roleA_stats = db.daoEntityStats.getByEntityIdAndBenchId( txn, null, roleA.getIdBench(), roleA.getId() );
        
        if ( roleA_stats != null ) {
            // we removed a link from role A
            roleA_stats.incNbDirectedLinksFromForBench( -1 );
            roleA_stats.incNbLinksFromForBench( -1 );
            
            // Save 
            roleA_stats.save( db, txn );
            
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
                ( bench.getIdLot() != singleLot.getIdLotParent() ) &&
                // No links from to entity
                ( roleA_nbLinksFromEntity == 0 ) && ( roleA_nbLinksToEntity == 0 ) 
            ) {
                
                // role A is single, move it to single lot
                roleA.setIdLot( singleLot.getId() );

                // save entity
                // Use queue to save roleA
                pushSaveEntity( roleA );
                //roleA.save( db, txn );
                
            }
        }
        
        // role B
        D6LEntityDirectedLinkStats roleB_stats = db.daoEntityStats.getByEntityIdAndBenchId( txn, null, roleB.getIdBench(), roleB.getId() );
        
        if ( roleB_stats != null ) {
            // we removed a link to role B
            roleB_stats.incNbDirectedLinksToForBench( -1 );
            roleB_stats.incNbLinksToForBench( -1 );
            
            // Save
            roleB_stats.save( db, txn );
            
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
                ( bench.getIdLot() != singleLot.getIdLotParent() ) &&
                // No links from to entity
                ( roleB_nbLinksFromEntity == 0 ) && ( roleB_nbLinksToEntity == 0 ) 
            ) {
    
                // role B is single, move it to single lot
                roleB.setIdLot( singleLot.getId() );
                
                // Use queue2 to save roleA
                pushSaveEntity2( roleB );
                //roleB.save( db, txn );
                
            }
        }
        */
    }

    /*
    private List<X6JobIF<D6LEntityIF>> allocateSingleAndBomSimplificationAndTopOfBom( 
		Transaction txn, 
		D6LEntityIF entity, Map<BomSimplifierKindEnum,D6Lot> mapBomSimplifierLots, D6Lot singleLot, 
		D6Bench bench, Set<Long> idObjectsProcessed,
		boolean removeComponentsFromBench
	) throws D6LException {
		
    	// Init bom simplifiers
		D6AbstractTopologicalDivider topologicalDividerAlgo = (D6AbstractTopologicalDivider) getAlgo();

    	for ( D6LAbstractBomSimplifier bomSimplifier : topologicalDividerAlgo.getListBomSimplifiers() ) {
    		
        	// Set passes
        	bomSimplifier.setPasses( iPass, iPassTechLot );
        	
    	}    	

        List<X6JobIF<D6LEntityIF>> postActions = new ArrayList<>();
        
        // check only unallocated objects
		if ( entity.getIdLot() != D6Lot.TECH_ID_UNALLOCATED ) {
			// already processed
			return postActions;
		}
		
		if ( entity.isLink() ) {
			// objects only
			return postActions;
		}
		
		// already processed?
		if ( idObjectsProcessed.contains( entity.getId() ) ) {
			// already processed
			return postActions;
		}
		
    	// count directed links from entity
    	long nbDirectedLinksFromEntity = 0;
    	
		// find links from this entity, for current bench
    	try (
        	ForwardCursor<? extends D6LinkIF> joinCursorDirectedLinksFromEntity = daoEntityLinks.getByBenchAndRoleA_AndDirection(
        		txn, 
            	// current bench
        		bench, 
        		// roleA -> object
        		entity.getId(), 
        		// only directed link
                DependencyBeanDirectionEnum.DirectedFromTo, 
        		CursorConfig.READ_UNCOMMITTED
        	);
    	) {
    		// count
	    	while ( joinCursorDirectedLinksFromEntity.next() != null ) {
	    		nbDirectedLinksFromEntity++;
	    	}
    	}
    	
    	// count links from entity
    	long nbLinksFromEntity = 0;
    	
    	try (
        	ForwardCursor<? extends D6LinkIF> joinCursorLinksFromEntity = daoEntityLinks.getByBenchAndRoleA(
        		txn, 
            	// current bench
        		bench, 
        		// roleA -> object
        		entity.getId(), 
        		CursorConfig.READ_UNCOMMITTED
        	);
    	) {
    		// count
	    	while ( joinCursorLinksFromEntity.next() != null ) {
	    		nbLinksFromEntity++;
	    	}
    	}
    	
    	// count directed links to entity
    	long nbDirectedLinksToEntity = 0;

    	try (
        	ForwardCursor<? extends D6LinkIF> joinCursorDirectedLinksToEntity = daoEntityLinks.getByBenchAndRoleB_AndDirection(
        		txn, 
            	// current bench
        		bench, 
            	// roleB -> object
        		entity.getId(), 
        		// only directed link
                DependencyBeanDirectionEnum.DirectedFromTo, 
        		CursorConfig.READ_UNCOMMITTED 
        	);
    	) {
    		// count
	    	while ( joinCursorDirectedLinksToEntity.next() != null ) {
	    		nbDirectedLinksToEntity++;
	    	}
    	}
    	
    	// count links to entity
    	long nbLinksToEntity = 0;

    	try (
        	ForwardCursor<? extends D6LinkIF> joinCursorLinksToEntity = daoEntityLinks.getByBenchAndRoleB(
        		txn, 
            	// current bench
        		bench, 
            	// roleB -> object
        		entity.getId(), 
        		CursorConfig.READ_UNCOMMITTED
        	);
    	) {
    		// for debug
	    	while ( joinCursorLinksToEntity.next() != null ) {
	    		nbLinksToEntity++;
	    	}
    	}
    	
    	// Save numbers is a stat object
    	D6LEntityDirectedLinkStats stat = new D6LEntityDirectedLinkStats( entity.getId(), bench.getId() );
    	stat.setNbDirectedLinksFromForBench( nbDirectedLinksFromEntity );
    	stat.setNbLinksFromForBench( nbLinksFromEntity );
    	stat.setNbDirectedLinksToForBench( nbDirectedLinksToEntity );
    	stat.setNbLinksToForBench( nbLinksToEntity );
    	
    	// save
    	stat.save( db, txn );
    	
		// Check entity is not a bom simplified lot
		D6LPackageSubtypeEnum requiredParentLotSubType = null;
		D6AbstractLot absCurLot = null;
		if ( entity instanceof D6AbstractLot ) {
			absCurLot = (D6AbstractLot) entity;
			requiredParentLotSubType = absCurLot.getRequiredParentLotSubType();
		}
		
		// If it's a required parent sub-type enum, allocation is done further on
		// if no links, entity is single
		if ( 
			( requiredParentLotSubType == null ) && 
			( singleLot != null ) && ( nbLinksFromEntity == 0 ) && ( nbLinksToEntity == 0 ) 
		) {

		    entity.setIdLot( singleLot.getId() );
	        // save using queue
			pushSaveEntity( entity );
			
		} else {
			
			// Get Bom simplifiers
			for ( D6LAbstractBomSimplifier bomSimplifier : topologicalDividerAlgo.getListBomSimplifiers() ) {
			    
			    // Get bom simplifier lot
			    D6Lot bomSimplifierLot = mapBomSimplifierLots.get( bomSimplifier.getKind() );
			    
    			if ( bomSimplifierLot != null ) {

    				boolean matchWithoutNumbersResult = bomSimplifier.matchWithoutNumbers( 
    					txn, this,
    					bench,
    					entity, stat 
    				);
    				
    				if ( matchWithoutNumbersResult ) {
	    	            // Create histogram entry
	    	            bomSimplifier.createAndSaveHistogramEntry( txn, getPass(), entity, nbDirectedLinksFromEntity, nbDirectedLinksToEntity );
    				}
    				
    				// Is it a lot requiring to be put in simplifier lot?
    				boolean matchByLotSubType = false;
    				
					if ( requiredParentLotSubType != null ) {
						
						// May be a match
						// As kind
						BomSimplifierKindEnum requiredParentKind = BomSimplifierKindEnum.valueOf( requiredParentLotSubType );
						
						// Check simplifier matches and entity is a lot
						if ( ( requiredParentKind == bomSimplifier.getKind() ) && ( absCurLot != null ) ) {
							// Match if lot sub type is required one
							matchByLotSubType = ( absCurLot.getLotSubtype() == requiredParentLotSubType );
						}
						
					}
    				
   			        MatchResult matchResult = null;
   			        
   			        // No need to match if matched by lot sub type
   			        if ( !matchByLotSubType ) {
   			        	matchResult = bomSimplifier.match( 
   			        		txn, this, 
   			        		bench, entity, 
   			        		matchWithoutNumbersResult, stat, singleLot, postActions 
   			        	);
   			        }
   			        
   			        if ( 
   			        	matchByLotSubType || 
   			        	( ( matchResult != null ) && matchResult.match ) 
   			        ) {
   			        	
	    				// Modify lot according to entity
   			        	if ( matchResult != null ) {
   			        		bomSimplifier.tuneLot( txn, bomSimplifierLot, matchResult.lotTuningInfo );
   			        	}
   			        	
	    			    // put to component lot
	    				entity.setIdLot( bomSimplifierLot.getId() );
	    				
	    	            // save using queue
	    	            pushSaveEntity( entity );
	    	            
	    				// remove components from benches
	    				if ( removeComponentsFromBench ) {
	    					X6JobIF<D6LEntityIF> removeComponentsFromBenchJob = 
	    						new RemoveComponentsFromBenchJob( txn, bench, entity );
	    					postActions.add( removeComponentsFromBenchJob );
	    				}
	    				
	    	            // Do we need a new simplifier lot?
	    	            if ( !bomSimplifier.isSingleExtractorLot() ) {
	    	            	
	    	            	// Create new lot
	    	            	D6Lot newLot = createBomSimplificationLot(txn, bench, iPass, topologicalDividerAlgo.getProducesLotType(), bomSimplifier );
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
	*/
    
    /**
     * Job removing a component from bench 
     */
    /*
    private class RemoveComponentsFromBenchJob implements X6JobIF<D6LEntityIF> {

    	private final Transaction txn;
    	private final D6Bench bench;
        private final D6LEntityIF targetEntity;
    	
    	public RemoveComponentsFromBenchJob( Transaction txn, D6Bench bench, D6LEntityIF targetEntity ) {
    	
    		super();
    		this.txn = txn;
    		this.bench = bench;
    		this.targetEntity = targetEntity;
    		
    	}
    	
		@Override
		public void doJob( D6LEntityIF _entity ) throws Exception {
			
			// entity is not used, targetEntity is used 
	    	// For entity
			targetEntity.setIdBench( D6Bench.NO_BENCH );
            // save using queue
            pushSaveEntity( targetEntity );
		    
		    // We have also to remove links connected to it
		    // Some dividers such as Metis or Louvain rely on links, letting links in bench would grap back entit-object 
		    
	    	try (
	    		D6UnionForwardCursor< ? extends D6LinkIF > links = 
	    			new D6UnionForwardCursor<D6LinkIF>(
	    				// Links by role A
	    				daoEntityLinks.getByBenchAndRoleA( txn, bench, targetEntity.getId(), CursorConfig.READ_UNCOMMITTED ),
	    				// Links by role B
	    				daoEntityLinks.getByBenchAndRoleB( txn, bench, targetEntity.getId(), CursorConfig.READ_UNCOMMITTED )
	    			);
	    	) {
	    		for ( D6LinkIF link: links ) {

	    			link.setIdBench( D6Bench.NO_BENCH );
	    			
		            // save using queue
		            pushSaveEntity( link );

	    		}
	    	}
			
		}
    	
    }
    */
    
	@Override
    protected String getShortName()
    {
        return "cmd-topdiv";
    }

	@Override
	protected void doExecute( boolean callAlgo ) throws D6LException {
		throw new D6LError( "TODO" );
	}

}
