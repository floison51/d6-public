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

package org.xlm.jxlm.d6light.data.algo.topological;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.hibernate.Session;
import org.xlm.jxlm.d6light.data.algo.D6LAbstractDividerAlgo;
import org.xlm.jxlm.d6light.data.algo.D6LAlgoCommandIF;
import org.xlm.jxlm.d6light.data.algo.topological.bom.D6LByDirectedLinkBomDivider;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier.BomSimplifierKindEnum;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LComponentsBomSimplifier;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LKitsBomSimplifier;
import org.xlm.jxlm.d6light.data.algo.topological.louvain.java.D6LLouvainJavaDivider;
import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.AbstractBomSimplifierType;
import org.xlm.jxlm.d6light.data.conf.BomSimplifierType;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.conf.ParamType;
import org.xlm.jxlm.d6light.data.conf.TopologicalDividerType;
import org.xlm.jxlm.d6light.data.conf.TopologicalDividerType.BomSimplifiers;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;
import org.xlm.jxlm.d6light.data.plugin.D6LPluginIF;

/**
 * Base abstract class for topological dividers
 * @author Loison
 *
 */
public abstract class D6LAbstractTopologicalDivider 
	extends D6LAbstractDividerAlgo 
	implements D6LTopologicalDividerIF 
{

    /** List of BomSiplifiers (components, kits) **/
    protected List<D6LAbstractBomSimplifier> listBomSimplifiers = new ArrayList<>();
    
    /**
     * Constructor
     * @param db D6 DB
     */
	public D6LAbstractTopologicalDivider() {
		super();
	}

	/**
	 * Factory method
	 * @param db D6 DB
	 * @param confAlgo algo config
	 * @param conf Global D6 conf
	 * @return Instance of algo
	 * @throws D6LException
	 */
	public static D6LTopologicalDividerIF getInstance( 
	    AbstractAlgoType confAlgo, D6LightDataConf conf
	    
	) throws D6LException {

		// get actual algo conf
		TopologicalDividerType config = (TopologicalDividerType) confAlgo;
		
		// instance depends on key
		D6LTopologicalDividerIF instance = null;
		
		switch ( config.getKey() ) {
			case LOUVAIN: {
				instance = new D6LLouvainJavaDivider();
				break;
			}
			case BILL_OF_MATERIAL: {
				instance = new D6LByDirectedLinkBomDivider();
				break;
			}
			
			default: {
				throw new D6LException( "Unsupported technical packager key " + config.getKey() );
			}
			
		}
		
		// finish instance initialization
		List<ParamType> params = Collections.emptyList();
		
		if ( config.getParams() != null ) {
		    params = config.getParams().getParam();
		}
		
        ( (D6LPluginIF ) instance ).recordAndValidateConfigParameters( params );
		
		return instance;
	}
	
	@Override
	public void setConf(
		D6LightDataConf conf, AbstractAlgoType algoConf 
	) throws D6LException {
		
		super.setConf( conf, algoConf );
		
        // init parameters
        TopologicalDividerType topologicalDividerConf = ( TopologicalDividerType ) algoConf;
        
        // Bom simplifiers: components, kits
        BomSimplifiers bomSimplifiersConf = topologicalDividerConf.getBomSimplifiers();
        
        if ( bomSimplifiersConf != null ) {
            
            // set bom simplifiers
        	
        	// Check for duplicates
            Set<BomSimplifierKindEnum> setBomSimplifierKinds = new HashSet<>();
            
            for ( JAXBElement<? extends AbstractBomSimplifierType> bomSimplifierConfElt : bomSimplifiersConf.getComponentsOrKits() ) {
            	
            	// Get kind
            	BomSimplifierKindEnum kind = BomSimplifierKindEnum.valueOf( bomSimplifierConfElt.getName().getLocalPart() );
            	
            	// Check unique
            	if ( setBomSimplifierKinds.contains( kind ) ) {
            		throw new D6LException( "Duplicate BOM Simplifier kind '" + kind + "'" );
            	}
            	
            	AbstractBomSimplifierType bomSimplifierConf = bomSimplifierConfElt.getValue();
            	
            	// Class?
            	String strClazz = bomSimplifierConf.getClazz();
            	
            	// Instanciate BOM Sipmlifier
            	D6LAbstractBomSimplifier bomSimplifier = null;
            	
            	if ( strClazz == null ) {
            		
            		// Pre-defined bom simplifiers
            		
	            	switch ( kind ) {
		            	case Components : {
		            		
		            		bomSimplifier = new D6LComponentsBomSimplifier( 
		            			(BomSimplifierType) bomSimplifierConf
		            		);
		                    break;
		            		
		            	}
		            	case Kits : {
		            		
		            		bomSimplifier = new D6LKitsBomSimplifier( 
		            			(BomSimplifierType) bomSimplifierConf
		            		);
		                    break;
		            		
		            	}
		            	default: {
		            		throw new D6LException( "Not supported BOM Simplifier kind '" + kind + "'" );
		            	}
		            	
	            	}
            	
            	} else {
            		
            		try {
            			
            			// Custom class
            			Class<? extends D6LAbstractBomSimplifier> customClazz = (Class<? extends D6LAbstractBomSimplifier>) Class.forName( strClazz );
            			// Get constructor
            			Constructor<? extends D6LAbstractBomSimplifier> constructor = 
            				customClazz.getConstructor( 
            					BomSimplifierType.class
            				);
            			// Get instance
            			bomSimplifier = constructor.newInstance( bomSimplifierConf );
            			
            		} catch ( Exception e ) {
            			D6LException.handleException( e );
            		}
            		
            	} 
            	
                listBomSimplifiers.add( bomSimplifier );
            }
            
        }
        
        // Freeze
        listBomSimplifiers = Collections.unmodifiableList( listBomSimplifiers );
		
	}

	@Override
	public List<D6LAbstractBomSimplifier> getListBomSimplifiers() {
		return listBomSimplifiers;
	}

    @Override
    public void checkOkForExecute()
        throws D6LException
    {
        // No checks needed
    }

	@Override
	public final void doRun( Session session, D6LAlgoCommandIF algoCommand ) throws D6LException {
		
		// delegate to algo
		doAlgoRun( session, algoCommand );
		
	}
	
	@Override
	public D6LPackageTypeEnum getProducesLotType() {
		return producesLotType;
	}

	/**
	 * Execution to be done by implementation classes
	 * @param txn
	 * @param iPass
	 */
	protected abstract void doAlgoRun( Session session, D6LAlgoCommandIF algoCommand ) throws D6LException;

}
