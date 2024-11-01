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

package org.xlm.jxlm.d6light.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xlm.jxlm.d6light.data.exception.D6LException;

/**
 * Utilitary class
 * @author Loison
 *
 */
public class D6LUtil {

    /** Regexp identifying wrong cars for valid file system name **/
    public static final String REGEXP_FORBIDDEN_FILE_CAR = "[/\\\\:*?\"<>|&$']";

	public static final long NB_BIG_PROCESSED_TICK 	   = 100000l;
	public static final long NB_MEDIUM_PROCESSED_TICK  = 10000l;
	
	/** Period in milli-seconds hiding progress ticks **/ 
    public static final long HIDE_PERIOD_MS   = 15l * 1000l;
	
    public static final String DATE_FORMAT_XSD_PATTERN_NO_MILLISECOND  = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_XSD_PATTERN                 = DATE_FORMAT_XSD_PATTERN_NO_MILLISECOND + ".S";
    
    private static final DateFormat DATE_FORMAT_XSD_NO_MILLISECOND  = new SimpleDateFormat( DATE_FORMAT_XSD_PATTERN_NO_MILLISECOND, Locale.ENGLISH );
    private static final DateFormat DATE_FORMAT_XSD                 = new SimpleDateFormat( DATE_FORMAT_XSD_PATTERN, Locale.ENGLISH );
    
    /** Date format compliant with file system constraints **/ 
    public static final DateFormat DATE_FORMAT_FILE_SYSTEM = new SimpleDateFormat( "yyyy-mm-dd_hh-mm-ss" );

    public static final String DATE_FORMAT_CSV_PATTERN_NO_MILLISECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_CSV_PATTERN = DATE_FORMAT_CSV_PATTERN_NO_MILLISECOND + ".S";
    
    private static final DateFormat DATE_FORMAT_CSV_NO_MILLISECOND  = new SimpleDateFormat( DATE_FORMAT_CSV_PATTERN_NO_MILLISECOND, Locale.ENGLISH );   
    private static final DateFormat DATE_FORMAT_CSV                 = new SimpleDateFormat( DATE_FORMAT_CSV_PATTERN, Locale.ENGLISH );

    private static D6LUtil me = null;
    
    public static synchronized D6LUtil getInstance() {
        
        if ( me == null ) {
            me = new D6LUtil();
        }
        
        return me;
        
    }

    private D6LUtil() {
        super();
    }
    
	/**
	 * formatXsdTimestamp
	 * @param ts
	 * @return
	 */
	public static String formatXsdTimestamp( long ts ) {
		return DATE_FORMAT_XSD.format( new Date( ts ) );
	}
	
    /**
     * Format time stamp to a format compliant with file systems
     * @param ts
     * @return
     * @throws D6LException 
     */
    public static String formatFileSystemTimestamp( long ts ) throws D6LException
    {
        String result = DATE_FORMAT_FILE_SYSTEM.format( new Date( ts ) );
        
        // check
        if ( REGEXP_FORBIDDEN_FILE_CAR.matches( result ) ) {
            throw new D6LException( "Incorrent file system string '" + result + "'" );
        }
        
        return result;
        
    }

    /**
     * parseXsdTimestamp
     * @param strDate
     * @return
     */
	public static Date parseXsdTimestamp( String strDate ) throws ParseException {
		return DATE_FORMAT_XSD.parse( strDate );
	}

	/**
	 * formatXsdTimestampNoMilliSecond
	 * @param ts
	 * @return
	 */
	public static String formatXsdTimestampNoMilliSecond( long ts ) {
		return DATE_FORMAT_XSD_NO_MILLISECOND.format( new Date( ts ) );
	}
	
	/**
	 * parseXsdTimestampNoMilliSecond
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date parseXsdTimestampNoMilliSecond( String strDate ) throws ParseException {
		return DATE_FORMAT_XSD_NO_MILLISECOND.parse( strDate );
	}

	/**
	 * formatCsvTimestamp
	 * @param ts
	 * @return
	 */
	public static String formatCsvTimestamp( long ts ) {
		return DATE_FORMAT_CSV.format( new Date( ts ) );
	}
	
