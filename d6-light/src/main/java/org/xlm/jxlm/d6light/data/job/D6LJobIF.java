
package org.xlm.jxlm.d6light.data.job;

import org.xlm.jxlm.d6light.data.exception.D6LException;

/**
 * Basic job
 * @author Loison
 *
 */
@FunctionalInterface
public interface D6LJobIF<T>
{

    public void doJob( T entity ) throws D6LException;

}
