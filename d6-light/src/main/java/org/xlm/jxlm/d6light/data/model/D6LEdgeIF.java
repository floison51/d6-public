package org.xlm.jxlm.d6light.data.model;

public interface D6LEdgeIF extends D6LSaveableIF, D6LEntityIF {

	D6LLinkDirectionEnum getLinkDirection();

	void setLinkDirection( D6LLinkDirectionEnum linkDirection );

}
