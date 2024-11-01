

package org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier;

import java.util.List;

import org.xlm.jxlm.audit.d6.data.algo.D6AlgoCommandIF;
import org.xlm.jxlm.audit.d6.data.algo.topological.bom.D6EntityDirectedLinkStats;
import org.xlm.jxlm.audit.d6.data.bench.D6Bench;
import org.xlm.jxlm.audit.d6.data.db.D6SystemizerDataDb;
import org.xlm.jxlm.audit.d6.data.lot.D6Lot;
import org.xlm.jxlm.audit.d6.data.measures.D6HistogramEntry;
import org.xlm.jxlm.audit.d6.data.measures.D6HistogramEntry.HistoKeyEnum;
import org.xlm.jxlm.audit.d6.data.meta.D6EntityIF;

import com.sleepycat.je.Transaction;

import org.xlm.jxlm.audit.x6.common.X6Exception;
import org.xlm.jxlm.audit.x6.common.data.conf.BomSimplifierType;
import org.xlm.jxlm.audit.x6.common.data.lot.D6LotSubtypeEnum;
import org.xlm.jxlm.audit.x6.common.thread.X6JobIF;

public class D6ComponentsBomSimplifier extends D6LAbstractBomSimplifier
{

    protected final int linksTrigger;
    
    public D6ComponentsBomSimplifier( 
        D6SystemizerDataDb db,
        BomSimplifierType conf
    )
    {
        super( db, conf );
        this.linksTrigger = conf.getLinksTrigger();
    }

    @Override
    public BomSimplifierKindEnum getKind()
    {
        return BomSimplifierKindEnum.Components;
    }

    @Override
    public D6LPackageSubtypeEnum getLotSubType()
    {
        return D6LPackageSubtypeEnum.COMPONENT_LOT;
    }
    
	@Override
	public boolean matchWithoutNumbers(
		Transaction txn, D6AlgoCommandIF algoCommand,
    	D6Bench bench,
		D6EntityIF entity, D6EntityDirectedLinkStats stat
	)
		throws X6Exception 
	{
		// Necessary conditions for being a component: all links are directed and no directed links to entity
        boolean matches = 
        	// no directed links to entity
        	( stat.getNbDirectedLinksFromForBench() == 0 ) &&
        	// At least 2 directed links to entity
        	( stat.getNbDirectedLinksToForBench() >= 2 ) &&
        	// No non-directed links from entity
        	( stat.getNbDirectedLinksFromForBench() == stat.getNbLinksFromForBench() ) &&
        	// No non-directed links to entity
        	( stat.getNbDirectedLinksToForBench() == stat.getNbLinksToForBench() )
        ;
        return matches;
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
        
        boolean matches = 
           matchWithoutNumbersResult && 
           ( stat.getNbDirectedLinksToForBench() >= linksTrigger );
        
        return new MatchResult( matches, null );
        
    }

	@Override
	public void createAndSaveHistogramEntry( Transaction txn, int pass, D6EntityIF entity, long nbDirectedLinksFromEntity,
			long nbDirectedLinksToEntity) {
		
		// Log component linksTo 
		D6HistogramEntry he = new D6HistogramEntry( HistoKeyEnum.nbDirectedLinksToCompo, pass, nbDirectedLinksToEntity );
		// Save it
		he.save( db, txn );
		
	}

}
