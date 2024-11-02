package org.xlm.jxlm.d6light.data.algo.topological.louvain;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.xlm.jxlm.d6light.data.algo.D6LAlgoCommandIF;
import org.xlm.jxlm.d6light.data.algo.topological.D6LAbstractTopologicalDivider;
import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.conf.TopologicalDividerType;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.model.D6LPackage;
import org.xlm.jxlm.d6light.data.packkage.D6LPackageTypeEnum;

/**
 * Base abstract class for Louvain divider
 * @author Loison
 *
 */
public abstract class D6LAbstractLouvainDivider extends D6LAbstractTopologicalDivider {

	/** Modularity calculated by Louvain, to check with D6 measure **/	
	public static final String MEASURE_KEY_LOUVAIN_MODULARITY = "modularityLouvain";
	
	/**
	 * Louvain folder
	 */
	protected File louvainFolder = null;
	private final String simpleName;
	private final char separator;

	/**
	 * Constructor
	 * @param db D6 DB
	 */
	public D6LAbstractLouvainDivider( String simpleName, char separator ) {
		super();
		this.simpleName = simpleName;
		this.separator = separator;
	}
	
	@Override
	public final void setConf( 
		D6LightDataConf conf, AbstractAlgoType algoConf
	) throws D6LException {

		super.setConf( conf, algoConf );
		
		// Louvain folder
		louvainFolder = new File( "TODO", simpleName );
		louvainFolder.mkdirs();

	}
	
	@Override
	public final void doPrepare( D6LAlgoCommandIF algoCommand ) throws D6LException {
		
		// Clean specific bench folder
		try {
			FileUtils.deleteDirectory( louvainFolder );
		} catch ( IOException e ) {
			D6LException.handleException( e );
		}

		louvainFolder.mkdirs();
		
		throw new D6LError( "TODO" );
		/*
		try (
			// clean bench local IDs
			EntityCursor<D6BenchLocalEntityId> localIds = db.daoBenchLocalEntityIdAccessor.byId.entities( txn, CursorConfig.READ_UNCOMMITTED );
		) {
			for ( D6BenchLocalEntityId localId: localIds ) {
				db.daoBenchLocalEntityIdAccessor.byId.delete( localId.getId() );
			}
		}

		// need local IDs
		boolean isNeedLocalIdsInBench = isNeedLocalIdsInBench();
		
		LOGGER.info( "Writing louvain bench files..." );
		
	    // browse benches to prepare Louvain
        try (
            // browse benches
            EntityCursor<D6Bench> benches = db.daoBenches.byId.entities( txn, CursorConfig.READ_UNCOMMITTED );
        ) {
            // browse benches
            for ( D6Bench bench : benches ) {
                
                if ( bench.getId() == D6Bench.NO_BENCH ) {
                    continue;
                }
                
        		// count
                int nbLinksInBench = ( int ) daoEntityLinks.getByBench( bench ).count();
                // Store for future usage
                bench.setUserData( "nbLinks", nbLinksInBench );
                bench.save( db, txn );
                
                // check
                if ( nbLinksInBench == 0 ) {
                    // next bench
                    continue;
                }
                
                // process bench
                doPrepareForLouvain( txn, isNeedLocalIdsInBench, nbLinksInBench, bench );
                stats.nbLinks += nbLinksInBench;
                
            }
        }
            
		return stats;
		*/
		
	}

	@Override
	protected void doAlgoRun( D6LAlgoCommandIF algoCommand ) throws D6LException {
		
		throw new D6LError( "TODO" );
		/*
		try (
            EntityCursor<D6Bench> benches = db.daoBenches.byId.entities( txn, CursorConfig.READ_UNCOMMITTED );
		) {
		
    		for ( D6Bench bench: benches ) {
    		    
    		    if ( bench.getId() == D6Bench.NO_BENCH ) {
    		        // skip
    		        continue;
    		    }
    		
    		    // If this is a bench with a real business lot, check it's not empty
    		    if ( bench.getIdLot() != D6Lot.ID_NO_LOT ) {
        			
    		        // empty bench?
        			int nbLinksInBench = ( int ) daoEntityLinks.getByBench( bench ).count();
        			
        			if ( nbLinksInBench == 0 ) {
        				// don't call louvain : nothing to do
        				continue;
        			}
        			
    		    }
    		    
   				// Run louvain and get modularity
   				double finalModularity = runLouvain( txn, bench );
   				LOGGER.info( "Louvain modularity " + finalModularity + " for lot " + bench.getIdLot() );
   				
   				// Set to lot
   				D6AbstractLot absLot = D6AbstractLot.getAbstractLot(db, txn, bench.getIdLot() );
   				absLot.putMeasure( new D6DoubleMeasure( MEASURE_KEY_LOUVAIN_MODULARITY, finalModularity ) );
   				
    			// Save lot
   				absLot.save( db, txn );
   				
    		}
    		
		} catch ( Exception e ) {
		    D6LException.handleException( e );
		}
		
		return stats;
		*/
	}

	protected abstract double runLouvain() throws D6LException;

