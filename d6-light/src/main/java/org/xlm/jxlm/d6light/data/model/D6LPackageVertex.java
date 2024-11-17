package org.xlm.jxlm.d6light.data.model;

import org.hibernate.SessionFactory;
import org.jgrapht.Graph;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

import jakarta.persistence.Entity;

@Entity
public class D6LPackageVertex extends D6LAbstractPackageEntity {

	public static final D6LPackageVertex UNALLOCATED = new D6LPackageVertex( D6LPackageTypeEnum.TECHNICAL_PKG );

	// Create a root target package
	public static final D6LPackageVertex ROOT_BENCH_PACKAGE = 
		new D6LPackageVertex( D6LPackageTypeEnum.BUSINESS_PKG );

	public static void initDb( SessionFactory sessionFactory, Graph<D6LPackageVertex, D6LPackageEdge> outGraph ) {
		
		// Create persisted objects
		sessionFactory.inTransaction( 
			session -> {
				session.persist( UNALLOCATED );
				session.persist( ROOT_BENCH_PACKAGE );
			});
		
	}
	
	public D6LPackageVertex() {
		super();
	}

	public D6LPackageVertex( D6LPackageTypeEnum type, D6LPackageSubtypeEnum displayType ) {
		super( type, displayType );
	}

	public D6LPackageVertex( D6LPackageTypeEnum type ) {
		super( type );
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

}
