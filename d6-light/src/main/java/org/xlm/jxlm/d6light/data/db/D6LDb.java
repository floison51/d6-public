package org.xlm.jxlm.d6light.data.db;

import static org.hibernate.cfg.JdbcSettings.FORMAT_SQL;
import static org.hibernate.cfg.JdbcSettings.HIGHLIGHT_SQL;
import static org.hibernate.cfg.JdbcSettings.PASS;
import static org.hibernate.cfg.JdbcSettings.SHOW_SQL;
import static org.hibernate.cfg.JdbcSettings.URL;
import static org.hibernate.cfg.JdbcSettings.USER;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jgrapht.Graph;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.measures.D6LEntityDirectedLinkStats;
import org.xlm.jxlm.d6light.data.measures.D6LHistogramEntry;
import org.xlm.jxlm.d6light.data.model.D6LAbstractEntity;
import org.xlm.jxlm.d6light.data.model.D6LAbstractPackageEntity;
import org.xlm.jxlm.d6light.data.model.D6LEdge;
import org.xlm.jxlm.d6light.data.model.D6LEntityDirectedLinkStatsAccessor;
import org.xlm.jxlm.d6light.data.model.D6LEntityRegistry;
import org.xlm.jxlm.d6light.data.model.D6LHistogramAccessor;
import org.xlm.jxlm.d6light.data.model.D6LPackageEdge;
import org.xlm.jxlm.d6light.data.model.D6LPackageVertex;
import org.xlm.jxlm.d6light.data.model.D6LVertex;


public class D6LDb {

	/** Graphs hold on IDs, data is held by persistent objects**/
	public Graph<D6LVertex, D6LEdge> inGraph;
	public Graph<D6LPackageVertex, D6LPackageEdge> outGraph;

	public final D6LEntityRegistry daoEntityRegistry = new D6LEntityRegistry( this );

	public final D6LEntityDirectedLinkStatsAccessor daoEntityStats = 
			new D6LEntityDirectedLinkStatsAccessor();

	public final D6LHistogramAccessor daoHistogram = 
			new D6LHistogramAccessor();

	private static D6LDb me = null;
	
	public static synchronized D6LDb getInstance() throws D6LError {
			
		if ( me == null ) {
			me = new D6LDb();
		}
		
		return me;
			
	}

	public void setGraphes(
		Graph<D6LVertex, D6LEdge> inGraph,
		Graph<D6LPackageVertex, D6LPackageEdge> outGraph
	) {
	
		this.inGraph = inGraph;
		this.outGraph = outGraph;
		
	}
	
	private D6LDb() {
		super();
	}
	
	private static SessionFactory sessionFactory;
	
	public static void initDb() {
		
        sessionFactory = new Configuration()
        		
                .addAnnotatedClass( D6LAbstractEntity.class )
                .addAnnotatedClass( D6LVertex.class )
                .addAnnotatedClass( D6LEdge.class )
                .addAnnotatedClass( D6LHistogramEntry.class )
                .addAnnotatedClass( D6LEntityDirectedLinkStats.class )

                .addAnnotatedClass( D6LAbstractPackageEntity.class )
                .addAnnotatedClass( D6LPackageVertex.class )
                .addAnnotatedClass( D6LPackageEdge.class )

                // use H2 in-memory database
                .setProperty(URL, "jdbc:h2:mem:db1")
                .setProperty(USER, "sa")
                .setProperty(PASS, "")
                
                // use Agroal connection pool
                .setProperty("hibernate.agroal.maxSize", 20)
                
                // display SQL in console
                .setProperty(SHOW_SQL, true)
                .setProperty(FORMAT_SQL, true)
                .setProperty(HIGHLIGHT_SQL, true)
                .buildSessionFactory();

        // export the inferred database schema
        sessionFactory.getSchemaManager().exportMappedObjects( true );
        
    }
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
