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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.gml.GmlImporter;
import org.xlm.jxlm.d6light.data.D6LGraphFormatEnum;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LLinkDirectionEnum;
import org.xlm.jxlm.d6light.data.model.graph.D6LGraphEdgeIF;
import org.xlm.jxlm.d6light.data.model.graph.D6LGraphEntityIF;

/**
 * Graph file importer
 */
public class D6LImporterWrapper<V extends D6LGraphEntityIF, E extends D6LGraphEdgeIF> {
	
	private Function<Integer, V> vertexFactory;
	
	private static final Map<Pair<String,String>,D6LLinkDirectionEnum> MAP_GMF_EDGE_DIRECTION;
	
	static {
		
		Map<Pair<String,String>,D6LLinkDirectionEnum> map = new HashMap<>();
		
		map.put( Pair.of( "standard", "standard" ), D6LLinkDirectionEnum.DirectedBoth );
		map.put( Pair.of( ""        , "standard" ), D6LLinkDirectionEnum.DirectedFromTo );
		map.put( Pair.of( "xx"      , "xx" ), D6LLinkDirectionEnum.NotDirected );
		
		MAP_GMF_EDGE_DIRECTION = Collections.unmodifiableMap( map );
		
	}

	public D6LImporterWrapper( Function<Integer, V> vertexFactory ) {
		super();
		this.vertexFactory = vertexFactory;
	}

	public GraphImporter<V,E> getGraphImporterInstance( 
		D6LGraphFormatEnum format
	) throws D6LException {

		GraphImporter<V,E> result = null;
		
		switch ( format ) {

			case GML: {
				
				GmlImporter<V,E> gmlImp = new GmlImporter<>();
				result = gmlImp;
				
				// Set vertex factory
				gmlImp.setVertexFactory( vertexFactory );
				
				// Set label consumer
				gmlImp.addVertexAttributeConsumer( 

					( pair_V_attrName, attribute ) -> {
						
						// Get vertex
						V v = pair_V_attrName.getFirst();

						if ( "label".equals( pair_V_attrName.getSecond() ) ) {
							
							// Get label 
							String label = attribute.getValue();
							// Set to vertex
							v.setLabel( label );
							
						}
						
					}
				);
				
				// Set edge consumer for label and direction
				gmlImp.addEdgeAttributeConsumer(
					( pair_E_attrName, attribute ) -> {
						
						// Get edge
						E edge = pair_E_attrName.getFirst();
						
						// Direction
						if ( "graphics".equals( pair_E_attrName.getSecond() ) ) {
							
							// Get graphics
							// [ fill "#000000" targetArrow "standard" Line [ point [ x 282.24640066964287 y 15.0 ] ...
							String graphics = attribute.getValue();
							
							// Search for source arrow
							String sourceArrowValue = getGmfValue( graphics, "sourceArrow" );
							// Search for target arrow
							String targetArrowValue = getGmfValue( graphics, "targetArrow" );
							
							// Normalize
							if ( sourceArrowValue == null ) {
								sourceArrowValue = "";
							}
							
							if ( targetArrowValue == null ) {
								targetArrowValue = "";
							}
							
							// Get link direction
							D6LLinkDirectionEnum direction = MAP_GMF_EDGE_DIRECTION.get( Pair.of( sourceArrowValue, targetArrowValue ) );
							
							// Set to edge
							edge.setLinkDirection( direction );
							
						}
						
						if ( "label".equals( pair_E_attrName.getSecond() ) ) {
							
							// Get label 
							String label = attribute.getValue();
							// Set to edge
							edge.setLabel( label );
							
						}

						
					}
				);


				break;
			}
	
			default: {
				throw new D6LException(MessageFormat.format( "Unknown import format ''{0}''.", format.name() ) );
			}
		}

		return result;

	}

	private String getGmfValue( String graphics, String token ) {
		
		String result = null;
		
		int iToken = graphics.indexOf( token );
		
		// Found?
		if ( iToken > 0 ) {
		
			// Get rid of start
			String graphics1 = graphics.substring( iToken + token.length() + 1 ).trim();
			// "standard" Line [ point [ 
			int iDelim = graphics1.indexOf( ' ' );
			if ( iDelim > 0 ) {
				String quotedValue = graphics1.substring( 0, iDelim );
				result = StringUtils.unwrap(quotedValue, "\"" ).trim();
			}
			
		}
		
		return result;
		
	}

}
