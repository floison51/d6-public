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

package org.xlm.jxlm.d6light.data.algo.topological.bom;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.xlm.jxlm.d6light.data.algo.D6LAlgoCommandIF;
import org.xlm.jxlm.d6light.data.algo.topological.D6LAbstractTopologicalDivider;
import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.conf.ParamType;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LEdge;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;
import org.xlm.jxlm.d6light.data.model.D6LPackage;
import org.xlm.jxlm.d6light.data.model.D6LVertex;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

/**
 * Build Bill Of Material lots by directed link types
 * @author Francois Loison
 *
 */
public class D6LByDirectedLinkBomDivider extends D6LAbstractTopologicalDivider {

    /** If true, handle diamond topologies to reduce to one bom **/
    public static final String PARAM_HANDLE_DIAMONDS = "handleDiamonds";

    private boolean isHandleDiamonds = true;
    
    private final Graph<D6LVertex, D6LEdge> inGraph = db.inGraph;
    
    /**
     * Constructor
     * @param db D6 DB
     */
	public D6LByDirectedLinkBomDivider() {
		super();
	}

	@Override
	public void setConf( 
		D6LightDataConf conf, AbstractAlgoType algoConf
	) throws D6LException {
		
		super.setConf( conf, algoConf );

	}
	
	@Override
	protected void recordAndValidateConfigParameters( List<String> paramNames, Map<String, String> propsParams, Map<String, ParamType> mapParams ) throws D6LException {

		// get our parameter
	    ParamType paramHandleDiamonds = mapParams.get( PARAM_HANDLE_DIAMONDS );
	    if ( paramHandleDiamonds != null ) {
	    	isHandleDiamonds = Boolean.parseBoolean( paramHandleDiamonds.getValue() );
	    }
	    
	}
	

	@Override
	protected void doAlgoRun( D6LAlgoCommandIF algoCommand ) throws D6LException {
		
		// process boms for current bench
		processBomsForBench();
					
        // Circuits are not allocated
		// Fix this
        allocateCircuitsToErrorLot();
        
	}

	/**
	 * Process Boms given a bench
	 * @param txn Transaction
	 * @param bench Banch
	 * @throws D6LException
	 */
	protected void processBomsForBench() throws D6LException {
		
		// find BOMs top objects
		LOGGER.info( "Get Bill Of Material top objects" );
		
    	// get boms for current bench
    	List<D6LVertex> bomHeadVertices = new ArrayList<>();
    	
    	Set<Integer> idBomHeadVertices = db.daoEntityStats.getBomHeads();
    	
		for ( int idBomHeadVertex: idBomHeadVertices ) {
		   
			// get vertex
			D6LVertex bomHeadVertex = db.daoEntityRegistry.getVertex( idBomHeadVertex );
			
			// process only unallocated boms
			if ( bomHeadVertex.getPackage() == D6LPackage.UNALLOCATED ) {
				bomHeadVertices.add( bomHeadVertex );
			}
			
    	}
    	
		LOGGER.info( "  found " + bomHeadVertices.size() + " BOM head objects" );
		
		LOGGER.info( "Build Bill Of Materials from BOM head objects" );

		// Lotize BOM heads
		LOGGER.info( "Step 1 - Lotize BOM heads" );
		lotizeBomHeads( bomHeadVertices );
		
		
		// Lotize BOM head children
		LOGGER.info( "Step 2 - Lotize BOM head children" );

		for ( D6LVertex bomHeadEntity : bomHeadVertices ) {
			
			// recurse BOM children by threads
			RecurseBomPseudoRunnable recurseBomRunnable = 
				new RecurseBomPseudoRunnable( bomHeadEntity, bomHeadEntity.getPackage() );
			
			recurseBomRunnable.run();
			
		}
        
		// ok, this an optimisation to (losange case)
		if ( isHandleDiamonds ) {
			finalizeBoms( bomHeadVertices );
		}
		
	}

	private void lotizeBomHeads( 
	    List<D6LVertex> bomHeadEntities
	) throws D6LException {
		
		// Browse BOM head objects
		for ( D6LVertex bomHeadObject: bomHeadEntities ) {
			
			// Create BOM lot
			D6LPackage bomHeadLot = getNewBom();
			
			// set BOM head entity as primary lot target
			bomHeadLot.setPrimaryTarget( bomHeadObject );
			
			// allocate bom head to lot
			bomHeadObject.setPackage( bomHeadLot );
			
		}
	}

	private D6LPackage getNewBom() throws D6LException {
	    
		D6LPackage bom = db.daoEntityRegistry.newPackage( producesLotType, D6LPackageSubtypeEnum.BOM );
		
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
	    List<D6LVertex> bomHeadEntities
	) throws D6LException {
		
		// finalization loop
		boolean goOn = true;
		while ( goOn ) {
			
			// flag to check if we fixed something
			boolean aFixHasBeenDone = false;
			
			// set of parent objects BOM ID
			Set<D6LPackage> setParentBoms = new HashSet<>();
			
			// browse bom heads
			for ( D6LVertex bomHead: bomHeadEntities ) {
				
				aFixHasBeenDone = 
				    finalizeBomsForBomHead( aFixHasBeenDone, setParentBoms, bomHead );
					
			}
			
			// end on loop
			goOn = aFixHasBeenDone;	// this loop fixed nothing
		}
		
	}

