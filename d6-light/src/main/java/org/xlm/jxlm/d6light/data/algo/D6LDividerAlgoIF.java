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

import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

/**
 * Interface for divider algos
 * @author Loison
 *
 */
public interface D6LDividerAlgoIF extends D6LAlgoIF {

	/**
	 * Default value for produce lot type
	 * Actual algo implementation must return their default value. 2 values accepted: BusinessLot or TechnicalLot.
	 * @return
	 */
	public D6LPackageTypeEnum getDefaultProducesLotType();
	
}
