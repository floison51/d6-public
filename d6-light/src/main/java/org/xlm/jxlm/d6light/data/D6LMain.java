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

package org.xlm.jxlm.d6light.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.GraphImporter;
import org.xlm.jxlm.d6light.data.algo.D6LAbstractAlgo;
import org.xlm.jxlm.d6light.data.algo.D6LAbstractAlgoCommand;
import org.xlm.jxlm.d6light.data.algo.D6LAlgoCommandIF;
import org.xlm.jxlm.d6light.data.algo.D6LAlgoIF;
import org.xlm.jxlm.d6light.data.command.D6LNotAllocatedException;
import org.xlm.jxlm.d6light.data.conf.AbstractAlgoType;
import org.xlm.jxlm.d6light.data.conf.D6LConfHelper;
import org.xlm.jxlm.d6light.data.conf.D6LightDataConf;
import org.xlm.jxlm.d6light.data.db.D6LDb;
import org.xlm.jxlm.d6light.data.exception.D6LError;
import org.xlm.jxlm.d6light.data.exception.D6LException;
import org.xlm.jxlm.d6light.data.exp.D6LExporterWrapper;
import org.xlm.jxlm.d6light.data.imp.D6LImporterWrapper;
import org.xlm.jxlm.d6light.data.model.D6LEdge;
import org.xlm.jxlm.d6light.data.model.D6LPackageEdge;
import org.xlm.jxlm.d6light.data.model.D6LPackageVertex;
import org.xlm.jxlm.d6light.data.model.D6LVertex;
import org.xlm.jxlm.d6light.data.resources.D6LPackageOntologyHelper;

/**
 * Main class for D6-light
 */
public class D6LMain {

	/** Logger **/
	public static final Logger LOGGER = LogManager.getLogger( "D6-light" );
	
	/** Option configuration file **/
	public static final String OPTION_CONF = "conf";
	
	/** Option ID algo: choose graph transformation algo to be applied **/
	public static final String OPTION_ID_ALGO = "idAlgo";
	
	/** Option graph file input **/
	public static final String OPTION_GRAPH_IN = "graphIn";
	
	/** Option graph file output **/
	public static final String OPTION_GRAPH_OUT = "graphOut";
	
	/** Option graph format **/
	public static final String OPTION_GRAPH_FORMAT = "graphFormat";

	private File d6ConfFile;

	private File graphInFile;

	private File graphOutFile;
	
	private D6LGraphFormatEnum graphFormat;

	private D6LightDataConf d6lConf;

    /**
     * Default constructor
     */
    public D6LMain() {
        super();
    }
    
 	/**
	 * Main method
	 * @param args
 	 * @throws D6LException 
	 */
	public static void main( String... args ) throws D6LException {
		
		D6LMain me = new D6LMain();
        
        me.doJob( args );

	}

	public void doJob( String... args ) throws D6LException {
		
        LOGGER.info( "Data Systemizer Light version " + getVersion() );
        
        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LOGGER;
        
        // print log file
        Appender appender = coreLogger.getAppenders().get( "FILE" );
        if ( ( appender != null ) && ( appender instanceof FileAppender ) ) {
            
            FileAppender fileAppender = (FileAppender) appender;
            String name = fileAppender.getFileName();
            LOGGER.info( "Log file: " + name );
            
        }

        // Set ontology to default
        D6LPackageOntologyHelper.setOntology( "" );
        // Prepare options
        Options options = prepareOptions();
        
        // Parse options
        CommandLine cmd = parseOptions( args, options );
        
        processCmd( cmd );
        
	    // start 
	    long tsStart = System.currentTimeMillis();
	    
        // end 
        long tsEnd = System.currentTimeMillis();
        
        // elapsed
        long tsElapsed = tsEnd - tsStart;
        long minElapsed = tsElapsed / 1000 / 60;
        
        LOGGER.info( "Processing minuts : " + minElapsed );

		
	}
	
