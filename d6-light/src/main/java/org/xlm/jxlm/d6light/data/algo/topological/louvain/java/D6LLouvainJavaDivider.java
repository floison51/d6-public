

package org.xlm.jxlm.d6light.data.algo.topological.louvain.java;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xlm.jxlm.d6light.data.algo.topological.louvain.D6LAbstractLouvainDivider;
import org.xlm.jxlm.d6light.data.conf.ParamType;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;

/**
 * Louvain divider using a java implementation
 * @author Loison
 *
 */
public class D6LLouvainJavaDivider extends D6LAbstractLouvainDivider {
	
	/** Logger **/
	public static final Logger LOGGER = LogManager.getLogger( D6LLouvainJavaDivider.class );
	
	public static final String PARAM_NB_ITERATIONS = "nbIterations";
	private int nbIterations = 30;
	
	/**
	 * Constructor
	 * @param db D6 DB
	 */
	public D6LLouvainJavaDivider() {
		super( "java", ',' );
	}
	
	@Override
	protected Set<String> getParamNames() {
		
		Set<String> params = new HashSet<>();
		params.addAll( super.getParamNames() );
		params.add( PARAM_NB_ITERATIONS );
		
		return Collections.unmodifiableSet( params );
	}
	
	/**
	 * Run Louvain algo
	 * @param txn
	 * @param bench
	 * @throws D6LException 
	 */
	@Override
	protected double runLouvain() throws D6LException {
		
		throw new D6LError( "TODO" );
		/*
		// Louvain input
		File louvainInputFile = bench.getBenchFile( louvainFolder );
		
		boolean setNormalCacheAfter = true;
		
		// Louvain output
		File louvainOutputFile = getLouvainOutputFile();
		
		LOGGER.info( "Run Louvain" );
		LOGGER.info( "  from file '" + louvainInputFile.getAbsolutePath() + "'" );
		LOGGER.info( "    to file '" + louvainOutputFile.getAbsolutePath() + "'" );
		
        final LouvainSelector ls = new LouvainSelector( louvainInputFile.getAbsolutePath(), louvainOutputFile.getAbsolutePath() );
        final LayeredCommunityStructure cs = ls.cluster( nbIterations, 0l );
        
        // Return maximum modularity: communities were built from max modularity
        return cs.getMaxModularity();
		*/	
	}

	@Override
	protected void recordAndValidateConfigParameters( List<String> paramNames, Map<String, String> propsParams, Map<String, ParamType> mapParams ) throws D6LException {
        
		// Ancestor params
		super.recordAndValidateConfigParameters( paramNames, propsParams, mapParams );
		
        Set<String> validParamNames = getParamNames();
        
        // Check params
        for ( String paramName : paramNames ) {
        
            if ( !validParamNames.contains( paramName ) ) {
                String mess = "Param '" + paramName + "' is not valid";
                LOGGER.error( mess );
                LOGGER.error( "Valid params: " + validParamNames );
                throw new D6LException( mess );
            }
            
        }
        
		// get nbIterations
        String strNbIterations = propsParams.get( PARAM_NB_ITERATIONS );
		if ( strNbIterations != null ) {
			// get integer
			nbIterations = Integer.parseInt( strNbIterations );
		}
		
	}

	@Override
	public String getName() {
		return "Java Louvain Technical Divider";
	}

	@Override
	protected String getResultSeparator() {
		return ":";
	}

}
