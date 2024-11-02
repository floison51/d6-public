

package org.xlm.jxlm.d6light.data.measures;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.measures.D6LHistogramEntry.HistoKeyEnum;

/**
 * Accessor for EntityDirectedLinkStats
 * @author Loison
 *
 */
public class D6LHistogramAccessor
{

	private static final AtomicInteger seqId = new AtomicInteger();
	
    /* Master accessors */
	Map<Integer,D6LHistogramEntry> mapById;
    
    /**
     * Constructor, opens all primary and secondary indices.
     * @param store Store
     */
    public D6LHistogramAccessor()
    {

    	mapById = new HashMap<>();
    	
    }

    public D6LHistogramEntry newHistogramEntry( HistoKeyEnum histoKey, long value ) {
    	
    	D6LHistogramEntry he = new D6LHistogramEntry( seqId.getAndIncrement(), histoKey, value );
    	register( he );
    	return he;
    	
    }
	private void register( D6LHistogramEntry he ) {
		
		if ( mapById.put( he.getId(), he ) != null ) {
			throw new D6LError( MessageFormat.format( "Duplicate id ${0}", he.getId() ) );
		}
		
	}

	public D6LHistogramEntry getById( int id ) {
		return mapById.get( id );
	}
    
}
