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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.plugin.D6LAbstractPlugin;
import org.xlm.jxlm.d6light.data.util.D6LUtil;

/**
 * Base abstract class for algorithm implementation
 * @author Loison
 *
 */
public abstract class D6LAbstractAlgo extends D6LAbstractPlugin implements D6LAlgoIF {

	private static final String ALGO_MAPPINGS_PROPERTIES = "org/xlm/jxlm/audit/d6/data/algoMappings.properties";
	
	protected D6LightDataConf conf = null;
	protected AbstractAlgoType algoConf = null;
	
	/** Property file containing confType -> java class mappings **/
	protected static final Properties propsFactory;	
	static {
		propsFactory = new Properties();
		try (
			InputStream inPropsFactory = D6LUtil.getInstance().getInputStream( "/" + ALGO_MAPPINGS_PROPERTIES );
		) {
			propsFactory.load( inPropsFactory );
		} catch ( Exception e ) {
			
		    // fatal
			String fatalError = "Can't read " + ALGO_MAPPINGS_PROPERTIES + " file from classpath";
			LOGGER.fatal( fatalError );
			
			if ( LOGGER.isDebugEnabled() ) {
			    LOGGER.debug( e );
			}
			
			throw new D6LError( fatalError );
		}
		
	}
	
	/**
	 * Constructor
	 */
	public D6LAbstractAlgo() {
		super();
	}

	/**
	 * Create an algo
	 * @param db D6 DB
	 * @param confAlgo Algo configuration
	 * @param sysDataConf D6 global configuration
	 * @param iPass current pass
	 * @return algo instance
	 * @throws D6LException
	 */
	public static D6LAlgoIF getInstance( 
	    AbstractAlgoType confAlgo, 
	    D6LightDataConf sysDataConf, int reservedDoNotUsed
	) throws D6LException {
	    
	    D6LAlgoIF algoInstance = null;
	    
	    try {
    		// Get mapping
    		String confClazzName = confAlgo.getClass().getName();
    
    		String javaClazzName = propsFactory.getProperty( "conf|" + confClazzName );
    		if ( javaClazzName == null ) {
    			throw new D6LException( "Can't find java mapping for conf " + confClazzName );
    		}
    		
    		// get as class
    		@SuppressWarnings("unchecked")
    		Class<? extends D6LAlgoIF> javaClazz = (Class<? extends D6LAlgoIF>) D6LUtil.loadBaseAndPluginClass( javaClazzName );
    		
    		// call static getInstance from class
    		Method getInstanceMethod = javaClazz.getMethod( 
    		    "getInstance", 
    		    AbstractAlgoType.class 
    		);
    		if ( getInstanceMethod == null ) {
    			throw new D6LException( "Can't find getInstance() method for class " + javaClazz );
    		}
    		
    		// create instance
    		algoInstance = (D6LAlgoIF) getInstanceMethod.invoke( null, new Object[] { confAlgo, sysDataConf } );
    		if (algoInstance == null ) {
    			throw new D6LException( "Can't instanciate algo id '" + confAlgo.getId() + "'" );
    		}
    		
	    } catch ( Exception e ) {
	        D6LException.handleException( e );
	    }

	    return algoInstance;
        
	    
	}
	
	@Override
	public void setConf( D6LightDataConf conf, AbstractAlgoType algoConf ) throws D6LException {
		
		this.conf = conf;
		this.algoConf = algoConf;
		
	}
	
	@Override
	public AbstractAlgoType getConf() {
		return algoConf;
	}

	@Override
	public final String getPluginName() {
		return getName();
	}

}
