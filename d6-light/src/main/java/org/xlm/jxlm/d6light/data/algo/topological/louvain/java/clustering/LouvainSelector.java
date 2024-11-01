/* MIT License

Copyright (c) 2018 Neil Justice

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. */

package org.xlm.jxlm.d6light.data.algo.topological.louvain.java.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xlm.jxlm.d6light.data.algo.topological.louvain.java.graph.LouvainGraph;
import org.xlm.jxlm.d6light.data.algo.topological.louvain.java.graph.LouvainGraphBuilder;


/**
 * Runs the louvain detector the set number of times and writes out the
 * partition data.
 */
public class LouvainSelector implements Clusterer {
	
  private final static Logger LOG = LogManager.getLogger(LouvainSelector.class);

  private final Random rnd = new Random();
  private final PartitionWriter writer = new PartitionWriter();
  private final String fileToRead;
  private final String fileToWrite;

  public LouvainSelector(String fileToRead, String fileToWrite) {
    this.fileToRead = fileToRead;
    this.fileToWrite = fileToWrite;
  }

  @Override
  public LayeredCommunityStructure cluster() {
    return cluster(10);
  }

  public LayeredCommunityStructure cluster(int times) {
	  
	  return cluster( times, null );
  }
  
  public LayeredCommunityStructure cluster(int times, Long initialSeed ) {

	long seed;
	double maxMod = 0d;
    double mod;
    double finalMod = Double.NaN;
    
    List<int[]> output = new ArrayList<>();

    if ( initialSeed != null ) {
    	rnd.setSeed( initialSeed );
    }
    
    
    LOG.info( "Running {} times:", times );
    for (int i = 0; i < times; i++) {
    	
      seed = rnd.nextLong();
      LOG.debug("Run {}:", i);
      final LouvainGraph g = new LouvainGraphBuilder().fromFile(fileToRead);
      final LouvainDetector detector = new LouvainDetector(g, seed);
      detector.cluster();
      
      // Trace
      boolean isTrace = ( i == times - 1 ); // Last pass
      
      mod = detector.modularity( isTrace );
      if (mod > maxMod) {
        maxMod = mod;
        output = detector.getCommunities();
      }
      
      finalMod = mod;
      
    }

    LOG.info("highest mod was {}", maxMod);
    writer.write(output, fileToWrite);
    
    return new LayeredCommunityStructure( output, finalMod, maxMod );
    
  }

}