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
import org.xlm.jxlm.d6light.data.conf.BomSimplifierType;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;
import org.xlm.jxlm.d6light.data.model.D6LPackage;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;

public class D6LComponentsBomSimplifier extends D6LAbstractBomSimplifier
{

    protected final int linksTrigger;
    
    public D6LComponentsBomSimplifier( 
        BomSimplifierType conf
    )
    {
        super(conf );
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
		D6LAlgoCommandIF algoCommand,
		D6LEntityIF entity, D6LEntityDirectedLinkStats stat
	)
		throws D6LException 
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
    	D6LAlgoCommandIF algoCommand, 
    	D6LEntityIF entity, boolean matchWithoutNumbersResult, 
    	D6LPackage singlePackage /*, List<X6JobIF<D6LEntityIF>> postActions*/ 
    ) 
    	throws D6LException
    {
            
        throw new Error( "TODO" );
        /*
        boolean matches = 
           matchWithoutNumbersResult && 
           ( stat.getNbDirectedLinksToForBench() >= linksTrigger );
        
        return new MatchResult( matches, null );
        */
    }

	@Override
	public void createAndSaveHistogramEntry( D6LEntityIF entity, long nbDirectedLinksFromEntity,
		long nbDirectedLinksToEntity
	) {
		
        throw new Error( "TODO" );
        /*
		// Log component linksTo 
		D6HistogramEntry he = new D6HistogramEntry( HistoKeyEnum.nbDirectedLinksToCompo, pass, nbDirectedLinksToEntity );
		// Save it
		he.save( db, txn );
		*/
	}

}
