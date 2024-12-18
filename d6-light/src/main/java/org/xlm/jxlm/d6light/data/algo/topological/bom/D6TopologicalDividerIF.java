

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

package org.xlm.jxlm.d6light.data.algo.topological.bom;

import java.util.List;

import org.xlm.jxlm.d6light.data.algo.D6LDividerAlgoIF;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier;

/**
 * Technical Divider: split data in technical lots
 * @author Francois Loison
 *
 */
public interface D6TopologicalDividerIF extends D6LDividerAlgoIF {

	/**
	 * True if technical lotizer needs a bench with local IDs (entities ID numerotation local to bench)<br/>
	 * False if technical lotizer can user global entity IDs (entities ID numerotation is global), faster.
	 * @return
	 */
	public boolean isNeedLocalIdsInBench();

	/**
	 * Return true is technical lotizer needs a single objects lotizing
	 * @return
	 */
	public boolean isAllowsSinglesAllocation();

    /**
     * Return true is technical lotizer needs a BOM simplification
     * @return
     */
    public boolean isNeedBomSimplification();
    

	/**
	 * Return bom simplifiers, mapped by kind
	 * @return
	 */
	public List<D6LAbstractBomSimplifier> getListBomSimplifiers();

    /**
     * Return true is technical lotizer needs bom simplified entities removed from bench
     * @return
     */
    public boolean isNeedBomSimplifiedEntitiesRemovedFromBench();
 
}
