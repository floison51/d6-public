

package org.xlm.jxlm.d6light.data.command;

import org.xlm.jxlm.d6light.data.model.D6LAbstractPackageEntity;

/**
 * Exception raised when an entity is not allocated
 * @author Loison
 *
 */
public class D6LNotAllocatedException extends Exception {

	private static final long serialVersionUID = -8734397591216852411L;
	
	private final int nbMaxEntitiesToDump;
	
	private final D6LAbstractPackageEntity pkg;

	/**
	 * Constructor
	 * @param message
	 * @param idLot
	 */
	public D6LNotAllocatedException( String message, D6LAbstractPackageEntity pkg ) {
		this( message, pkg, 1000 );
	}
	
	/**
	 * Constructor
	 * @param message
	 * @param idLot
	 * @param nbEntitiesToDump
	 */
	public D6LNotAllocatedException( String message, D6LAbstractPackageEntity pkg, int nbEntitiesToDump ) {
		super( message );
		this.pkg = pkg;
		this.nbMaxEntitiesToDump = nbEntitiesToDump;
	}

	public int getNbMaxEntitiesToDump() {
		return nbMaxEntitiesToDump;
	}

	public D6LAbstractPackageEntity getPackage() {
		return pkg;
	}
	
}
