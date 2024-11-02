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

package org.xlm.jxlm.d6light.data.algo;

import org.hibernate.Session;
import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

/**
 * Interface for Data Systemizer algorithms
 * @author Loison
 *
 */
public interface D6LAlgoIF {

	/**
	 * Algo name
	 * @return
	 */
	public String getName();
	
    /**
     * Set D6 global configuration
     * @param conf
     * @param algoConf
     * @throws D6LException
     */
	public void setConf( 
		D6LightDataConf conf, AbstractAlgoType algoConf
	) throws D6LException;

	/**
	 * Get algo conf
	 * @return algo conf
	 */
	public AbstractAlgoType getConf();

    /**
     * Check algo is OK for execution
     * @throws D6LException If not ok for execution
     */
    public void checkOkForExecute() throws D6LException;

	/**
	 * Data preparation - algo specific
	 * @param txn
	 * @param algoCommand
	 * @return algo conf
	 * @throws D6LException
	 */
	public void doPrepare( Session session, D6LAlgoCommandIF algoCommand ) throws D6LException;
	
	/**
	 * Run algo
     * @param algoCommand
	 * @throws D6LException
	 */
	public void doRun( Session session, D6LAlgoCommandIF algoCommand ) throws D6LException;
	
    /**
     * Get lot type produced with algo : Business or Technical or null if no lots are produced by algo
     * @return
     */
    public D6LPackageTypeEnum getProducesLotType();


}
