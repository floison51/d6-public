package org.xlm.jxlm.d6light.data.model;

import org.hibernate.Session;
import org.jgrapht.Graph;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;

@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class D6LPackageVertex extends D6LAbstractPackageEntity {

	/** Unallocated package **/
	public static final D6LPackageVertex UNALLOCATED = 
		new D6LPackageVertex( D6LPackageTypeEnum.TECHNICAL_PKG, "Unallocated" );

	// Create a root target package
	public static final D6LPackageVertex ROOT_BENCH_PACKAGE = 
		new D6LPackageVertex( D6LPackageTypeEnum.BUSINESS_PKG, "Root package" );

	public static void initDb( D6LDb db, Session session ) {
		
		// Create persisted objects
		session.persist( UNALLOCATED );
		session.persist( ROOT_BENCH_PACKAGE );
		
		db.outGraph.addVertex( UNALLOCATED );
		db.outGraph.addVertex( ROOT_BENCH_PACKAGE );
		
		session.flush();
		
	}

	@OneToOne
	private D6LVertex primaryVertex = null;
	
	public D6LPackageVertex() {
		super();
	}

	public D6LPackageVertex( int id, D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		super( id, type, displayType );
	}

	public D6LPackageVertex( D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		super( type, displayType );
	}

	public D6LPackageVertex( D6LPackageTypeEnum type ) {
		super( type );
	}

	public D6LPackageVertex( D6LPackageTypeEnum type, String name ) {
		super( type, null, name );
	}

	@Override
	public D6LEntityKindEnum getKind() {
		return D6LEntityKindEnum.vertex;
	}

	public void setPrimaryTarget( D6LVertex primaryVertex ) {
		this.primaryVertex  = primaryVertex;
	}

	@Override
	public void delete(Session session) {
		
		// Delete from outGraph
		Graph<D6LPackageVertex, D6LPackageEdge> outGraph = D6LDb.getInstance().outGraph;
		outGraph.removeVertex( this );
		
		// Remove from session
		super.delete(session);
		
	}
	
}
