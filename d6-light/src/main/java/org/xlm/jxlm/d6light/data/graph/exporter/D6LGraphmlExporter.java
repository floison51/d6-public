package org.xlm.jxlm.d6light.data.graph.exporter;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.transform.sax.TransformerHandler;

import org.jgrapht.Graph;
import org.jgrapht.event.TraversalListenerAdapter;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.model.D6LEdgeIF;
import org.xlm.jxlm.d6light.data.model.D6LEntityIF;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class D6LGraphmlExporter<
	V  extends D6LEntityIF, E  extends D6LEdgeIF, 
	PV extends D6LEntityIF, PE extends D6LEdgeIF
> 
	extends GraphMLAbstractExporterHeritable<V, E> 
{
	
	protected Graph<PV, PE> gPackages = null;

    protected void writeNodes( TransformerHandler handler, Graph<V, E> g )
        throws SAXException
    {
    	if ( hasPackages() ) {
    		writeNodesWithPackages( handler, g );
    	} else {
       		// No packages, call ancestor
    		super.writeNodes( handler, g ) ;
    	}
    	
    }

    @Override
    protected void writeGraphStart( TransformerHandler handler, Graph<V, E> g )
        throws SAXException
    {
		if ( !hasPackages() ) {
			// No packages, call ancestor
			super.writeGraphStart( handler, g );
		}
    }
            
    protected void writeGraphStartRaw( TransformerHandler handler, Graph<V, E> g )
        throws SAXException
    {
		// No packages, call ancestor
		super.writeGraphStart( handler, g ) ;
    }
        
    protected void writeNodesRaw( TransformerHandler handler, Graph<V, E> g )
        throws SAXException
    {
		// No packages, call ancestor
		super.writeNodes( handler, g ) ;
    }
    
    protected void writeNodesWithPackages( TransformerHandler handler, Graph<V, E> g )
        throws SAXException
    {
    	
    	// Start graph node
        // <graph>
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute(
            "", "", "edgedefault", "CDATA", gPackages.getType().isDirected() ? "directed" : "undirected");
        handler.startElement("", "", "graph", attr);
    	
    	// Iterate packages DFS wise
    	DepthFirstIterator<PV,PE> itPackages =
    		new DepthFirstIterator<>( gPackages );
    	
    	final D6LGraphmlExporter<V,E,PV,PE> me = this;
    	
    	// Set visitors
    	itPackages.addTraversalListener(
    		new TraversalListenerAdapter<PV,PE>() {
    			
    			@Override
    			public void vertexTraversed( VertexTraversalEvent<PV> event ) {
    				
    				PV pkg = event.getVertex();
    				
    				// Get unique ID
    				String idPkg = "pkg." + pkg.getId();
    				
    				// Vertices belonging to package
    				Set<V> ourVertices = 
    					g.vertexSet()
    						.stream()
    						.filter( v -> v.getPackageEntity().getId() == pkg.getId() )
    						.collect( Collectors.toSet() );
    				
	            	// Get sub graph of vertices belonging to package
	            	final AsSubgraph<V, E> subGraph = 
	            		new AsSubgraph<>( 
	            			g,
	            			ourVertices,			// Vertices belonging to current package
	            			Collections.emptySet()	// No edges: we export nodes
	            		);

    	            try {
						
	    				// Write package node start
	    	            // <node>
	    	            AttributesImpl attr = new AttributesImpl();
	    	            attr.addAttribute( "", "", "id", "CDATA", idPkg );
	    	            
    	            	handler.startElement("", "", "node", attr );

    	            	// Start graph of vertices belonging to package
		            	me.writeGraphStartRaw( handler, g );
		            	
    	            	// Export nodes belonging to package
    	            	me.writeNodesRaw( handler, subGraph );
    	            	
    	            	// end graph
    	            	handler.endElement( "", "", "graph" );	    	            	
    	            	
					} catch ( SAXException e ) {
						throw new D6LError( e );
					}
    				
    			}

    			@Override
    			public void vertexFinished( VertexTraversalEvent<PV> event ) {
    				
    				// Close package node
    	            try {
    	            	handler.endElement( "", "", "node" );
					} catch ( SAXException e ) {
						throw new D6LError( e );
					}
   				
    			}
    		}
    	);
    	
    	// Traverse ant let listers do the magic
    	while ( itPackages.hasNext() ) {
    		itPackages.next();
    	}
	    	
    }
    
	protected boolean hasPackages() {
		return ( gPackages != null ) && !gPackages.vertexSet().isEmpty();
	}
    
	public Graph<PV,PE> getPackageGraph() {
		return gPackages;
	}

	public void setPackageGraph(Graph<PV,PE> gPackages) {
		this.gPackages = gPackages;
	}
	
	

}
