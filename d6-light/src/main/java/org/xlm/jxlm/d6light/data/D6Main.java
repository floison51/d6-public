package org.xlm.jxlm.d6light.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
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
import org.xlm.jxlm.d6light.data.imp.D6ImportFormatEnum;

/**
 * Main class for D6-light
 */
public class D6Main {

	/** Logger **/
	public static final Logger LOGGER = LogManager.getLogger( "D6-light" );
	
	/** Option configuration file **/
	public static final String OPTION_CONF = "conf";
	
	/** Option graph file input **/
	public static final String OPTION_GRAPH_IN = "graphIn";
	
	/** Option graph format **/
	public static final String OPTION_GRAPH_FORMAT = "graphFormat";
	
    /**
     * Default constructor
     */
    public D6Main() {
        super();
    }
    
 	/**
	 * Main method
	 * @param args
 	 * @throws D6Exception 
	 */
	public static void main( String... args ) throws D6Exception {
		
		D6Main me = new D6Main();
        
        me.doJob( args );

	}

	public void doJob( String... args ) throws D6Exception {
		
        LOGGER.info( "TT Data Systemizer version " + getVersion() );
        
        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LOGGER;
        
        // print log file
        Appender appender = coreLogger.getAppenders().get( "FILE" );
        if ( ( appender != null ) && ( appender instanceof FileAppender ) ) {
            
            FileAppender fileAppender = (FileAppender) appender;
            String name = fileAppender.getFileName();
            LOGGER.info( "Log file: " + name );
            
        }

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
	
    private void processCmd( CommandLine cmd ) throws D6Exception {
		
    	// Get conf file
    	File confFile = getFileFromOption( cmd, OPTION_CONF );
    	
    	// Get graph file
    	File graphInFile = getFileFromOption( cmd, OPTION_GRAPH_IN );

    	// Get graph format
    	String strGraphFormat = cmd.getOptionValue( OPTION_GRAPH_FORMAT );
    	
    	// Parse it
    	D6ImportFormatEnum graphFormat = D6ImportFormatEnum.valueOf( strGraphFormat );
    	
	}

	private File getFileFromOption( CommandLine cmd, String option ) throws D6Exception {
		
		String path = cmd.getOptionValue( option );
 
		// As file
    	File file = new File( path );
    	
    	// Check
    	if ( !file.exists() ) {
    		throw new D6Exception( MessageFormat.format( "File {0} doesn't exist.", file ) );
    	}

    	if ( !file.canRead() ) {
    		throw new D6Exception( MessageFormat.format( "File {0} is not readable.", file ) );
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
	
		Option graphInOption = new Option( OPTION_GRAPH_IN, true, "Input graph file" );
		graphInOption.setRequired( true );
		graphInOption.setArgName( "Input graph file" );
		options.addOption( graphInOption );
	
		Option graphFormatOption = new Option( OPTION_GRAPH_FORMAT, true, "Graph format" );
		graphFormatOption.setRequired( true );
		graphFormatOption.setArgName( "Graph format" );
		options.addOption( graphFormatOption );

		return options;
	
    }
    
    private CommandLine parseOptions( String[] args, Options options )
        throws D6Exception
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
		    throw new D6Exception( "Can't parse arguments" );
		}
        return cmd;
    }
    
	private void help( Options options ) throws D6Exception {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "java " + D6Main.class.getName() , options );
		throw new D6Exception( "Syntax error" );
	}
	
	/**
	 * Get program version, extracted from maven version
	 * @return
	 */
	public static String getVersion() {

		String version;
		version = getX6LibVersion( D6Main.class.getPackage().getName() );
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
		InputStream isInfo = D6Main.class.getResourceAsStream( cpPath );
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
