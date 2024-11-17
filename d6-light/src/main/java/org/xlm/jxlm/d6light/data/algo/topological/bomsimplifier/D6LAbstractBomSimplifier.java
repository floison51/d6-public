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

package org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.jgrapht.Graph;
import org.xlm.jxlm.d6light.data.algo.D6LAlgoCommandIF;
import org.xlm.jxlm.d6light.data.conf.AbstractBomSimplifierType;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.job.D6LJobIF;
import org.xlm.jxlm.d6light.data.measures.D6LEntityDirectedLinkStats;
import org.xlm.jxlm.d6light.data.model.D6LAbstractPackageEntity;
import org.xlm.jxlm.d6light.data.model.D6LEdge;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;
import org.xlm.jxlm.d6light.data.model.D6LPackageVertex;
import org.xlm.jxlm.d6light.data.model.D6LVertex;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

/**
 * This class holds imformation for Bom Simplifiers: components and kits detection
 * @author Loison
 *
 */
public abstract class D6LAbstractBomSimplifier
{

    /** Kind of Bom siplifier **/
    public enum BomSimplifierKindEnum { 
    	Components, Kits;

	    /**
	     * Convert from D6LotSubtypeEnum
	     * @param lotSubTypeEnum
	     * @return
	     * @throws D6LException 
	     */
		public static BomSimplifierKindEnum valueOfSubType( D6LPackageSubtypeEnum lotSubTypeEnum ) throws D6LException {
			
			switch( lotSubTypeEnum ) {
				case COMPONENT_LOT : {
					return Components;
				}
				case KIT_LOT : {
					return Kits;
				}
				default : {
					throw new D6LException( "Unmappable lot sub-type " + lotSubTypeEnum );
				}
			}
		}
    
    }
    
    protected final boolean singleExtractorLot;

	protected final D6LDb db = D6LDb.getInstance();
    
	private final Graph<D6LVertex, D6LEdge> inGraph = db.inGraph;
	
	private final D6LPackageVertex benchLot = D6LPackageVertex.ROOT_BENCH_PACKAGE; 
	
    private D6LAbstractBomSimplifier( 
        boolean singleExtractorLot
    )
    {
        super();
        this.singleExtractorLot = singleExtractorLot;
    }
    
    public D6LAbstractBomSimplifier( 
        AbstractBomSimplifierType conf
    )
    {
        this( 
        	// Default value for isSingleExtractorLot
        	( conf.isSingleExtractorLot() != null ) ? conf.isSingleExtractorLot() : true
        );
    }
    
    public abstract BomSimplifierKindEnum getKind();
    public abstract D6LPackageSubtypeEnum getLotSubType();

    /**
     * Tune lot with a matching entity
     * @param bomSimplifierLot
     * @throws D6LException 
     */
    /*
	public void tuneLot( Transaction txn, D6Package bomSimplifierLot, Object tuningInfo ) throws D6LException {
		// Nothing done here
	}
	*/
    
	public class MatchResult {
		
		public final boolean match;
		public final Object lotTuningInfo;
		
		public MatchResult( boolean match, Object lotTuningInfo ) {
			super();
			this.match = match;
			this.lotTuningInfo = lotTuningInfo;
		}
		
	}
	
	/**
	 * Return true if BomSimplifier (kit, component) matches without taking in consideration numbers<p/>
	 * If true, an stats entry is stored
	 * 
	 * @param algoCommand
	 * @return
	 * @throws D6LException
	 */
    public abstract boolean matchWithoutNumbers( 
        Session session,
    	D6LAlgoCommandIF algoCommand, 
    	D6LEntityIF entity, D6LEntityDirectedLinkStats stat 
    ) throws D6LException;

    /**
     * Return true if BOM simplification matches
     * @param nbDirectedLinksFromEntity
     * @param nbDirectedLinksToEntity
     * @param postActions 
     * @return object needed for tuning lot
     * @throws D6LException 
     */
    public abstract MatchResult match( 
        Session session,
    	D6LAlgoCommandIF algoCommand, 
    	D6LVertex entity, boolean matchWithoutNumbersResult, D6LEntityDirectedLinkStats stat, 
    	D6LAbstractPackageEntity singlePackage, List<D6LJobIF<D6LEntityIF>> postActions
    ) throws D6LException;
   
