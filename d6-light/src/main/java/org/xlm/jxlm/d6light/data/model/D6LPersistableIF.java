package org.xlm.jxlm.d6light.data.model;

import org.hibernate.Session;

public interface D6LPersistableIF {

	void create( Session session );

	void save ( Session session );

	void delete( Session session );

}
