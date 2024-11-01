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

import java.lang.reflect.Constructor;

import org.xlm.jxlm.d6light.data.command.D6LAbstractCommand;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.util.D6LUtil;

/**
 * Abstract algo command
 * @author Francois Loison
 *
 */
public abstract class D6LAbstractAlgoCommand 
	extends D6LAbstractCommand 
	implements D6LAlgoCommandIF 
{

    private D6LAlgoIF algo;

	/**
     * Default constructor
     */
    public D6LAbstractAlgoCommand() {
        super();
    }
    
    /**
     * Constructor
     * @param conf D6 global conf
     * @param db D6 DB
     * @param listFatalErrors listFatalErrors
     * @param iPass iPass
     * @throws X6Exception 
     */
    public D6LAbstractAlgoCommand( 
        D6LightDataConf conf
    ) throws D6LException {
        super( conf );
    }

    /**
     * Create instance given algo class
     * @param algoClass
     * @param conf
     * @return
     * @throws D6LException
     */
	@SuppressWarnings( "unchecked" )
	public static D6LAlgoCommandIF newInstance( 
		Class<? extends D6LAlgoIF> algoClass,
		D6LightDataConf conf
	) throws D6LException {
		
	    D6LAbstractAlgoCommand algoCommand = null;
	    
	    try {
    		
	        String algoClassName = algoClass.getName();
    		
    		// get mapping
    		String algoCommandClassName = D6LAbstractAlgo.propsFactory.getProperty( "command|" + algoClassName );
    		
    		if ( algoCommandClassName == null ) {
    			// try ancestor class
    			Class<? extends D6LAlgoIF> algoClazz = (Class<? extends D6LAlgoIF>) D6LUtil.loadBaseAndPluginClass( algoClassName );
    			Class<? extends D6LAlgoIF> algoAncestorClazz = (Class<? extends D6LAlgoIF>) algoClazz.getSuperclass();
    			algoCommandClassName = D6LAbstractAlgo.propsFactory.getProperty( "command|" + algoAncestorClazz.getName() );
    			
    			if ( algoCommandClassName == null ) {
    				throw new D6LException( "Can't find java mapping for command " + algoClassName );
    			}
    			
    		}
    		
    		// get class
    		Class<? extends D6LAbstractAlgoCommand> algoCommandClass = 
    			(Class<? extends D6LAbstractAlgoCommand>) D6LUtil.loadBaseAndPluginClass( algoCommandClassName );
    		
    		// create instance
    		Constructor<? extends D6LAbstractAlgoCommand> cAlgoCommand = 
    			algoCommandClass.getDeclaredConstructor( D6LightDataConf.class );
    		algoCommand = cAlgoCommand.newInstance( conf );
    		
	    } catch ( Exception e ) {
	        
	        D6LException.handleException( e );
	    }
		return algoCommand;
		
	}
	
	
	/**
	 * Allocate entities to process to benches, then launch extra command based preparation
	 * @throws D6LException
	 */
	@Override
	protected void doPrepare( final boolean callAlgo ) throws D6LException {
		
		// Delegate to algo
        getAlgo().doPrepare( this );
        
	}

	/**
	 * Command based run
	 * @throws D6LException
	 */
	protected void doRun( final boolean callAlgo ) throws D6LException {
		
		// delegate to algo
    	if ( callAlgo ) {
    	    
    	    D6LAlgoIF algo = getAlgo();
            
    	    // run
    	    algo.doRun( this );
    	    
    	}
    	
	}
	
	@Override
	public D6LAlgoIF getAlgo() {
		return this.algo;
	}

	protected void doExecute() throws D6LException {
	    
		doExecute( false );
		
	}

    @Override
    public String getConfId()
    {
        String confId;
        
        confId = getAlgo().getConf().getId();
        
        return confId;
    }

    @Override
    public String getDisplayName()
    {
        // Get name from algo
        return getAlgo().getName();
    }

}
