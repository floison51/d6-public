

package org.xlm.jxlm.d6light.data.algo.topological.bom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xlm.jxlm.audit.d6.data.D6Util;
import org.xlm.jxlm.audit.d6.data.D6Util.D6Progress;
import org.xlm.jxlm.audit.d6.data.algo.D6AlgoCommandIF;
import org.xlm.jxlm.audit.d6.data.algo.topological.D6AbstractTopologicalDivider;
import org.xlm.jxlm.audit.d6.data.bench.D6Bench;
import org.xlm.jxlm.audit.d6.data.command.Stats;
import org.xlm.jxlm.audit.d6.data.command.D6AbstractCommand.TransactionNeeds;
import org.xlm.jxlm.audit.d6.data.conf.D6DataConf;
import org.xlm.jxlm.audit.d6.data.db.D6SystemizerDataDb;
import org.xlm.jxlm.audit.d6.data.lot.D6Lot;
import org.xlm.jxlm.audit.d6.data.meta.D6EntityAccessorIF;
import org.xlm.jxlm.audit.d6.data.meta.D6EntityIF;
import org.xlm.jxlm.audit.d6.data.meta.D6EntityInfoIF;
import org.xlm.jxlm.audit.d6.data.meta.D6EntityLinkAccessorIF;
import org.xlm.jxlm.audit.d6.data.meta.D6InfoManagedEntityIF;
import org.xlm.jxlm.audit.d6.data.meta.D6LinkIF;

import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityJoin;
import com.sleepycat.persist.ForwardCursor;

import org.xlm.jxlm.audit.x6.common.X6Error;
import org.xlm.jxlm.audit.x6.common.X6Exception;
import org.xlm.jxlm.audit.x6.common.data.conf.AbstractAlgoType;
import org.xlm.jxlm.audit.x6.common.data.conf.ParamType;
import org.xlm.jxlm.audit.x6.common.data.lot.D6LotSubtypeEnum;
import org.xlm.jxlm.audit.x6.common.data.lot.D6LotTypeEnum;
import org.xlm.jxlm.audit.x6.common.thread.X6ThreadManager;
import org.xlm.jxlm.audit.x6.core.beans.DependencyBeanDirectionEnum;

/**
 * Build Bill Of Material lots by directed link types
 * @author Francois Loison
 *
 */
public class D6ByDirectedLinkBomDivider extends D6LAbstractTopologicalDivider {

    /** If true, handle diamond topologies to reduce to one bom **/
    public static final String PARAM_HANDLE_DIAMONDS = "handleDiamonds";

    private int nbConcurrentThreads = 0;
    
    private boolean isHandleDiamonds = true;
    
    private D6EntityAccessorIF<? extends D6EntityIF> daoEntities = null;
    private D6EntityLinkAccessorIF<? extends D6LinkIF> daoEntityLinks = null;
    
    
    /**
     * Constructor
     * @param db D6 DB
     */
	public D6ByDirectedLinkBomDivider(D6SystemizerDataDb db) {
		super( db );
	}

	@Override
	public void setConf( D6DataConf conf, AbstractAlgoType confAlgo ) throws X6Exception {
		
	    super.setConf( conf, confAlgo );

	    // nb of threads
		nbConcurrentThreads = conf.getNbThreads();
		
	}
	
	@Override
	protected void recordAndValidateConfigParameters( List<String> paramNames, Map<String, String> propsParams, Map<String, ParamType> mapParams ) throws X6Exception {

		// get our parameter
	    ParamType paramHandleDiamonds = mapParams.get( PARAM_HANDLE_DIAMONDS );
	    if ( paramHandleDiamonds != null ) {
	    	isHandleDiamonds = Boolean.parseBoolean( paramHandleDiamonds.getValue() );
	    }
	    
	}
	

	@Override
	protected Stats doAlgoRun( Transaction txn, D6AlgoCommandIF algoCommand ) throws X6Exception {
		
		Stats stats = new Stats();
		
		// init daos
		daoEntities 	= algoCommand.getDaoEntities();
		daoEntityLinks 	= algoCommand.getDaoEntityLinks();
		
		// browse benches
		try (
		    EntityCursor<D6Bench> benches = db.daoBenches.byId.entities( txn, null );
		) {
			for ( D6Bench bench: benches ) {
				// skip no bench
				if ( bench.getId() == D6Bench.NO_BENCH ) {
					continue;
				}
				
				// process boms for current bench
				processBomsForBench( txn, bench );
					
			}
			
		}
		
        // Circuits are not allocated
		// Fix this
        allocateCircuitsToErrorLot( txn );
        
		return stats;
		
	}