    private void processCmd( CommandLine cmd ) throws D6LException {
		
    	// Get conf file
    	this.d6ConfFile = getFileFromOption( cmd, OPTION_CONF, true );
    	
    	// Read conf
    	// Get JAXB config
    	try ( 
    		InputStream isConf = new FileInputStream ( this.d6ConfFile ); 
    	) {
    		this.d6lConf = D6LConfHelper.getConf( isConf, null );
    	} catch ( IOException ioe ) {
    		D6LException.handleException( ioe );
    	}
    	
    	// Get graph in file
    	this.graphInFile = getFileFromOption( cmd, OPTION_GRAPH_IN, true );

    	// Get graph out file
    	this.graphOutFile = getFileFromOption( cmd, OPTION_GRAPH_OUT, false );
    	this.graphOutFile.getParentFile().mkdirs();
    	
    	// Get graph format
    	String strGraphFormat = cmd.getOptionValue( OPTION_GRAPH_FORMAT );
    	
    	// Parse it
    	this.graphFormat = D6LGraphFormatEnum.valueOf( strGraphFormat );
    	
    	// Initialize empty graph
    	Graph<D6LVertex,D6LEdge> inGraph = new SimpleDirectedGraph<>( 
    		D6LEdge.class
    	);
    	Graph<D6LPackageVertex,D6LPackageEdge> outGraph = new SimpleDirectedGraph<>( 
    		D6LPackageEdge.class
    	);
    	
    	// Import graph
    	importInGraph( inGraph );
    	
    	// Freeze source graph
    	inGraph = new AsUnmodifiableGraph<>( inGraph );
    	
    	// Init DB
    	D6LDb.getInstance( inGraph, outGraph );
    	
    	// Algo
    	String idAlgo = cmd.getOptionValue( OPTION_ID_ALGO );
    	
    	// Go!
    	runAlgo( idAlgo );
    	
    	// Output
    	exportOutputGraph( D6LDb.getInstance().outGraph );
    	
	}

	private void runAlgo( String idAlgo ) throws D6LException {
		
		// Index algos
		Map<String,AbstractAlgoType> indexAlgoConfById = new HashMap<>();
		
		for ( AbstractAlgoType algoConf : d6lConf.getAlgos().getTopologicalDivider() ) {
			
			// Put in index
			if ( indexAlgoConfById.put( algoConf.getId(), algoConf) != null ) {
				// We have a duplicate
				throw new D6LException(
					MessageFormat.format(
						"Duplicate algo ID ''${0}'' in configuration file ''${1}''",
						algoConf.getId(), this.d6ConfFile.getAbsolutePath()
					)
				);
			}
		}
		
		// Get algo
		AbstractAlgoType algoConf = indexAlgoConfById.get( idAlgo );
		
		// Check
		if ( algoConf == null ) {
			// Not found
			throw new D6LException(
				MessageFormat.format(
					"Algo ID ''${0}'' not found in configuration file ''${1}''",
					idAlgo, this.d6ConfFile.getAbsolutePath()
				)
			);
		}
		
		// Instantiate algo
		D6LAlgoIF algo =  D6LAbstractAlgo.getInstance( algoConf, this.d6lConf );
		
		// set conf to algo
		algo.setConf( this.d6lConf, algoConf );
		
		// Create command
		D6LAlgoCommandIF cmdAlgo = D6LAbstractAlgoCommand.newInstance(
			algo, 
			this.d6lConf
		);
		
		// Create DB transaction
		D6LDb.getInstance().getSessionFactory().inTransaction( 
			session -> {
				try {
					// Run algo
					cmdAlgo.execute( session );
				} catch ( D6LException e ) {
					D6LError.handleThrowable( e );
				} catch ( D6LNotAllocatedException e ) {
					D6LError.handleThrowable( e );
				}
			}
		);

	}

	protected void importInGraph( Graph<D6LVertex, D6LEdge> graph ) throws D6LException {
		
		// Importer wrapper
    	D6LImporterWrapper<D6LVertex, D6LEdge> importerWrapper = new D6LImporterWrapper<>(
    		id -> new D6LVertex( id )
    	);
    	
    	// Get graph importer according to format
    	GraphImporter<D6LVertex, D6LEdge> importer =  
    		importerWrapper.getGraphImporterInstance( graphFormat );
    	
    	// Import graph
    	importer.importGraph( graph, graphInFile );
    	
	}

