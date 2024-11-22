package org.xlm.jxlm.d6light.data.model.graph;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

import org.xlm.jxlm.d6light.data.exception.D6LError;

public class D6LGraphAdapter<V extends D6LGraphEntityIF, E extends D6LGraphEdgeIF> 
	extends D6LAbstractGraphAdapter<V, E> {

	private static final long serialVersionUID = 3950068783745866172L;

	/**
     * Create a new adapter.
     * 
     * @param graph the graph
     */
    public D6LGraphAdapter( Class<V> vertexClass, Class<E> edgeClass, boolean isDirected )
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
    public D6LGraphAdapter(
    	Class<V> vertexClass, Class<E> edgeClass, boolean isDirected,
        Supplier<V> vertexSupplier, Supplier<E> edgeSupplier
    )
    {
        super( vertexClass, edgeClass, isDirected, vertexSupplier, edgeSupplier );
    }
    
    /**
     * Create an edge.
     * 
     * @param s the source vertex
     * @param t the target vertex
     * @return the edge
     */
    final E createEdge(V s, V t)
    {
        throw new D6LError( "Not supported" );
    }

	@Override
	public boolean addEdge( V sourceVertex, V targetVertex, E e ) {
		
		// Persist it to get edge id
		e.create( session );
		
		// From graph key
		boolean result = gKeys.addEdge( sourceVertex.getId(), targetVertex.getId(), e.getId() );
		
		return result;
		
	}

	@Override
	public V addVertex() {
        throw new D6LError( "Not supported" );
	}

	@Override
	public boolean addVertex( V v ) {

		// From graph key
		boolean result = gKeys.addVertex( v.getId() );
		
		// Persist it
		v.create( session );
		
		return result;
	}

	private Constructor<E> cEdge = null;
	
	@Override
	public E addEdge( V sourceVertex, V targetVertex ) {

		// From graph key
		Integer edgeKey = gKeys.addEdge( sourceVertex.getId(), targetVertex.getId() );

		if ( edgeKey == null ) {
			return null;
		}
		
		try {
			// Create edge
			if ( cEdge == null ) {
				cEdge = edgeClass.getDeclaredConstructor( int.class );
			}
		
			E e = cEdge.newInstance( edgeKey );
			
			// Persist it
			e.create( session );
			
			return e;
			
		} catch ( Exception e ) {
			D6LError.handleThrowable( e );
			
		}
		
		// Should not be here
		return null;
		
	}

	@Override
	public E removeEdge( V sourceVertex, V targetVertex ) {

		// From graph key
		int eKey = gKeys.removeEdge( sourceVertex.getId(), targetVertex.getId() );
		
		return session.get( edgeClass, eKey );
		
	}

	@Override
	public boolean removeEdge( E e ) {

		// From graph key
		boolean result = gKeys.removeEdge( e.getId() );
		
		return result;
	}

	@Override
	public boolean removeVertex( V v ) {

		// From graph key
		boolean result = gKeys.removeVertex( v.getId() );
		
		return result;
	}

	@Override
	public void setEdgeWeight( E e, double weight ) {

		// From key graph
		gKeys.setEdgeWeight( e.getId(), weight );
		
	}

}
