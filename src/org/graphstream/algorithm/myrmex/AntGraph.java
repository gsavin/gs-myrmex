/*
 * This file is part of AntCo2.
 * 
 * AntCo2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * AntCo2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with AntCo2.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2009 - 2010
 * 	Antoine Dutot
 * 	Guilhelm Savin
 */
package org.graphstream.algorithm.myrmex;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.implementations.AdjacencyListGraph;

/**
 * The internal graph of AntCo2.
 * 
 * @author adutot, gsavin
 * 
 */
public class AntGraph extends AdjacencyListGraph {
	/**
	 * The context of this graph.
	 */
	protected AntContext ctx;

	/**
	 * Constructor for the graph.
	 * 
	 * @param context
	 *            context in which ants will evolve
	 */
	public AntGraph(AntContext context) {
		super(String.format("antco2-%X@%X", System.currentTimeMillis(), Thread
				.currentThread().getId()));

		this.ctx = context;

		setNodeFactory(new NodeFactory<AntNode>() {
			public AntNode newInstance(String id, Graph graph) {
				return new AntNode(ctx, (AntGraph) graph, id);
			}
		});

		setEdgeFactory(new EdgeFactory<AntEdge>() {
			public AntEdge newInstance(String id, Node src, Node dst,
					boolean directed) {
				return new AntEdge(ctx, 1, id, (AntNode) src, (AntNode) dst, directed);
			}
		});
	}
}
