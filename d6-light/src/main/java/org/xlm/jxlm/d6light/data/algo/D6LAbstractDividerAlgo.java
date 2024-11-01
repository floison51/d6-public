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

import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.AbstractDividerType;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;


/**
 * Abstract divider algo
 * @author Loison
 *
 */
public abstract class D6LAbstractDividerAlgo extends D6LAbstractAlgo implements D6LDividerAlgoIF {

    /**
     * Type of lot produced by this algo, 2 values accepted: BusinessLot or TechnicalLot
     */
    protected D6LPackageTypeEnum producesLotType = null;

    /**
     * Constructor
     */
	public D6LAbstractDividerAlgo() {
	}

	@Override
	/**
	 * Handle 'producesLotType' value. Children overriding this method must call super().
	 */
	public void setConf( D6LightDataConf conf, AbstractAlgoType algoConf ) throws D6LException {

		// Ancestor
		super.setConf( conf, algoConf );
		
		// produce value
		if ( algoConf instanceof AbstractDividerType ) {
			
			AbstractDividerType dividerConf = (AbstractDividerType) algoConf;
			if ( dividerConf.getProduces() != null ) {
				switch ( dividerConf.getProduces() ) {
					case BUSINESS: {
						producesLotType = D6LPackageTypeEnum.BUSINESS_PKG;
						break;
					}
					case TECHNICAL: {
						producesLotType = D6LPackageTypeEnum.TECHNICAL_PKG;
						break;
					}
					default: {
						throw new D6LError( "Internal error" );
					}
				}
			
			}
		}
		
		if ( producesLotType == null ) {
			// back to default value
			producesLotType = getDefaultProducesLotType();
		}
		
		// check
		switch ( producesLotType ) {
			case BUSINESS_PKG:
			case TECHNICAL_PKG: {
				// ok
				break;
			}
			default: {
				throw new D6LException( "lot type '" + producesLotType.name() + "' is not a valid lot type for 'produces' attribute" );
			}
		}
	}

}
