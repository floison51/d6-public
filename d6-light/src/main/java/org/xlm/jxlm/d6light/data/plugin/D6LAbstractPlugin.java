

package org.xlm.jxlm.d6light.data.plugin;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.conf.ParamType;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.util.D6LUtil;

/**
 * Base abstract class for plugins
 * @author Francois Loison
 *
 */
public abstract class D6LAbstractPlugin implements D6LPluginIF {
	
	/** Logger **/
	public static final Logger LOGGER = D6LAbstractPlugin.LOGGER;

	/** Verbose **/
	protected boolean isVerbose = true;
	
	/**
	 * Constructor
	 * @param db
	 */
	public D6LAbstractPlugin() {

		super();
		
	}
	
	@Override
	/**
	 * Digest parameters, send them to actual plugin
	 */
	public void recordAndValidateConfigParameters( List<ParamType> confParams )
		throws D6LException 
	{
		// un-ordered parameters
		Map<String,String> propsParams = new HashMap<>();
		
		// same but as conf param
		Map<String,ParamType> mapParams = new HashMap<>();
		
		// ordered parameter names
		List<String> paramNames = new ArrayList<>();
		
		// browse params
		for ( ParamType confParam: confParams ) {
			
			String paramName = confParam.getName();
			
			// existing?
			if ( propsParams.containsKey( paramName ) ) {
				throw new D6LException( "Duplicate parameter " + confParam.getName() + " in plugin " + getPluginName() );
			}
			// add param
			paramNames.add( paramName );
			String paramValue = confParam.getValue();
			propsParams.put( paramName, paramValue );
			mapParams.put( paramName, confParam );
		}
		
		// call actual class
		recordAndValidateConfigParameters( paramNames, propsParams, mapParams );
		
	}

	/**
	 * Digest parameters, record them
	 * @param propsParams
	 * @throws D6LException
	 */
	protected void recordAndValidateConfigParameters( List<String> paramNames, Map<String, String> propsParams, Map<String, ParamType> mapParams ) 
		throws D6LException 
	{

	    if ( LOGGER.isTraceEnabled() ) {
	        LOGGER.trace( this.getClass() + "recordAndValidateConfigParameters" );
	        LOGGER.trace( propsParams );
            LOGGER.trace( mapParams );
	    }
	    
		// call optional getParamNames
		Set<String> allowedParamNames = getParamNames();
		
		if ( allowedParamNames != null ) {
			
		    // browse params
			for ( String paramName: paramNames ) {
				
			    // allowed?
				if ( !allowedParamNames.contains( paramName ) ) {
					
				    // no
					StringBuilder mess = notAllowedMessage( allowedParamNames, paramName );
					
					throw new D6LException( mess.toString() );
					
				}
				
			}
			
		}
		
	}

    private StringBuilder notAllowedMessage( Set<String> allowedParamNames, String paramName )
    {
        StringBuilder mess = new StringBuilder( "Parameter '" + paramName + "' not allowed for plugin '" + getPluginName() + "'" );
        mess.append( '\n' );
        mess.append( "Allowed parameter names:\n" );
        for ( String allowedParamName: allowedParamNames ) {
        	mess.append( "  " ).append( allowedParamName ).append( "\n" );
        }
        return mess;
    }

	/**
	 * Allowed parameter names
	 * @return
	 */
	protected Set<String> getParamNames() {
		// should be overidden
		return null;
	}

	/**
	 * Return a pluggin instance, using constructor ( db )
	 * @param db
	 * @param clazzName
	 * @return
	 * @throws Exception 
	 */
	public static D6LPluginIF getPluginInstance( String clazzName ) throws D6LException {

		D6LPluginIF instance = null;
	    
	    try {
    		@SuppressWarnings("unchecked")
    		Class<? extends D6LPluginIF> clazz = (Class<? extends D6LPluginIF>) D6LUtil.loadBaseAndPluginClass( clazzName );
    		
    		Constructor<? extends D6LPluginIF> constructor = clazz.getConstructor();
    		instance = constructor.newInstance();
    		
	    } catch( Exception e ) {
	        D6LException.handleException( e );
	    }

	    return instance;
	    
	}

    /**
     * Return a pluggin instance, using constructor ( db, conf )
     * @param db
     * @param conf
     * @param clazzName
     * @return
     * @throws Exception 
     */
    public static D6LPluginIF getPluginInstance( D6LightDataConf conf, String clazzName ) throws D6LException {

        D6LPluginIF instance = null;
        
        try {
            @SuppressWarnings("unchecked")
            Class<? extends D6LPluginIF> clazz = 
            	(Class<? extends D6LPluginIF>) D6LUtil.loadBaseAndPluginClass( clazzName );
            
            Constructor<? extends D6LPluginIF> constructor = clazz.getConstructor( D6LightDataConf.class );
            instance = constructor.newInstance( conf );
            
        } catch( Exception e ) {
            D6LException.handleException( e );
        }

        return instance;
        
    }

    protected void paramError( String paramName ) throws D6LException {
        paramError( paramName, null );
    }

	protected void paramError( String paramName, String detail ) throws D6LException {
		String errorMessage = "Missing parameter '" + paramName + "' in plugin " + getPluginName();
		if ( detail != null ) {
		    errorMessage += "\n" + detail;
		}
		throw new D6LException( errorMessage );
	}

}
