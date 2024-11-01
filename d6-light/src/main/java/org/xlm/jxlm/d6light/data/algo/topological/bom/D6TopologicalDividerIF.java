

package org.xlm.jxlm.d6light.data.algo.topological.bom;

import java.util.List;

import org.xlm.jxlm.audit.d6.data.algo.D6DividerAlgoIF;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier;

/**
 * Technical Divider: split data in technical lots
 * @author Francois Loison
 *
 */
public interface D6TopologicalDividerIF extends D6DividerAlgoIF {

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