    protected void readLouvainResult( 
        Map<Long, Map<Integer, Long>> mapLouvainLotIdToTechLotId,
        D6LPackage businessLot, int nbPartition
    )
        throws D6LException
    {
    	throw new D6LError( "TODO" );
    	/*
        // read result file
    	
    	// Get resultSeparator
    	String resultSeparator = getResultSeparator();
    	
        File outFile = getLouvainOutputFile( bench );
        LOGGER.info( "Reading Louvain output file " + outFile );
        
        // first pass : set technical lot to objects
        try (
            Scanner scanner = new Scanner( outFile );
        ) 
        {
        	while ( scanner.hasNextLine() ) {
        	    
        	    // Index of Louvain lot produced by Louvain calculation
        		
        		// localIndex:?:lot
        		String line = scanner.nextLine();
        		
        		// applicable?
        		if ( line.contains( resultSeparator) ) {

        			String[] tokens = StringUtils.split( line, resultSeparator );
	        		
	        		long louvainLocalObjectId  = Long.parseLong( tokens[ 0 ] );
	        		// Some tokens have 3 values, other 2 values
	        		int iTokenLot = ( tokens.length >= 3 ) ? 2 : 1;
	        		int iLouvainLot = Integer.parseInt( tokens[ iTokenLot ] );
	        		
	        		processLouvainLot( 
	        		    txn, iPass, isNeedLocalIdsInBench, mapLouvainLotIdToTechLotId,
	                    bench, businessLot, iLouvainLot, louvainLocalObjectId
	                );
	        		
        		}
        		
        	}

        } catch ( Exception e ) {
        	D6LException.handleException( e );
        }
        */
    }


	protected abstract String getResultSeparator();

	protected File getLouvainOutputFile() {
		return getLouvainOutputFile( "out.txt" );
	}
	
	protected File getLouvainOutputFile( String extension ) {
		
    	throw new D6LError( "TODO" );
		/*
		// Louvain input
		File louvainInputFile = bench.getBenchFile( louvainFolder );
		
		// Output
		File louvainOutputFile = new File( louvainFolder, louvainInputFile.getName() + "." + extension );
		
		return louvainOutputFile;
		*/
		
	}

	protected void processLouvainLot( 
        Map<Long, Map<Integer, Long>> mapLouvainLotIdToTechLotId,
        D6LPackage businessLot, int iLouvainLot, long louvainLocalObjectId
    )
    	throws D6LException
    {
		
    	throw new D6LError( "TODO" );
		/*
        // unknown object (from louvain point of view)
        if ( iLouvainLot < 0 ) {
        	throw new D6LException( "Unknown Louvain lot for index " + louvainLocalObjectId );
        }

        // ID of objects and links lot
        Long idNewLot = mapLouvainLotIdToTechLotId.get( bench.getId() ).get( iLouvainLot );

        // get louvain lot
        if ( idNewLot == null ) {
        	
            idNewLot = createNewLot( businessLot );

        	// store in map
        	mapLouvainLotIdToTechLotId.get( bench.getId() ).put( iLouvainLot, idNewLot );
        	
        }

        // object id
        long idObject = louvainLocalObjectId;
        
        if ( isNeedLocalIdsInBench ) {
        	// louvain index needs to be converted to object index
        	
        	// get global object id
        	idObject = 
        	    db.daoBenchLocalEntityIdAccessor.getGlobalEntityId( txn, bench.getId(), louvainLocalObjectId );
        	
        	if ( idObject < 0 ) {
        		throw new D6LException( "Can't find object from louvain object index " + louvainLocalObjectId );
        	}
        }

        // get entity
        D6EntityIF entity = db.daoMetaEntities.byIdGet( txn, iPass, iPassTechLot, idObject, null );
        
        // check
        if ( entity == null ) {
        	throw new D6LException( "Can't find object for id = " + idObject + ", from louvain object index " + louvainLocalObjectId );
        }
        
        // set lot to object
        entity.setIdLot( idNewLot );
        
        // update object
        entity.save( db, txn );
        */
    }

    private Long createNewLot( D6LPackage businessLot )
        throws D6LException
    {
    	throw new D6LError( "TODO" );
		/*
        Long idNewLot;
        D6LPackageVertex louvainLot;
        // create lot
        louvainLot = new D6LPackageVertex( producesLotType, null, iPass );
        
        // to avoid strange display
        if ( louvainLot.getLotType() == D6LotTypeEnum.BUSINESS_LOT ) {
        	louvainLot.setName( "" );
        }
        
        // add to business lot
        louvainLot.setIdLotParent( businessLot.getId() );
        // set bench
        louvainLot.setIdBench( bench.getId() );

        // save it
        louvainLot.save( db, txn );
        
        // get back id
        idNewLot = louvainLot.getId();
        return idNewLot;
        */
    }
    
	@Override
	public boolean isAllowsSinglesAllocation() {
		// needed
		return true;
	}

	@Override
	public boolean isNeedBomSimplification() {
	    
		// needed if conf asks for simplification
		boolean isNeeded = ( ( ( TopologicalDividerType ) algoConf ).getBomSimplifiers() != null );
		return isNeeded;
		
	}

	@Override
	public D6LPackageTypeEnum getDefaultProducesLotType() {	
		return D6LPackageTypeEnum.TECHNICAL_PKG;
	}

}
