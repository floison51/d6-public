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

	public D6LAbstractPackageEntity getOrCreateSingleLot( Session session, D6LPackageTypeEnum packageType ) throws D6LException {
    
		D6LAbstractPackageEntity singleLot = getSingleLot( session );
        
        // exists?
        if ( singleLot == null ) {
            
            singleLot = createSingleLot( 
            	session,
            	packageType, D6LAbstractPackageEntity.TECH_NAME_SINGLE
            );
            
        }
        
        return singleLot;
 	}

	protected D6LAbstractPackageEntity createSingleLot( 
		Session session, 
		D6LPackageTypeEnum lotType, String lotName 
	 ) throws D6LException {
	  
        // create a single lot for current bench
        // sub-type to identify component lot later on
		D6LPackageVertex singleLot = new D6LPackageVertex( lotType, D6LPackageSubtypeEnum.SINGLE_LOT ); 
		
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
	public D6LAbstractPackageEntity getSingleLot( Session session ) throws D6LException {
		
		// get single lots for current bench
		
		D6LAbstractPackageEntity singleLot = null;
		
		SelectionQuery<D6LAbstractPackageEntity> query = 
			session
				.createSelectionQuery( "from D6LPackageVertex where packageSubtype=?1", D6LAbstractPackageEntity.class )
				.setParameter( 1, D6LPackageSubtypeEnum.SINGLE_LOT );
		
		List<D6LAbstractPackageEntity> singlePackages = query.getResultList();

		if ( singlePackages.size() > 1 ) {
			throw new D6LException( "Found several Single packages" );
		}

		if ( singlePackages.size() == 1 ) {
			// Found
			singleLot = singlePackages.get( 0 );
		}

		return singleLot;
		
	}

	protected Stream<? extends D6LEntityIF> queryByClassAndPackage( Session session, Class<? extends D6LEntityIF> clazz, D6LPackageEntityIF pkgEntity ) {
		
		// from D6LVertice
		StringBuilder sbQuery = new StringBuilder( "from " + clazz.getSimpleName() );
		
		if ( pkgEntity != null ) {
			// from D6LVertice where packageEntity=?1
			sbQuery.append( " where packageEntity=?1" );
		}
		
		Query<? extends D6LEntityIF> query = session.createQuery( sbQuery.toString(), clazz );
		
		if ( pkgEntity != null ) {
			query.setParameter( 1, pkgEntity );
		}		
		
		return query.getResultStream();
	}
	
	@SuppressWarnings("unchecked")
	public Stream<D6LVertex> getVertices( Session session ) {
		return (Stream<D6LVertex>) queryByClassAndPackage( session, D6LVertex.class, null );
	}

	@SuppressWarnings("unchecked")
	public Stream<D6LVertex> getVertices( Session session, D6LPackageEntityIF pkgEntity ) {
		return (Stream<D6LVertex>) queryByClassAndPackage( session, D6LVertex.class, pkgEntity );
	}

	@SuppressWarnings("unchecked")
	public Stream<D6LEdge> getEdges( Session session ) {
		return (Stream<D6LEdge>) queryByClassAndPackage( session, D6LEdge.class, null );
	}

	@SuppressWarnings("unchecked")
	public Stream<D6LEdge> getEdges( Session session, D6LPackageEntityIF pkgEntity ) {
		return (Stream<D6LEdge>) queryByClassAndPackage( session, D6LEdge.class, pkgEntity );
	}

	@SuppressWarnings("unchecked")
	public Stream<D6LAbstractPackageEntity> getPackages( Session session ) {
		return (Stream<D6LAbstractPackageEntity>) queryByClassAndPackage( session, D6LAbstractPackageEntity.class, null );
	}

	@SuppressWarnings("unchecked")
	public Stream<D6LAbstractPackageEntity> getPackageEdges( Session session ) {
		return (Stream<D6LAbstractPackageEntity>) queryByClassAndPackage( session, D6LAbstractPackageEntity.class, null );
	}

}
