

package org.xlm.jxlm.d6light.data.bom;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.xlm.jxlm.d6light.data.D6LAbstractDataTestCase;
import org.xlm.jxlm.d6light.data.D6LGraphFormatEnum;
import org.xlm.jxlm.d6light.data.D6LMain;

/**
 * Test D6L : Part BOM components factorization with reuse trigger = 2 
 * @author Loison
 *
 */
public class TestD6LDataBom_3 extends D6LAbstractDataTestCase {

	public TestD6LDataBom_3() throws Exception {
		super();
	}

	static final public String TEST_CASE = "t03";

	static final public File CONF_FILE = new File( "src/conf/D6LConf-Bom.xml" );
	
	static final public File SOURCE = new File( ".", "src/test/data/bom/" + TEST_CASE );
	static final public File SOURCE_G = new File( SOURCE, "Objects.gml" );
	
	static final public File WORK = new File( ".", "target/test-bom/" + TEST_CASE );
	static final public File WORK_G = new File( WORK, "graph/packages.gml" );
	
	/**
	 * Test Technical Bom Divider
	 * @throws Exception
	 */
	@Test
	public void testBom() throws Exception {
		
		List<String> opts = Arrays.asList(
			"-" + D6LMain.OPTION_CONF, CONF_FILE.getAbsolutePath(),
			"-" + D6LMain.OPTION_ID_ALGO, "technicalDivider-Bom-2",
			
			"-" + D6LMain.OPTION_GRAPH_FORMAT, D6LGraphFormatEnum.GML.name(),
			
			"-" + D6LMain.OPTION_GRAPH_IN , SOURCE_G.getAbsolutePath(),
			"-" + D6LMain.OPTION_GRAPH_OUT, WORK_G.getAbsolutePath()
		);
		
				
		D6LMain.main( opts.toArray( new String[] {} ) ) ;

		// check graphes
		checkGraphIsomorphism( "org/xlm/jxlm/d6light/data/bom/" + TEST_CASE );

	}
	
	@Override
	protected File getSourceFolder() {
		return SOURCE;
	}
	
	@Override
	protected File getWorkFolder() {
		return WORK;
	}
	
}
