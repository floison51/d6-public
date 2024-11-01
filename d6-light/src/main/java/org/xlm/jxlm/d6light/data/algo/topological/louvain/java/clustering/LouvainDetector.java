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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xlm.jxlm.d6light.data.algo.topological.louvain.java.graph.LouvainGraph;
import org.xlm.jxlm.d6light.data.algo.topological.louvain.java.graph.LouvainGraphBuilder;
import org.xlm.jxlm.d6light.data.algo.topological.louvain.util.ArrayUtils;

import gnu.trove.map.hash.TIntIntHashMap;

/**
 * Implementation of the Louvain method of community detection.
 */
public class LouvainDetector implements Clusterer {
  private final static Logger LOG = LogManager.getLogger(LouvainDetector.class);
  private final List<LouvainGraph> graphs = new ArrayList<>();
  private final Maximiser m = new Maximiser();
  private final Random rnd;
  private final LayerMapper mapper = new LayerMapper();
  private int totalMoves = 0;
  private int layer = 0; // current community layer
  private List<int[]> communities;

  private LouvainDetector() {
    rnd = new Random();
  }

  public LouvainDetector(LouvainGraph g, long seed) {
    this();
    graphs.add(g);
    rnd.setSeed(seed);
    LOG.info("Using seed " + seed);
  }

  public LouvainDetector(LouvainGraph g) {
    this();
    graphs.add(g);
    final long seed = rnd.nextLong();
    rnd.setSeed(seed);
    LOG.info("Using seed " + seed);
  }

  @Override
  public LayeredCommunityStructure cluster() {
    return cluster(Integer.MAX_VALUE);
  }

  public LayeredCommunityStructure cluster(int maxLayers) {
    if (maxLayers <= 0) {
      maxLayers = Integer.MAX_VALUE;
    }
    LOG.info("Detecting graph communities...");

    do {
      LOG.debug("Round :" + layer);
      totalMoves = m.run(graphs.get(layer));
      if (totalMoves > 0 && maxLayers >= layer) {
        addNewLayer();
      }
    }
    while (totalMoves > 0 && maxLayers >= layer);

    communities = mapper.run();
    return new LayeredCommunityStructure( communities );
    
  }

  /**
   * Get the modularity of the highest layer of the graph. If called before the clusterer has run, will throw
   * an {@link IndexOutOfBoundsException}.
   */
  public double modularity( boolean isTrace ) {
    return graphs.get(layer).partitioning().modularity( isTrace );
  }

  /**
   * Get the modularity of a specific layer of the graph. If called before the clusterer has run, will throw
   * an {@link IndexOutOfBoundsException}.
   *
   * @param l the index of the layer.
   */
  public double modularity(int l, boolean isTrace) {
    if (l >= graphs.size()) {
      throw new ArrayIndexOutOfBoundsException("Graph has " + graphs.size() + " layers, asked for layer " + l);
    }
    return graphs.get(l).partitioning().modularity( false );
  }

  public List<int[]> getCommunities() {
    return communities;
  }

  /**
   * Get the number of layers created during the detection process.
   *
   * If called before the clusterer has run, will return 0.
   */
  public int getLayerCount() {
    return layer;
  }

  /**
   * Get the graphs created during the detection process.
   *
   * If called before the clusterer has run, will return an empty list.
   *
   * @return an immutable view of the graphs list.
   */
  public List<LouvainGraph> getGraphs() {
    return Collections.unmodifiableList(graphs);
  }

  private void addNewLayer() {
    final LouvainGraph last = graphs.get(layer);
    final TIntIntHashMap map = mapper.createLayerMap(last);
    layer++;
    final LouvainGraph coarse = new LouvainGraphBuilder().coarseGrain(last, map);
    graphs.add(coarse);
  }


  class Maximiser {
    private static final double PRECISION = 0.000001;

    private LouvainGraph g;
    private int[] shuffledNodes;

    private int run(LouvainGraph g) {
      this.g = g;
      shuffledNodes = new int[g.order()];
      ArrayUtils.fillRandomly(shuffledNodes);
      totalMoves = 0;

      final long s1 = System.nanoTime();
      reassignCommunities();
      final long e1 = System.nanoTime();
      final double time = (e1 - s1) / 1000000000d;
      LOG.trace("seconds taken: " + time);

      return totalMoves;
    }

    private void reassignCommunities() {
      double mod = g.partitioning().modularity( false );
      double oldMod;
      int moves;
      boolean hasChanged;

      do {
        hasChanged = true;
        oldMod = mod;
        moves = maximiseLocalModularity();
        totalMoves += moves;
        mod = g.partitioning().modularity( false );
        if (mod - oldMod <= PRECISION) {
          hasChanged = false;
        }
        if (moves == 0) {
          hasChanged = false;
        }
      } while (hasChanged);
      LOG.debug("Mod: " + mod +
          " Comms: " + g.partitioning().numComms() +
          " Moves: " + totalMoves);
    }

    private int maximiseLocalModularity() {
      int moves = 0;
      for (int i = 0; i < g.order(); i++) {
        final int node = shuffledNodes[i];
        if (makeBestMove(node)) {
          moves++;
        }
      }
      return moves;
    }

    private boolean makeBestMove(int node) {
      double max = 0d;
      int best = -1;

      for (int i = 0; i < g.neighbours(node).size(); i++) {
        final int community = g.partitioning().community(g.neighbours(node).get(i));
        final double inc = deltaModularity(node, community);
        if (inc > max) {
          max = inc;
          best = community;
        }
      }

      if (best >= 0 && best != g.partitioning().community(node)) {
        g.partitioning().moveToComm(node, best);
        return true;
      } else {
        return false;
      }
    }

    // change in modularity if node is moved to community
    private double deltaModularity(int node, int community) {
      final double dnodecomm = g.partitioning().dnodecomm(node, community);
      final double ctot = g.partitioning().totDegree(community);
      final double wdeg = g.degree(node);
      return dnodecomm - ((ctot * wdeg) / g.m2());
    }
  }
}