	/**
	 * parseCsvTimestamp
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date parseCsvTimestamp( String strDate ) throws ParseException {
		return DATE_FORMAT_CSV.parse( strDate );
	}

	/**
	 * formatCsvTimestampNoMilliSecond
	 * @param ts
	 * @return
	 */
	public static String formatCsvTimestampNoMilliSecond( long ts ) {
		return DATE_FORMAT_CSV_NO_MILLISECOND.format( new Date( ts ) );
	}
	
	/**
	 * parseCsvTimestampNoMilliSecond
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date parseCsvTimestampNoMilliSecond( String strDate ) throws ParseException {
		return DATE_FORMAT_CSV_NO_MILLISECOND.parse( strDate );
	}

	/**
	 * updateFirstCreationTimestamp
	 * @param firstCreationTimestamp
	 * @param ts
	 * @return
	 */
	public static Long updateFirstCreationTimestamp( Long firstCreationTimestamp, Long ts ) {
	    
	    Long curFirstCreationTimestamp = firstCreationTimestamp;
	    
		if ( 
		        ( ts != null ) 
		        && 
		        ( 
		            ( curFirstCreationTimestamp == null ) || curFirstCreationTimestamp > ts ) 
		        ) 
		{
		    curFirstCreationTimestamp = ts;
		}
		
		return curFirstCreationTimestamp;
	}
	
	/**
	 * updateLastUpdateTimestamp
	 * @param lastUpdateTimestamp
	 * @param ts
	 * @return
	 */
	public static Long updateLastUpdateTimestamp( Long lastUpdateTimestamp, Long ts ) {
	    return updateLastUpdateTimestamp( lastUpdateTimestamp, ts, false );
	}
	
	public static Long updateLastUpdateTimestamp( Long lastUpdateTimestamp, Long ts, boolean force ) {
	    Long curLastUpdateTimestamp = lastUpdateTimestamp;
		if ( 
		    ( ts != null ) 
		    && 
		    (
		         force || ( ( curLastUpdateTimestamp == null ) || ( curLastUpdateTimestamp < ts ) ) 
		    )
		) {
		    curLastUpdateTimestamp = ts;
		}
		return curLastUpdateTimestamp;
	}
	
    /**
     * Get duration in second
     * @param startMs
     * @param endMs
     * @return
     */
    public static double getDuration( long startMs, long endMs )
    {
        double duration;
        duration = ( ( double ) endMs - ( double ) startMs ) / 1000d;
        return duration;
    }


    public static ClassLoader getBaseAndPluginClassLoader() {
        
        // Use this sophisticated trick to load current thread classpath modified dynamically and not from application static class path
        ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
        
        return threadContextClassLoader;
    }
    
    /**
     * Load a class from D6 base class path and from external plugins
     * @param clazz
     * @return
     * @throws ClassNotFoundException 
     */
    public static Class<?> loadBaseAndPluginClass( String clazzName ) throws ClassNotFoundException
    {
        
        // Use this sophisticated trick to load current thread classpath modified dynamically and not from application static class path
        ClassLoader threadContextClassLoader = getBaseAndPluginClassLoader();
        
        try {

            Class<?> clazz = threadContextClassLoader.loadClass( clazzName );
            return clazz;
            
        } catch ( ClassNotFoundException e ) {
            throw e;
        }
        
    }

    /**
     * Load a resource from D6 base class path and from external plugins
     * @param clazz
     * @return
     * @throws ClassNotFoundException 
     */
    public InputStream getInputStream( String resPath ) throws Exception
    {
        
        // Use this sophisticated trick to load current thread classpath modified dynamically and not from application static class path
        InputStream isResource = null;
        
        // try a file
        File fileRes = new File( resPath );
        if ( fileRes.exists() ) {
            isResource = new FileInputStream( fileRes );
        }

        if ( isResource == null ) {
            
            // load resource from class path
            String normalizedResPath = resPath;
            
            if ( !resPath.startsWith( "/" ) ) {
                normalizedResPath = "/" + normalizedResPath;
            }
            isResource = this.getClass().getResourceAsStream( normalizedResPath );

            if ( isResource == null ) {
                // try another class loader
                isResource = D6LUtil.class.getResourceAsStream( normalizedResPath );
            }
            
        }

        if ( isResource == null ) {
            // try another class loader
            isResource = Thread.currentThread().getContextClassLoader().getResourceAsStream( resPath );
        }
        
        if ( isResource == null ) {
            // unrecoverable
            throw new D6LException( "Can't load resource '" + resPath + "' from class path" );
        }
        return isResource;
            
    }

}
