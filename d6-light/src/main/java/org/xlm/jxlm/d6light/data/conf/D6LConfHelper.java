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

package org.xlm.jxlm.d6light.data.conf;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.input.XmlStreamReader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xlm.jxlm.d6light.data.D6Exception;
import org.xml.sax.InputSource;

/**
 * Systemizer Data configuration helper
 * @author Francois Loison
 *
 */
public class D6LConfHelper {

	public static final String PKG_D6_LIGHT_DATA_CONF = "org.xlm.jxlm.d6light.data.conf";
	public static final String XSD_D6_LIGHT_DATA_CONF = "/org/xlm/jxlm/d6light/data/conf/d6l-conf.xsd";
	
	private D6LConfHelper() {
        throw new IllegalAccessError( "Utility class" );
	}
	
	/**
	 * Get configuration
	 * @param is configuration is 
	 * @param props properties to be resolved
	 * @return Configuration
	 * @throws Exception
	 */
	public static D6LightDataConf getConf( InputStream is, Properties props ) throws D6Exception {
		
		// get raw conf
	    
		D6LightDataConf conf  = ( D6LightDataConf ) getParsed( 
			is, PKG_D6_LIGHT_DATA_CONF, XSD_D6_LIGHT_DATA_CONF,
			props // parse ANT style props
		);
		
		return conf;
	}
	
 	/**
 	 * Parses a JAXB inputStream
 	 * @param is JAXB inputStream
 	 * @param pakkage package of JAXB java stubs
 	 * @param xsdClassPath XSD class path
 	 * @return parsed JAXB object
 	 * @throws Exception
 	 */
	public static Object getParsed( InputStream inputStream, String pakkage, String xsdClassPath ) throws D6Exception {
		return getParsed( inputStream, pakkage, xsdClassPath, null, null );
	}

	/**
     * Parses a JAXB inputStream
     * @param is JAXB inputStream
     * @param pakkage package of JAXB java stubs
     * @param xsdClassPath XSD class path
	 * @param xmlEncoding XML encoding
	 * @return parsed JAXB object
	 * @throws Exception
	 */
	public static Object getParsed( InputStream inputStream, String pakkage, String xsdClassPath, StringBuilder xmlEncoding ) throws D6Exception {
		return getParsed(inputStream, pakkage, xsdClassPath, null, xmlEncoding  );
	}

	/**
     * Parses a JAXB inputStream
     * @param is JAXB inputStream
     * @param pakkage package of JAXB java stubs
     * @param xsdClassPath XSD class path
	 * @param antProps Properties used to parse stream (${xxx} replacement)
	 * @return parsed JAXB object
	 * @throws Exception
	 */
	public static Object getParsed( InputStream inputStream, String pakkage, String xsdClassPath, Properties antProps ) throws D6Exception {
		return getParsed( inputStream, pakkage, xsdClassPath, antProps, null );
	}
	
	/**
	 * Parses a JAXB inputStream
     * @param is JAXB inputStream
     * @param pakkage package of JAXB java stubs
     * @param xsdClassPath XSD class path
     * @param antProps Properties used to parse stream (${xxx} replacement)
     * @param xmlEncoding XML encoding probed from document
     * @return parsed JAXB object
	 * @throws Exception
	 */
	public static Object getParsed( InputStream inputStream, String pakkage, String xsdClassPath, Properties antProps, StringBuilder xmlEncoding ) 
	    throws D6Exception
	{

	    try {
    		// read XML file
    
    		// make sure validation is OK
    		JAXBContext jc = JAXBContext.newInstance( pakkage, D6LConfHelper.class.getClassLoader() );
    		
    		Unmarshaller unmarshaller = jc.createUnmarshaller();
    		
    		SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
    		InputStream isSchema = D6LConfHelper.class.getResourceAsStream( xsdClassPath );
    		if ( isSchema == null ) {
    			throw new D6Exception( "Can't find schema from classpath " + xsdClassPath );
    		}
    		Schema schema = schemaFactory.newSchema( new StreamSource( isSchema) );
    		unmarshaller.setSchema( schema );
    
    		// read and validate
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		dbf.setNamespaceAware( true );
    		
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		
    		Document domDoc = null;
    		
    		try (
	    		// Use a reader sniffing encoding
	    		XmlStreamReader xmlStreamReader = new XmlStreamReader( inputStream );
	    	) {
	    		
    			domDoc = db.parse( new InputSource( xmlStreamReader ) );
	    		
	    		// retrieve XML encoding
	    		if ( xmlEncoding != null ) {
	    		    xmlEncoding.delete( 0, xmlEncoding.length() );
	    			xmlEncoding.append( domDoc.getXmlEncoding() );
	    		}
	    		
    		}
    		
    		if ( antProps != null ) {
    			
    			Project antProject = new Project();
    			for ( Entry<Object, Object> entry: antProps.entrySet() ) {
    				String name = (String) entry.getKey();
    				String value = (String) entry.getValue();
    				antProject.setProperty( name, value );
    			}
    
    			PropertyHelper antPropHelper = PropertyHelper.getPropertyHelper( antProject );
    			
    			parseAntProps( antPropHelper, domDoc.getDocumentElement() );
    			
    		}

    		Object parsed = unmarshaller.unmarshal( domDoc );
    		
    		if ( parsed instanceof JAXBElement ) {
    			JAXBElement<?> jaxbElement = (JAXBElement<?>) parsed;
    			return jaxbElement.getValue();
    		} else {
    			return parsed;
    		}

	    } catch ( Exception e ) {
	        
	        D6Exception.handleException( e );
	        
	    }
	    
	    // Should not reach here
	    return null;
	    
	}

	private static void parseAntProps( final PropertyHelper propHelper, Node node ) {
		
		// get element text
		if ( node instanceof Text ) {
			
			String txt = node.getNodeValue();
			// parse text
			txt = propHelper.replaceProperties( txt );
			// modify text element
			node.setNodeValue( txt );
			
		}
		
		// if element : parse attributes
		if ( node instanceof Element ) {
			
			Element elt = (Element) node;
			NamedNodeMap attrs = elt.getAttributes();
			for ( int i = 0; i < attrs.getLength(); i++ ) {
				
				// Parse attr value
				String attrValue = attrs.item( i ).getNodeValue();
				String parsedAttrValue = propHelper.replaceProperties( attrValue );
				attrs.item( i ).setNodeValue( parsedAttrValue );
				
			}
		}
		
		// browse children
		NodeList nlChildren = node.getChildNodes();
		for ( int i = 0; i < nlChildren.getLength(); i++ ) {
			
			Node nodeChild = nlChildren.item( i );
			// recurse
			parseAntProps( propHelper, nodeChild );
			
		}
		
	}

}