    private boolean finalizeBomsForBomHead( boolean aFixHasBeenDone,
                                            Set<D6LPackage> setParentBoms, D6LVertex bomHead )
        throws D6LException
    {
        
        boolean new_aFixHasBeenDone = aFixHasBeenDone;
        
        // clear set of parent objects BOM ID
        setParentBoms.clear();
        
        // get parent objects
    	Set<D6LEdge> bomHeadParentLinks = inGraph.incomingEdgesOf( bomHead );
    	for ( D6LEdge link: bomHeadParentLinks ) {
    		
    		// get parent entity
    		D6LVertex parentEntity = inGraph.getEdgeSource( link );
    		// store bom ID if same bench
   			setParentBoms.add( parentEntity.getPackage() );
    		
    	}
        
        // only one parent bom ID and parent bom ID != current bom ID?
        if ( setParentBoms.size() == 1 ) {
        	// get unique id
        	D6LPackage parentBom = setParentBoms.iterator().next();
        	// different from current bomID
        	if ( parentBom != bomHead.getPackage() ) {
        		
        		// yes, a repair is needed
        	    new_aFixHasBeenDone = true;
        		
        		// this bom head can be 'crushed' in current bench scope
        		replaceBomId( bomHead, parentBom );
        		
        	}
        }
        
        return new_aFixHasBeenDone;
        
    }


	private void replaceBomId( 
	    D6LVertex currentBomHead, D6LPackage toBomId
	) throws D6LException {
		
		// select entities allocated to currentBomId for bench
		
		// Entities
		Iterator<D6LEntityIF> iterator = db.daoEntityRegistry.entityIterator();
		
		while ( iterator.hasNext() ) {
			
			D6LEntityIF entity = iterator.next();
			
			if ( entity.getPackage() == currentBomHead.getPackage() ) {
				
				entity.setPackage( toBomId );
				
			}
		}
		
		// change bom head
		currentBomHead.setPackage( toBomId );
		
	}

    /**
     * Allocate circuit to error lot
     * @param txn
     * @throws D6LException 
     * @throws DatabaseException 
     */
    private void allocateCircuitsToErrorLot() throws D6LException
    {
        
    	Iterator<D6LEntityIF> iterator = db.daoEntityRegistry.entityIterator();
    	while ( iterator.hasNext() ) {
    		D6LEntityIF entity = iterator.next();
    		
    		if ( entity.getPackage() == D6LPackage.UNALLOCATED ) {
    			throw new D6LException( 
    				MessageFormat.format( "Unallocated entity {0}", entity.getId() )
    			);
    		}
    	}
                
    }

    /**
	 * Recurse BOM runnable
	 */
	private class RecurseBomPseudoRunnable {

		private D6LVertex bomHeadEntity;
		private D6LPackage bom;
		
		public RecurseBomPseudoRunnable( D6LVertex bomHeadEntity, D6LPackage bom ) {
			super();
			this.bomHeadEntity = bomHeadEntity;
			this.bom = bom;
		}

		public void run() throws D6LException {

			// Init set of objects and links belonging to BOM
			Set<Integer> bomObjectContent = new HashSet<>();

			// recurse BOM
			try {
				recurseBom( bom, bomHeadEntity, bomObjectContent );
			} catch ( Exception e ) {
				throw new D6LException( e );
			}
			
			// clean set
			bomObjectContent.clear();
		}
		
		private void recurseBom( 
			D6LPackage bom, D6LVertex bomEntity, final Set<Integer> bomEntityContent
		) throws Exception {
			
			// Add current object to bom content
			bomEntityContent.add( bomEntity.getId() );
			
			// set bom ID to current object
			
			if ( bomEntity.getPackage() == D6LPackage.UNALLOCATED ) {
					
				// not allocated yet
				bomEntity.setPackage( bom );
				
			}
			
			// Get links to avoid modification into cursor to avoid leaving too much openened cursors
			Set<D6LEdge> childrenLinks = inGraph.outgoingEdgesOf( bomEntity );
			
			// browse children
			for ( D6LEdge link: childrenLinks ) {
				
				// set bom to link
				link.setPackage( bom );
				
				D6LVertex child = inGraph.getEdgeTarget( link );
				
				if ( 
	                  // have we already browsed child?
				      ( bomEntityContent.contains( child.getId() ) )
				      ||
				      // already allocated?
				      ( child.getPackage() != D6LPackage.UNALLOCATED )
				){
					// yes, next child
					continue;
				}
				
				// recurse child
				recurseBom( bom, child, bomEntityContent );
				
			}
			
		}
		
	}
	
	@Override
	public void doPrepare( D6LAlgoCommandIF algoCommand ) throws D6LException {
		// No preparation
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
	public String getName() {
		return "Bill Of Material Topological Divider";
	}
		
	@Override
	public D6LPackageTypeEnum getDefaultProducesLotType() {
		
		return D6LPackageTypeEnum.TECHNICAL_PKG;
	}


}
