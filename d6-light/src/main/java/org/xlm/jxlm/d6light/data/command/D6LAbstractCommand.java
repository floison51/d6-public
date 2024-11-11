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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.exception.D6LException;

/**
 * Base abstract class for commands
 * @author Loison
 *
 */
public abstract class D6LAbstractCommand implements D6LCommandIF {

    protected static final Logger LOGGER = LogManager.getLogger( D6LAbstractCommand.class );
	
	/** Configuration **/
	protected D6LightDataConf conf;
	
	protected final D6LDb db = D6LDb.getInstance();
	
	/**
	 * Default constructor
	 */
	public D6LAbstractCommand() {
		super();
	}
	
	/**
	 * Constructor
	 * @param conf
	 * @param db
	 * @param listFatalErrors
	 * @throws X6Exception 
	 */
	public D6LAbstractCommand( D6LightDataConf conf ) throws D6LException {
		super();
		this.conf= conf;
	}

	@Override
	public final void execute( Session session ) throws D6LException
	{
		
        doPrepare( session, true );
        
        // flush session
        session.flush();
        
        doRun( session, true );
		
        // flush session
        session.flush();
        
	}

	/**
	 * Prepare before execution
	 * @param session 
	 */
	protected abstract void doPrepare( Session session, final boolean callAlgo ) throws D6LException;

	/**
	 * Actual command execution
	 * @param session 
	 * 
	 * @param txn
	 * @throws X6Exception
	 * @throws Exception
	 */
	protected abstract void doRun( Session session, final boolean callAlgo ) throws D6LException;


    /**
     * Algo short name, for threads.
     * @return
     */
    abstract protected String getShortName();
    
}
