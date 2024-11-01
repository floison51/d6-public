/**
 *  Public Data Systemizer, see https://doi.org/10.1016/j.compind.2023.104053
 *  Copyright (C) 2025 Francois LOISON
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see https://www.gnu.org/licenses/gpl-3.0.html
**/

package org.xlm.jxlm.d6light.data.model;

import org.jgrapht.graph.DefaultEdge;

public class D6LEdge extends DefaultEdge implements D6LEntityIF {

	/** Serial ID **/
	private static final long serialVersionUID = -4376861182806887574L;
	
	private String label;
	private int idPackage;

	@Override
	public String toString() {
		return "D6OuterEdge [getSource()=" + getSource() + ", getTarget()=" + getTarget() + "]";
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public int getIdPackage() {
		return idPackage;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setIdPackage(int idPackage) {
		this.idPackage = idPackage;
	}
	
	/*
	public D6OuterEdge( int id ) {
		super();
		this.innerEdge = new D6InnerEdge( id );
	}
	
	public class D6InnerEdge extends D6AbstractEntity {

		public D6InnerEdge( int id ) {
			super( id );
		}
		
	}
	
	private D6InnerEdge innerEdge;

	@Override
	public int getId() {
		return innerEdge.getId();
	}

	@Override
	public String getLabel() {
		return innerEdge.getLabel();
	}
	
	public void setId(int id) {
		this.innerEdge.setId( id );
	}

	public void setLabel(String label) {
		this.innerEdge.setLabel( label );
	}
	*/
	
	
}
