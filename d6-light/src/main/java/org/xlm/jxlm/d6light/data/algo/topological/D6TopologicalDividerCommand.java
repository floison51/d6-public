

package org.xlm.jxlm.d6light.data.algo.topological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xlm.jxlm.audit.d6.data.D6Util.D6Progress;
import org.xlm.jxlm.audit.d6.data.algo.D6AbstractDividerAlgoCommand;
import org.xlm.jxlm.audit.d6.data.algo.D6DividerAlgoIF;
import org.xlm.jxlm.audit.d6.data.algo.topological.bom.D6EntityDirectedLinkStats;
import org.xlm.jxlm.audit.d6.data.algo.topological.bomsimplifiers.D6AbstractBomSimplifier;
import org.xlm.jxlm.audit.d6.data.algo.topological.bomsimplifiers.D6AbstractBomSimplifier.BomSimplifierKindEnum;
import org.xlm.jxlm.audit.d6.data.algo.topological.bomsimplifiers.D6AbstractBomSimplifier.MatchResult;
import org.xlm.jxlm.audit.d6.data.bench.D6Bench;
import org.xlm.jxlm.audit.d6.data.command.Stats;
import org.xlm.jxlm.audit.d6.data.conf.D6RegExpParamHelper;
import org.xlm.jxlm.audit.d6.data.db.D6UnionForwardCursor;
import org.xlm.jxlm.audit.d6.data.lot.D6AbstractLot;
import org.xlm.jxlm.audit.d6.data.lot.D6Lot;
import org.xlm.jxlm.audit.d6.data.meta.D6EntityIF;
import org.xlm.jxlm.audit.d6.data.meta.D6LinkIF;

import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.ForwardCursor;

import org.xlm.jxlm.audit.x6.common.X6Error;
import org.xlm.jxlm.audit.x6.common.X6Exception;
import org.xlm.jxlm.audit.x6.common.data.conf.AbstractAlgoType;
import org.xlm.jxlm.audit.x6.common.data.conf.RegExpType;
import org.xlm.jxlm.audit.x6.common.data.conf.TopologicalDividerType;
import org.xlm.jxlm.audit.x6.common.data.lot.D6LotSubtypeEnum;
import org.xlm.jxlm.audit.x6.common.data.lot.D6LotTypeEnum;
import org.xlm.jxlm.audit.x6.common.thread.X6JobIF;
import org.xlm.jxlm.audit.x6.core.beans.DependencyBeanDirectionEnum;

/**
 * Topological divider command
 * @author Loison
 *
 */
public class D6TopologicalDividerCommand extends D6AbstractDividerAlgoCommand {

	private static final long NB_BIG_PROCESSED_TICK = 100000;
	
	/** For debug, activate/desactivate logic controlling parent lots of bom simplifier lots **/
	public static final boolean REWORK_BOM_SIMPLIFIER_LOTS_PARENTS = true;
	
	@Override
	public Stats doPrepare( Integer idParentMilestone, Transaction txn, final boolean callAlgo ) throws X6Exception {
		
	    int idPrepare = cmdStats.startMilestone( idParentMilestone, "Prepare benches and directed link stats" );
	    
	    try {
	        
    	    LOGGER.info( "Start Prepare benches" );
    
    	    // Ancestor creates benches
    	    // don't call algo because we do after
    		Stats stats = super.doPrepare( idParentMilestone, txn, false );
    		
    		// Existing objects are allocated to Business lot, move them to Technical lot
    		moveLotEntities( txn, daoEntities, daoEntityLinks, D6Lot.BUSINESS_ID_EXISTING, D6Lot.TECH_ID_EXISTING );
    		
    		// Bad objects are allocated to Bad Business lot, move them to Bad Technical lot
    		moveLotEntities( txn, daoEntities, daoEntityLinks, D6Lot.BUSINESS_ID_BAD, D6Lot.TECH_ID_BAD );
    		
    		D6AbstractTopologicalDivider topologicalDividerAlgo = (D6AbstractTopologicalDivider) getAlgo();
    		
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
    				    
    				    startThread( prepareForBenchRunnable );
    				    
    				}
    				
    			} catch ( Throwable t ) {
    			    
    			    throw new X6Exception( t );
    			    
    			} finally {
    				benches.close();
    			}
    			