	/**
	 * Process Boms given a bench
	 * @param txn Transaction
	 * @param bench Banch
	 * @throws X6Exception
	 */
	protected void processBomsForBench( Transaction txn, D6Bench bench ) throws X6Exception {
		// find BOMs top objects
		LOGGER.info( "Get Bill Of Material top objects for bench " + bench.getId() );
		
    	// get boms for current bench
    	List<D6EntityIF> bomHeadEntities = new ArrayList<>();
    	
    	try (
	    	ForwardCursor<Long> cursorBomHeadStats = 
    			db.daoEntityStats.getBomHeadsForBench( txn, CursorConfig.READ_UNCOMMITTED, bench.getId() );
    	) {
    	    
    		for ( Long idStat: cursorBomHeadStats ) {
    		    
    			// Get stat
    			D6EntityDirectedLinkStats stat = db.daoEntityStats.byId.get( txn, idStat, null );

    			// get object
    			D6EntityIF bomHeadObject = daoEntities.getById().get( txn, stat.getIdObject(), null );
    			
    			// process only unallocated boms
    			if ( bomHeadObject.getIdLot() == D6Lot.TECH_ID_UNALLOCATED ) {
    				bomHeadEntities.add( bomHeadObject );
    			}
    			
    		}
    	}
    	
		LOGGER.info( "  found " + bomHeadEntities.size() + " BOM head objects" );
		
		LOGGER.info( "Build Bill Of Materials from BOM head objects" );

		// Lotize BOM heads
		LOGGER.info( "Step 1 - Lotize BOM heads" );
		lotizeBomHeads( txn, bench, bomHeadEntities, iPass );
		
		
		// Lotize BOM head children
		LOGGER.info( "Step 2 - Lotize BOM head children" );

		try (
			// Prepare a thread manager
			X6ThreadManager threadManager = 
			     new X6ThreadManager(
			         conf.props, D6ByDirectedLinkBomDivider.class.getSimpleName(),
			         "bom", nbConcurrentThreads, true 
			     );
				
		    // Progress for bom heads
		    D6Progress progressBomHeads = new D6Progress( db, bomHeadEntities.size(), "", "BOM Heads" );		
		    // Progress for bom children
		    D6Progress progressBomChildren = new D6Progress( db, -1, "  ", "BOM children" );
		) 
		{
    		for ( D6EntityIF bomHeadEntity : bomHeadEntities ) {
    			
    			progressBomHeads.iItem++;
    			if ( ( progressBomHeads.iItem % progressBomHeads.iTick ) == 1 ) {
    				progressBomHeads.show();
    			}
    			
    			// recurse BOM children by threads
    			RecurseBomRunnable recurseBomRunnable = 
    				new RecurseBomRunnable( txn, iPass, bomHeadEntity, bench, bomHeadEntity.getIdLot(), progressBomChildren );
    			
    			threadManager.startThread( recurseBomRunnable );
    			
    		}
            
    		// Wait for threads end
            threadManager.join();
            
    		// error?
    		List<Throwable> throables = threadManager.getThroables();
    		if ( !throables.isEmpty() ) {
    			// show first one
    			throw new X6Exception( throables.get( 0 ) );
    		}
    		
		} catch ( Throwable t ) {
		    X6Error.handleThrowable( t );
		}
		
		// ok, this an optimisation to (losange case)
		if ( isHandleDiamonds ) {
			finalizeBoms( txn, bench, bomHeadEntities );
		}
		
	}

