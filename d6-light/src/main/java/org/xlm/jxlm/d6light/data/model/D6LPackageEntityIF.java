package org.xlm.jxlm.d6light.data.model;

import org.xlm.jxlm.d6light.data.packkage.D6LPackageSubtypeEnum;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

public interface D6LPackageEntityIF extends D6LEntityIF {

	D6LEntityKindEnum getKind();
	
	D6LPackageTypeEnum getPackageType();
	void setPackageType( D6LPackageTypeEnum packageType);
	
	D6LPackageSubtypeEnum getPackageSubtype();
	void setPackageSubtype( D6LPackageSubtypeEnum subType );
	
}
