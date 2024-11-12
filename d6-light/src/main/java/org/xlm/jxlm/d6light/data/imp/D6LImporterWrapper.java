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

package org.xlm.jxlm.d6light.data.imp;

import java.text.MessageFormat;
import java.util.function.Function;

import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.gml.GmlImporter;
import org.xlm.jxlm.d6light.data.D6LGraphFormatEnum;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;

/**
 * Graph file importer
 */
public class D6LImporterWrapper<V extends D6LEntityIF, E> {
	
	private Function<Integer, V> vertexFactory;

	public D6LImporterWrapper( Function<Integer, V> vertexFactory ) {
		super();
		this.vertexFactory = vertexFactory;
	}

	public GraphImporter<V,E> getGraphImporterInstance( 
		D6LGraphFormatEnum format
	) throws D6LException {

		GraphImporter<V,E> result = null;
		BaseEventDrivenImporter<V,E> intermediate = null;
		
		switch ( format ) {

			case GML: {
				
				GmlImporter<V,E> gmlImp = new GmlImporter<>();
				intermediate = gmlImp;
				result = gmlImp;
				
				// Set vertex factory
				gmlImp.setVertexFactory( vertexFactory );
				
				break;
			}
	
			default: {
				throw new D6LException(MessageFormat.format( "Unknown import format ''{0}''.", format.name() ) );
			}
		}

		// Set label consumer
		intermediate.addVertexAttributeConsumer( 
				( pair_V_attrName, attribute ) -> {
					
					if ( "label".equals( pair_V_attrName.getSecond() ) ) {
						
						// Get label 
						String label = attribute.getValue();
						// Set to vertex
						pair_V_attrName.getFirst().setLabel( label );
						
					}
					
				}
			);

		return result;

	}

}
