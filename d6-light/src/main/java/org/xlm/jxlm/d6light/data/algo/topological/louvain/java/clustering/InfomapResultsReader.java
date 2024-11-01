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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * Reads the .tree files produced by the Infomap community detection algorithm.
 * See <a href="http://www.mapequation.org/code.html" target="_top>
 * The official Infomap website</a> for more on Infomap.
 */
public class InfomapResultsReader implements Clusterer {
  private final File file;
  private final Charset charset;
  private final List<int[]> communities = new ArrayList<>();
  private final TIntObjectHashMap<String> commTags = new TIntObjectHashMap<>();
  private int nodeCount = 0;
  private int layerCount = 0;

  public InfomapResultsReader(String filename, Charset charset) {
    file = new File(filename);
    this.charset = charset;
  }

  public InfomapResultsReader(String filename) {
    file = new File(filename);
    this.charset = StandardCharsets.UTF_8;
  }

  @Override
  public LayeredCommunityStructure cluster() {
    read();
    process();
    return new LayeredCommunityStructure(communities);
  }

  public void read() {
    try {
      getInfo();
      initialise();
      readLines();
    } catch (NumberFormatException | IOException e) {
      throw new IllegalStateException(e);
    }
  }

  // reads lines of format:
  // 1:1:1 0.00244731 "83698" 83698
  // and puts node ID and community info into <int, String> map
  private void readLines() throws NumberFormatException, IOException {
    String line;

    try (FileInputStream fis = new FileInputStream(file);
         InputStreamReader isr = new InputStreamReader(fis, charset);
         BufferedReader reader = new BufferedReader(isr)) {
      while ((line = reader.readLine()) != null) {
        if (!line.startsWith("#")) {
          final String[] splitLine = line.split(" ");
          final int node = Integer.parseInt(splitLine[3]);
          final String layers = splitLine[0].substring(0, splitLine[0].lastIndexOf(":"));
          commTags.put(node, layers);
        }
      }
    }
  }

  // gets the no. of nodes and layers from the file
  private void getInfo() throws NumberFormatException, IOException {
    String line;

    try (FileInputStream fis = new FileInputStream(file);
         InputStreamReader isr = new InputStreamReader(fis, charset);
         BufferedReader reader = new BufferedReader(isr)) {
      while ((line = reader.readLine()) != null) {
        if (!line.startsWith("#")) {
          final String[] splitLine = line.split(" ");
          final int node = Integer.parseInt(splitLine[3]);
          final int layers = splitLine[0].split(":").length - 1;
          if (node > nodeCount) {
            nodeCount = node;
          }
          if (layers > layerCount) {
            layerCount = layers;
          }
        }
      }
    }
    nodeCount++;
  }

  private void initialise() {
    for (int layer = 0; layer < layerCount; layer++) {
      final int[] comms = new int[nodeCount];
      communities.add(comms);
    }
  }

  private void print() {
    for (int node = 0; node < nodeCount; node++) {
      System.out.print(node + ":");
      for (int layer = 0; layer < layerCount; layer++) {
        System.out.print(communities.get(layer)[node] + ":");
      }
      System.out.println();
    }
  }

  private void process() {
    for (int layer = 0; layer < layerCount; layer++) {
      assignCommunityIDs(layer);
    }
  }

  private void assignCommunityIDs(int layer) {
    final TObjectIntHashMap<String> map = new TObjectIntHashMap<>();
    int count = 0;
    final int[] comms = communities.get(layer);

    for (int node = 0; node < nodeCount; node++) {
      final String tag = truncateTag(commTags.get(node), layer);
      if (!map.containsKey(tag)) {
        map.put(tag, count);
        count++;
      }
    }

    for (int node = 0; node < nodeCount; node++) {
      final String tag = truncateTag(commTags.get(node), layer);
      comms[node] = map.get(tag);
    }
  }

  // infomap communities are ordered as: 'top:high:mid:low'  if layer == 2, this would
  // cut off ':mid:low', returning 'top:high'
  private String truncateTag(String tag, int layer) {
    for (int i = 0; i < layer; i++) {
      final int t = tag.lastIndexOf(":");
      if (t != -1) {
        tag = tag.substring(0, t);
      }
    }
    return tag;
  }
}