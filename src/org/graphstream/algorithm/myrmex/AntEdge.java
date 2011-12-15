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

import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractNode;

/**
 * Defines an edge crossable by ants. Pheromons can be dropped on such edges.
 * 
 * @author adutot, gsavin
 * 
 */
public class AntEdge extends AbstractEdge implements Pheromonable {

	protected Pheromones pheromones;
	
	/**
	 * Weight of the edge.
	 */
	protected float value;

	protected AntContext ctx;
	
	/**
	 * Constructor for an edge.
	 * 
	 * @param ctx
	 *            ant context
	 * @param value
	 *            weight of the edge
	 * @param id
	 *            id of the edge
	 * @param from
	 *            source node when edge is directed, else first extremity
	 * @param to
	 *            target node when edge is directed, else second extremity
	 * @param directed
	 *            is the edge directed or not
	 */
	public AntEdge(AntContext ctx, float value, String id, AbstractNode from,
			AbstractNode to, boolean directed) {
		super(id, from, to, directed);

		this.value = value;
		this.ctx = ctx;

		// Also avoid a weight of 0.

		if (this.value == 0)
			this.value = 1;
		
		reset();
	}

	public void reset() {
		switch(ctx.getAntParams().getDropPheromoneOn()) {
		case EDGES:
		case NODES_AND_EDGES:
			pheromones = new Pheromones(ctx);
		}
	}
	
	/**
	 * Step method for this edge. Pheromones evaporation is done here.
	 * 
	 * @param ctx
	 *            ants context
	 */
	public void step(AntContext ctx) {
		if( pheromones != null )
			pheromones.step(ctx);
	}

	/**
	 * Get the weight of the edge.
	 * 
	 * @return weight of the edge
	 */
	public float getValue() {
		return value;
	}

	/**
	 * Set the weight of the edge.
	 * 
	 * @param value
	 *            new weight of the edge
	 */
	public void setValue(float value) {
		this.value = value;
	}
	
	public Pheromones getPheromones() {
		return pheromones == null ? Pheromones.EMPTY : pheromones;
	}
}
