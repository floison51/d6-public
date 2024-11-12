package org.xlm.jxlm.d6light.data;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.GraphImporter;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.imp.D6LImporterWrapper;
import org.xlm.jxlm.d6light.data.model.D6LEdge;
import org.xlm.jxlm.d6light.data.model.D6LPackage;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

public class D6LPackageRegistry {
	
	private Logger LOGGER = D6LAbstractDataTestCase.LOGGER;
	
    private Map<Integer,D6LPackage> indexPackages = new HashMap<>();
    private Map<Integer,D6LEdge>    indexPackageLinks = new HashMap<>();
    
	private Graph<D6LPackage, D6LEdge> gPackages = new SimpleGraph<>( D6LEdge.class );
	
	public D6LPackageRegistry( File graphFile, D6LGraphFormatEnum gFormat ) throws Exception {
		
		// Import graph
		D6LImporterWrapper<D6LPackage, D6LEdge> importWrapper = 
			new D6LImporterWrapper<>(
				id -> new D6LPackage( id, D6LPackageTypeEnum.TECHNICAL_PKG )
			);
		
		GraphImporter<D6LPackage, D6LEdge> importer = importWrapper.getGraphImporterInstance( gFormat ); 
			
		importer.importGraph( gPackages, graphFile );
		// Index packages
		indexPackagesAndLinks( gPackages );
		
	}
	
	
	private void indexPackagesAndLinks( Graph<D6LPackage, D6LEdge> gPackages ) throws D6LException {
		
        // Index packages
        for ( D6LPackage pkg : gPackages.vertexSet() ) {
        	
        	// vertex
        	D6LPackage lotDuplicate = indexPackages.put( pkg.getId(), pkg );
            if ( lotDuplicate != null ) {
                throw new D6LException( "Duplicated package id " + pkg.getId() );
            }
        	
        }

        // Index edges
        for ( D6LEdge edge : gPackages.edgeSet() ) {
        	
    		// Link
        	D6LEdge lotLinkDuplicate = indexPackageLinks.put( edge.getId(), edge );
            if ( lotLinkDuplicate != null ) {
                throw new D6LException( "Duplicated package link id " + lotLinkDuplicate.getId() );
            }
        }

        // Freeze
        indexPackages     = Collections.unmodifiableMap( indexPackages );  
        indexPackageLinks = Collections.unmodifiableMap( indexPackageLinks );
        
	}

	public void checkLotIsomorphisms( D6LPackageRegistry otherLotRegistry ) throws D6LException {
		
		LOGGER.info( "Check Package isomorphism" );
		
		boolean iso = checkLotIsomorphism( this.gPackages, otherLotRegistry.gPackages );
		
		if ( !iso ) {
			throw new D6LException( "Not isomorph" );
		}
			
	}

	private boolean checkLotIsomorphism(
			Graph<D6LPackage, D6LEdge> g1, Graph<D6LPackage, D6LEdge> g2 
	) {
		
		VF2GraphIsomorphismInspector<D6LPackage, D6LEdge> isoInspector = 
			new VF2GraphIsomorphismInspector<>( g1, g2 );
		
		return isoInspector.isomorphismExists();
		
	}

}
