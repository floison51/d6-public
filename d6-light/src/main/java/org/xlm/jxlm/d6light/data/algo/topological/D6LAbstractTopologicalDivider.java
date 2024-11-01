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

import org.apache.tools.ant.taskdefs.SQLExec.Transaction;
import org.xlm.jxlm.audit.d6.data.algo.D6AlgoCommandIF;
import org.xlm.jxlm.audit.d6.data.algo.topological.adherence.D6ByDirectedLinkAdherenceDivider;
import org.xlm.jxlm.audit.d6.data.algo.topological.errordirectednavigator.D6ByDirectedLinkErrorNavigatorDivider;
import org.xlm.jxlm.audit.d6.data.algo.topological.louvain.genuine.D6LouvainGenuineDivider;
import org.xlm.jxlm.audit.d6.data.algo.topological.louvain.java.D6LouvainJavaDivider;
import org.xlm.jxlm.audit.d6.data.algo.topological.magnet.D6ByMagnetDivider;
import org.xlm.jxlm.audit.d6.data.algo.topological.metis.D6MetisDivider;
import org.xlm.jxlm.audit.d6.data.algo.topological.packetizer.D6PacketizerBomDivider;
import org.xlm.jxlm.audit.d6.data.command.Stats;
import org.xlm.jxlm.audit.d6.data.conf.D6DataConf;
import org.xlm.jxlm.audit.d6.data.db.D6SystemizerDataDb;
import org.xlm.jxlm.audit.d6.data.plugin.D6AbstractSystemizerDataPlugin;
import org.xlm.jxlm.audit.d6.data.plugin.D6SystemizerDataPluginIF;
import org.xlm.jxlm.audit.x6.common.X6Exception;
import org.xlm.jxlm.audit.x6.common.data.conf.LotExtractorType;
import org.xlm.jxlm.d6light.data.algo.topological.bom.D6ByDirectedLinkBomDivider;
import org.xlm.jxlm.d6light.data.algo.topological.bom.D6TopologicalDividerIF;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LAbstractBomSimplifier.BomSimplifierKindEnum;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6ComponentsBomSimplifier;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6KitsBomSimplifier;
import org.xlm.jxlm.d6light.data.algo.topological.bomsimplifier.D6LotExtractorBomSimplifier;
import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.AbstractBomSimplifierType;
import org.xlm.jxlm.d6light.data.conf.BomSimplifierType;
import org.xlm.jxlm.d6light.data.conf.ParamType;
import org.xlm.jxlm.d6light.data.conf.TopologicalDividerType;
import org.xlm.jxlm.d6light.data.conf.TopologicalDividerType.BomSimplifiers;

/**
 * Base abstract class for topological dividers
 * @author Loison
 *
 */
