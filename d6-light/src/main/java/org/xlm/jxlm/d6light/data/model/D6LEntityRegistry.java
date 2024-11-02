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

package org.xlm.jxlm.d6light.data.model;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

public class D6LEntityRegistry {
	
	private final D6LDb db;
	
	public D6LEntityRegistry( D6LDb db ) {
		super();
		this.db = db;
	}

	public D6LPackage getOrCreateSingleLot( Session session, D6LPackageTypeEnum packageType ) throws D6LException {
    
		D6LPackage singleLot = getSingleLot( session );
        
        // exists?
        if ( singleLot == null ) {
            
            singleLot = createSingleLot( 
            	session,
            	packageType, D6LPackage.TECH_NAME_SINGLE
            );
            
        }
        
        return singleLot;
 	}

	protected D6LPackage createSingleLot( 
		Session session, 
		D6LPackageTypeEnum lotType, String lotName 
	 ) throws D6LException {
	  
        // create a single lot for current bench
        // sub-type to identify component lot later on
		D6LPackage singleLot = new D6LPackage( lotType, D6LPackageSubtypeEnum.SINGLE_LOT ); 
		
        // Persist
        session.persist( singleLot );
        
        // add to graph
        db.outGraph.addVertex( singleLot );
        
        return singleLot;
 
    }
	
    /**
	 * Get single lot
	 * @param txn
	 * @param cursorConfig
	 * @param idBench
	 * @param iPass
	 * @return
	 * @throws X6Exception
	 */
	public D6LPackage getSingleLot( Session session ) throws D6LException {
		
		// get single lots for current bench
		
		D6LPackage singleLot = null;
		
		SelectionQuery<D6LPackage> query = session
			.createSelectionQuery( "from D6LPackage where packageSubtype=?1", D6LPackage.class )
			.setParameter( 1, D6LPackageSubtypeEnum.SINGLE_LOT );
		
		List<D6LPackage> singlePackages = query.getResultList();

		if ( singlePackages.size() > 1 ) {
			throw new D6LException( "Found several Single packages" );
		}

		if ( singlePackages.size() == 1 ) {
			// Found
			singleLot = singlePackages.get( 0 );
		}

		return singleLot;
		
	}

	public Stream<D6LVertex> getVertices( Session session, D6LPackage packkage ) {
		
		Query<D6LVertex> query = session
			.createQuery( "from D6Vertex where packkage =%0", D6LVertex.class )
			.setParameter( 0, packkage );
		
		return query.getResultStream();
		
	}

}
