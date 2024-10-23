package org.xlm.jxlm.d6light.data.imp;

import java.text.MessageFormat;

import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.csv.CSVImporter;
import org.jgrapht.nio.gml.GmlImporter;
import org.xlm.jxlm.d6light.data.D6Exception;

/**
 * Graph file importer 
 */
public class D6Importer<V,E> {

	
	protected GraphImporter<V,E> getGraphImporterInstance( D6ImportFormatEnum format ) throws D6Exception {
		
		GraphImporter<V,E> result = null;
		
		switch ( format ) {
		
			case CSV : {
				result = new CSVImporter<V,E>();
				break;
			}
			
			case GML : {
				result = new GmlImporter<V,E>();
				break;
			}
			
			default : {
				throw new D6Exception( MessageFormat.format( "Unknown import format '{0}'.", format.name() ) );
			}
		}
		
		return result;
		
	}
	
}
