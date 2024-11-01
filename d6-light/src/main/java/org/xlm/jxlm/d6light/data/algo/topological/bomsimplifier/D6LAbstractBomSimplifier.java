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

import org.xlm.jxlm.d6light.data.algo.D6LAlgoCommandIF;
import org.xlm.jxlm.d6light.data.algo.topological.D6LEntityDirectedLinkStats;
import org.xlm.jxlm.d6light.data.conf.AbstractBomSimplifierType;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;
import org.xlm.jxlm.d6light.data.model.D6LPackageVertex;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;

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
		public static BomSimplifierKindEnum valueOf( D6LPackageSubtypeEnum lotSubTypeEnum ) throws D6LException {
			
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
    
    protected final boolean specificBusinessLot;

    protected final boolean singleExtractorLot;
    
    private D6LAbstractBomSimplifier( 
        boolean specificBusinessLot, boolean singleExtractorLot
    )
    {
        super();
        this.specificBusinessLot = specificBusinessLot;
        this.singleExtractorLot = singleExtractorLot;
    }
    
    public D6LAbstractBomSimplifier( 
        AbstractBomSimplifierType conf
    )
    {
        this( 
        	conf.isSpecificBusinessLot(), 
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
    	D6LAlgoCommandIF algoCommand, 
    	D6LEntityIF entity, boolean matchWithoutNumbersResult, 
    	D6LPackageVertex singlePackage /*, List<X6JobIF<D6LEntityIF>> postActions*/ 
    ) throws D6LException;
   
    /**
     * Move component of kit lot to upper level
     * @param txn
     * @throws D6LException 
     */
    public void moveSimplifiedTechnicalLotsToBusinessLotIfNeeded( 
    )
    	throws D6LException
    {
    	throw new Error( "TODO" );
    	/*
        if ( specificBusinessLot ) {
            
            // browse benches
            try (
                EntityCursor<D6Bench> benches = db.daoBenches.byId.entities( txn, null );
            ) {
                for ( D6Bench bench: benches ) {
                    
                    // skip no bench
                    if ( bench.getId() == D6Bench.NO_BENCH ) {
                        continue;
                    }
                    
                    moveSimplifiedTechnicalLotsToBusinessLot( db, txn, iPass, iPassTechLot, bench.getIdLot() );
                            
                }
            }
            
        }   // end if isBusinessComponentLot()
        */
        
    }
    
    /**
     * Move components or kits technical lots to component ro kit business lot
     * @param txn
     * @param iPass
     * @param parentIdLot
     * @throws D6LException 
     */
    protected void moveSimplifiedTechnicalLotsToBusinessLot( 
    ) throws D6LException {
        
    	throw new Error( "TODO" );
    	/*
        // Get sub-lot type
        final D6LPackageSubtypeEnum lotSubType = getLotSubType();

        if ( lotSubType == null ) {
        	// Nothing to do
        	return;
        }
        
        // create or get business component lot
        D6Lot outerSimplifiedLot = null;
        
        D6Lot benchLot = db.daoLots.byId.get( txn, idLotOfCurrentBench, null );

        // get parent lot until we get a Business Lot
        D6Lot targetBusinessLot = benchLot;
        
        do {
            // move to parent
            targetBusinessLot = db.daoLots.byId.get( txn, targetBusinessLot.getIdLotParent(), null );
            
        }
        while ( 
            ( targetBusinessLot != null ) && 
            ( targetBusinessLot.getId() != D6Lot.ID_NO_LOT ) && 
            ( targetBusinessLot.getLotType() != D6LPackageTypeEnum.BUSINESS_LOT ) 
        );
        
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
        
        // to be created?
        if ( outerSimplifiedLot == null ) {
            
        	outerSimplifiedLot = createOuterSimplifiedLot(db, txn, iPass, lotSubType, targetBusinessLot);
            
        }
        
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
			final D6LPackageSubtypeEnum lotSubType, D6LPackageVertex targetBusinessLot) throws D6LException {
    	throw new Error( "TODO" );
    	/*
		D6Lot outerSimplifiedLot;
		outerSimplifiedLot = new D6Lot( D6LPackageTypeEnum.BUSINESS_LOT, null, iPass );
		outerSimplifiedLot.setName( lotSubType.getLotName() );
		outerSimplifiedLot.setLotSubtype( lotSubType );
		outerSimplifiedLot.setIdLotParent( targetBusinessLot.getId() );
		
		outerSimplifiedLot.save( db, txn );
		return outerSimplifiedLot;
		*/
	}

	public boolean isSpecificBusinessLot() {
		return specificBusinessLot;
	}

	public boolean isSingleExtractorLot() {
		return singleExtractorLot;
	}

	/**
	 * If a component is extracted, stats for used objects must be updated
	 *
	 */
    public class ReworkChildrenJob /* implements X6JobIF<D6EntityIF> */ {

         /** Algo command **/
        private final D6LAlgoCommandIF algoCommand;
        
        /** Kit to operate on **/
        private final D6LEntityIF kit;
        
        /** Single lot **/
        private final D6LPackageVertex singleLot;
        
        public ReworkChildrenJob( D6LAlgoCommandIF algoCommand, D6LEntityIF kit, D6LPackageVertex singleLot ) {
            super();
            this.algoCommand = algoCommand;
            this.kit = kit;
            this.singleLot = singleLot;
        }

        /*
        @Override
        public void doJob( D6EntityIF notUsed )
            throws Exception
        {
            
            D6EntityLinkAccessorIF<? extends D6LinkIF> daoEntityLinks = algoCommand.getDaoEntityLinks();
            
            try {
                
                // Get kit children
                try (
                    EntityCursor<? extends D6LinkIF> kitLinks = daoEntityLinks.getByIdRoleA().subIndex( kit.getId() ).entities( txn, null );
                ) {
                    for ( D6LinkIF kitLink : kitLinks ) {
                        
                        // Get to entity
                        D6EntityIF child = db.daoMetaEntities.byIdGet( txn, iPass, iPassTechLot, kitLink.getIdRoleB(), null );
                        
                        // Get stats
                        D6EntityDirectedLinkStats childStats = db.daoEntityStats.byIdEntity.get( child.getId() );
                        
                        if ( childStats != null ) {
                        	
	                        // rework child stats
	                        // Remove kit
	                        switch ( kitLink.getLinkDirection() ) {
	                            
	                            case DirectedFromTo : {
	                                childStats.incNbDirectedLinksToForBench( -1 );
	                                childStats.incNbLinksToForBench( -1 );
	                                break;
	                            }
	                            case DirectedToFrom : {
	                                childStats.incNbDirectedLinksFromForBench( -1 );
	                                childStats.incNbLinksFromForBench( -1 );
	                                break;
	                            }
	                            case DirectedBoth : {
	                                childStats.incNbLinksFromForBench( -1 );
	                                // No break: intentional
	                            }
	                            case NotDirected : {
	                                childStats.incNbLinksToForBench( -1 );
	                                break;
	                            }
	                        }
	                        
	                        // Save 
	                        childStats.save( db, txn );
	                        
	                        // Is it now a single entity?
	                        // if no links, entity is single
	                        // But not for bom simplifier lots
	                        D6LPackageSubtypeEnum requiredParentLotSubType = null;
	                        if ( child instanceof D6AbstractLot ) {
	                        	requiredParentLotSubType = ( ( D6AbstractLot ) child ).getRequiredParentLotSubType();
	                        }
	                        
	                        if ( 
	                        	( singleLot != null ) && 
	                        	( childStats.getNbLinksFromForBench() == 0 ) && ( childStats.getNbLinksToForBench() == 0 ) 
	                        	&&
	                        	// Not a bom simplifier lot
	                        	( requiredParentLotSubType == null )
	                        ) {
	
	                            child.setIdLot( singleLot.getId() );
	                            // save using queue
	                            child.save( db, txn );
	                            
	                        }
	                        
                        }
                        
                    }
                }
                
            } catch ( Throwable t ) {
                
                D6SystemizerData.LOGGER.fatal( "Can't process kit '" + kit.toString() + "'" );
                throw t;
                
            }
            
        }
        */
    }

    /**
     * Create histogram entry
     * @param pass
     * @param entity
     * @param nbDirectedLinksFromEntity
     * @param nbDirectedLinksToEntity
     */
	public void createAndSaveHistogramEntry( D6LEntityIF entity, long nbDirectedLinksFromEntity, long nbDirectedLinksToEntity ) {
		// Do nothing
		
	}

}
