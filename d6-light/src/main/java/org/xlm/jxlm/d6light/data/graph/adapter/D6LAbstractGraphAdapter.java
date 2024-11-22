package org.xlm.jxlm.d6light.data.graph.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.hibernate.Session;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.model.graph.D6LGraphEdgeIF;
import org.xlm.jxlm.d6light.data.model.graph.D6LGraphEntityIF;

/**
 * A base abstract implementation for the graph adapter class using Guava's {@link Graph}. This is a
 * helper class in order to support both mutable and immutable graphs.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <G> type of the underlying Guava's graph
 */
public abstract class D6LAbstractGraphAdapter<V extends D6LGraphEntityIF, E extends D6LGraphEdgeIF>
    extends AbstractGraph<V,E>
    implements Graph<V,E>, Cloneable, Serializable
{
    private static final long serialVersionUID = -6742507788742087708L;

    protected static final String LOOPS_NOT_ALLOWED = "loops not allowed";

    protected Supplier<V> vertexSupplier;
    protected Supplier<E> edgeSupplier;
    
    protected Session session;
    
    protected Class<V> vertexClass;
    protected Class<E> edgeClass;

    private static final AtomicInteger SEQ_EDGE_KEY = new AtomicInteger( 0 );
    
    /** Graph of keys **/
    protected final Graph<Integer,Integer> gKeys;
    
    /**
     * Create a new adapter.
     * 
     * @param graph the graph
     */
    public D6LAbstractGraphAdapter( Class<V> vertexClass, Class<E> edgeClass, boolean isDirected )
    {
        this( vertexClass, edgeClass, isDirected, null, null );
    }

    /**
     * Create a new adapter.
     * 
     * @param graph the graph
     * @param vertexSupplier the vertex supplier
     * @param edgeSupplier the edge supplier
     * @param vertexOrderMethod the method used to ensure a total order of the graph vertices. This
     *        is required in order to make edge source/targets be consistent.
     */
    public D6LAbstractGraphAdapter(
    	Class<V> vertexClass, Class<E> edgeClass, boolean isDirected,
        Supplier<V> vertexSupplier, Supplier<E> edgeSupplier
    )
    {
        this.vertexClass = vertexClass;
        this.edgeClass = edgeClass;
    	this.vertexSupplier = vertexSupplier;
        this.edgeSupplier = edgeSupplier;
        
        // init underlying graph
        if ( isDirected ) {
        	
        	 gKeys = new SimpleDirectedGraph<>( 
	    		null, 
	    		() -> SEQ_EDGE_KEY.getAndIncrement(), 
	    		false
	    	);
        		    
        } else {
        	
       	 	gKeys = new SimpleGraph<>( 
	    		null, 
	    		() -> SEQ_EDGE_KEY.getAndIncrement(), 
	    		false
	    	);
       		    
        }
        
    }

    @Override
    public Supplier<V> getVertexSupplier()
    {
        return vertexSupplier;
    }

    /**
     * Set the vertex supplier that the graph uses whenever it needs to create new vertices.
     * 
     * <p>
     * A graph uses the vertex supplier to create new vertex objects whenever a user calls method
     * {@link Graph#addVertex()}. Users can also create the vertex in user code and then use method
     * {@link Graph#addVertex(Object)} to add the vertex.
     * 
     * <p>
     * In contrast with the {@link Supplier} interface, the vertex supplier has the additional
     * requirement that a new and distinct result is returned every time it is invoked. More
     * specifically for a new vertex to be added in a graph <code>v</code> must <i>not</i> be equal
     * to any other vertex in the graph. More formally, the graph must not contain any vertex
     * <code>v2</code> such that <code>v2.equals(v)</code>.
     * 
     * @param vertexSupplier the vertex supplier
     */
    public void setVertexSupplier(Supplier<V> vertexSupplier)
    {
        this.vertexSupplier = vertexSupplier;
    }

    @Override
    public Supplier<E> getEdgeSupplier()
    {
        return edgeSupplier;
    }

    /**
     * Set the edge supplier that the graph uses whenever it needs to create new edges.
     * 
     * <p>
     * A graph uses the edge supplier to create new edge objects whenever a user calls method
     * {@link Graph#addEdge(Object, Object)}. Users can also create the edge in user code and then
     * use method {@link Graph#addEdge(Object, Object, Object)} to add the edge.
     * 
     * <p>
     * In contrast with the {@link Supplier} interface, the edge supplier has the additional
     * requirement that a new and distinct result is returned every time it is invoked. More
     * specifically for a new edge to be added in a graph <code>e</code> must <i>not</i> be equal to
     * any other edge in the graph (even if the graph allows edge-multiplicity). More formally, the
     * graph must not contain any edge <code>e2</code> such that <code>e2.equals(e)</code>.
     * 
     * @param edgeSupplier the edge supplier
     */
    public void setEdgeSupplier(Supplier<E> edgeSupplier)
    {
        this.edgeSupplier = edgeSupplier;
    }

    @Override
    public E getEdge( V sourceVertex, V targetVertex )
    {
        if (sourceVertex == null || targetVertex == null 
        	|| !gKeys.vertexSet().contains( sourceVertex.getId() )
            || !gKeys.vertexSet().contains( targetVertex.getId() ) 
           )
        {
            return null;
        } else {
        	
        	// We expect one edge
        	Integer edgeKey = gKeys.getEdge( sourceVertex.getId(), targetVertex.getId() );
        	
        	if ( edgeKey == null ) {
        		return null;
        	}
        	
        	E edge = session.get( edgeClass, edgeKey );
        	
        	return edge;

        }
    }

    protected Set<E> getFromSession( Set<Integer> keys ) {
    	
    	List<Integer> listKeys = new ArrayList<>( keys );
    	
    	List<E> listEdges = session.byMultipleIds( edgeClass ).multiLoad( listKeys );
    	
    	// Check not null
    	for ( E e : listEdges ) {
    		if ( e == null ) {
    			throw new D6LError( "Null edge" );
    		}
    	}

    	return Collections.unmodifiableSet( new HashSet<>( listEdges ) );
    	
    }
    
    @Override
    public Set<E> getAllEdges( V sourceVertex, V targetVertex )
    {
        if (
        	sourceVertex == null || targetVertex == null || 
        	!gKeys.vertexSet().contains( sourceVertex.getId() ) ||
            !gKeys.vertexSet().contains( targetVertex.getId() )
           )
        {
            return null;
        } else {

        	Set<Integer> allEdgeKeys = gKeys.getAllEdges( sourceVertex.getId(), targetVertex.getId() );
        	return getFromSession( allEdgeKeys );
        	
        }
    }

    @Override
    public Set<V> vertexSet()
    {

    	List<Integer> listAllVerticesKey = new ArrayList<>( gKeys.vertexSet() );
    	List<V> listAllVertices = session.byMultipleIds( vertexClass ).multiLoad( listAllVerticesKey );
    	
    	return Collections.unmodifiableSet( new HashSet<>( listAllVertices ) );
    	
    }

    @Override
    public Set<E> edgeSet()
    {

    	return getFromSession( gKeys.edgeSet() );
    	
    }

 	@Override
    public V getEdgeSource( E e )
    {
    	
    	Integer vKey = gKeys.getEdgeSource( e.getId() );
    	V v = session.get( vertexClass, vKey );
    	
        return v;
    }

 	@Override
    public V getEdgeTarget( E e)
    {
    	Integer vKey = gKeys.getEdgeTarget( e.getId() );
    	V v = session.get( vertexClass, vKey );
    	
        return v;
    }

    @Override
    public GraphType getType()
    {
    	return gKeys.getType();
    }

    @Override
    public boolean containsEdge( E e )
    {
        return gKeys.containsEdge( e.getId() );
    }

    @Override
    public boolean containsVertex(V v)
    {
        return gKeys.containsVertex( v.getId() );
    }

    @Override
    public int degreeOf(V v)
    {
        return gKeys.degreeOf( v.getId() );
    }

    @Override
    public Set<E> edgesOf( V v )
    {
    	
    	Set<Integer> edgesOfKey = gKeys.edgesOf( v.getId() );
    	return getFromSession( edgesOfKey );
    	
    }

    @Override
    public int inDegreeOf( V vertex )
    {
        return gKeys.inDegreeOf( vertex.getId() );
    }

    @Override
    public Set<E> incomingEdgesOf( V v)
    {
    	Set<Integer> edgeKeys = gKeys.incomingEdgesOf( v.getId() );
    	return getFromSession( edgeKeys );
    }

    @Override
    public int outDegreeOf( V v )
    {
    	 return gKeys.outDegreeOf( v.getId() );
    }

    @Override
    public Set<E> outgoingEdgesOf( V v )
    {
    	Set<Integer> edgeKeys = gKeys.outgoingEdgesOf( v.getId() );
    	return getFromSession( edgeKeys );
    }

    @Override
    public double getEdgeWeight( E e )
    {
    	return gKeys.getEdgeWeight( e.getId() );
    }

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
