

package org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier;

import java.util.List;

import org.xlm.jxlm.audit.d6.data.algo.D6AlgoCommandIF;
import org.xlm.jxlm.audit.d6.data.algo.topological.bom.D6EntityDirectedLinkStats;
import org.xlm.jxlm.audit.d6.data.bench.D6Bench;
import org.xlm.jxlm.audit.d6.data.conf.D6RegExpParamHelper;
import org.xlm.jxlm.audit.d6.data.db.D6SystemizerDataDb;
import org.xlm.jxlm.audit.d6.data.lot.D6AbstractLot;
import org.xlm.jxlm.audit.d6.data.lot.D6Lot;
import org.xlm.jxlm.audit.d6.data.meta.D6EntityIF;
import org.xlm.jxlm.audit.d6.data.meta.D6EntityInfoIF;

import com.sleepycat.je.Transaction;

import org.xlm.jxlm.audit.x6.common.X6Exception;
import org.xlm.jxlm.audit.x6.common.data.conf.LotExtractorType;
import org.xlm.jxlm.audit.x6.common.data.lot.D6LotSubtypeEnum;
import org.xlm.jxlm.audit.x6.common.thread.X6JobIF;

public class D6LotExtractorBomSimplifier extends D6LAbstractBomSimplifier
{

	protected final LotExtractorType conf;
	
    public D6LotExtractorBomSimplifier( 
        D6SystemizerDataDb db,
        LotExtractorType conf 
    ) {
        super( db, conf );
        this.conf = conf;
    }

    @Override
    public BomSimplifierKindEnum getKind()
    {
        return BomSimplifierKindEnum.LotExtractor;
    }
    
    @Override
    public D6LPackageSubtypeEnum getLotSubType()
    {
        return D6LPackageSubtypeEnum.EXTRACTED_LOT;
    }
    
	@Override
	public boolean matchWithoutNumbers(
		Transaction txn, D6AlgoCommandIF algoCommand,
    	D6Bench bench,
		D6EntityIF entity, D6EntityDirectedLinkStats stat
	)
		throws X6Exception 
	{
		// No history for lot extractor
        return false;
	}
    
    @Override
    public MatchResult match( 
        Transaction txn, D6AlgoCommandIF algoCommand,
    	D6Bench bench,
        D6EntityIF entity, boolean matchWithoutNumbersResult, D6EntityDirectedLinkStats stat, 
        D6Lot singleLot, List<X6JobIF<D6EntityIF>> postActions 
    ) 
    	throws X6Exception
    {
        
    	// We support only lots
    	if ( !( entity instanceof D6Lot) ) {
    		// Nothing to do
    		return new MatchResult( false, null );
    		
    	}
    	D6Lot lot = ( D6Lot ) entity;
    	
    	// Get BOM head
    	String bomHeadFctId = getBomHeadFctId( txn, lot, iPassTechLot );
     	
    	if ( bomHeadFctId == null ) {
    		// Nothing to check
    		return new MatchResult( false, null );
    	}
    	
    	// BOM head matches?
		boolean match = D6RegExpParamHelper.regExpParamMatch( conf.getBomHeadRegexp(), bomHeadFctId ); 

		if ( match ) {
	        // children need stats to be reworked: they have one linkFrom less and they may become single
	        // Create post job
	        
	        ReworkChildrenJob job = new ReworkChildrenJob( db, algoCommand, iPass, iPassTechLot, txn, entity, singleLot );
	        postActions.add( job );
		}
		
        return new MatchResult( match, bomHeadFctId );

    }

    private String getBomHeadFctId( Transaction txn, D6AbstractLot lot, int iPassTechLot ) throws X6Exception {
    	
    	String bomHeadUid = lot.getPrimaryTargetEntityUid();
    	
    	if ( bomHeadUid == null ) {
    		return null;
    	}
    	
    	// As object
    	D6EntityIF bomHeadEntity = db.daoMetaEntities.getByUniversalId( txn, bomHeadUid, null );
    	D6EntityInfoIF bomHeadEntityInfo = 
    		db.daoMetaEntities.byIdGetInfo( iPassTechLot, iPassTechLot, bomHeadEntity.getId() );
    	
    	String bomHeadFctId = bomHeadEntityInfo.getFctId();
    	
    	return bomHeadFctId;
    	
    }
    
    @Override
	public void tuneLot( Transaction txn, D6Lot bomSimplifierLot, Object tuningInfo ) throws X6Exception {
	
    	// Set bom head fct id as fctId
    	String bomHeadFctId = null;
    	
    	if ( tuningInfo instanceof String ) {
    		bomHeadFctId =  (String) tuningInfo;	
    	} else {
    		D6AbstractLot simplifiedLot = ( D6AbstractLot ) tuningInfo;
    		bomHeadFctId = simplifiedLot.getFctId();
    	}
    	
    	bomSimplifierLot.setFctId( bomHeadFctId );
    	
    	// Save lot
    	bomSimplifierLot.save( db, txn );
    	
	}

    
}