	private void lotizeBomHeads( 
	    Transaction txn, D6Bench bench, List<D6EntityIF> bomHeadEntities, int iPass
	) throws X6Exception {
		
	    try (
	        // Progress
		    D6Progress progress = new D6LUtil.D6Progress( db, bomHeadEntities.size(), "", "BOM Heads" );
		) {
    		// Browse BOM head objects
    		for ( D6EntityIF bomHeadObject: bomHeadEntities ) {
    			
    			// progress
    			progress.iItem++;
    			if ( ( progress.iItem % progress.iTick ) == 1 ) {
    				progress.show();
    			}
    			
    			// Create BOM lot
    			D6Lot bomHeadLot = getNewBom( bench, iPass );
    			
    			// set BOM head entity as primary lot target
    			bomHeadLot.setPrimaryTargetEntityUid( bomHeadObject.getUniversalId() );
    			
    			// BOM fct ID is BOM head fct ID
    			if ( bomHeadObject instanceof D6InfoManagedEntityIF ) {
    				D6EntityInfoIF bomHeadObjectInfo = db.daoObjectInfos.byId.get( bomHeadObject.getId() );
    				if ( bomHeadObjectInfo != null ) {
    					bomHeadLot.setFctId( bomHeadObjectInfo.getFctId() );
    				}
    			}
    			
    			// save it
    			bomHeadLot.save( db, txn );
    			
    			// allocate bom head to lot
    			bomHeadObject.setIdLot( bomHeadLot.getId() );
    			// save it
    			bomHeadObject.save( db, txn );
    			
    		}
	    }
	}

	private D6Lot getNewBom( D6Bench bench, int iPass ) throws X6Exception {
	    
		D6Lot bom = new D6Lot( producesLotType, D6Lot.DISPLAY_TYPE_BOM, iPass );
		// set bench
		bom.setIdBench( bench.getId() );
		// set business parent lot
		bom.setIdLotParent( bench.getIdLot() );
		
		return bom;
	}

	/**
	 * Fix unneeded boms
	 * @param txn
	 * @param bench
	 * @param bomHeadEntities
	 * @throws Exception 
	 */
	private void finalizeBoms( 
	    Transaction txn, D6Bench bench, List<D6EntityIF> bomHeadEntities
	) throws X6Exception {
		
		// finalization loop
		boolean goOn = true;
		while ( goOn ) {
			
			// flag to check if we fixed something
			boolean aFixHasBeenDone = false;
			
			// set of parent objects BOM ID
			Set<Long> setParentBomIds = new HashSet<>();
			
			// browse bom heads
			for ( D6EntityIF bomHead: bomHeadEntities ) {
				
				aFixHasBeenDone = 
				    finalizeBomsForBomHead( txn, bench, aFixHasBeenDone, setParentBomIds, bomHead );
					
			}
			
			// end on loop
			goOn = aFixHasBeenDone;	// this loop fixed nothing
		}
		
	}

    private boolean finalizeBomsForBomHead( Transaction txn, D6Bench bench, boolean aFixHasBeenDone,
                                            Set<Long> setParentBomIds, D6EntityIF bomHead )
        throws X6Exception
    {
        
        boolean new_aFixHasBeenDone = aFixHasBeenDone;
        
        // clear set of parent objects BOM ID
        setParentBomIds.clear();
        
        try (
            // get parent objects
            ForwardCursor<? extends D6LinkIF> bomHeadParentLinks = 
            	daoEntityLinks.getLinksAssociatedToEntityByRoleB_and_LinkLevel( 
            		txn, bomHead.getId(), 
            		// only directed to links
            		DependencyBeanDirectionEnum.DirectedFromTo, 
            		// we target link level, we need links like O <--L--> O, so link level = 1
            		1,
            		CursorConfig.READ_UNCOMMITTED
            	);
        ) {
        	for ( D6LinkIF link: bomHeadParentLinks ) {
        		// get parent entity
        		D6EntityIF parentEntity = daoEntities.getById().get( txn, link.getIdRoleA(), null );
        		// store bom ID if same bench
        		if ( parentEntity.getIdBench() == bench.getId() ) {
        			setParentBomIds.add( parentEntity.getIdLot() );
        		}
        	}
        }
        
        // only one parent bom ID and parent bom ID != current bom ID?
        if ( setParentBomIds.size() == 1 ) {
        	// get unique id
        	long parentBomId = setParentBomIds.iterator().next();
        	// different from current bomID
        	if ( parentBomId != bomHead.getIdLot() ) {
        		
        		// yes, a repair is needed
        	    new_aFixHasBeenDone = true;
        		
        		// this bom head can be 'crushed' in current bench scope
        		replaceBomId( txn, bench, bomHead, parentBomId );
        		
        	}
        }
        
        return new_aFixHasBeenDone;
    }


