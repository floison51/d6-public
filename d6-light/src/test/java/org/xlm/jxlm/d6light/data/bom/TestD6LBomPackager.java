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

package org.xlm.jxlm.d6light.data.bom;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.xlm.jxlm.d6light.data.D6LGraphFormatEnum;
import org.xlm.jxlm.d6light.data.D6LMain;
import org.xlm.jxlm.d6light.data.exception.D6LException;


public class TestD6LBomPackager {

	@Test
	public void testBomPackager() throws D6LException {
		
		List<String> opts = Arrays.asList(
			"-" + D6LMain.OPTION_CONF, "src/conf/d6l-testBOM-import.xml",
			"-" + D6LMain.OPTION_ID_ALGO, "simple-BOM-facto",
			
			"-" + D6LMain.OPTION_GRAPH_FORMAT, D6LGraphFormatEnum.GML.name(),
			
			"-" + D6LMain.OPTION_GRAPH_IN , "src/test/resources/org/xlm/jxlm/d6light/data/bom/t01/testBom-t01.gml",
			"-" + D6LMain.OPTION_GRAPH_OUT, "target/test/bom/t01/testBom-t01.gml"
		);
		
				
		D6LMain.main( opts.toArray( new String[] {} ) ) ;
	}

}
