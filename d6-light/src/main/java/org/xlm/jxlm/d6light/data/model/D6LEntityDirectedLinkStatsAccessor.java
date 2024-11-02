

package org.xlm.jxlm.d6light.data.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;
import org.xlm.jxlm.d6light.data.measures.D6LEntityDirectedLinkStats;

/**
 * Accessor for Berkeley DB persistent EntityDirectedLinkStats
 * @author Loison
 *
 */
public class D6LEntityDirectedLinkStatsAccessor
{

	public D6LEntityDirectedLinkStats getByEntity( Session session, D6LVertex vertex ) {
		
		D6LEntityDirectedLinkStats result = 
			session.byNaturalId( D6LEntityDirectedLinkStats.class ).load();
		
		return result;
	}

    /**
     * Get Bom heads.<p/>
     * Bom Heads have more than 2 links directed to them.
     * @param txn Transaction
     * @param config configuration
     * @param idBench idBench
     * @return
     */
    public Set<D6LVertex> getBomHeads( Session session ) {
    	
    	// Bom Heads have more than 2 links directed to them
    	SelectionQuery<D6LEntityDirectedLinkStats> query = session
    		.createSelectionQuery( 
    			"from D6LEntityDirectedLinkStats where nbDirectedLinks=0", 
    			D6LEntityDirectedLinkStats.class 
    		);
    	
    	List<D6LEntityDirectedLinkStats> stats = query.getResultList();
    	
    	Set<D6LVertex> result = new HashSet<>();
        
    	for ( D6LEntityDirectedLinkStats stat : stats ) {
    		
    		result.add( stat.getVertex() );
    	}
    	
    	return Collections.unmodifiableSet( result );
    }

    /**
     * Get top object-entity given a bench
     * @param txn Transaction
     * @param config configuration
     * @param idBench idBench
     * @return
     */
    /*
    public ForwardCursor<Long> getTopObjectEntityForBench( Transaction txn, CursorConfig config, long idBench ) {
        // Top object entities have no "directed links to".
        
        ForwardCursor<Long> cursor;
        
        EntityJoin<Long,D6LEntityDirectedLinkStats> join = new EntityJoin<>( byId );
        
        // bench condition
        join.addCondition( byIdBench, idBench );
        
        // add nb links to = 0
        join.addCondition( byNbDirectedLinksToForBench, 0l );
        
        // get cursor
        cursor = join.keys( txn, config );
        
        return cursor;
    }
    */
    /**
     * Get entity stats given bench ID anf entity ID 
     * @param txn
     * @param config
     * @param idBench
     * @return
     */
    /*
    public D6LEntityDirectedLinkStats getByEntityIdAndBenchId( Transaction txn, CursorConfig config, long idBench, long idEntity ) {
        
        EntityJoin<Long,D6LEntityDirectedLinkStats> join = new EntityJoin<>( byId );
        
        // bench condition
        join.addCondition( byIdBench, idBench );
        
        // entity condition
        join.addCondition( byIdEntity, idEntity );
        
        // get cursor
        try (
             ForwardCursor<D6LEntityDirectedLinkStats> cursor = join.entities( txn, config );
        ) {
            // return null or only value
            return cursor.next();
        }
        
    }
    */
}