	private void replaceBomId( 
	    Transaction txn, D6Bench bench, D6EntityIF currentBomHead, long toBomId
	) throws X6Exception {
		
		// select entities allocated to currentBomId for bench
		
		// Entities
		
		EntityJoin<Long, ? extends D6EntityIF> joinEntities = daoEntities.getByBenchAndLot( bench, currentBomHead.getIdLot() );
		try (
			ForwardCursor<? extends D6EntityIF> entities = joinEntities.entities(txn, CursorConfig.READ_UNCOMMITTED );
		) {
			// set to new bom
			for ( D6EntityIF entity: entities ) {
				entity.setIdLot( toBomId );
				entity.save( db, txn );
			}
		}
		
		// Links
		
		EntityJoin<Long, ? extends D6EntityIF> joinEntityLink = daoEntityLinks.getByBenchAndLot( bench, currentBomHead.getIdLot() );
		try (
			ForwardCursor<? extends D6EntityIF> entityLinks = joinEntityLink.entities( txn, CursorConfig.READ_UNCOMMITTED );
		) {
			// set to new bom
			for ( D6EntityIF entityLink: entityLinks ) {
				D6LinkIF link = (D6LinkIF) entityLink;
				
				link.setIdLot( toBomId );
				link.save( db, txn );
			}
		}
		
		// change bom head
		currentBomHead.setIdLot( toBomId );
		currentBomHead.save( db, txn );
		
	}

    /**
     * Allocate circuit to error lot
     * @param txn
     * @throws X6Exception 
     * @throws DatabaseException 
     */
    private void allocateCircuitsToErrorLot( Transaction txn ) throws DatabaseException, X6Exception
    {
        
        // browse benches
        try (
            EntityCursor<D6Bench> benches = db.daoBenches.byId.entities( txn, null );
        ) {
            for ( D6Bench bench: benches ) {
                // skip no bench
                if ( bench.getId() == D6Bench.NO_BENCH ) {
                    continue;
                }
                
                // Error lot
                D6Lot errorLot = null;

                // Unallocated entities?
                try (
                    ForwardCursor<? extends D6EntityIF> unallocatedEntities = 
                        daoEntities.getByBenchAndLot( bench, D6Lot.TECH_ID_UNALLOCATED ).entities( txn, CursorConfig.READ_COMMITTED );
                ) {
                    
                    for ( D6EntityIF unallocatedEntity: unallocatedEntities ) {
                        
                        // move to error lot
                        if ( errorLot == null ) {
                            errorLot = createErrorLot( txn, bench );
                        }
                        unallocatedEntity.setIdLot( errorLot.getId() );
                        unallocatedEntity.save( db, null );;
                    }
                    
                }
                
            }
            
        }
        
    }


	/**
     * Create error lot
     * @param txn
     * @param bench
     * @return
	 * @throws X6Exception 
     */
    private D6Lot createErrorLot( Transaction txn, D6Bench bench ) throws X6Exception
    {
        D6Lot errorLot = new D6Lot( getProducesLotType(), "Unallocated", iPass );
        errorLot.setLotSubtype( D6LPackageSubtypeEnum.DEFAULT_LOT );
        
        // assign to parent lot from bench 
        errorLot.setIdLot( bench.getIdLot() );
        
        // save lot
        errorLot.save( db, txn );
        
        return errorLot;
    }


    /**
	 * Recurse BOM runnable
	 */
	private class RecurseBomRunnable implements Runnable {
		
		private D6EntityIF bomHeadEntity;
		private D6Bench bench;
		private long bomId;
		private D6Progress progress;
		private Transaction txn;
		private int iPass;
		
		public RecurseBomRunnable( Transaction txn, int iPass, D6EntityIF bomHeadEntity, D6Bench bench, long bomId, D6Progress progress ) {
			super();
			this.txn = txn;
			this.iPass = iPass;
			this.bomHeadEntity = bomHeadEntity;
			this.bench = bench;
			this.bomId = bomId;
			this.progress = progress;
		}


