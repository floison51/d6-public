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

import org.hibernate.Session;
import org.xlm.jxlm.d6light.data.algo.D6LAlgoCommandIF;
import org.xlm.jxlm.d6light.data.conf.BomSimplifierType;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.job.D6LJobIF;
import org.xlm.jxlm.d6light.data.measures.D6LEntityDirectedLinkStats;
import org.xlm.jxlm.d6light.data.measures.D6LHistogramEntry;
import org.xlm.jxlm.d6light.data.measures.D6LHistogramEntry.HistoKeyEnum;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;
import org.xlm.jxlm.d6light.data.model.D6LPackage;
import org.xlm.jxlm.d6light.data.model.D6LVertex;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;

public class D6LKitsBomSimplifier extends D6LAbstractBomSimplifier
{

    protected final int linksTrigger;
    
    public D6LKitsBomSimplifier( 
        BomSimplifierType conf
    )
    {
        super( conf );
        this.linksTrigger = conf.getLinksTrigger();
    }

    @Override
    public BomSimplifierKindEnum getKind()
    {
        return BomSimplifierKindEnum.Kits;
    }
    
    @Override
    public D6LPackageSubtypeEnum getLotSubType()
    {
        return D6LPackageSubtypeEnum.KIT_LOT;
    }
    
	@Override
	public boolean matchWithoutNumbers(
		Session session,
		D6LAlgoCommandIF algoCommand,
		D6LEntityIF entity, D6LEntityDirectedLinkStats stat
	)
		throws D6LException 
	{
		// Necessary conditions for being a kit : all links are directed and no directed links to entity
        boolean matches = 
        	// no directed links to entity
        	( stat.getNbDirectedLinksToForBench() == 0 ) &&
        	// At least 2 directed links from entity
        	( stat.getNbDirectedLinksFromForBench() >= 2 ) &&
        	// No non-directed links from entity
        	( stat.getNbDirectedLinksFromForBench() == stat.getNbLinksFromForBench() ) &&
        	// No non-directed links to entity
        	( stat.getNbDirectedLinksToForBench() == stat.getNbLinksToForBench() )
        ;
        return matches;
	}
    
    @Override
    public MatchResult match( 
    	Session session,
    	D6LAlgoCommandIF algoCommand, 
    	D6LVertex entity, boolean matchWithoutNumbersResult, D6LEntityDirectedLinkStats stat, 
    	D6LPackage singlePackage, List<D6LJobIF<D6LEntityIF>> postActions
    ) 
    	throws D6LException
    {
        
        boolean match = 
           matchWithoutNumbersResult && 
           ( stat.getNbDirectedLinksFromForBench() >= linksTrigger );
        
        if ( match ) {
            
            // children need stats to be reworked: they have one linkFrom less and they may become single
            // Create post job
            
            ReworkChildrenJob job = new ReworkChildrenJob( session, algoCommand, entity, singlePackage );
            postActions.add( job );
            
        }
        
        return new MatchResult( match, null );
        
    }
    
	@Override
	public void createAndSaveHistogramEntry( 
		Session session, 
		D6LEntityIF entity, 
		long nbDirectedLinksFromEntity,
		long nbDirectedLinksToEntity
	) {
		
		// Log component linksTo 
		D6LHistogramEntry histo = 
			new D6LHistogramEntry( 
				HistoKeyEnum.nbDirectedLinksFromKit, 
				nbDirectedLinksFromEntity 
			);
		
		// Persist
		session.persist( histo );
		
	}

}
