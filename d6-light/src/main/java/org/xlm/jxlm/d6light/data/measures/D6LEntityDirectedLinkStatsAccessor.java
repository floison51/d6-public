

package org.xlm.jxlm.d6light.data.measures;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Accessor for Berkeley DB persistent EntityDirectedLinkStats
 * @author Loison
 *
 */
public class D6LEntityDirectedLinkStatsAccessor
{

    /* Master accessors */
	Map<Integer,D6LEntityDirectedLinkStats> mapByEntityId;
    
    /**
     * Constructor, opens all primary and secondary indices.
     * @param store Store
     */
    public D6LEntityDirectedLinkStatsAccessor()
    {

    	mapByEntityId = new HashMap<>();
    	
    }

	public D6LEntityDirectedLinkStats getByEntityId( int entityId ) {
		return mapByEntityId.get( entityId );
	}

	public Iterator<Entry<Integer,D6LEntityDirectedLinkStats>> getIterator() {
		return mapByEntityId.entrySet().iterator();
	}
    
    /**
     * Get Bom heads.<p/>
     * Bom Heads have more than 2 links directed to them.
     * @param txn Transaction
     * @param config configuration
     * @param idBench idBench
     * @return
     */
    public Set<Integer> getBomHeads() {
    	// Bom Heads have more than 2 links directed to them
    	
    	Set<Integer> result = new HashSet<>();
        
    	for ( Entry<Integer,D6LEntityDirectedLinkStats> entry : mapByEntityId.entrySet() ) {
    		
    		if ( entry.getValue().getNbDirectedLinksFromForBench() == 0 ) {
    			result.add( entry.getKey() );
    		}
    	}
    	
    	return Collections.unmodifiableSet( result );
    }

	public D6LEntityDirectedLinkStats newStats( int idEntity ) {
		D6LEntityDirectedLinkStats stats = new D6LEntityDirectedLinkStats( idEntity );
		mapByEntityId.put( idEntity, stats );
		return stats;
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