		@Override
		public void run() {

			// Init set of objects and links belonging to BOM
			Set<Long> bomObjectContent = new HashSet<>();

			// recurse BOM
			try {
				recurseBom( txn, iPass, bench, bomId, bomHeadEntity, bomObjectContent );
			} catch ( Exception e ) {
				throw new X6Error( e );
			}
			
			// clean set
			bomObjectContent.clear();
		}
		
		private void recurseBom( 
			final Transaction txn, int iPass,
			final D6Bench bench,
			long bomId, D6EntityIF bomEntity, final Set<Long> bomEntityContent
		) throws Exception {
			
			// stats
			synchronized ( progress ) {
				progress.iItem++;
				
				if ( ( progress.iItem % progress.iTick ) == 1 ) {
					// show progress
					progress.show();
				}
			}
			
			// Add current object to bom content
			bomEntityContent.add( bomEntity.getId() );
			
			// set bom ID to current object
			
			if ( 
			      ( 
			          bench.isQuick() && 
			          ( bomEntity.getIdLot() == D6Lot.TECH_ID_UNALLOCATED ) 
			      )
			      ||
			      ( 
			          !bench.isQuick() &&
			          ( bomEntity.getIdBench() == bench.getId() )
			          && 
                      // this is our bench
			          ( bomEntity.getIdLot() == D6Lot.TECH_ID_UNALLOCATED )
			      )
			) {
					
				// not allocated yet
				bomEntity.setIdLot( bomId );
				// save it
				bomEntity.save( db, txn );
				
			}
			
			// Get links to avoid modification into cursor to avoid leaving too much openened cursors
			List<D6LinkIF> childrenLinks = new ArrayList<>();
			
			try (
				// get children
				ForwardCursor<? extends D6LinkIF> bomChildren = 
					daoEntityLinks.getLinksAssociatedToEntityByRoleA_and_LinkLevel( 
						txn, bomEntity.getId(),
						// only directed to links
						DependencyBeanDirectionEnum.DirectedFromTo, 
			    		// we target link level, we need links like O <--L--> O, so link level = 1
			    		1,
						null 
					);
			) {
				for ( D6LinkIF link: bomChildren ) {
					childrenLinks.add( link );
				}
					
			}
			
			// browse children
			for ( D6LinkIF link: childrenLinks ) {
				
				// set bom to link
				link.setIdLot( bomId );
				// save link
				link.save( db, txn );
				
				D6EntityIF child = db.daoMetaEntities.byIdGet( txn, iPass, iPassTechLot, link.getIdRoleB(), null );
				
				if ( 
	                  // have we already browsed child?
				      ( bomEntityContent.contains( child.getId() ) )
				      ||
				      // already allocated?
				      ( child.getIdLot() != D6Lot.TECH_ID_UNALLOCATED )
				){
					// yes, next child
					continue;
				}
				
				// recurse child
				recurseBom( txn, iPass, bench, bomId, child, bomEntityContent );
				
			}
			
		}
	
	}
	
	@Override
	public Stats doPrepare( Integer idParentMilestone, Transaction txn, D6AlgoCommandIF algoCommand ) throws X6Exception {
		// No preparation
		return null;
	}

	@Override
	public void clean( Integer idParentMilestone, Transaction txn, D6AlgoCommandIF algoCommand ) throws X6Exception {
		// done by preparation
	}

	@Override
	public boolean isNeedLocalIdsInBench() {
		return false;
	}

	@Override
	public Stats doReadExecutionResult( Integer idParentMilestone, Transaction txn, D6AlgoCommandIF algoCommand, int iPass ) throws X6Exception {
		
		return null;
	}

    @Override
	public TransactionNeeds getTransactionNeeds() {
		// None for perf reason
		return null;
	}

	@Override
	public boolean isAllowsSinglesAllocation() {
		// Yes we can
		return true;
	}
		
	@Override
	public boolean isNeedBomSimplification() {
		// Needed
		return true;
	}
	
    @Override
    public boolean isNeedBomSimplifiedEntitiesRemovedFromBench() {
        return true;
    }


	@Override
	public String getName() {
		return "Bill Of Material Topological Divider";
	}
		
	@Override
	public D6LPackageTypeEnum getDefaultProducesLotType() {
		
		return D6LPackageTypeEnum.TECHNICAL_LOT;
	}


}