                // Make sure queues are flushed
                flushQueuesAndThreads();
    			
    		}
    
    		// Delegate to algo
        	if ( callAlgo ) {
    			Stats statsAlgo = getAlgo().doPrepare( idParentMilestone, txn, this );
    			if ( statsAlgo != null ) {
    				stats.accumulate( statsAlgo );
    			}
    			return stats;
        	}
    
            LOGGER.info( "End Prepare benches" );
            
            return stats;
            
	    } finally {
	    
	        cmdStats.endMilestone( idPrepare );
	    }
	    
	}


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
                
            } catch ( X6Exception e ) {
                throw new X6Error( e );
            }
        }
	}


	private void allocateSinglesAndBomSimplification( 
		Transaction txn, D6Bench bench,
		int iPass,
		boolean allocateSingles, RegExpType allocateSinglesIfParentContainerRegEx, 
		boolean isNeedBomSimplification, boolean isNeedBomSimplifiedEntitiesRemovedFromBench 
	) throws X6Exception {
		
	    LOGGER.info( "Allocate singles and do bom simplification for bench '" + bench.getId() + "'" );
	    
	    List<X6JobIF<D6EntityIF>> postActions = new ArrayList<>();
	    
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
		    EntityCursor<? extends D6EntityIF> entities = daoEntities.getByBench( bench ).entities( txn, CursorConfig.READ_UNCOMMITTED );
		    D6Progress progress = new D6Progress( db, -1, "    ", " bench entities" );
		) 
		{	
			
			for ( D6EntityIF entity: entities ) {
				
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
				List<X6JobIF<D6EntityIF>> curPostActions = allocateSingleAndBomSimplificationAndTopOfBom( 
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

            for ( X6JobIF<D6EntityIF> postAction : postActions ) {
                postAction.doJob( null );
            }
            
        } catch ( Exception e ) {
            X6Exception.handleException( e );
        } finally {
            flushQueues();
        }
        
		// allocate links to component lot, if both roles are in component lot
        LOGGER.info( "  links" );
		try (
		    EntityCursor<? extends D6EntityIF> links = daoEntityLinks.getByBench( bench ).entities( txn, CursorConfig.READ_UNCOMMITTED );
		    D6Progress progress = new D6Progress( db, -1, "  ", " bench entities" );
		) {
			
			for ( D6EntityIF linkEntity: links ) {
				
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
				
				D6EntityIF roleA_entity = db.daoMetaEntities.byIdGet( txn, iPass, iPassTechLot, link.getIdRoleA(), null );
                D6EntityIF roleB_entity = db.daoMetaEntities.byIdGet( txn, iPass, iPassTechLot, link.getIdRoleB(), null );

                // remove components from benches
                for ( D6Lot bomSimplificationLot : mapBomSimplificationLots.values() ) {
                
                    // role B in component lot?
                    if ( 
                        isNeedBomSimplifiedEntitiesRemovedFromBench && 
                        ( roleB_entity.getIdLot() == bomSimplificationLot.getId() ) 
                     ) {
                        
                        // check
                        if ( ! ( link.getLinkDirection() == DependencyBeanDirectionEnum.DirectedFromTo ) ) {
                            throw new X6Exception( "Expected a directed link when removing link from bench, idLink = " + link.getId() );
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
	 * @throws X6Exception
	 */
	private D6Lot createBomSimplificationLot(
		Transaction txn, D6Bench bench, int iPass,
		D6LPackageTypeEnum defaultLotType, D6LAbstractBomSimplifier bomSimplifier
	)
		throws X6Exception 
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

	private void setParameters(D6LAbstractBomSimplifier bomSimplifier, D6Lot bomSimplificationLot)
			throws X6Exception {
		switch ( bomSimplifier.getKind() ) {
		    
		    case Components: {
				
		        // sub-type to identify component lot later on
				bomSimplificationLot.setLotSubtype( D6LPackageSubtypeEnum.COMPONENT_LOT );
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
		        throw new X6Exception( "Unknown BOM Simplifier kind: " + bomSimplifier.getKind() );
		    }
		    
		}
	}

	/**
     * We change stats for role A and role B, when applicable we move roleA and role B to single lot
     * @param link
	 * @throws X6Exception 
     */
    private void reworkStatsAndSingleLotForDirectedLinkToComponent( 
        Transaction txn, D6Lot singleLot, D6Bench bench, D6EntityIF roleA, D6EntityIF roleB 
    ) throws X6Exception
    {

        // Get existing stats
        
        // role A
        D6EntityDirectedLinkStats roleA_stats = db.daoEntityStats.getByEntityIdAndBenchId( txn, null, roleA.getIdBench(), roleA.getId() );
        
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
        D6EntityDirectedLinkStats roleB_stats = db.daoEntityStats.getByEntityIdAndBenchId( txn, null, roleB.getIdBench(), roleB.getId() );
        
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
        
    }

    private List<X6JobIF<D6EntityIF>> allocateSingleAndBomSimplificationAndTopOfBom( 
		Transaction txn, 
		D6EntityIF entity, Map<BomSimplifierKindEnum,D6Lot> mapBomSimplifierLots, D6Lot singleLot, 
		D6Bench bench, Set<Long> idObjectsProcessed,
		boolean removeComponentsFromBench
	) throws X6Exception {
		
    	// Init bom simplifiers
		D6AbstractTopologicalDivider topologicalDividerAlgo = (D6AbstractTopologicalDivider) getAlgo();

    	for ( D6LAbstractBomSimplifier bomSimplifier : topologicalDividerAlgo.getListBomSimplifiers() ) {
    		
        	// Set passes
        	bomSimplifier.setPasses( iPass, iPassTechLot );
        	
    	}    	

        List<X6JobIF<D6EntityIF>> postActions = new ArrayList<>();
        
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
    	D6EntityDirectedLinkStats stat = new D6EntityDirectedLinkStats( entity.getId(), bench.getId() );
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
	    					X6JobIF<D6EntityIF> removeComponentsFromBenchJob = 
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

    /**
     * Job removing a component from bench 
     */
    private class RemoveComponentsFromBenchJob implements X6JobIF<D6EntityIF> {

    	private final Transaction txn;
    	private final D6Bench bench;
        private final D6EntityIF targetEntity;
    	
    	public RemoveComponentsFromBenchJob( Transaction txn, D6Bench bench, D6EntityIF targetEntity ) {
    	
    		super();
    		this.txn = txn;
    		this.bench = bench;
    		this.targetEntity = targetEntity;
    		
    	}
    	
		@Override
		public void doJob( D6EntityIF _entity ) throws Exception {
			
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
    
	@Override
    protected String getShortName()
    {
        return "cmd-topdiv";
    }

}
