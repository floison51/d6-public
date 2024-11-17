package org.xlm.jxlm.d6light.data.model;

import org.hibernate.Session;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class D6LPackageVertex extends D6LAbstractPackageEntity {

	public static final D6LPackageVertex UNALLOCATED = new D6LPackageVertex( D6LPackageTypeEnum.TECHNICAL_PKG, "Unallocated" );

	// Create a root target package
	public static final D6LPackageVertex ROOT_BENCH_PACKAGE = 
		new D6LPackageVertex( D6LPackageTypeEnum.BUSINESS_PKG );

	public static void initDb( D6LDb db, Session session ) {
		
		// Create persisted objects
		session.persist( UNALLOCATED );
		session.persist( ROOT_BENCH_PACKAGE );
		
		db.outGraph.addVertex( UNALLOCATED );
		db.outGraph.addVertex( ROOT_BENCH_PACKAGE );
		
	}

	@OneToOne
	private D6LVertex primaryVertex = null;
	
	public D6LPackageVertex() {
		super();
	}

	public D6LPackageVertex( D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		super( type, displayType );
	}

	public D6LPackageVertex( D6LPackageTypeEnum type ) {
		super( type );
	}

	public D6LPackageVertex( D6LPackageTypeEnum type, String label ) {
		super( type );
		this.label = label;
	}

	public D6LPackageVertex( int id, D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		super( id, type, displayType );
	}

	public D6LPackageVertex( int id, D6LPackageTypeEnum type ) {
		super( id, type );
	}

	@Override
	public D6LEntityKindEnum getKind() {
		return D6LEntityKindEnum.vertex;
	}

	public void setPrimaryTarget( D6LVertex primaryVertex ) {
		this.primaryVertex  = primaryVertex;
	}

}
