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

import org.xlm.jxlm.d6light.data.algo.topological.louvain.exception.LouvainException;
import org.xlm.jxlm.d6light.data.algo.topological.louvain.java.graph.LouvainGraph;

import gnu.trove.map.hash.TIntIntHashMap;

/**
 * Given a list of graphs where each node in graph n + 1 is a community im
 * graph n, maps the partitionings of all graphs (except the first) as if they
 * were partitionings of the first graph.
 */
public class LayerMapper {
  private final List<LouvainGraph> graphs = new ArrayList<>();
  // maps between communities on L and nodes on L + 1:
  private final List<TIntIntHashMap> layerMaps = new ArrayList<>();
  private int layer = 0;

  // map from community -> node on layer above
  protected TIntIntHashMap createLayerMap(LouvainGraph g) {
    int count = 0;
    layer++;
    final boolean[] isFound = new boolean[g.order()];
    final TIntIntHashMap map = new TIntIntHashMap();
    // Arrays.sort(communities);

    for (int node = 0; node < g.order(); node++) {
      final int comm = g.partitioning().community(node);
      if (!isFound[comm]) {
        map.put(comm, count);
        isFound[comm] = true;
        count++;
      }
    }
    if (map.size() != g.partitioning().numComms()) {
      throw new LouvainException("Map creation failed: " +
          g.partitioning().numComms() + " != " +
          map.size());
    }
    layerMaps.add(map);
    graphs.add(g);
    return map;
  }

  // uses the layer maps to assign a community from each layer to the base layer
  // graph.
  protected List<int[]> run() {
    final List<int[]> rawComms = new ArrayList<>();
    final List<int[]> communities = new ArrayList<>();
    communities.add(graphs.get(0).partitioning().communities());

    for (int i = 0; i < layer; i++) {
      rawComms.add(graphs.get(i).partitioning().communities());
    }

    for (int i = 0; i < layer - 1; i++) {
      communities.add(mapToBaseLayer(i, rawComms));
    }

    return communities;
  }

  // maps layers to each other until the specified layer has been mapped to the
  // base layer
  private int[] mapToBaseLayer(int layer, List<int[]> rawComms) {
    int[] a = mapToNextLayer(graphs.get(layer), layerMaps.get(layer),
        rawComms.get(layer + 1));
    layer--;

    while (layer >= 0) {
      a = mapToNextLayer(graphs.get(layer), layerMaps.get(layer), a);
      layer--;
    }

    return a;
  }

  // maps each node in a layer to its community on the layer above it
  private int[] mapToNextLayer(LouvainGraph g, TIntIntHashMap map, int[] commsL2) {
    final int[] commsL1 = g.partitioning().communities();
    final int[] NL1toCL2 = new int[g.order()];

    for (int nodeL1 = 0; nodeL1 < g.order(); nodeL1++) {
      final int commL1 = commsL1[nodeL1];
      final int nodeL2 = map.get(commL1);
      final int commL2 = commsL2[nodeL2];
      NL1toCL2[nodeL1] = commL2;
    }

    return NL1toCL2;
  }
}
