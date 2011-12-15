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

import java.util.Arrays;

import org.graphstream.graph.implementations.AdjacencyListNode;

/**
 * Defines a node position for ants.
 * 
 * @author adutot, gsavin
 * 
 */
public class AntNode extends AdjacencyListNode implements Pheromonable {
	
	protected Pheromones pheromones = null;
	
	/**
	 * Number of ants of each colour. Indices in this array maps to indices in
	 * the colonies.
	 */
	protected int[] antCountsPerColony;

	/**
	 * Same as {@link #antCountsPerColony}, but used by commit.
	 */
	protected int[] antCountsPerColonyTmp;

	/**
	 * Total ant count of all colours on this node.
	 */
	protected int totalAntCount;

	/**
	 * Same as {@link #totalAntCount}, but used by commit.
	 */
	protected int totalAntCountTmp;

	/**
	 * Flag indicating if commit is needed.
	 */
	protected boolean needCommit = false;

	/**
	 * Allows to attribute a value to this node.
	 */
	protected float value;

	protected AntContext ctx;
	
	/**
	 * Constructor of an AntCo2Node.
	 * 
	 * @param ctx
	 *            ants context
	 * @param colony
	 *            initial node colony
	 * @param g
	 *            graph
	 * @param id
	 *            id of the node
	 */
	public AntNode(AntContext ctx, AntGraph g, String id) {
		super(g, id);
		this.ctx = ctx;
		reset();
	}

	public void reset() {
		antCountsPerColony = new int[ctx.getColonyCount()];
		antCountsPerColonyTmp = new int[ctx.getColonyCount()];
		
		switch(ctx.getAntParams().getDropPheromoneOn()) {
		case NODES:
		case NODES_AND_EDGES:
			pheromones = new Pheromones(ctx);
		}
	}
	
	/**
	 * Attribute a new value to the node.
	 * 
	 * @param value
	 */
	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * Retrieve the value attributed to the node.
	 * 
	 * @return the value attributed to the node
	 */
	public float getValue() {
		return value;
	}

	/**
	 * Total ant count on this node during the last step.
	 * 
	 * @return The ant count.
	 */
	public int getTotalAntCount() {
		return totalAntCount;
	}

	/**
	 * Number of ants of a given colour on this node during the last step.
	 * 
	 * @param color
	 *            The colour.
	 * @return The corresponding number of ants on this node.
	 */
	public int getAntCountForColony(Colony colony) {
		int index = colony.getIndex();

		if (index >= antCountsPerColony.length)
			return 0;

		return antCountsPerColony[index];
	}
	
	public Pheromones getPheromones() {
		return pheromones == null ? Pheromones.EMPTY : pheromones;
	}

	public float getEdgesTotalPheromoneLoad() {
		float sum = 0;
		
		for(AntEdge e : this.<AntEdge>getEachEdge())
			sum += e.getPheromones().getTotalLoad();
		
		return sum;
	}
	
	/**
	 * Commit changes.
	 */
	public void commit() {
		if (needCommit) {
			totalAntCount = totalAntCountTmp;

			for (int i = 0; i < antCountsPerColony.length; ++i)
				antCountsPerColony[i] = antCountsPerColonyTmp[i];

			needCommit = false;
		}
	}

	/**
	 * Step this node.
	 * 
	 * @param ctx
	 */
	public void step(AntContext ctx) {
		if( pheromones != null )
			pheromones.step(ctx);
		commit();
	}

	/**
	 * Get edges adjacent to this node.
	 * 
	 * @return an iterable on adjacent edges
	 */
	public Iterable<? extends AntEdge> eachEdge() {
		return getEdgeSet();
	}

	/**
	 * An ant arrived on this node. This only update temporary informations. Use
	 * {@link #commit()} to update. Commit should be called only at the end of
	 * each AntCO� step.
	 * 
	 * @param ant
	 *            The ant to register.
	 */
	public void registerAnt(Ant ant) {
		needCommit = true;
		totalAntCountTmp += 1;

		int index = ant.getColony().getIndex();
		checkColonyArraySizes(index);
		antCountsPerColonyTmp[index]++;
	}

	/**
	 * An ant left this node. This only update temporary informations. Use
	 * {@link #commit()} to update. Commit should be called only at the end of
	 * each AntCO� step.
	 * 
	 * @param ant
	 *            The ant to de-register.
	 */
	public void unregisterAnt(Ant ant) {
		needCommit = true;
		totalAntCountTmp -= 1;

		int index = ant.getColony().getIndex();
		checkColonyArraySizes(index);
		antCountsPerColonyTmp[index] -= 1;
	}

	/**
	 * Check that the antCountPerColor arrays are large enough.
	 * 
	 * @param index
	 */
	protected void checkColonyArraySizes(int index) {
		if (index >= antCountsPerColony.length)
			resizeArrays(index + 1);
	}

	/**
	 * Resize arrays.
	 * 
	 * @param newSize
	 */
	protected void resizeArrays(int newSize) {
		antCountsPerColonyTmp = Arrays.copyOf(antCountsPerColonyTmp, newSize);
		antCountsPerColony = Arrays.copyOf(antCountsPerColony, newSize);
	}
}
