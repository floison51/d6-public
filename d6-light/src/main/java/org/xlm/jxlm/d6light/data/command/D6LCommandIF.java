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

package org.xlm.jxlm.d6light.data.command;

import org.xlm.jxlm.d6light.data.exception.D6LException;

/**
 * Command interface
 * @author Loison
 *
 */
public interface D6LCommandIF {

    /**
     * Comman display name
     * @return
     */
    public String getDisplayName();
    
	/**
	 * Execute command
	 * @param inGraph Input of command
	 * @param outGraph 
	 * @return
	 * @throws X6Exception 
	 * @throws D6NotAllocatedException 
	 */
	public void execute() throws D6LException;

	/**
	 * Get conf ID read from D6 configuration file, example : &lt;algoRef refId="bom-algo"/&gt;
	 * @return
	 */
    public String getConfId();
    
}
