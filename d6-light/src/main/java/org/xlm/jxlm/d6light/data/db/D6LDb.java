package org.xlm.jxlm.d6light.data.db;

import org.xlm.jxlm.d6light.data.measures.D6LEntityDirectedLinkStatsAccessor;
import org.xlm.jxlm.d6light.data.measures.D6LHistogramAccessor;
import org.xlm.jxlm.d6light.data.model.D6LEntityRegistry;

public class D6LDb {

	public final D6LEntityRegistry daoEntityRegistry = 
			new D6LEntityRegistry();

	public final D6LEntityDirectedLinkStatsAccessor daoEntityStats = 
			new D6LEntityDirectedLinkStatsAccessor();

	public final D6LHistogramAccessor daoHistogram = 
			new D6LHistogramAccessor();

	private static D6LDb me = null;
	
	public static synchronized D6LDb getInstance() {
		
		if ( me == null ) {
			me = new D6LDb();
		}
		
		return me;
		
	}

	private D6LDb() {
		super();
	}
	
}