    /**
     * Move components or kits technical lots to component ro kit business lot
     * @param txn
     * @param iPass
     * @param parentIdLot
     * @throws D6LException 
     */
    protected void moveSimplifiedTechnicalLotsToBusinessLot( 
    	Session session
    ) throws D6LException {
        
        // Get sub-lot type
        final D6LPackageSubtypeEnum lotSubType = getLotSubType();

        if ( lotSubType == null ) {
        	// Nothing to do
        	return;
        }
        
        // create or get business component lot
        D6LAbstractPackageEntity outerSimplifiedLot = null;
        
        // get parent lot until we get a Business Lot
        D6LAbstractPackageEntity targetBusinessLot = benchLot;
        
        /*
        do {
            // move to parent
            targetBusinessLot = db.daoLots.byId.get( txn, targetBusinessLot.getIdLotParent(), null );
            
        }
        while ( 
            ( targetBusinessLot != null ) && 
            ( targetBusinessLot.getId() != D6Lot.ID_NO_LOT ) && 
            ( targetBusinessLot.getLotType() != D6LPackageTypeEnum.BUSINESS_LOT ) 
        );
        */
        
        /*
        // Are we allowed to re-use an outer lot 
        if ( isSingleExtractorLot() ) {
        	
	        EntityJoin<Long, D6Lot> joinBiz = new EntityJoin<>( db.daoLots.byId );
	        joinBiz.addCondition( db.daoLots.bySubtype, lotSubType );
	        joinBiz.addCondition( db.daoLots.byType, D6LPackageTypeEnum.BUSINESS_LOT );
	
	        // lot belongs to parent business lot
	        joinBiz.addCondition( db.daoLots.byIdLotParent, targetBusinessLot.getId() );
	        
	        try (
	            ForwardCursor<D6Lot> outerSimplifiedLots = joinBiz.entities( txn,  null );
	        ) {
	            outerSimplifiedLot = outerSimplifiedLots.next();
	        }
	        
        }
        */
        
        // to be created?
        if ( outerSimplifiedLot == null ) {
            
        	outerSimplifiedLot = createOuterSimplifiedLot( session, lotSubType, targetBusinessLot);
            
        }
        
        /*
        // move inner components lots to biz lot
        EntityJoin<Long, D6Lot> joinTech = new EntityJoin<>( db.daoLots.byId );
        // component lot condition
        joinTech.addCondition( db.daoLots.bySubtype, lotSubType );
        // current parrent lot condition
        joinTech.addCondition( db.daoLots.byIdLotParent, idLotOfCurrentBench );
        
        try (
            ForwardCursor<D6Lot> innerSimplifiedLots = joinTech.entities( txn,  null );
        ) {
            for ( D6Lot innerSimplifiedLot: innerSimplifiedLots ) {
                
                // move to biz component lot
                innerSimplifiedLot.setIdLot( outerSimplifiedLot.getId() );
                innerSimplifiedLot.save( db, txn );
                
                // Are we allowed to re-use an outer lot 
                if ( !isSingleExtractorLot() ) {

                	// Tune current outer simplified lot
                	tuneLot( txn, outerSimplifiedLot, innerSimplifiedLot ); 
                	
                	// Create a new one
                	outerSimplifiedLot = createOuterSimplifiedLot(db, txn, iPass, lotSubType, targetBusinessLot);
                	
                }

            }
        }
        */
    }

	private D6LPackageVertex createOuterSimplifiedLot(
	    Session session,
		final D6LPackageSubtypeEnum lotSubType, D6LAbstractPackageEntity targetBusinessLot
	) throws D6LException {

		D6LPackageVertex outerSimplifiedLot;
		outerSimplifiedLot = new D6LPackageVertex( D6LPackageTypeEnum.BUSINESS_PKG, lotSubType );
		outerSimplifiedLot.setName( lotSubType.getLotName() );
		
		// Persist, add to graph
		session.persist( outerSimplifiedLot );
		db.outGraph.addVertex( outerSimplifiedLot );
		
		return outerSimplifiedLot;
		
	}

	public boolean isSingleExtractorLot() {
		return singleExtractorLot;
	}

	/**
	 * If a component is extracted, stats for used objects must be updated
	 *
	 */
    public class ReworkChildrenJob implements D6LJobIF<D6LEntityIF> {

    	private final Session session;
    	
         /** Algo command **/
        private final D6LAlgoCommandIF algoCommand;
        
        /** Kit to operate on **/
        private final D6LVertex kit;
        
        /** Single lot **/
        private final D6LAbstractPackageEntity singleLot;
        
        public ReworkChildrenJob( Session session, D6LAlgoCommandIF algoCommand, D6LVertex kit, D6LAbstractPackageEntity singleLot ) {
            super();
            this.session = session;
            this.algoCommand = algoCommand;
            this.kit = kit;
            this.singleLot = singleLot;
        }

        @Override
        public void doJob( D6LEntityIF notUsed )  throws D6LException {
            
            // Get kit children
        	Set<D6LEdge> kitLinks = inGraph.outgoingEdgesOf( kit );
            
        	for ( D6LEdge kitLink : kitLinks ) {
                
                // Get to entity
                D6LVertex child = inGraph.getEdgeTarget( kitLink );
                
                // Get stats
                D6LEntityDirectedLinkStats childStats = 
                	db.daoEntityStats.getByEntity( session, child );
                
                if ( childStats != null ) {
                	
                    // rework child stats
                    // Remove kit
                	
                	// Kink is by constraint from/to directed
                    childStats.incNbDirectedLinksToForBench( -1 );
                    childStats.incNbLinksToForBench( -1 );
                                            
                    if ( 
                    	( singleLot != null ) && 
                    	( childStats.getNbLinksFromForBench() == 0 ) && ( childStats.getNbLinksToForBench() == 0 ) 
                     ) {

                        child.setPackageEntity( singleLot );
                        
                    }
                    
                }
                
            }
        }
    }

    /**
     * Create histogram entry
     * @param pass
     * @param entity
     * @param nbDirectedLinksFromEntity
     * @param nbDirectedLinksToEntity
     */
	public void createAndSaveHistogramEntry( 
		Session session, 
		D6LEntityIF entity, long nbDirectedLinksFromEntity, long nbDirectedLinksToEntity 
	) {
		// Do nothing
		
	}

}
