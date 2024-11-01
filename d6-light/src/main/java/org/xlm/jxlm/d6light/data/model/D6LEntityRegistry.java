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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

public class D6LEntityRegistry {

	private static Map<Integer,D6LEntityIF> registryIn = new HashMap<>();
	
	private static AtomicInteger seqVertex = new AtomicInteger();

	private static Map<Integer,D6LEntityIF> registryOut = new HashMap<>();
	
	private static AtomicInteger seqPackageVertex = new AtomicInteger();

	private static void registerVertex( D6LVertex v ) {

		int id = v.getId();
		
		if ( registryIn.containsKey( id ) ) {
			throw new D6LError( MessageFormat.format( "Duplicate id ${0}", id ) );
		}
		
		registryIn.put( id, v );
		
	}
	
	/**
	 * For import: id provided by importer
	 * @param id
	 * @return
	 * @throws D6LError
	 */
	public static D6LVertex newVertex( int id ) throws D6LError {
		
		D6LVertex vertex = new D6LVertex( id );
		registerVertex( vertex );
		
		return vertex;
		
	}
	
	private static void registerPackageVertex( D6LPackage pv ) {
		
		int id = pv.getId();
		
		if ( registryOut.containsKey( id ) ) {
			throw new D6LError( MessageFormat.format( "Duplicate package id ${0}", id ) );
		}
		
		registryOut.put( id,  pv );
	}
	
	/*
	public static D6LPackageVertex newPackageVertex() throws D6LError {
		
		D6LPackageVertex packageVertex = new D6LPackageVertex( seqPackageVertex.getAndIncrement() );
		registerPackageVertex( packageVertex );
		
		return packageVertex;
		
	}
	*/
	
	public static D6LPackage newPackageVertex( 
		D6LPackageTypeEnum type, D6LPackageSubtypeEnum subtype 
	) throws D6LError {
		
		D6LPackage packageVertex = 
			new D6LPackage( 
				seqPackageVertex.getAndIncrement(),
				type, subtype
			);
		registerPackageVertex( packageVertex );
		
		return packageVertex;
		
	}

	public static D6LPackage getOrCreateSingleLot( D6LPackageTypeEnum packageType ) throws D6LException {
    
		D6LPackage singleLot = getSingleLot();
        
        // exists?
        if ( singleLot == null ) {
            
            singleLot = createSingleLot( 
            	packageType, D6LPackage.TECH_NAME_SINGLE
            );
        }
        
        return singleLot;
 	}

	protected static D6LPackage createSingleLot( 
		D6LPackageTypeEnum lotType, String lotName 
	 ) throws D6LException {
	  
        // create a single lot for current bench
		D6LPackage singleLot = 
			new D6LPackage( 
				seqPackageVertex.getAndIncrement(), 
				lotType, 
				null 
				
			);
        // sub-type to identify component lot later on
        singleLot.setPackageSubtype( D6LPackageSubtypeEnum.SINGLE_LOT );
        // name
        singleLot.setName( lotName );
 
        // Register
		registerPackageVertex( singleLot );
       
        return singleLot;
 
    }

    /**
	 * Get single lot given a bench and a pass<p/>
	 * @param txn
	 * @param cursorConfig
	 * @param idBench
	 * @param iPass
	 * @return
	 * @throws X6Exception
	 */
	public static D6LPackage getSingleLot() throws D6LException {
		
		// get single lots for current bench
		
		D6LPackage singleLotForBench = null;
		
		for ( D6LEntityIF outEntity : registryOut.values() ) {
			
			if ( outEntity instanceof D6LPackage ) {
				
				D6LPackage pv = ( D6LPackage ) outEntity;
				
				if ( pv.getPackageSubtype() == D6LPackageSubtypeEnum.SINGLE_LOT ) {
					
					if ( singleLotForBench != null ) {
						throw new D6LException( "Found several Single packages" );
					}
					
					singleLotForBench = pv;
				}
			}
		}
		
		return singleLotForBench;
		
	}



}
