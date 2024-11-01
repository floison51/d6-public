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

public class LayeredCommunityStructure {

	private final List<CommunityStructure> layerStructures;
	private final int layers;

	private final double finalModularity;

	private final double maxModularity;

	public LayeredCommunityStructure(List<int[]> communities) {
		this(communities, Double.NaN, Double.NaN);
	}

	public LayeredCommunityStructure(List<int[]> communities, double finalModularity, double maxModularity) {

		layers = communities.size();
		layerStructures = new ArrayList<>();
		for (int layer = 0; layer < layers; layer++) {
			layerStructures.add(new CommunityStructure(communities.get(layer)));
		}

		this.finalModularity = finalModularity;
		this.maxModularity = maxModularity;

	}

	public CommunityStructure layer(int layer) {
		if (layer >= layers) {
			throw new IndexOutOfBoundsException("layer index out of bounds");
		}
		return layerStructures.get(layer);
	}

	public int layers() {
		return layers;
	}

	public double getFinalModularity() {
		return finalModularity;
	}

	public double getMaxModularity() {
		return maxModularity;
	}
}
