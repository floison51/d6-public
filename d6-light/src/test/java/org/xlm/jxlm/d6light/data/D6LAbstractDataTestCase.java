package org.xlm.jxlm.d6light.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class D6LAbstractDataTestCase {

    public static final Logger LOGGER = LogManager.getLogger( "TEST" );
    
	static final public String HOME = new File( "." ).getAbsolutePath();
	
	static final public File TEST_TEMP_FOLDER = new File( "target/test/temp" );

	static {
	    TEST_TEMP_FOLDER.mkdirs();
	}

	public D6LAbstractDataTestCase() throws Exception {
		
		super();
		
	}

	/**
	 * Get working folder
	 * @return
	 */
	protected File getWorkFolder() {
		return null;
	}

    /**
     * Get working folder
     * @return
     */
    protected File getDataOutFolder() {
        return new File( getWorkFolder(), "data/out" );
    }

	/**
	 * Get source folder
	 * @return
	 */
	protected File getSourceFolder() {
		return null;
	}

	protected void checkGraphIsomorphism( String strCheckerFolder ) throws Exception {
		
		// get full checker folder
		File checkerFolder = new File( HOME, "src/test/resources/" + strCheckerFolder );
		if ( !checkerFolder.exists() ) {
			throw new Exception( "Can't find checker folder " + checkerFolder.getAbsolutePath() );
		}
		
		File checherFile = new File( checkerFolder, "packages.gml" );
		
		// Lot registry - checker
		D6LPackageRegistry checkerPackageRegistry = new D6LPackageRegistry( checherFile, D6LGraphFormatEnum.GML );
		
		File actualWorkFolder = getWorkFolder();
		File actualFile = new File( actualWorkFolder, "graph/packages.gml" );
		
		// Lot registry - actual
		D6LPackageRegistry actualPackageRegistry = new D6LPackageRegistry( actualFile, D6LGraphFormatEnum.GML );
		
		// Check isomorphisms
		checkerPackageRegistry.checkLotIsomorphisms( actualPackageRegistry );
		
	}

	protected void retryAcceptIfAllSuccess( int n, String testName, TestFunctionIF fct ) throws Exception {
		
        for ( int i = 0; i < n; i++ ) {

            LOGGER.info( "********************************************************************" );
            LOGGER.info( testName + " " + i + "    " + i + "    " + i );
            LOGGER.info( "********************************************************************" );
            
            // Apply function
            fct.test( i );
            
        }

	}
	
	protected void retryAcceptIfOneSuccess( int n, String testName, TestFunctionIF fct ) throws Exception {
		
		List<Exception> exceptions = new ArrayList<>();
		
		boolean isOk = false;
		
        for ( int i = 0; i < n; i++ ) {

            LOGGER.info( "********************************************************************" );
            LOGGER.info( testName + " " + i + "    " + i + "    " + i );
            LOGGER.info( "********************************************************************" );
            
            // Apply function
            try {
            	
            	fct.test( i );
            	
            	// Success
            	isOk = true;
            	break;
            	
            } catch ( Exception e ) {
            	// add to list
            	exceptions.add( e );
            }
            
        }
        
        if ( !isOk ) {
        	// Display
        	for ( Exception e : exceptions ) {
        		LOGGER.error( "Test case: " + testName, e );
        	}
        	throw new Exception( "Test case '" + testName + "' failed " + n + " times" );
        }

	}

}
