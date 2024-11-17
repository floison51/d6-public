package org.xlm.jxlm.d6light.data.model.graph;

import org.xlm.jxlm.d6light.data.model.D6LLinkDirectionEnum;

public interface D6LGraphEdgeIF extends D6LGraphEntityIF {

	D6LLinkDirectionEnum getLinkDirection();

	void setLinkDirection( D6LLinkDirectionEnum linkDirection );

}