public abstract class D6LAbstractTopologicalDivider 
	extends D6AbstractDividerAlgo 
	implements D6LTopologicalDividerIF 
{

    /** List of BomSiplifiers (components, kits) **/
    protected List<D6LAbstractBomSimplifier> listBomSimplifiers = new ArrayList<>();
    
    /**
     * Constructor
     * @param db D6 DB
     */
	public D6LAbstractTopologicalDivider( D6SystemizerDataDb db ) {
		super( db );
	}

	/**
	 * Factory method
	 * @param db D6 DB
	 * @param confAlgo algo config
	 * @param conf Global D6 conf
	 * @return Instance of algo
	 * @throws X6Exception
	 */
	public static D6TopologicalDividerIF getInstance( 
	    D6SystemizerDataDb db, AbstractAlgoType confAlgo, D6DataConf conf
	) throws X6Exception {

		// get actual algo conf
		TopologicalDividerType config = (TopologicalDividerType) confAlgo;
		
		// instance depends on key
		D6TopologicalDividerIF instance = null;
		
		switch ( config.getKey() ) {
			case METIS: {
				instance = new D6MetisDivider( db );
				break;
			}
			case LOUVAIN_GENUINE: {
				instance = new D6LouvainGenuineDivider( db );
				break;
			}
			case LOUVAIN_JAVA: {
				instance = new D6LouvainJavaDivider( db );
				break;
			}
			case BILL_OF_MATERIAL: {
				instance = new D6ByDirectedLinkBomDivider( db );
				break;
			}
			case MAGNET: {
				instance = new D6ByMagnetDivider( db );
				break;
			}
            case BY_DIRECTED_LINK_ERROR_NAVIGATOR: {
                instance = new D6ByDirectedLinkErrorNavigatorDivider( db );
                break;
            }
			case ADHERENCE: {
				instance = new D6ByDirectedLinkAdherenceDivider( db );
				break;
			}
			case NATURAL_ORDER: {
				instance = new D6TrivialTopologicalDivider( db, D6TrivialTopologicalDivider.Kind.NaturalOrder );
				break;
			}
            case PACKETISER: {
                instance = new D6PacketizerBomDivider( db );
                break;
            }
			case CUSTOM: {
				// get class
				instance =  ( D6TopologicalDividerIF) D6AbstractSystemizerDataPlugin.getPluginInstance( db, config.getClazz() );
				break;
			}
			
			default: {
				throw new X6Exception( "Unsupported technical lotizer key " + config.getKey() );
			}
			
		}
		
		// finish instance initialization
		List<ParamType> params = Collections.emptyList();
		
		if ( config.getParams() != null ) {
		    params = config.getParams().getParam();
		}
		
        ( (D6SystemizerDataPluginIF ) instance ).recordAndValidateConfigParameters( params );
		
		return instance;
	}
	
	@Override
	public void setConf( D6DataConf conf, AbstractAlgoType algoConf ) throws X6Exception {
		
		super.setConf( conf, algoConf );
		
        // init parameters
        TopologicalDividerType topologicalDividerConf = ( TopologicalDividerType ) algoConf;
        
        // Bom simplifiers: components, kits
        BomSimplifiers bomSimplifiersConf = topologicalDividerConf.getBomSimplifiers();
        
        if ( bomSimplifiersConf != null ) {
            
            // set bom simplifiers
        	
        	// Check for duplicates
            Set<BomSimplifierKindEnum> setBomSimplifierKinds = new HashSet<>();
            
            for ( JAXBElement<? extends AbstractBomSimplifierType> bomSimplifierConfElt : bomSimplifiersConf.getComponentsOrKitsOrLotExtractor() ) {
            	
            	// Get kind
            	BomSimplifierKindEnum kind = BomSimplifierKindEnum.valueOf( bomSimplifierConfElt.getName().getLocalPart() );
            	
            	// Check unique
            	if ( setBomSimplifierKinds.contains( kind ) ) {
            		throw new X6Exception( "Duplicate BOM Simplifier kind '" + kind + "'" );
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
		            		
		            		bomSimplifier = new D6ComponentsBomSimplifier( db, (BomSimplifierType) bomSimplifierConf );
		                    break;
		            		
		            	}
		            	case Kits : {
		            		
		            		bomSimplifier = new D6KitsBomSimplifier( db, (BomSimplifierType) bomSimplifierConf );
		                    break;
		            		
		            	}
		            	case LotExtractor : {
		            		
		            		bomSimplifier = new D6LotExtractorBomSimplifier( db, (LotExtractorType) bomSimplifierConf );
		                    break;
		            		
		            	}
		            	default: {
		            		throw new X6Exception( "Not supported BOM Simplifier kind '" + kind + "'" );
		            	}
		            	
	            	}
            	
            	} else {
            		
            		try {
            			
            			// Custom class
            			Class<? extends D6LAbstractBomSimplifier> customClazz = (Class<? extends D6LAbstractBomSimplifier>) Class.forName( strClazz );
            			// Get constructor
            			Constructor<? extends D6LAbstractBomSimplifier> constructor = 
            				customClazz.getConstructor( 
            					D6SystemizerDataDb.class, BomSimplifierType.class
            				);
            			// Get instance
            			bomSimplifier = constructor.newInstance( db, bomSimplifierConf );
            			
            		} catch ( Exception e ) {
            			X6Exception.handleException( e );
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
        throws X6Exception
    {
        // No checks needed
    }

	@Override
	public final Stats doRun( Integer idParentMilestone, Transaction txn, D6AlgoCommandIF algoCommand ) throws X6Exception {
		
		// delegate to algo
		Stats stats = doAlgoRun( txn, algoCommand );
		
    	// Init bom simplifiers
    	for ( D6LAbstractBomSimplifier bomSimplifier : listBomSimplifiers ) {
    		
        	// Set passes
        	bomSimplifier.setPasses( iPass, iPassTechLot );
        	
    	}

		// move component lot to upper level if needed
		for ( D6LAbstractBomSimplifier bomSimplifier : listBomSimplifiers ) {
			
			// Move simplifier lot if needed
			bomSimplifier.moveSimplifiedTechnicalLotsToBusinessLotIfNeeded( db, txn );
			
		}
		
		return stats;
	}
	
	/**
	 * Execution to be done by implementation classes
	 * @param txn
	 * @param iPass
	 */
	protected abstract Stats doAlgoRun( Transaction txn, D6AlgoCommandIF algoCommand ) throws X6Exception;

	@Override
	public boolean isBenchNeeded() {
		// Bench needed
		return true;
	}

}