	private void exportOutputGraph(Graph<D6LPackageVertex,D6LPackageEdge> graph ) throws D6LException {

		// Exporter wrapper
    	D6LExporterWrapper<D6LPackageVertex,D6LPackageEdge> exporterWrapper = new D6LExporterWrapper<>();
    	
    	// Get graph importer according to format
    	GraphExporter<D6LPackageVertex, D6LPackageEdge> exporter =  
    		exporterWrapper.getGraphExporterInstance( graphFormat );
    	
    	// Export graph
    	exporter.exportGraph( graph, graphOutFile );
	}

	private File getFileFromOption( CommandLine cmd, String option, boolean isCheck ) throws D6LException {
		
		String path = cmd.getOptionValue( option );
 
		// As file
    	File file = new File( path );
    	
    	// Check
    	if ( isCheck ) {
    		
	    	if ( !file.exists() ) {
	    		throw new D6LException( MessageFormat.format( "File {0} doesn't exist.", file ) );
	    	}
	
	    	if ( !file.canRead() ) {
	    		throw new D6LException( MessageFormat.format( "File {0} is not readable.", file ) );
	    	}
	    	
    	}
    	
    	return file;
    	
	}

	private Options prepareOptions()
    {
        
		// create Options object
		Options options = new Options();
		
        // Add options
		
		Option confOption = new Option( OPTION_CONF, true, "D6 Light configuration file" );
		confOption.setRequired( true );
		confOption.setArgName( "D6 Light configuration file" );
		options.addOption( confOption );
	
		Option confIdAlgo = new Option( OPTION_ID_ALGO, true, "ID of algo to run" );
		confIdAlgo.setRequired( true );
		confIdAlgo.setArgName( "ID of algo to run" );
		options.addOption( confIdAlgo );
	
		Option graphInOption = new Option( OPTION_GRAPH_IN, true, "Input graph file" );
		graphInOption.setRequired( true );
		graphInOption.setArgName( "Input graph file" );
		options.addOption( graphInOption );
	
		Option graphOutOption = new Option( OPTION_GRAPH_OUT, true, "Output graph file" );
		graphOutOption.setRequired( true );
		graphOutOption.setArgName( "Output graph file" );
		options.addOption( graphOutOption );
	
		Option graphFormatOption = new Option( OPTION_GRAPH_FORMAT, true, "Graph format" );
		graphFormatOption.setRequired( true );
		graphFormatOption.setArgName( "Graph format" );
		options.addOption( graphFormatOption );

		return options;
	
    }
    
    private CommandLine parseOptions( String[] args, Options options )
        throws D6LException
    {
        CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		
		try {
		    
			cmd = parser.parse( options, args );
			
		} catch ( Exception e ) {
		    
			LOGGER.fatal( "Can't parse arguments" );
			LOGGER.fatal( e.getMessage() );
			
			if ( LOGGER.isTraceEnabled() ) {
			    LOGGER.trace( e );
			}
			
			help( options );
	        
		}
		
		if ( cmd == null ) {
		    throw new D6LException( "Can't parse arguments" );
		}
        return cmd;
    }
    
	private void help( Options options ) throws D6LException {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "java " + D6LMain.class.getName() , options );
		throw new D6LException( "Syntax error" );
	}
	
	/**
	 * Get program version, extracted from maven version
	 * @return
	 */
	public static String getVersion() {

		String version = getX6LibVersion( D6LMain.class.getPackage().getName() );
		return version;
		
	}

	/**
	 * Get version of a X6 library
	 * @param packkage
	 * @return
	 * @throws Exception 
	 */
	public static String getX6LibVersion( String packkage ) {
		
		// X6 Libs contains file called "info.properties"
		String cpPath = "/" + packkage.replace( '.', '/' ) + "/info.properties";
		InputStream isInfo = D6LMain.class.getResourceAsStream( cpPath );
		if ( isInfo==null ) {
			// Old lib: no version available
			return null;
		}
		
		// Load properties
		Properties props = new Properties();
		try {
			props.load( isInfo );
			return props.getProperty( "version" );
		} catch (IOException e) {
			// Not found
			return null;
		}
	}

}
